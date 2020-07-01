package com.tetrea.game.tetris

import com.squareup.moshi.Json

data class AttackTable(
    @Json(name = "max") val max: Int,
    @Json(name = "table") val table: List<Int>
)