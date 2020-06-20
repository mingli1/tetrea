package com.tetrea.game.battle

import com.tetrea.game.screen.BattleScreen

class BattleState(private val config: BattleConfig, private val screen: BattleScreen) {

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    var enemyHp = config.enemy.maxHp
    val enemyMaxHp = config.enemy.maxHp

    var playerScore = 0
    var enemyScore = 0
    var gameNumber = 0
    var playerWonGame = false

    fun update(dt: Float) {
        playerText = "YOU $playerScore"
        enemyText = "$enemyScore ${config.enemy.name}"
    }

    fun attackEnemy(attack: Int): Boolean {
        enemyHp -= attack
        screen.scene.attackEnemyHp(attack)
        if (enemyHp <= 0) {
            enemyHp = 0
            return true
        }
        return false
    }

    fun resetEnemyHp() {
        enemyHp = enemyMaxHp
    }

    fun updateGameNumber() = gameNumber++

    fun updateScores() {
        playerScore += if (playerWonGame) 1 else 0
        enemyScore += if (playerWonGame) 0 else 1
    }

    fun getMatchState() = when {
        playerScore == config.firstTo -> MatchState.PlayerWin
        enemyScore == config.firstTo -> MatchState.EnemyWin
        config.firstTo > 1 && playerScore == config.firstTo - 1 && enemyScore == config.firstTo - 1 -> MatchState.Tiebreaker
        playerScore == config.firstTo - 1 || enemyScore == config.firstTo - 1 -> MatchState.MatchPoint
        else -> MatchState.Ongoing
    }
}