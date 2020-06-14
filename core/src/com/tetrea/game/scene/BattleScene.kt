package com.tetrea.game.scene

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.BattleState
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.res.Color
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.component.AnimatedBar
import com.tetrea.game.scene.effect.TextParticleSpawner
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig
import com.tetrea.game.tetris.util.LineClearType

class BattleScene(
    private val boardX: Float,
    private val boardY: Float,
    private val tetris: Tetris,
    private val state: BattleState,
    private val config: TetrisConfig,
    private val stage: Stage,
    private val res: Resources
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

    private val timeLabel: Label
    private val apmLabel: Label
    private val ppsLabel: Label
    private val textParticleSpawner: TextParticleSpawner

    private val enemyHpBar = AnimatedBar(
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
    }

    fun render(batch: Batch) {
        batch.draw(res.getTexture("score_header"), 6f, stage.height - 24f)
        batch.draw(res.getTexture("tetris_board_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("item_slots_bg"), boardX - 66, boardY - 1)
        batch.draw(res.getTexture("enemy_hp_bar"), 36f, stage.height - 54f)

        enemyHpBar.render(batch)
    }

    fun startCountdown() {
        tetris.generateQueue()

        countdown = config.startDelay
        countdownLabel.isVisible = true
        countdownLabel.setText(countdown)
        countdownTimer = 0f
        startCountdown = true
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
        textParticleSpawner.spawn(
            type.desc,
            type.color,
            boardX + (config.width / 2) * SQUARE_SIZE,
            boardY + (config.height + 3) * SQUARE_SIZE,
            zi = 0f,
            vxScale = 0f,
            vyScale = 0.4f,
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
}