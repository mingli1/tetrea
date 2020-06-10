package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.TetreaGame
import com.tetrea.game.V_HEIGHT
import com.tetrea.game.V_WIDTH
import com.tetrea.game.input.TetrisAndroidInput
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig

class BattleScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var tetris: Tetris

    private val multiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput
    private var androidInput: TetrisAndroidInput? = null

    override fun show() {
        super.show()

        tetris = Tetris(V_WIDTH / 2 - (10 * 12) / 2f, V_HEIGHT / 2 - (10 * 20) / 2f, TetrisConfig(), game.res)
        inputHandler = TetrisInputHandler(tetris, 0.117f, 0f, 0f)
        tetrisKeyInput = TetrisKeyInput(inputHandler)

        androidInput = TetrisAndroidInput(stage, inputHandler, game.res)

        multiplexer.clear()
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(tetrisKeyInput)

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        tetris.update(dt)
        inputHandler.update(dt)
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
}