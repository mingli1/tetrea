package com.tetrea.game.battle

import com.squareup.moshi.Json
import com.tetrea.game.battle.enemy.Enemy

data class BattleConfig(
    @Json(name = "worldId") val worldId: Int = 0,
    @Json(name = "levelId") val levelId: Int = 0,
    @Json(name = "firstTo") val firstTo: Int,
    @Json(name = "enemy") val enemy: Enemy,
    @Json(name = "attackPatterns") val attackPatterns: List<AttackPattern> = emptyList(),
    @Json(name = "attackScheme") val attackScheme: List<Attack>? = null,
    @Json(name = "tetrisConfig") val tetrisConfig: String = "default"
) {
    val compositeKey = "$worldId-$levelId"
    fun hasPattern(pattern: AttackPattern) = attackPatterns.contains(pattern)
}