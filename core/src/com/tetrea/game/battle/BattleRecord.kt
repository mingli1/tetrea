package com.tetrea.game.battle

import com.squareup.moshi.Json
import com.tetrea.game.util.Int2

data class BattleRecord(
    @Json(name = "defeated") var defeated: Boolean = false,
    @Json(name = "attempts") var attempts: Int = 0,
    @Json(name = "bestScore") var bestScore: Int2? = null,
    @Json(name = "allTimeRecord") val allTimeRecord: Int2 = Int2()
)