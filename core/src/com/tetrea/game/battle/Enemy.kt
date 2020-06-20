package com.tetrea.game.battle

import com.squareup.moshi.Json

data class Enemy(
    @Json(name = "name") val name: String,
    @Json(name = "maxHp") val maxHp: Int,
    @Json(name = "attack") val attack: Int,
    @Json(name = "defense") val defense: Int,
    @Json(name = "speed") val speed: Int
)