package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tetrea.game.TetreaGame
import com.tetrea.game.extension.formatMMSS
import com.tetrea.game.input.TetrisAndroidInput
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.isAndroid
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig

class BattleScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var tetris: Tetris

    private val multiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput
    private var androidInput: TetrisAndroidInput? = null

    private val config = TetrisConfig()
    private var boardX = 0f
    private var boardY = 0f

    private lateinit var timeLabel: Label
    private lateinit var apmLabel: Label
    private lateinit var ppsLabel: Label

    override fun show() {
        super.show()

        boardX = stage.width / 2 - (config.width * SQUARE_SIZE) / 2f + 3
        boardY = (stage.height / 2 - (config.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 0f else 32f

        tetris = Tetris(boardX, boardY, TetrisConfig(), game.res)
        inputHandler = TetrisInputHandler(tetris, 0.117f, 0f, 0f)
        tetrisKeyInput = TetrisKeyInput(inputHandler)

        if (isAndroid()) androidInput = TetrisAndroidInput(stage, inputHandler, game.res)

        multiplexer.clear()
        multiplexer.addProcessor(stage)
        if (!isAndroid()) multiplexer.addProcessor(tetrisKeyInput)

        Gdx.input.inputProcessor = multiplexer

        addStatActors()
    }

    override fun update(dt: Float) {
        tetris.update(dt)
        inputHandler.update(dt)

        timeLabel.setText(tetris.stats.time.formatMMSS())
        apmLabel.setText(String.format("%.1f", tetris.stats.apm + 245.6))
        ppsLabel.setText(String.format("%.2f", tetris.stats.pps))
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(40 / 255f, 40 / 255f, 40 / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        tetris.render(game.batch)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun addStatActors() {
        stage.addActor(game.res.getLabel(
            "TIME",
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 12f,
            fontScale = 0.5f
        ))
        timeLabel = game.res.getLabel(
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 8f,
            fontScale = 1f
        )
        stage.addActor(timeLabel)

        stage.addActor(Image(game.res.getTexture("apm_icon")).apply {
            x = boardX - 6f
            y = boardY - 26f
        })
        stage.addActor(game.res.getLabel(
            "APM",
            x = boardX + 16f,
            y = boardY - 18f,
            fontScale = 0.5f
        ))
        apmLabel = game.res.getLabel(
            x = boardX + 16f,
            y = boardY - 22f,
            fontScale = 1f
        )
        stage.addActor(apmLabel)

        stage.addActor(Image(game.res.getTexture("pps_icon")).apply {
            x = boardX + 72f
            y = boardY - 26f
        })
        stage.addActor(game.res.getLabel(
            "PPS",
            x = boardX + 90f,
            y = boardY - 18f,
            fontScale = 0.5f
        ))
        ppsLabel = game.res.getLabel(
            x = boardX + 90f,
            y = boardY - 22f,
            fontScale = 1f
        )
        stage.addActor(ppsLabel)
    }
}