package com.tetrea.game.scene

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.res.Color
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig

class BattleScene(
    private val boardX: Float,
    private val boardY: Float,
    private val tetris: Tetris,
    private val config: TetrisConfig,
    stage: Stage,
    res: Resources
) {

    private val countdownLabel: Label = res.getLabel(color = Color.GAME_YELLOW, fontScale = 2f).apply {
        setSize(config.width * SQUARE_SIZE.toFloat(), config.height * SQUARE_SIZE.toFloat())
        setAlignment(Align.center)
        setPosition(boardX, boardY)
    }
    private var countdown = 0
    private var countdownTimer = 0f
    private var startCountdown = false

    private val timeLabel: Label
    private val apmLabel: Label
    private val ppsLabel: Label

    init {
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

        startCountdown()
    }

    fun update(dt: Float) {
        timeLabel.setText(tetris.stats.time.formatMMSS())
        apmLabel.setText(String.format("%.1f", tetris.stats.apm))
        ppsLabel.setText(String.format("%.2f", tetris.stats.pps))

        if (startCountdown) {
            countdownTimer += dt
            if (countdownTimer >= 1f) {
                countdown--
                when (countdown) {
                    0 -> {
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
    }

    fun startCountdown() {
        tetris.generateQueue()

        countdown = config.startDelay
        countdownLabel.isVisible = true
        countdownLabel.setText(countdown)
        countdownTimer = 0f
        startCountdown = true
    }
}