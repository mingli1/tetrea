package com.tetrea.game.battle

import com.squareup.moshi.Json

data class Attack(
    @Json(name = "action") val action: Action,
    @Json(name = "time") val time: Float,
    @Json(name = "lines") val lines: Int? = null,
    @Json(name = "heal") val heal: Int? = null
)

enum class Action {
    @Json(name = "SendLines") SendLines,
    @Json(name = "Random") Random,
    @Json(name = "Heal") Heal
}