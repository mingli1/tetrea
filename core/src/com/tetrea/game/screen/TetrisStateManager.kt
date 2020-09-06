package com.tetrea.game.screen

import com.badlogic.gdx.graphics.Color
import com.tetrea.game.tetris.util.LineClearType

interface TetrisStateManager {

    fun addGarbage(numLines: Int)

    fun spawnComboParticle(combo: Int)

    fun spawnB2bParticle(b2b: Int)

    fun spawnCenterParticle(text: String, color: Color)

    fun spawnSpikeParticle(spike: Int)

    fun spawnLineClearParticle(type: LineClearType)

    fun spawnNumberParticle(
        lines: Int,
        x: Float,
        y: Float,
        crit: Boolean
    )

    fun attackEnemy(attack: Int): Boolean

    fun resetGarbage()

    fun setPlayerWonGame(win: Boolean)

    fun onGameOver()

    fun cancelGarbage(lines: Int)
}