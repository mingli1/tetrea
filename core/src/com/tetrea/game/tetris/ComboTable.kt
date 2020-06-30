package com.tetrea.game.tetris

import com.squareup.moshi.Json

data class ComboTable(
    @Json(name = "maxAttack") val maxAttack: Int,
    @Json(name = "attackTable") val attackTable: List<Int>
)