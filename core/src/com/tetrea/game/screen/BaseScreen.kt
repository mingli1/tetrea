package com.tetrea.game.screen

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrea.game.IS_DEBUG
import com.tetrea.game.TetreaGame
import com.tetrea.game.V_HEIGHT
import com.tetrea.game.V_WIDTH

abstract class BaseScreen(protected val game: TetreaGame) : Screen, Disposable {

    protected val stage: Stage
    protected val viewport: Viewport
    protected val cam: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    }

    init {
        viewport = ExtendViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat(), cam)
        stage = Stage(viewport, game.batch)
    }

    override fun show() {
        if (IS_DEBUG) stage.addActor(game.fpsLabel)
    }

    abstract fun update(dt: Float)

    override fun render(dt: Float) {
        update(dt)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height)

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        stage.dispose()
    }
}