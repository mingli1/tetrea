package com.tetrea.game.battle

import com.tetrea.game.scene.BattleScene

class BattleState(private val config: BattleConfig) {

    lateinit var scene: BattleScene

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    var enemyHp = config.enemy.maxHp
    val enemyMaxHp = config.enemy.maxHp

    private var playerScore = 0
    private var enemyScore = 0

    fun attackEnemy(attack: Int) {
        enemyHp -= attack
        if (enemyHp < 0) enemyHp = 0
        scene.attackEnemyHp(attack)
    }

    fun update(dt: Float) {
        playerText = "YOU $playerScore"
        enemyText = "$enemyScore ${config.enemy.name}"
    }
}