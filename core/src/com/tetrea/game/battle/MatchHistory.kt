package com.tetrea.game.battle

import com.squareup.moshi.Json

data class MatchHistory(
    @Json(name = "playerRating") val playerRating: Int,
    @Json(name = "enemyRating") val enemyRating: Int,
    @Json(name = "enemyName") val enemyName: String,
    @Json(name = "playerScore") val playerScore: Int,
    @Json(name = "enemyScore") val enemyScore: Int
)