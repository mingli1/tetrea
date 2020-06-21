package com.tetrea.game.battle

import com.squareup.moshi.Json

enum class AttackPattern {
    @Json(name = "Cheeser") Cheeser,
    @Json(name = "Spiker") Spiker,
    @Json(name = "Defensive") Defensive
}