package com.tetrea.game.battle

import com.squareup.moshi.Json

data class Attack(
    @Json(name = "action") val action: Action,
    @Json(name = "time") val time: Float = 0f,
    @Json(name = "lines") val lines: Int? = null,
    @Json(name = "heal") val heal: Int? = null,
    @Json(name = "numMoves") val numMoves: Int? = null
)

enum class Action {
    @Json(name = "SendLines") SendLines,
    @Json(name = "Random") Random,
    @Json(name = "Heal") Heal
}