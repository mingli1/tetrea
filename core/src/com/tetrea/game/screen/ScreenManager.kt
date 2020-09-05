package com.tetrea.game.screen

import com.badlogic.gdx.utils.Disposable
import com.tetrea.game.global.TetreaGame

const val HOME_SCREEN = "HOME_SCREEN"
const val BATTLE_SCREEN = "BATTLE_SCREEN"
const val RESULTS_SCREEN = "RESULTS_SCREEN"
const val VERSUS_SELECT_SCREEN = "VERSUS_SELECT_SCREEN"
const val ARCADE_SCREEN = "ARCADE_SCREEN"
const val TETRIS_SCREEN = "TETRIS_SCREEN"
const val PROFILE_SCREEN = "PROFILE_SCREEN"
const val LEVEL_SELECT_SCREEN = "LEVEL_SELECT_SCREEN"
const val SETTINGS_SCREEN = "SETTINGS_SCREEN"

class ScreenManager(private val game: TetreaGame) : Disposable {

    private val disposables = mutableListOf<BaseScreen>()

    @Throws(java.lang.IllegalArgumentException::class)
    fun getScreen(key: String): BaseScreen = when (key) {
        HOME_SCREEN -> HomeScreen(game)
        BATTLE_SCREEN -> BattleScreen(game)
        RESULTS_SCREEN -> ResultsScreen(game)
        VERSUS_SELECT_SCREEN -> VersusSelectScreen(game)
        LEVEL_SELECT_SCREEN -> LevelSelectScreen(game)
        SETTINGS_SCREEN -> SettingsScreen(game)
        PROFILE_SCREEN -> ProfileScreen(game)
        ARCADE_SCREEN -> ArcadeScreen(game)
        TETRIS_SCREEN -> TetrisScreen(game)
        else -> throw IllegalArgumentException("Screen $key not found.")
    }

    fun addLateDisposable(disposable: BaseScreen) = disposables.add(disposable)

    override fun dispose() {
        if (disposables.isEmpty()) return
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}