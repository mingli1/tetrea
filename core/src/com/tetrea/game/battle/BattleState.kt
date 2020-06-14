package com.tetrea.game.battle

class BattleState(private val config: BattleConfig) {

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    private var playerScore = 0
    private var enemyScore = 0

    fun update(dt: Float) {
        playerText = "${config.player} $playerScore"
        enemyText = "$enemyScore ${config.enemy}"
    }
}