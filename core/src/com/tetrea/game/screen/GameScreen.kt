package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.TetreaGame

class GameScreen(game: TetreaGame) : BaseScreen(game) {

    override fun update(dt: Float) {

    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(dt)
        stage.draw()
    }
}