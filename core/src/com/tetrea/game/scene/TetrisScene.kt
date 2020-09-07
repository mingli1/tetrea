package com.tetrea.game.scene

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.formatComma
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.res.SoundManager
import com.tetrea.game.scene.dialog.PauseDialog
import com.tetrea.game.screen.GameMode
import com.tetrea.game.screen.TetrisScreen
import com.tetrea.game.tetris.TetrisConfig
import com.tetrea.game.tetris.util.PieceType

class TetrisScene(
    private val boardX: Float,
    private val boardY: Float,
    private val gameMode: GameMode,
    private val config: TetrisConfig,
    private val stage: Stage,
    private val res: Resources,
    private val soundManager: SoundManager,
    private val screen: TetrisScreen
) {

    private val countdownLabel = res.getLabel(color = GAME_YELLOW, fontScale = 2f).apply {
        setSize(config.width * SQUARE_SIZE.toFloat(), config.height * SQUARE_SIZE.toFloat())
        setAlignment(Align.center)
        setPosition(boardX, boardY)
    }
    private var countdown = 0
    private var countdownTimer = 0f
    private var startCountdown = false

    private val timeLabel = res.getLabel(
        x = boardX + (config.width * SQUARE_SIZE) + 8f,
        y = boardY + 8f,
        fontScale = 1f
    )
    private val statsLabel1: Label
    private val statsLabel2: Label
    private var statsLabel3: Label? = null

    private val statsX = boardX - 62f
    private val statsY1 = boardY + 168f
    private val statsY2 = boardY + 134f
    private val statsY3 = boardY + 100f

    private val primaryDescLabel = res.getLabel(
        text = when (gameMode) {
            GameMode.Sprint, GameMode.Cheese -> "LINES LEFT"
            GameMode.Ultra -> "SCORE"
            else -> ""
        }
    ).apply {
        setSize(this@TetrisScene.stage.width, 20f)
        setPosition(0f, boardY + (config.height + 3) * SQUARE_SIZE + 30f)
        setAlignment(Align.center)
    }
    private val primaryLabel = res.getLabel(fontScale = 2f).apply {
        setSize(this@TetrisScene.stage.width, 30f)
        setPosition(0f, boardY + (config.height + 3) * SQUARE_SIZE)
        setAlignment(Align.center)
    }

    private val pauseDialog = PauseDialog(
        res,
        soundManager,
        screen,
        windowStyleKey = "green_bg",
        buttonStyleKey = "green_button",
        gameMode = gameMode
    )

    init {
        stage.addActor(res.getLabel(
            when (gameMode) {
                GameMode.Sprint -> "40L SPRINT"
                GameMode.Ultra -> "2 MIN ULTRA"
                GameMode.Cheese -> "100L CHEESE RACE"
                else -> ""
            },
            x = 3f,
            y = stage.height - 23f,
            fontScale = 1f
        ).apply {
            setSize(250f, 16f)
            setAlignment(Align.center)
        })
        stage.addActor(countdownLabel)

        stage.addActor(res.getLabel(
            "TIME",
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 12f,
            fontScale = 0.5f
        ))
        stage.addActor(timeLabel)

        stage.addActor(primaryDescLabel)
        stage.addActor(primaryLabel)

        stage.addActor(res.getLabel(
            when (gameMode) {
                GameMode.Sprint, GameMode.Cheese -> "PPS"
                else -> "APM"
            },
            x = statsX,
            y = statsY1,
            fontScale = 0.5f
        ))
        statsLabel1 = res.getLabel(
            x = statsX,
            y = statsY1 - 4,
            fontScale = 1f
        )
        stage.addActor(statsLabel1)

        stage.addActor(res.getLabel(
            when (gameMode) {
                GameMode.Sprint -> "INPUTS/PIECE"
                GameMode.Ultra -> "POINTS/PIECE"
                else -> "# PIECES"
            },
            x = statsX,
            y = statsY2,
            fontScale = 0.5f
        ))
        statsLabel2 = res.getLabel(
            x = statsX,
            y = statsY2 - 4,
            fontScale = 1f
        )
        stage.addActor(statsLabel2)

        if (gameMode != GameMode.Cheese) {
            stage.addActor(res.getLabel(
                when (gameMode) {
                    GameMode.Sprint -> "# PIECE"
                    GameMode.Ultra -> "PPS"
                    else -> ""
                },
                x = statsX,
                y = statsY3,
                fontScale = 0.5f
            ))
            statsLabel3 = res.getLabel(
                x = statsX,
                y = statsY3 - 4,
                fontScale = 1f
            )
            stage.addActor(statsLabel3)
        }
    }

    fun update(dt: Float) {
        timeLabel.setText(if (gameMode == GameMode.Ultra)
            screen.tetris.ultraTimer.formatMMSS()
        else
            screen.tetris.clockTimer.formatMMSS()
        )

        primaryLabel.setText(when (gameMode) {
            GameMode.Sprint -> screen.tetris.sprintLines.toString()
            GameMode.Ultra -> screen.tetris.score.formatComma()
            else -> ""
        })

        statsLabel1.setText(when (gameMode) {
            GameMode.Sprint, GameMode.Cheese -> String.format("%.2f", screen.tetris.pps)
            else -> String.format("%.2f", screen.tetris.apm)
        })
        statsLabel2.setText(when (gameMode) {
            GameMode.Sprint -> String.format("%.2f", screen.tetris.inputsPerPiece)
            GameMode.Ultra -> String.format("%.1f", screen.tetris.pointsPerBlock)
            else -> screen.tetris.piecesPlaced.toString()
        })
        statsLabel3?.setText(when (gameMode) {
            GameMode.Sprint -> screen.tetris.piecesPlaced.toString()
            GameMode.Ultra -> String.format("%.2f", screen.tetris.pps)
            else -> ""
        })

        if (startCountdown) {
            countdownTimer += dt
            if (countdownTimer >= 1f) {
                countdown--
                when (countdown) {
                    0 -> {
                        updateCountdown("GO!")
                        soundManager.onGo()
                        screen.tetris.start()
                    }
                    -1 -> {
                        countdownLabel.isVisible = false
                        startCountdown = false
                    }
                    else -> {
                        soundManager.onCountdown()
                        updateCountdown(countdown.toString())
                    }
                }
                countdownTimer = 0f
            }
        }
    }

    fun render(batch: Batch) {
        batch.draw(res.getTexture("black_75_opacity"), 0f, 0f, stage.width, stage.height)
        batch.draw(res.getTexture("arcade_header"), 6f, stage.height - 24f)
        batch.draw(res.getTexture("tetris_board_bg"), boardX - 66, boardY - 1)

        renderTetris(batch)
    }

    fun showPauseDialog() {
        if (!stage.actors.contains(pauseDialog)) {
            pauseDialog.show(stage)
        }
    }

    fun startCountdown() {
        screen.tetris.generateQueue()
        screen.tetris.resetVisibleStats()

        countdown = config.startDelay
        countdownLabel.isVisible = true
        updateCountdown(countdown.toString())
        countdownTimer = 0f
        startCountdown = true

        soundManager.onCountdown()
    }

    fun onRestart() {
        screen.tetris.reset()
        screen.tetris.currPiece = null
        screen.tetris.bag.clear()

        startCountdown()
    }

    private fun renderTetris(batch: Batch) {
        for (y in 0 until config.height * 2) {
            for (x in 0 until config.width) {
                if (y < config.height) {
                    batch.draw(res.getBoardUnit(),
                        boardX + screen.tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + screen.tetris.content[y][x].y * SQUARE_SIZE)
                }

                if (screen.tetris.content[y][x].filled && !screen.isPaused()) {
                    batch.draw(res.getSquare(screen.tetris.content[y][x].square.pieceType),
                        boardX + screen.tetris.content[y][x].x * SQUARE_SIZE,
                        boardY + screen.tetris.content[y][x].y * SQUARE_SIZE)
                }
            }
        }

        if (screen.isPaused()) return

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
    }

    private fun updateCountdown(text: String) {
        countdownLabel.setText(text)
        countdownLabel.clearActions()
        countdownLabel.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(1f), Actions.alpha(1f)))
    }
}