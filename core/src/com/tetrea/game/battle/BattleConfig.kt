package com.tetrea.game.battle

import com.squareup.moshi.Json

data class BattleConfig(
    @Json(name = "firstTo") val firstTo: Int,
    @Json(name = "enemy") val enemy: Enemy,
    @Json(name = "attackPatterns") val attackPatterns: List<AttackPattern> = emptyList(),
    @Json(name = "attackScheme") val attackScheme: List<Attack>? = null
) {

    fun hasPattern(pattern: AttackPattern) = attackPatterns.contains(pattern)
}