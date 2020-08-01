package com.tetrea.game.battle.skill

import com.squareup.moshi.Json

data class Skill(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "desc") val desc: String,
    @Json(name = "rating") val rating: Float
)