package com.tetrea.game.screen

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.global.isAndroid
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.LineClearType

const val ARG_GAME_MODE = "ARG_GAME_MODE"

class TetrisScreen(game: TetreaGame) : BaseScreen(game), TetrisStateManager {

    lateinit var tetris: Tetris

    private val inputMultiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput

    override fun show() {
        super.show()

        game.musicManager.startBattleMusic()

        val gameMode = arguments?.get(ARG_GAME_MODE) as? GameMode ?: GameMode.Sprint
        val tetrisConfig = game.res.getTetrisConfig("arcade")
        val boardX = stage.width / 2 - (tetrisConfig.width * SQUARE_SIZE) / 2f + 3
        val boardY = (stage.height / 2 - (tetrisConfig.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 16f else 32f

        tetris = Tetris(boardX, boardY, tetrisConfig, this, game.soundManager)
    }

    override fun addGarbage(numLines: Int) {

    }

    override fun spawnComboParticle(combo: Int) {

    }

    override fun spawnB2bParticle(b2b: Int) {

    }

    override fun spawnCenterParticle(text: String, color: Color) {

    }

    override fun spawnSpikeParticle(spike: Int) {

    }

    override fun spawnLineClearParticle(type: LineClearType) {

    }

    override fun spawnNumberParticle(lines: Int, x: Float, y: Float, crit: Boolean) {

    }

    override fun attackEnemy(attack: Int): Boolean {
        return false
    }

    override fun resetGarbage() {

    }

    override fun setPlayerWonGame(win: Boolean) {

    }

    override fun startGameOverSequence() {

    }

    override fun cancelGarbage(lines: Int) {

    }
}