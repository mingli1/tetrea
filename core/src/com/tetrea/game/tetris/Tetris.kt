package com.tetrea.game.tetris

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.extension.default
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.screen.BattleScreen
import com.tetrea.game.tetris.model.Piece
import com.tetrea.game.tetris.model.Square
import com.tetrea.game.tetris.model.Unit
import com.tetrea.game.tetris.util.LineClearType
import com.tetrea.game.tetris.util.PieceType
import com.tetrea.game.util.Timer
import kotlin.math.max

class Tetris(
    private val screenX: Float,
    private val screenY: Float,
    private val config: TetrisConfig,
    private val screen: BattleScreen
) {

    var started = false

    private val piecesPool = mutableListOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )
    private val holdPool = listOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )

    var currPiece: Piece? = null
    val stats = TetrisStats()

    var canHold = true
    var rightHeld = false
    var leftHeld = false

    var holdPiece: Piece? = null
    val bag = mutableListOf<Piece>()
    private val garbage = mutableListOf<Int>()

    val content = Array(config.height * 2) { y ->
        Array(config.width) { x ->
            Unit(Square(this, PieceType.None), x, y, false)
        }
    }
    private val rowsToClear = mutableListOf<Int>()
    var piecesPlaced = 0
    private var combo = 0
    private var b2b = 0
    private var totalB2b = 0
    private var linesSent = 0
    private var totalAttack = 0
    private var attack = 0
    private var spike = 0
    private var currLineClearType = LineClearType.None

    var clockTimer = 0f
    var apm = 0f
    var pps = 0f
    private var gravityTimer = Timer(config.gravity, { currPiece?.move(0, -1) }, true)

    private var lockDelay1Timer = 0f
    private var lockDelay2Timer = 0f
    private var startLockDelay2 = false
    private var lockDelay3Timer = Timer(config.lockDelay3, { hardDrop() }, true)

    private var startRotationTimer = false
    private var rotationTimer = 0f
    private var longRotationTimer = 0f
    private var garbageTimer = Timer(config.garbageDelay, { receiveGarbage() })

    private var solidGarbageRow = 0

    init { reset(resetGarbage = false) }

    fun start() {
        reset()
        currPiece = getNextPiece()
        started = true
    }

    fun update(dt: Float) {
        if (!started) return

        clockTimer += dt
        apm = totalAttack / clockTimer * 60
        pps = piecesPlaced / clockTimer

        gravityTimer.update(dt)

        if (!currPiece?.canMove(0, -1).default(true) && !startLockDelay2) {
            if (startRotationTimer) {
                longRotationTimer += dt
                rotationTimer += dt
                if (rotationTimer >= config.lockDelay1) {
                    hardDrop()
                    startRotationTimer = false
                }
                if (longRotationTimer >= config.lockDelay2) {
                    hardDrop()
                    startRotationTimer = false
                }
            } else {
                lockDelay1Timer += dt
                if (lockDelay1Timer >= config.lockDelay1) hardDrop()
            }
        } else {
            lockDelay1Timer = 0f
        }

        if (startLockDelay2) {
            if ((leftHeld && !currPiece?.canMove(-1, 0).default(true)) || (rightHeld && !currPiece?.canMove(1, 0).default(true))) {
                startLockDelay2 = false
            } else {
                lockDelay2Timer += dt
                if (lockDelay2Timer >= config.lockDelay2) {
                    startLockDelay2 = false
                    hardDrop()
                }
            }
        }

        lockDelay3Timer.update(dt)
        garbageTimer.update(dt)
    }

    fun isWithinBounds(x: Int, y: Int): Boolean {
        return x in 0 until config.width && y in 0 until config.height * 2
    }

    fun isUnitEmpty(x: Int, y: Int): Boolean {
        return !content[y][x].filled
    }

    fun queueGarbage(numLines: Int) {
        garbage.add(numLines)
        screen.scene.addGarbage(numLines)
    }

    fun addSquare(x: Int, y: Int, square: Square) {
        content[y][x].square.let {
            it.x = square.x
            it.y = square.y
            it.pieceType = square.pieceType
        }
        content[y][x].filled = true
    }

    fun increaseGravity() {
        if (gravityTimer.delay < config.gravity) {
            gravityTimer.delay -= 0.1f
        } else {
            gravityTimer.delay -= 0.8f
        }
    }

    fun addSolidGarbage(numLines: Int) {
        offsetStack(numLines)
        solidGarbageRow += numLines

        for (y in 0 until solidGarbageRow) {
            for (x in 0 until config.width) {
                content[y][x].run {
                    filled = true
                    square.pieceType = PieceType.Solid
                }
            }
        }
        // top out
        if (solidGarbageRow >= config.height) gameOver(false)
        if (!currPiece?.canMove(0, -1).default(true)) gameOver(false)
    }

    fun isWithinHeight(y: Int) = y < config.height * 2

    fun isToppedOut(y: Int) = y >= config.height

    fun hardDrop() {
        instantSoftDrop()
        currPiece?.lock()
        if (currPiece?.isToppedOut().default(false)) gameOver(false)
        else currPiece = getNextPiece()
    }

    fun instantDas(right: Boolean) {
        for (i in 0 until config.width) {
            if (!currPiece?.move(if (right) 1 else -1, 0).default(true)) break
        }
    }

    fun instantSoftDrop() {
        for (i in 0 until config.height + 1) {
            if (!currPiece?.move(0, -1).default(true)) break
        }
    }

    fun holdCurrPiece() {
        if (canHold) {
            val piece = holdPiece
            holdPiece = holdPool.find { it.pieceType == currPiece?.pieceType }?.apply { init(0, 0) }
            currPiece = piece?.pieceType?.let { hold ->
                piecesPool.find { it.pieceType == hold }?.apply { init(4, config.height + 1) }
            } ?: getNextPiece()
            canHold = false
        }
    }

    fun getLineClears(squares: Array<Square>) {
        rowsToClear.clear()
        attack = 0

        for (y in config.height - 1 downTo 0) {
            var rowFilled = true
            for (x in 0 until config.width) {
                if (content[y][x].square.pieceType == PieceType.Solid || !(content[y][x].filled || squares.any { it.x == x && it.y == y })) {
                    rowFilled = false
                }
            }
            if (rowFilled) rowsToClear.add(y)
        }
        val numLinesToClear = rowsToClear.size

        if (numLinesToClear == 0) {
            combo = 0
            spike = 0
            if (garbage.isNotEmpty()) garbageTimer.start()
            return
        } else {
            combo++
        }

        stats.maxCombo = max(stats.maxCombo, combo)

        attack += if (combo >= config.comboTable.table.size) {
            config.comboTable.max
        } else {
            config.comboTable.table[combo]
        }
        if (combo > 1) screen.scene.spawnComboParticle(combo)

        val b2bBonus = if (b2b >= config.b2bTable.table.size) {
            config.b2bTable.max
        } else {
            config.b2bTable.table[b2b]
        }

        if (currPiece?.pieceType == PieceType.T && currPiece?.isTwist().default(false)) {
            applyTSpin(numLinesToClear, b2bBonus)
            b2b++
            totalB2b++
            if (b2b > 1) screen.scene.spawnB2BParticle(b2b)
            return
        }
        applyLineClears(numLinesToClear, b2bBonus)

        if (numLinesToClear < 4) b2b = 0
        else {
            b2b++
            totalB2b++
        }

        if (b2b > 1) screen.scene.spawnB2BParticle(b2b)
    }

    fun clearLines() {
        if (rowsToClear.isEmpty()) return
        rowsToClear.forEach { row ->
            if (row + 1 < config.height * 2) {
                for (i in row + 1 until config.height * 2) {
                    content[i - 1].forEachIndexed { index, unit ->
                        val topUnit = content[i][index]
                        unit.filled = topUnit.filled
                        unit.square.pieceType = topUnit.square.pieceType

                        topUnit.filled = false
                        topUnit.square.pieceType = PieceType.None
                    }
                }
            }
        }

        if (content.all { row -> row.all { !it.filled } }) {
            stats.numPC++
            attack += config.attackPC
            screen.scene.spawnCenterParticle(LineClearType.PerfectClear.desc, LineClearType.PerfectClear.color)
        }

        var crit = false
        if (MathUtils.random() <= config.critChance) {
            attack = (attack * config.critMultiplier).toInt()
            crit = true
        }

        totalAttack += attack
        cancelGarbage()
        linesSent += attack

        if (attack > 0) spike += attack
        else spike = 0

        stats.maxSpike = max(stats.maxSpike, spike)

        if (spike >= config.spikeThreshold) {
            screen.scene.spawnSpikeParticle(spike)
        } else {
            screen.scene.spawnLineClearParticle(currLineClearType)
        }

        if (attack > 0) {
            currPiece?.let {
                screen.scene.spawnNumberParticle(
                    attack,
                    screenX + it.squares[0].x * SQUARE_SIZE,
                    screenY + it.squares[0].y * SQUARE_SIZE,
                    crit
                )
            }
            if (screen.state.attackEnemy(attack)) gameOver(true)
        }
    }

    fun generateQueue() {
        bag.clear()
        repeat(2) { addToBag() }
    }

    fun resetVisibleStats() {
        clockTimer = 0f
        apm = 0f
        pps = 0f
    }

    fun reset(resetGarbage: Boolean = true) {
        started = false

        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                content[y][x].let {
                    it.filled = false
                    it.square.pieceType = PieceType.None
                }
            }
        }
        holdPiece = null
        canHold = true
        piecesPlaced = 0
        garbage.clear()
        gravityTimer.delay = config.gravity

        combo = 0
        totalB2b = 0
        b2b = 0
        totalAttack = 0
        linesSent = 0
        currLineClearType = LineClearType.None

        solidGarbageRow = 0
        startLockDelay2 = false
        garbageTimer.reset()

        if (resetGarbage) screen.scene.resetGarbage()
    }

    fun toggleLockDelay2(start: Boolean) {
        startLockDelay2 = start
    }

    fun onRotate() {
        startRotationTimer = true
        rotationTimer = 0f
        lockDelay2Timer = 0f
    }

    fun getGhostPieceY(square: Square): Int {
        var y = square.y
        var offset = -1
        for (i in 0 until config.height + 1) {
            if (!currPiece?.canMove(0, offset).default(true)) break
            y--
            offset--
        }
        return y
    }

    fun topOfStack(): Int {
        var topOfStack = 0
        for (y in config.height - 1 downTo solidGarbageRow) {
            if (content[y].any { it.filled }) {
                topOfStack = y
                break
            }
        }
        return topOfStack
    }

    private fun gameOver(win: Boolean) {
        started = false
        apm = totalAttack / clockTimer * 60
        pps = piecesPlaced / clockTimer
        currPiece = null
        recordStats()
        screen.state.playerWonGame = win
        screen.scene.startGameOverSequence()
    }

    private fun receiveGarbage() {
        if (garbage.isEmpty()) return
        val lines = garbage.sum()
        offsetStack(lines)
        var currY = solidGarbageRow
        for (i in garbage.size - 1 downTo 0) {
            var numLines = garbage[i]
            if (currY + numLines > config.height * 2) {
                numLines = config.height * 2 - currY
            }
            val holeX = MathUtils.random(config.width - 1)
            for (y in currY until currY + numLines) {
                for (x in 0 until config.width) {
                    if (x != holeX) {
                        content[y][x].run {
                            filled = true
                            square.pieceType = PieceType.Garbage
                        }
                    }
                }
            }
            currY += numLines
            if (currY >= config.height * 2) break
        }
        // top out
        if (lines >= config.height) gameOver(false)

        screen.scene.cancelGarbage(lines)
        garbage.clear()
    }

    private fun cancelGarbage() {
        if (garbage.isEmpty()) return
        if (attack <= 0) return
        val totalGarbage = garbage.sum()
        var newAttack = attack - totalGarbage
        if (newAttack < 0) newAttack = 0

        screen.scene.cancelGarbage(attack)

        val remainingGarbage = totalGarbage - attack
        attack = newAttack
        if (remainingGarbage <= 0) {
            garbage.clear()
            return
        }

        var numToDrop = 0
        var total = totalGarbage
        for (i in garbage.indices) {
            if (total - garbage[i] > remainingGarbage) {
                total -= garbage[i]
                numToDrop++
            } else if (total - garbage[i] < remainingGarbage) {
                garbage[i] -= total - remainingGarbage
                break
            } else {
                numToDrop++
                break
            }
        }
        repeat(numToDrop) {
            garbage.removeAt(0)
        }
    }

    private fun getNextPiece(): Piece {
        val nextPiece = bag.removeAt(0).apply {
            init(4, config.height + 1)
        }
        if (bag.size <= config.bagSize) addToBag()

        resetTimers()
        return nextPiece
    }

    private fun offsetStack(lines: Int) {
        if (lines == 0) return
        val topOfStack = topOfStack()

        for (y in topOfStack downTo solidGarbageRow) {
            for (x in 0 until config.width) {
                if (y + lines < config.height * 2) {
                    content[y + lines][x].run {
                        filled = content[y][x].filled
                        square.pieceType = content[y][x].square.pieceType
                    }
                }
                content[y][x].run {
                    filled = false
                    square.pieceType = PieceType.None
                }
            }
        }
        currPiece?.let { piece ->
            val maxY = piece.getMaxY()
            if (maxY + lines >= config.height + 1) piece.move(0, config.height + 2 - maxY)
            else piece.move(0, lines)
        }
    }

    private fun addToBag() {
        piecesPool.shuffle()
        bag.addAll(piecesPool)
    }

    private fun resetTimers() {
        gravityTimer.reset()

        lockDelay1Timer = 0f
        lockDelay2Timer = 0f
        lockDelay3Timer.reset()

        rotationTimer = 0f
        longRotationTimer = 0f
    }

    private fun applyLineClears(lines: Int, b2b: Int) {
        when (lines) {
            1 -> {
                stats.numSingle++
                attack += config.attackSingle
                currLineClearType = LineClearType.None
            }
            2 -> {
                stats.numDouble++
                attack += config.attackDouble
                currLineClearType = LineClearType.Double
            }
            3 -> {
                stats.numTriple++
                attack += config.attackTriple
                currLineClearType = LineClearType.Triple
            }
            4 -> {
                stats.numQuad++
                attack += config.attackQuad + b2b
                currLineClearType = LineClearType.Quad
            }
        }
    }

    private fun applyTSpin(lines: Int, b2b: Int) {
        when (lines) {
            1 -> {
                stats.numTSS++
                attack += config.attackTSS + b2b
                currLineClearType = LineClearType.TSS
            }
            2 -> {
                stats.numTSD++
                attack += config.attackTSD + b2b
                currLineClearType = LineClearType.TSD
            }
            3 -> {
                stats.numTST++
                attack += config.attackTST + b2b
                currLineClearType = LineClearType.TST
            }
        }
    }

    private fun recordStats() {
        stats.time += clockTimer
        stats.numPiecesPlaced += piecesPlaced
        stats.numB2B += totalB2b
        stats.attack += totalAttack
        stats.linesSent += linesSent
        stats.apmList.add(apm)
        stats.ppsList.add(pps)
    }
}