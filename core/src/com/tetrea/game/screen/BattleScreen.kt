package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.TetreaGame
import com.tetrea.game.input.TetrisAndroidInput
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.isAndroid
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.BattleScene
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig

class BattleScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var tetris: Tetris
    private lateinit var scene: BattleScene

    private val multiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput
    private var androidInput: TetrisAndroidInput? = null

    override fun show() {
        super.show()

        val config = TetrisConfig()
        val boardX = stage.width / 2 - (config.width * SQUARE_SIZE) / 2f + 3
        val boardY = (stage.height / 2 - (config.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 0f else 32f

        tetris = Tetris(boardX, boardY, TetrisConfig(), game.res)
        inputHandler = TetrisInputHandler(tetris, 0.117f, 0f, 0f)
        tetrisKeyInput = TetrisKeyInput(inputHandler)
        scene = BattleScene(boardX, boardY, tetris, config, stage, game.res)
        tetris.scene = scene

        if (isAndroid()) androidInput = TetrisAndroidInput(stage, inputHandler, game.res)

        multiplexer.clear()
        multiplexer.addProcessor(stage)
        if (!isAndroid()) multiplexer.addProcessor(tetrisKeyInput)

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        if (tetris.started) tetris.update(dt)
        inputHandler.update(dt)
        scene.update(dt)
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        game.batch.draw(game.res.getTexture("battle_bg_sky"), 0f, 0f)
        tetris.render(game.batch)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }
}