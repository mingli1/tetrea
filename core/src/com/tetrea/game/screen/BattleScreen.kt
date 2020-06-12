package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
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

        addLabels()
    }

    override fun update(dt: Float) {
        tetris.update(dt)
        inputHandler.update(dt)

        timeLabel.setText(tetris.stats.time.formatMMSS())
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

    private fun addLabels() {
        stage.addActor(game.res.getLabel(
            "TIME",
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 12f
        ))
        timeLabel = game.res.getLabel(
            "",
            x = boardX + (config.width * SQUARE_SIZE) + 8f,
            y = boardY + 6f,
            fontScale = 1f
        )
        stage.addActor(timeLabel)
    }
}