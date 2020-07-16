package com.tetrea.game.battle

import com.squareup.moshi.Json
import com.tetrea.game.battle.enemy.Enemy

data class BattleConfig(
    @Json(name = "worldId") val worldId: Int = 0,
    @Json(name = "levelId") val levelId: Int = 0,
    @Json(name = "bestOf") val bestOf: Int,
    @Json(name = "enemy") val enemy: Enemy,
    @Json(name = "attackPatterns") val attackPatterns: List<AttackPattern> = emptyList(),
    @Json(name = "attackScheme") val attackScheme: List<Attack>? = null,
    @Json(name = "tetrisConfig") val tetrisConfig: String = "default",
    @Json(name = "aiLevel") val aiLevel: AILevel = AILevel.None,
    val isMatchmaking: Boolean = false
) {
    val compositeKey = "$worldId-$levelId"
    fun hasPattern(pattern: AttackPattern) = attackPatterns.contains(pattern)
}