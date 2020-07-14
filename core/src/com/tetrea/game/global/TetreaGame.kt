package com.tetrea.game.global

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SaveManager
import com.tetrea.game.screen.*
import kotlin.math.min

class TetreaGame : Game() {

    lateinit var batch: Batch
    lateinit var res: Resources
    lateinit var saveManager: SaveManager
    lateinit var screenManager: ScreenManager
    lateinit var player: Player
    lateinit var settings: Settings

    lateinit var fpsLabel: Label

    private var currentScreen: BaseScreen? = null

    override fun create() {
        batch = SpriteBatch()
        res = Resources()
        saveManager = SaveManager(res)
        screenManager = ScreenManager(this)

        player = saveManager.saveData.player
        settings = saveManager.saveData.settings

        if (IS_DEBUG) {
            fpsLabel = res.getLabel().apply { setPosition(5f, 5f) }
        }

        updateScreen(screenManager.getScreen(HOME_SCREEN))
    }

    fun updateScreen(screen: BaseScreen) {
        setScreen(screen)
        screenManager.dispose()
        if (currentScreen is LateDisposable) {
            currentScreen?.let { screenManager.addLateDisposable(it) }
        } else {
            currentScreen?.dispose()
        }
        currentScreen = screen
    }


    override fun pause() {
        currentScreen?.notifyPause()
    }

    override fun render() {
        screen.render(min(DELTA_TIME_BOUND, Gdx.graphics.deltaTime))
        if (IS_DEBUG) {
            fpsLabel.setText("${Gdx.graphics.framesPerSecond} fps")
        }
    }

    override fun dispose() {
        if (currentScreen is BattleScreen) {
            val screen = currentScreen as BattleScreen
            if (!screen.isMatchFinished) {
                player.quitDuringBattle = true
            }
            screen.finishBattlePrematurely()
        }

        batch.dispose()
        res.dispose()
        screenManager.dispose()
        currentScreen?.dispose()
    }
}