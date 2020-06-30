package com.tetrea.game.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.Action
import com.tetrea.game.battle.MatchState
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.res.*
import com.tetrea.game.scene.component.AnimatedBar
import com.tetrea.game.scene.component.HealthBar
import com.tetrea.game.scene.effect.TextParticleSpawner
import com.tetrea.game.screen.BattleScreen
import com.tetrea.game.tetris.TetrisConfig
import com.tetrea.game.tetris.util.LineClearType
import com.tetrea.game.tetris.util.PieceType
import com.tetrea.game.util.Timer

private const val END_SEQUENCE_DELAY = 2f

class BattleScene(
    private val boardX: Float,
    private val boardY: Float,
    private val config: TetrisConfig,
    private val stage: Stage,
    private val res: Resources,
    private val screen: BattleScreen
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

    private val countdownLabel = res.getLabel(color = GAME_YELLOW, fontScale = 2f).apply {
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
    private val resultsLabel = res.getLabel(color = GAME_YELLOW, fontScale = 1.25f).apply {
        width = config.width * SQUARE_SIZE.toFloat()
        setAlignment(Align.center)
        setPosition(boardX, boardY + 130)
    }
    private val scoreLabel = res.getLabel(fontScale = 1.5f).apply {
        width = config.width * SQUARE_SIZE.toFloat()
        setAlignment(Align.center)
        setPosition(boardX, boardY + 100)
    }

    private val tiebreakerDrawable = TextureRegionDrawable(res.getTexture("tiebreaker_tag"))
    private val matchPointDrawable = TextureRegionDrawable(res.getTexture("match_point_tag"))
    private val victoryDrawable = TextureRegionDrawable(res.getTexture("victory_tag"))
    private val defeatDrawable = TextureRegionDrawable(res.getTexture("defeat_tag"))
    private var matchStateTag = Image().apply {
        setPosition(boardX + 10, boardY + 50)
        setSize(100f, 20f)
    }

    private val gameOverTimer = Timer(END_SEQUENCE_DELAY, { showPostGameResult1() })

    private val timeLabel: Label
    private val apmLabel: Label
    private val ppsLabel: Label
    private val textParticleSpawner: TextParticleSpawner

    private val enemyHpBar = HealthBar(
        movementDelay = 0.75f,
        x = 37f,
        y = stage.height - 44,
        maxValue = screen.state.enemyMaxHp.toFloat(),
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
    private var enemyChargeDelay = 0f
    private var enemyChargeBarWidth = 0f
    private var enemyChargeBarTexture = res.getTexture("yellow")

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
            screen.state.firstToText,
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
        stage.addActor(matchStateTag)

        startCountdown()

        textParticleSpawner = TextParticleSpawner(res, stage)
    }

    fun update(dt: Float) {
        playerHeaderLabel.setText(screen.state.playerText)
        enemyHeaderLabel.setText(screen.state.enemyText)

        timeLabel.setText(screen.tetris.clockTimer.formatMMSS())
        apmLabel.setText(String.format("%.2f", screen.tetris.apm))
        ppsLabel.setText(String.format("%.2f", screen.tetris.pps))

        if (startCountdown) {
            countdownTimer += dt
            if (countdownTimer >= 1f) {
                countdown--
                when (countdown) {
                    0 -> {
                        hideGameNumberLabel()
                        hideMatchStateTag()
                        updateCountdown("GO!")
                        screen.tetris.start()
                    }
                    -1 -> {
                        countdownLabel.isVisible = false
                        startCountdown = false
                    }
                    else -> updateCountdown(countdown.toString())
                }
                countdownTimer = 0f
            }
        }
        textParticleSpawner.update(dt)

        enemyHpBar.update(dt)
        enemyHpLabel.setText("${screen.state.enemyHp}/${screen.state.enemyMaxHp}")

        garbageBar.update(dt)

        enemyChargeBarWidth = if (screen.tetris.started) {
            Interpolation.linear.apply(0f, 220f, screen.state.attackTimer / enemyChargeDelay)
        } else {
            0f
        }

        gameOverTimer.update(dt)
    }

    fun render(batch: Batch) {
        batch.draw(res.getTexture("score_header"), 6f, stage.height - 24f)
        batch.draw(res.getTexture("tetris_board_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("item_slots_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("enemy_hp_bar"), 36f, stage.height - 54f)
        batch.draw(enemyChargeBarTexture, 37f, stage.height - 53f, enemyChargeBarWidth, 4f)
        batch.draw(screen.state.enemyAvatar, 6f, stage.height - 55f)

        enemyHpBar.render(batch)
        renderTetris(batch)
    }

    fun startEnemyCharge(delay: Float, action: Action) {
        enemyChargeDelay = delay
        enemyChargeBarTexture = when (action) {
            Action.Heal -> res.getTexture("bar_restore")
            else -> res.getTexture("yellow")
        }
    }

    fun spawnNumberParticle(lines: Int, x: Float, y: Float) {
        textParticleSpawner.spawn(
            lines.toString(),
            when (lines) {
                1, 2, 3 -> Color.WHITE
                4, 5, 6 -> GAME_YELLOW
                else -> GAME_LIGHT_BLUE
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
            GAME_YELLOW,
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
            Color.WHITE,
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
            GAME_ORANGE,
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

    fun healEnemyHp(heal: Int) = enemyHpBar.applyChange(heal.toFloat(), false)

    fun addGarbage(lines: Int) = garbageBar.applyChange(lines.toFloat(), false)

    fun cancelGarbage(lines: Int) = garbageBar.applyChange(lines.toFloat(), true)

    fun resetGarbage() = garbageBar.reset()

    private fun renderTetris(batch: Batch) {
        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                if (y < config.height) {
                    batch.draw(res.getBoardUnit(),
                        boardX + screen.tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + screen.tetris.content[y][x].y * SQUARE_SIZE)
                }

                if (screen.tetris.content[y][x].filled) {
                    batch.draw(res.getSquare(screen.tetris.content[y][x].square.pieceType),
                        boardX + screen.tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + screen.tetris.content[y][x].y * SQUARE_SIZE)
                }
            }
        }
        screen.tetris.currPiece?.let { currPiece ->
            currPiece.squares.forEach {
                batch.draw(res.getSquare(currPiece.pieceType),
                    boardX + it.x * SQUARE_SIZE,
                    boardY + it.y * SQUARE_SIZE)
                batch.draw(res.getGhost(currPiece.pieceType),
                    boardX + it.x * SQUARE_SIZE,
                    boardY + screen.tetris.getGhostPieceY(it) * SQUARE_SIZE)
            }
        }

        screen.tetris.holdPiece?.let { piece ->
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
        if (screen.tetris.bag.isNotEmpty()) {
            for (i in 0 until config.numPreviews) {
                val piece = screen.tetris.bag[i]
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
        screen.scene.garbageBar.render(batch)
    }

    private fun startCountdown() {
        screen.tetris.generateQueue()
        screen.state.updateGameNumber()
        screen.state.resetState()
        enemyHpBar.reset()

        resultsLabel.isVisible = false
        scoreLabel.isVisible = false
        gameNumberLabel.setText("GAME ${screen.state.gameNumber}")
        gameNumberLabel.isVisible = true
        countdown = config.startDelay
        countdownLabel.isVisible = true
        updateCountdown(countdown.toString())
        countdownTimer = 0f
        startCountdown = true
    }

    fun startGameOverSequence() {
        gameNumberLabel.isVisible = true
        gameNumberLabel.setText("GAME ${screen.state.gameNumber}")
        resultsLabel.isVisible = true
        resultsLabel.addAction(Actions.sequence(
            Actions.run { resultsLabel.setText("FINISHED!") },
            Actions.alpha(1f),
            Actions.fadeOut(END_SEQUENCE_DELAY, Interpolation.slowFast),
            Actions.run { resultsLabel.setText(if (screen.state.playerWonGame) "YOU WIN!" else "YOU LOST!") },
            Actions.alpha(1f)
        ))
        gameOverTimer.start()
    }

    private fun showPostGameResult1() {
        screen.tetris.reset()
        screen.tetris.currPiece = null
        screen.tetris.bag.clear()

        scoreLabel.isVisible = true
        scoreLabel.addAction(Actions.sequence(
            Actions.run { scoreLabel.setText("${screen.state.playerScore} - ${screen.state.enemyScore}") },
            Actions.alpha(1f),
            Actions.fadeOut(END_SEQUENCE_DELAY),
            Actions.run { showPostGameResult2() },
            Actions.fadeIn(0.5f)
        ))
    }

    private fun showPostGameResult2() {
        screen.state.updateScores()
        scoreLabel.setText("${screen.state.playerScore} - ${screen.state.enemyScore}")
        val matchState = screen.state.getMatchState()
        matchStateTag.drawable = when (matchState) {
            MatchState.Tiebreaker -> tiebreakerDrawable
            MatchState.MatchPoint -> matchPointDrawable
            MatchState.PlayerWin -> victoryDrawable
            MatchState.EnemyWin -> defeatDrawable
            else -> null
        }
        if (matchStateTag.drawable != null) {
            matchStateTag.isVisible = true
            matchStateTag.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        }

        resultsLabel.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(END_SEQUENCE_DELAY, Interpolation.slowFast)))
        scoreLabel.addAction(Actions.sequence(
            Actions.alpha(1f),
            Actions.fadeOut(END_SEQUENCE_DELAY, Interpolation.slowFast),
            Actions.run { handleMatchState() }
        ))
    }

    private fun handleMatchState() {
        val matchState = screen.state.getMatchState()
        if (matchState == MatchState.PlayerWin || matchState == MatchState.EnemyWin) {
            screen.onBattleEnd(matchState, screen.state.playerScore, screen.state.enemyScore)
        } else {
            startCountdown()
        }
    }

    private fun updateCountdown(text: String) {
        countdownLabel.setText(text)
        countdownLabel.clearActions()
        countdownLabel.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(1f), Actions.alpha(1f)))
    }

    private fun hideGameNumberLabel() {
        gameNumberLabel.addAction(Actions.sequence(
            Actions.alpha(1f),
            Actions.fadeOut(1f),
            Actions.run { gameNumberLabel.isVisible = false },
            Actions.alpha(1f)
        ))
    }

    private fun hideMatchStateTag() {
        if (matchStateTag.drawable != null) {
            matchStateTag.addAction(Actions.sequence(
                Actions.alpha(1f),
                Actions.fadeOut(1f),
                Actions.run { matchStateTag.isVisible = false },
                Actions.alpha(1f)
            ))
        }
    }
}