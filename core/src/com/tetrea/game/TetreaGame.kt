package com.tetrea.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tetrea.game.res.Resources
import com.tetrea.game.screen.BATTLE_SCREEN
import com.tetrea.game.screen.BaseScreen
import com.tetrea.game.screen.RESULTS_SCREEN
import com.tetrea.game.screen.ScreenFactory
import kotlin.math.min

class TetreaGame : Game() {

    lateinit var batch: Batch
    lateinit var res: Resources
    lateinit var screenFactory: ScreenFactory

    lateinit var fpsLabel: Label

    private var currentScreen: BaseScreen? = null

    override fun create() {
        batch = SpriteBatch()
        res = Resources()
        screenFactory = ScreenFactory(this)

        if (IS_DEBUG) {
            fpsLabel = res.getLabel().apply { setPosition(5f, 5f) }
        }

        updateScreen(screenFactory.getScreen(RESULTS_SCREEN))
    }

    fun updateScreen(screen: BaseScreen) {
        setScreen(screen)
        currentScreen?.dispose()
        currentScreen = screen
    }

    override fun render() {
        screen.render(min(DELTA_TIME_BOUND, Gdx.graphics.deltaTime))
        if (IS_DEBUG) {
            fpsLabel.setText("${Gdx.graphics.framesPerSecond} fps")
        }
    }

    override fun dispose() {
        batch.dispose()
        res.dispose()
        currentScreen?.dispose()
    }
}