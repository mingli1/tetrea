package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.TetreaGame
import com.tetrea.game.input.BattleInputHandler
import com.tetrea.game.input.BattleKeyInput
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.TetrisConfig

class BattleScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var tetris: Tetris
    private lateinit var inputHandler: BattleInputHandler
    private lateinit var battleKeyInput: BattleKeyInput

    override fun show() {
        super.show()

        tetris = Tetris(64f, 10f, TetrisConfig(), game.res)
        inputHandler = BattleInputHandler(tetris, 0.117f, 0f, 0f)
        battleKeyInput = BattleKeyInput(inputHandler)

        Gdx.input.inputProcessor = battleKeyInput
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