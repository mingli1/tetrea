package com.tetrea.game.battle.attack

import com.squareup.moshi.Json

data class Attack(
    @Json(name = "action") val action: Action,
    @Json(name = "time") val time: Float = 0f,
    @Json(name = "lines") val lines: Int? = null,
    @Json(name = "heal") val heal: Int? = null,
    @Json(name = "numMoves") val numMoves: Int? = null
)

enum class Action(
    val barColor: String,
    val ratingThreshold: Float,
    val text: String = ""
) {
    @Json(name = "SendLines") SendLines("yellow", 0f),
    @Json(name = "Random") Random("yellow", 0f),
    @Json(name = "Heal") Heal("bar_restore", 0f),
    @Json(name = "SolidGarbage") SolidGarbage("solid_garbage_bar", 1200f, "SOLID\nGARBAGE"),
    @Json(name = "Gravity") Gravity("gravity_bar", 1000f, "GRAVITY\nINCREASED"),
    @Json(name = "Immune") Immune("immune_bar", 2000f, "ENEMY\nIMMUNE"),
    @Json(name = "DamageReduction") DamageReduction("damage_reduction_bar", 1500f, "ATTACK\nREDUCED")
}