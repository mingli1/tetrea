package com.tetrea.game.battle

data class BattleConfig(
    val firstTo: Int,
    val player: String = "YOU", // TODO: replace with Player object
    val enemy: String = "ENEMY" // TODO: replace with Enemy object
)