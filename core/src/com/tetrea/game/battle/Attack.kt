package com.tetrea.game.battle

import com.squareup.moshi.Json

data class Attack(
    @Json(name = "action") val action: Action,
    @Json(name = "time") val time: Float = 0f,
    @Json(name = "lines") val lines: Int? = null,
    @Json(name = "heal") val heal: Int? = null,
    @Json(name = "numMoves") val numMoves: Int? = null
)

enum class Action(val barColor: String, val ratingThreshold: Float) {
    @Json(name = "SendLines") SendLines("yellow", 0f),
    @Json(name = "Random") Random("yellow", 0f),
    @Json(name = "Heal") Heal("bar_restore", 0f),
    @Json(name = "Shield") Shield("yellow", 2000f),
    @Json(name = "SolidGarbage") SolidGarbage("yellow", 1400f),
    @Json(name = "Gravity") Gravity("yellow", 1000f),
    @Json(name = "Immune") Immune("yellow", 2400f),
    @Json(name = "DamageReduction") DamageReduction("yellow", 1200f)
}