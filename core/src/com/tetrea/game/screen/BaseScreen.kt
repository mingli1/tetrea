package com.tetrea.game.screen

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrea.game.global.IS_DEBUG
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.global.V_HEIGHT
import com.tetrea.game.global.V_WIDTH
import com.tetrea.game.input.MultiTouchDisabler

const val FADE_DURATION = 0.2f

abstract class BaseScreen(protected val game: TetreaGame) : Screen, Disposable {

    var arguments: Map<String, Any>? = null
    protected val stage: Stage
    private val viewport: Viewport
    protected val cam: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    }
    protected var gameState = GameState.Resume

    private var shouldFade = true
    protected var transition = Transition.None

    protected val multiplexer: InputMultiplexer

    init {
        viewport = ExtendViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat(), cam)
        stage = Stage(viewport, game.batch)
        multiplexer = InputMultiplexer().apply {
            addProcessor(MultiTouchDisabler())
            addProcessor(stage)
        }
    }

    fun navigateTo(key: String, arguments: Map<String, Any>? = null, shouldFade: Boolean = true) {
        val screen = game.screenManager.getScreen(key)
        screen.arguments = arguments
        screen.shouldFade = shouldFade

        if (shouldFade) {
            transition = Transition.FadeOut
            stage.addAction(Actions.sequence(
                Actions.alpha(1f),
                Actions.fadeOut(FADE_DURATION),
                Actions.run {
                    game.updateScreen(screen)
                    transition = Transition.AfterFadeOut
                }
            ))
        } else {
            game.updateScreen(screen)
        }
    }

    fun isPaused() = gameState == GameState.Pause

    override fun show() {
        if (IS_DEBUG) stage.addActor(game.fpsLabel)

        if (shouldFade) {
            transition = Transition.FadeIn
            stage.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(FADE_DURATION),
                Actions.run { transition = Transition.None }
            ))
        }
    }

    open fun update(dt: Float) {}

    override fun render(dt: Float) {
        update(dt)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        stage.dispose()
    }

    open fun notifyResume() {
        gameState = GameState.Resume
    }

    open fun notifyPause() {
        gameState = GameState.Pause
    }

    enum class GameState {
        Resume,
        Pause
    }

    enum class Transition {
        FadeIn,
        FadeOut,
        AfterFadeOut,
        None
    }
}