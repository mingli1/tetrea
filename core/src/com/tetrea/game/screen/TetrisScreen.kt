package com.tetrea.game.screen

import com.badlogic.gdx.InputMultiplexer
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.tetris.Tetris

const val ARG_GAME_MODE = "ARG_GAME_MODE"

class TetrisScreen(game: TetreaGame) : BaseScreen(game) {

    lateinit var tetris: Tetris

    private val inputMultiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput

    override fun show() {
        super.show()

        game.musicManager.startBattleMusic()

        val gameMode = arguments?.get(ARG_GAME_MODE) as? GameMode ?: GameMode.Sprint


    }
}