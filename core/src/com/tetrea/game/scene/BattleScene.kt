package com.tetrea.game.scene

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.BattleState
import com.tetrea.game.battle.MatchState
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.res.Color
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.component.AnimatedBar
import com.tetrea.game.scene.component.HealthBar
import com.tetrea.game.scene.effect.TextParticleSpawner
import com.tetrea.game.screen.BattleScreen
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig
import com.tetrea.game.tetris.util.LineClearType
import com.tetrea.game.tetris.util.PieceType
import com.tetrea.game.util.Timer

class BattleScene(
    private val boardX: Float,
    private val boardY: Float,
    private val tetris: Tetris,
    private val state: BattleState,
    private val config: TetrisConfig,
    private val stage: Stage,
    private val res: Resources,
    private val battleScreen: BattleScreen
) {

    private val playerHeaderLabel = res.getLabel(fontScale = 1f).apply {
        setSize(99f, 16f)
        setAlignment(Align.right)
        setPosition(-3f, this@BattleScene.stage.height - 23)
    }
    private val enemyHeaderLabel = res.getLabel(fontScale = 1f).apply {
        setSize(99f, 16f)
        setAlignment(Align.left)
        setPosition(168f, this@BattleScene.stage.height - 23)
    }

    private val countdownLabel = res.getLabel(color = Color.GAME_YELLOW, fontScale = 2f).apply {
        setSize(config.width * SQUARE_SIZE.toFloat(), config.height * SQUARE_SIZE.toFloat())
        setAlignment(Align.center)
        setPosition(boardX, boardY)
    }
    private var countdown = 0
    private var countdownTimer = 0f
    private var startCountdown = false

    private val gameNumberLabel = res.getLabel().apply {
        width = config.width * SQUARE_SIZE.toFloat()
        setAlignment(Align.center)
        setPosition(boardX, boardY + 180)
    }
    private val resultsLabel = res.getLabel(color = Color.GAME_YELLOW, fontScale = 1.25f).apply {
        width = config.width * SQUARE_SIZE.toFloat()
        setAlignment(Align.center)
        setPosition(boardX, boardY + 130)
    }
    private val scoreLabel = res.getLabel(fontScale = 1.5f).apply {
        width = config.width * SQUARE_SIZE.toFloat()
        setAlignment(Align.center)
        setPosition(boardX, boardY + 100)
    }
    private var matchStateTag: TextureRegion? = null
    private val gameOverTimer = Timer(2f, { showPostGameResult1() })
    private val postGameResultTimer1 = Timer(2f, { showPostGameResult2() })
    private val postGameResultTimer2 = Timer(2f, { handleMatchState() })

    private val timeLabel: Label
    private val apmLabel: Label
    private val ppsLabel: Label
    private val textParticleSpawner: TextParticleSpawner

    private val enemyHpBar = HealthBar(
        movementDelay = 0.75f,
        x = 37f,
        y = stage.height - 44,
        maxValue = state.enemyMaxHp.toFloat(),
        maxWidth = 220f,
        height = 13f,
        barTexture = res.getTexture("red"),
        decayTexture = res.getTexture("bar_decay"),
        restoreTexture = res.getTexture("bar_restore")
    )
    private val enemyHpLabel = res.getLabel().apply {
        setSize(100f, 13f)
        setAlignment(Align.left)
        setPosition(44f, this@BattleScene.stage.height - 44)
    }
    private var enemyChargeDelay = MathUtils.random(4, 6)
    private var enemyChargeTimer = 0f
    private var enemyChargeBarWidth = 0f

    private val garbageBar = AnimatedBar(
        x = boardX - 5,
        y = boardY,
        speed = 0.6f,
        vertical = true,
        maxValue = config.height.toFloat(),
        maxWidth = 4f,
        maxHeight = config.height * SQUARE_SIZE.toFloat(),
        barTexture = res.getTexture("red")
    )

    init {
        stage.addActor(res.getLabel(
            state.firstToText,
            x = 107f,
            y = stage.height - 23f,
            fontScale = 1f
        ).apply {
            setSize(50f, 16f)
            setAlignment(Align.center)
        })
        stage.addActor(playerHeaderLabel)
        stage.addActor(enemyHeaderLabel)
        stage.addActor(enemyHpLabel)

        stage.addActor(countdownLabel)

        stage.addActor(res.getLabel(
            "TIME",
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 12f,
            fontScale = 0.5f
        ))
        timeLabel = res.getLabel(
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 8f,
            fontScale = 1f
        )
        stage.addActor(timeLabel)

        stage.addActor(Image(res.getTexture("apm_icon")).apply {
            x = boardX - 6f
            y = boardY - 26f
        })
        stage.addActor(res.getLabel(
            "APM",
            x = boardX + 16f,
            y = boardY - 18f,
            fontScale = 0.5f
        ))
        apmLabel = res.getLabel(
            x = boardX + 16f,
            y = boardY - 22f,
            fontScale = 1f
        )
        stage.addActor(apmLabel)

        stage.addActor(Image(res.getTexture("pps_icon")).apply {
            x = boardX + 72f
            y = boardY - 26f
        })
        stage.addActor(res.getLabel(
            "PPS",
            x = boardX + 90f,
            y = boardY - 18f,
            fontScale = 0.5f
        ))
        ppsLabel = res.getLabel(
            x = boardX + 90f,
            y = boardY - 22f,
            fontScale = 1f
        )
        stage.addActor(ppsLabel)
        stage.addActor(gameNumberLabel)
        stage.addActor(resultsLabel)
        stage.addActor(scoreLabel)

        startCountdown()

        textParticleSpawner = TextParticleSpawner(res, stage)
    }

    fun update(dt: Float) {
        playerHeaderLabel.setText(state.playerText)
        enemyHeaderLabel.setText(state.enemyText)

        timeLabel.setText(tetris.stats.time.formatMMSS())
        apmLabel.setText(String.format("%.1f", tetris.stats.apm))
        ppsLabel.setText(String.format("%.2f", tetris.stats.pps))

        if (startCountdown) {
            countdownTimer += dt
            if (countdownTimer >= 1f) {
                countdown--
                when (countdown) {
                    0 -> {
                        gameNumberLabel.isVisible = false
                        matchStateTag = null
                        countdownLabel.setText("GO!")
                        tetris.start()
                    }
                    -1 -> {
                        countdownLabel.isVisible = false
                        startCountdown = false
                    }
                    else -> countdownLabel.setText(countdown)
                }
                countdownTimer = 0f
            }
        }
        textParticleSpawner.update(dt)

        enemyHpBar.update(dt)
        enemyHpLabel.setText("${state.enemyHp}/${state.enemyMaxHp}")

        garbageBar.update(dt)

        if (tetris.started) {
            enemyChargeTimer += dt
            enemyChargeBarWidth = Interpolation.linear.apply(0f, 220f, enemyChargeTimer / enemyChargeDelay)
            if (enemyChargeTimer >= enemyChargeDelay) {
                enemyChargeBarWidth = 0f
                enemyChargeTimer = 0f
                enemyChargeDelay = MathUtils.random(4, 6)
                tetris.queueGarbage(MathUtils.random(1, 6))
            }
        } else {
            enemyChargeBarWidth = 0f
            enemyChargeTimer = 0f
        }

        gameOverTimer.update(dt)
        postGameResultTimer1.update(dt)
        postGameResultTimer2.update(dt)
    }

    fun render(batch: Batch) {
        batch.draw(res.getTexture("score_header"), 6f, stage.height - 24f)
        batch.draw(res.getTexture("tetris_board_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("item_slots_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("enemy_hp_bar"), 36f, stage.height - 54f)
        batch.draw(res.getTexture("yellow"), 37f, stage.height - 53f, enemyChargeBarWidth, 4f)

        enemyHpBar.render(batch)
        renderTetris(batch)

        matchStateTag?.let {
            batch.draw(it, boardX + 10, boardY + 60)
        }
    }

    fun startGameOverSequence() {
        gameNumberLabel.isVisible = true
        gameNumberLabel.setText("GAME ${state.gameNumber}")
        resultsLabel.isVisible = true
        resultsLabel.setText("FINISHED!")
        gameOverTimer.start()
    }

    fun spawnNumberParticle(lines: Int, x: Float, y: Float) {
        textParticleSpawner.spawn(
            lines.toString(),
            when (lines) {
                1, 2, 3 -> Color.GAME_WHITE
                4, 5, 6 -> Color.GAME_YELLOW
                else -> Color.GAME_LIGHT_BLUE
            },
            x, y
        )
    }

    fun spawnLineClearParticle(type: LineClearType) {
        if (type == LineClearType.None) return
        textParticleSpawner.spawn(
            type.desc,
            type.color,
            boardX + (config.width / 2) * SQUARE_SIZE,
            boardY + (config.height + 3) * SQUARE_SIZE,
            zi = 0f,
            vxScale = 0f,
            vyScale = 0.3f,
            vzScale = 0f,
            zNegVxScale = 0f,
            zNegVyScale = 0f,
            zNegVzScale = 0f,
            zPosVzScale = 0f,
            useGaussian = false
        )
    }

    fun spawnSpikeParticle(spike: Int) {
        textParticleSpawner.spawn(
            "$spike SPIKE",
            Color.GAME_YELLOW,
            boardX + (config.width / 2) * SQUARE_SIZE,
            boardY + (config.height + 3) * SQUARE_SIZE,
            zi = 0f,
            vxScale = 0f,
            vyScale = 0.3f,
            vzScale = 0f,
            zNegVxScale = 0f,
            zNegVyScale = 0f,
            zNegVzScale = 0f,
            zPosVzScale = 0f,
            useGaussian = false
        )
    }

    fun spawnPerfectClearParticle() {
        textParticleSpawner.spawn(
            LineClearType.PerfectClear.desc,
            LineClearType.PerfectClear.color,
            boardX + (config.width / 2) * SQUARE_SIZE,
            boardY + (config.height / 2) * SQUARE_SIZE,
            zi = 0f,
            vxScale = 0f,
            vyScale = 0.7f,
            vzScale = 0f,
            zNegVxScale = 0f,
            zNegVyScale = 0f,
            zNegVzScale = 0f,
            zPosVzScale = 0f,
            useGaussian = false
        )
    }

    fun spawnComboParticle(combo: Int) {
        textParticleSpawner.spawn(
            "COMBO X${combo - 1}",
            Color.GAME_WHITE,
            boardX - 38f,
            boardY + config.height * SQUARE_SIZE + 4f,
            zi = 0f,
            lifetime = 1.5f,
            vxScale = 0f,
            vyScale = 0.5f,
            vzScale = 0f,
            zNegVxScale = 0f,
            zNegVyScale = 0f,
            zNegVzScale = 0f,
            zPosVzScale = 0f,
            useGaussian = false,
            fontScale = 0.75f
        )
    }

    fun spawnB2BParticle(b2b: Int) {
        textParticleSpawner.spawn(
            "B2B X${b2b - 1}",
            Color.GAME_ORANGE,
            boardX + config.width * SQUARE_SIZE + 24f,
            boardY + config.height * SQUARE_SIZE + 4f,
            lifetime = 1.5f,
            zi = 0f,
            vxScale = 0f,
            vyScale = 0.5f,
            vzScale = 0f,
            zNegVxScale = 0f,
            zNegVyScale = 0f,
            zNegVzScale = 0f,
            zPosVzScale = 0f,
            useGaussian = false,
            fontScale = 0.75f
        )
    }

    fun attackEnemyHp(attack: Int) = enemyHpBar.applyChange(attack.toFloat(), true)

    fun addGarbage(lines: Int) = garbageBar.applyChange(lines.toFloat(), false)

    fun cancelGarbage(lines: Int) = garbageBar.applyChange(lines.toFloat(), true)

    fun resetGarbage() = garbageBar.reset()

    private fun renderTetris(batch: Batch) {
        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                if (y < config.height) {
                    batch.draw(res.getBoardUnit(),
                        boardX + tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + tetris.content[y][x].y * SQUARE_SIZE)
                }

                if (tetris.content[y][x].filled) {
                    batch.draw(res.getSquare(tetris.content[y][x].square.pieceType),
                        boardX + tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + tetris.content[y][x].y * SQUARE_SIZE)
                }
            }
        }
        tetris.currPiece?.let { currPiece ->
            currPiece.squares.forEach {
                batch.draw(res.getSquare(currPiece.pieceType),
                    boardX + it.x * SQUARE_SIZE,
                    boardY + it.y * SQUARE_SIZE)
                batch.draw(res.getGhost(currPiece.pieceType),
                    boardX + it.x * SQUARE_SIZE,
                    boardY + tetris.getGhostPieceY(it) * SQUARE_SIZE)
            }
        }

        tetris.holdPiece?.let { piece ->
            piece.squares.forEach {
                batch.draw(res.getSquare(piece.pieceType),
                    when (piece.pieceType) {
                        PieceType.I, PieceType.O -> (boardX - 47) + (it.x * SQUARE_SIZE)
                        else -> (boardX - 41) + (it.x * SQUARE_SIZE)
                    },
                    when (piece.pieceType) {
                        PieceType.I -> ((boardY + 9) + ((config.height - 4) * SQUARE_SIZE)) + (it.y * SQUARE_SIZE)
                        else -> ((boardY + 3) + ((config.height - 4) * SQUARE_SIZE)) + (it.y * SQUARE_SIZE)
                    })
            }
        }
        if (tetris.bag.isNotEmpty()) {
            for (i in 0 until config.numPreviews) {
                val piece = tetris.bag[i]
                val x = when (piece.pieceType) {
                    PieceType.I, PieceType.O -> boardX + (config.width * SQUARE_SIZE) + 18
                    else -> boardX + (config.width * SQUARE_SIZE) + 25
                }
                val y = boardY + ((config.height - 4) * SQUARE_SIZE) - (i * 38)
                piece.previewSquares.forEach {
                    batch.draw(res.getSquare(piece.pieceType),
                        x + it.x * SQUARE_SIZE,
                        y + it.y * SQUARE_SIZE)
                }
            }
        }
        state.scene.garbageBar.render(batch)
    }

    private fun startCountdown() {
        tetris.generateQueue()
        state.updateGameNumber()
        state.resetEnemyHp()
        enemyHpBar.reset()

        resultsLabel.isVisible = false
        scoreLabel.isVisible = false
        gameNumberLabel.setText("GAME ${state.gameNumber}")
        gameNumberLabel.isVisible = true
        countdown = config.startDelay
        countdownLabel.isVisible = true
        countdownLabel.setText(countdown)
        countdownTimer = 0f
        startCountdown = true
    }

    private fun showPostGameResult1() {
        tetris.reset()
        tetris.currPiece = null
        tetris.bag.clear()

        resultsLabel.setText(if (state.playerWonGame) "YOU WIN!" else "YOU LOST!")
        scoreLabel.isVisible = true
        scoreLabel.setText("${state.playerScore} - ${state.enemyScore}")
        postGameResultTimer1.start()
    }

    private fun showPostGameResult2() {
        state.updateScores()
        scoreLabel.setText("${state.playerScore} - ${state.enemyScore}")
        val matchState = state.getMatchState()
        matchStateTag = when (matchState) {
            MatchState.Tiebreaker -> res.getTexture("tiebreaker_tag")
            MatchState.MatchPoint -> res.getTexture("match_point_tag")
            else -> null
        }
        postGameResultTimer2.start()
    }

    private fun handleMatchState() {
        val matchState = state.getMatchState()
        if (matchState == MatchState.PlayerWin || matchState == MatchState.EnemyWin) {
            battleScreen.onBattleEnd(matchState)
        } else {
            startCountdown()
        }
    }
}