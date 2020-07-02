package com.tetrea.game.battle

import com.tetrea.game.util.Int2

data class BattleRecord(
    var defeated: Boolean = false,
    var attempts: Int = 0,
    var bestScore: Int2? = null,
    val allTimeRecord: Int2 = Int2()
)