package com.tetrea.game.battle

class BattleState(private val config: BattleConfig) {

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    var enemyHp = config.enemy.maxHp
    val enemyMaxHp = config.enemy.maxHp

    private var playerScore = 0
    private var enemyScore = 0

    fun attackEnemy(attack: Int) {

    }

    fun update(dt: Float) {
        playerText = "YOU $playerScore"
        enemyText = "$enemyScore ${config.enemy.name}"
    }
}