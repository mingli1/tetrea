package com.tetrea.game.screen

import com.tetrea.game.TetreaGame

const val BATTLE_SCREEN = "BATTLE_SCREEN"
const val RESULTS_SCREEN = "RESULTS_SCREEN"

class ScreenFactory(private val game: TetreaGame) {

    @Throws(java.lang.IllegalArgumentException::class)
    fun getScreen(key: String): BaseScreen = when (key) {
        BATTLE_SCREEN -> BattleScreen(game)
        RESULTS_SCREEN -> ResultsScreen(game)
        else -> throw IllegalArgumentException("Screen $key not found.")
    }
}