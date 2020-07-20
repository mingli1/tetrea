package com.tetrea.game.battle.attack

import com.squareup.moshi.Json

enum class AttackPattern(val text: String) {
    @Json(name = "Cheeser") Cheeser("CHEESER"),
    @Json(name = "Spiker") Spiker("SPIKER"),
    @Json(name = "Defensive") Defensive("DEFENSIVE"),
    @Json(name = "FourWider") FourWider("FOUR WIDER")
}