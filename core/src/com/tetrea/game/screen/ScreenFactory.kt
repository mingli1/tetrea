package com.tetrea.game.screen

import com.tetrea.game.global.TetreaGame

const val BATTLE_SCREEN = "BATTLE_SCREEN"
const val RESULTS_SCREEN = "RESULTS_SCREEN"
const val LEVEL_SELECT_SCREEN = "LEVEL_SELECT_SCREEN"

class ScreenFactory(private val game: TetreaGame) {

    @Throws(java.lang.IllegalArgumentException::class)
    fun getScreen(key: String): BaseScreen = when (key) {
        BATTLE_SCREEN -> BattleScreen(game)
        RESULTS_SCREEN -> ResultsScreen(game)
        LEVEL_SELECT_SCREEN -> LevelSelectScreen(game)
        else -> throw IllegalArgumentException("Screen $key not found.")
    }
}