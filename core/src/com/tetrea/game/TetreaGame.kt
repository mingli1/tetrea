package com.tetrea.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tetrea.game.res.Resources
import com.tetrea.game.screen.BaseScreen
import com.tetrea.game.screen.GameScreen
import kotlin.math.min

class TetreaGame : Game() {

    lateinit var batch: Batch
    lateinit var res: Resources

    lateinit var fpsLabel: Label

    val gameScreen: GameScreen by lazy { GameScreen(this) }
    private lateinit var currentScreen: BaseScreen

    override fun create() {
        batch = SpriteBatch()
        res = Resources()

        if (IS_DEBUG) {
            fpsLabel = res.getDefaultLabel().apply { setPosition(5f, 5f) }
        }

        updateScreen(gameScreen)
    }

    fun updateScreen(screen: BaseScreen) {
        setScreen(screen)
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
    }
}