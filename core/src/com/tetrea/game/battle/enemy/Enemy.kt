package com.tetrea.game.battle.enemy

import com.squareup.moshi.Json

data class Enemy(
    @Json(name = "name") val name: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "maxHp") val maxHp: Int,
    @Json(name = "attack") val attack: Int,
    @Json(name = "defense") val defense: Int,
    @Json(name = "speed") val speed: Int,
    var rating: Float = 0f
)