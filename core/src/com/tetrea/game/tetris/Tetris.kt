package com.tetrea.game.tetris

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.tetris.model.Piece
import com.tetrea.game.tetris.model.Square
import com.tetrea.game.tetris.model.Unit
import com.tetrea.game.tetris.util.PieceType
import com.tetrea.game.util.Timer

class Tetris(
    private val screenX: Float,
    private val screenY: Float,
    private val config: TetrisConfig,
    private val res: Resources
) {

    private val PIECES_POOL = mutableListOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )
    private val HOLD_POOL = listOf(
        Piece(this, PieceType.L),
        Piece(this, PieceType.J),
        Piece(this, PieceType.S),
        Piece(this, PieceType.Z),
        Piece(this, PieceType.T),
        Piece(this, PieceType.I),
        Piece(this, PieceType.O)
    )

    lateinit var currPiece: Piece
    val stats = TetrisStats()

    var canHold = true
    var rightHeld = false
    var leftHeld = false

    private var holdPiece: Piece? = null
    private val bag = mutableListOf<Piece>()
    private val garbage = mutableListOf<Int>()

    private val content = Array(config.height * 2) { y ->
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

    private var clockTimer = 0f
    private var gravityTimer = Timer(config.gravity, { currPiece.move(0, -1) }, true)

    private var lockDelay1Timer = 0f
    private var lockDelay2Timer = 0f
    private var startLockDelay2 = false
    private var lockDelay3Timer = Timer(config.lockDelay3, { hardDrop() }, true)

    private var startRotationTimer = false
    private var rotationTimer = 0f
    private var longRotationTimer = 0f
    private var garbageTimer = Timer(config.garbageDelay, { receiveGarbage() })

    private var solidGarbageRow = 0

    init {
        reset()
        queueGarbage(4)
    }

    fun update(dt: Float) {
        clockTimer += dt

        stats.time = clockTimer
        stats.pps = piecesPlaced / clockTimer
        stats.numB2B = totalB2b
        stats.attack = totalAttack
        stats.apm = totalAttack / clockTimer * 60
        stats.linesSent = linesSent

        gravityTimer.update(dt)

        if (!currPiece.canMove(0, -1) && !startLockDelay2) {
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
            if ((leftHeld && !currPiece.canMove(-1, 0)) || (rightHeld && !currPiece.canMove(1, 0))) {
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
    }

    fun addSquare(x: Int, y: Int, square: Square) {
        content[y][x].square.let {
            it.x = square.x
            it.y = square.y
            it.pieceType = square.pieceType
        }
        content[y][x].filled = true
    }

    fun addSolidGarbage(numLines: Int) {
        offsetStack(numLines)
        solidGarbageRow = numLines

        for (y in 0 until numLines) {
            for (x in 0 until config.width) {
                content[y][x].run {
                    filled = true
                    square.pieceType = PieceType.Solid
                }
            }
        }
        // top out
        if (numLines >= config.height) reset()
        if (!currPiece.canMove(0, -1)) reset()
    }

    fun isWithinHeight(y: Int) = y < config.height * 2

    fun isToppedOut(y: Int) = y >= config.height

    fun hardDrop() {
        instantSoftDrop()
        currPiece.lock()
        if (currPiece.isToppedOut()) reset()
        else currPiece = getNextPiece()
    }

    fun instantDas(right: Boolean) {
        for (i in 0 until config.width) {
            if (!currPiece.move(if (right) 1 else -1, 0)) break
        }
    }

    fun instantSoftDrop() {
        for (i in 0 until config.height + 1) {
            if (!currPiece.move(0, -1)) break
        }
    }

    fun holdCurrPiece() {
        if (canHold) {
            val piece = holdPiece
            holdPiece = HOLD_POOL.find { it.pieceType == currPiece.pieceType }?.apply { init(0, 0) }
            currPiece = piece?.pieceType?.let { hold ->
                PIECES_POOL.find { it.pieceType == hold }?.apply { init(4, config.height + 1) }
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
            if (garbage.isNotEmpty()) garbageTimer.start()
            return
        } else {
            combo++
        }

        attack += config.comboTable(combo)

        val applyB2bBonus = b2b > 0

        if (currPiece.pieceType == PieceType.T && currPiece.isTwist()) {
            applyTSpin(numLinesToClear, applyB2bBonus)
            b2b++
            totalB2b++
            return
        }
        applyLineClears(numLinesToClear, applyB2bBonus)

        if (numLinesToClear < 4) b2b = 0
        else {
            b2b++
            totalB2b++
        }
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
        }
        totalAttack += attack
        cancelGarbage()
        linesSent += attack
    }

    fun reset() {
        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                content[y][x].let {
                    it.filled = false
                    it.square.pieceType = PieceType.None
                }
            }
        }
        bag.clear()
        repeat(2) { addToBag() }
        currPiece = getNextPiece()
        holdPiece = null
        canHold = true
        piecesPlaced = 0
        clockTimer = 0f
        garbage.clear()

        combo = 0
        totalB2b = 0
        b2b = 0
        totalAttack = 0
        linesSent = 0

        solidGarbageRow = 0
        startLockDelay2 = false
        garbageTimer.reset()

        stats.reset()
    }

    fun toggleLockDelay2(start: Boolean) {
        startLockDelay2 = start
    }

    fun onRotate() {
        startRotationTimer = true
        rotationTimer = 0f
        lockDelay2Timer = 0f
    }

    fun render(batch: Batch) {
        batch.draw(res.getTexture("tetris_board_bg"), screenX - 66, screenY - 1)
        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                if (y < config.height) {
                    batch.draw(res.getBoardUnit(),
                        screenX + content[y][x].x * SQUARE_SIZE,
                        screenY + content[y][x].y * SQUARE_SIZE)
                }

                if (content[y][x].filled) {
                    batch.draw(res.getSquare(content[y][x].square.pieceType),
                        screenX + content[y][x].x * SQUARE_SIZE,
                        screenY + content[y][x].y * SQUARE_SIZE)
                }
            }
        }
        currPiece.squares.forEach {
            batch.draw(res.getSquare(currPiece.pieceType),
                screenX + it.x * SQUARE_SIZE,
                screenY + it.y * SQUARE_SIZE)
            batch.draw(res.getGhost(currPiece.pieceType),
                screenX + it.x * SQUARE_SIZE,
                screenY + getGhostPieceY(it) * SQUARE_SIZE)
        }
        holdPiece?.let { piece ->
            piece.squares.forEach {
                batch.draw(res.getSquare(piece.pieceType),
                    when (piece.pieceType) {
                        PieceType.I, PieceType.O -> (screenX - 47) + (it.x * SQUARE_SIZE)
                        else -> (screenX - 41) + (it.x * SQUARE_SIZE)
                    },
                    when (piece.pieceType) {
                        PieceType.I -> ((screenY + 9) + ((config.height - 4) * SQUARE_SIZE)) + (it.y * SQUARE_SIZE)
                        else -> ((screenY + 3) + ((config.height - 4) * SQUARE_SIZE)) + (it.y * SQUARE_SIZE)
                    })
            }
        }
        for (i in 0 until config.numPreviews) {
            val piece = bag[i]
            val x = when (piece.pieceType) {
                PieceType.I, PieceType.O -> screenX + (config.width * SQUARE_SIZE) + 18
                else -> screenX + (config.width * SQUARE_SIZE) + 25
            }
            val y = screenY + ((config.height - 4) * SQUARE_SIZE) - (i * 38)
            piece.previewSquares.forEach {
                batch.draw(res.getSquare(piece.pieceType),
                    x + it.x * SQUARE_SIZE,
                    y + it.y * SQUARE_SIZE)
            }
        }
        batch.draw(res.getTexture("red"), screenX - 5, screenY, 4f, getGarbageBarHeight())
    }

    private fun receiveGarbage() {
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
        if (lines >= config.height) reset()
        if (!currPiece.canMove(0, -1)) reset()

        garbage.clear()
    }

    private fun cancelGarbage() {
        if (garbage.isEmpty()) return
        if (attack <= 0) return
        val totalGarbage = garbage.sum()
        var newAttack = attack - totalGarbage
        if (newAttack < 0) newAttack = 0

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

    private fun getGhostPieceY(square: Square): Int {
        var y = square.y
        var offset = -1
        for (i in 0 until config.height + 1) {
            if (!currPiece.canMove(0, offset)) break
            y--
            offset--
        }
        return y
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
        var topOfStack = 0
        for (y in config.height - 1 downTo solidGarbageRow) {
            if (content[y].any { it.filled }) {
                topOfStack = y
                break
            }
        }

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
    }

    private fun addToBag() {
        PIECES_POOL.shuffle()
        bag.addAll(PIECES_POOL)
    }

    private fun resetTimers() {
        gravityTimer.reset()

        lockDelay1Timer = 0f
        lockDelay2Timer = 0f
        lockDelay3Timer.reset()

        rotationTimer = 0f
        longRotationTimer = 0f
    }

    private fun getGarbageBarHeight(): Float {
        val lines = garbage.sum()
        val totalHeight = config.height.toFloat() * SQUARE_SIZE
        if (lines > config.height) return totalHeight
        return (lines / config.height.toFloat()) * totalHeight
    }

    private fun applyLineClears(lines: Int, b2b: Boolean) {
        when (lines) {
            1 -> stats.numSingle++
            2 -> {
                stats.numDouble++
                attack += config.attackSingle
            }
            3 -> {
                stats.numTriple++
                attack += config.attackDouble
            }
            4 -> {
                stats.numQuad++
                attack += config.attackQuad + (if (b2b) config.b2bBonus else 0)
            }
        }
    }

    private fun applyTSpin(lines: Int, b2b: Boolean) {
        when (lines) {
            1 -> {
                stats.numTSS++
                attack += config.attackTSS + (if (b2b) config.b2bBonus else 0)
            }
            2 -> {
                stats.numTSD++
                attack += config.attackTSD + (if (b2b) config.b2bBonus else 0)
            }
            3 -> {
                stats.numTST++
                attack += config.attackTST + (if (b2b) config.b2bBonus else 0)
            }
        }
    }
}