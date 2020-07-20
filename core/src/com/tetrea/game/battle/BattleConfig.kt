package com.tetrea.game.battle

import com.squareup.moshi.Json
import com.tetrea.game.battle.attack.Action
import com.tetrea.game.battle.attack.Attack
import com.tetrea.game.battle.attack.AttackPattern
import com.tetrea.game.battle.enemy.AILevel
import com.tetrea.game.battle.enemy.Enemy

data class BattleConfig(
    @Json(name = "worldId") val worldId: Int = 0,
    @Json(name = "levelId") val levelId: Int = 0,
    @Json(name = "bestOf") val bestOf: Int,
    @Json(name = "enemy") val enemy: Enemy,
    @Json(name = "attackPatterns") val attackPatterns: List<AttackPattern> = emptyList(),
    @Json(name = "opening") val opening: List<Attack>? = null,
    @Json(name = "attackScheme") val attackScheme: List<Attack>? = null,
    @Json(name = "tetrisConfig") val tetrisConfig: String = "default",
    @Json(name = "aiLevel") val aiLevel: AILevel = AILevel.None,
    @Json(name = "abilities") val abilities: List<Action> = emptyList(),
    val isMatchmaking: Boolean = false
) {
    val compositeKey = "$worldId-$levelId"
    fun hasPattern(pattern: AttackPattern) = attackPatterns.contains(pattern)
}