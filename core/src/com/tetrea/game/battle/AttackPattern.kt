package com.tetrea.game.battle

import com.squareup.moshi.Json

enum class AttackPattern(val text: String) {
    @Json(name = "Cheeser") Cheeser("CHEESER"),
    @Json(name = "Spiker") Spiker("SPIKER"),
    @Json(name = "Defensive") Defensive("DEFENSIVE")
}