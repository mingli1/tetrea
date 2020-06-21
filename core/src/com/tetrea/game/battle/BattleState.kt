package com.tetrea.game.battle

import com.tetrea.game.screen.BattleScreen

class BattleState(
    private val config: BattleConfig,
    private val screen: BattleScreen
) {

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    var enemyHp = config.enemy.maxHp
    val enemyMaxHp = config.enemy.maxHp

    var playerScore = 0
    var enemyScore = 0
    var gameNumber = 0
    var playerWonGame = false

    private var attackIndex = 0
    var attackTimer = 0f

    fun update(dt: Float) {
        if (screen.tetris.started) {
            config.attackScheme?.let { handleAttackScheme(dt, it) } ?: handleRandomScheme(dt)
        }

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

    fun resetState() {
        enemyHp = enemyMaxHp
        attackIndex = 0
        attackTimer = 0f
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

    private fun healEnemy(heal: Int) {
        enemyHp += heal
        screen.scene.healEnemyHp(heal)
        if (enemyHp > enemyMaxHp) enemyHp = enemyMaxHp
    }

    private fun handleRandomScheme(dt: Float) {

    }

    private fun handleAttackScheme(dt: Float, attackScheme: List<Attack>) {
        val attack = attackScheme[attackIndex]

        when (attack.action) {
            Action.Random -> {}
            Action.SendLines, Action.Heal -> screen.scene.startEnemyCharge(attack.time, attack.action)
        }
        attackTimer += dt
        if (attackTimer >= attack.time) {
            when (attack.action) {
                Action.SendLines -> attack.lines?.let { screen.tetris.queueGarbage(it) }
                Action.Heal -> attack.heal?.let { healEnemy(it) }
                else -> {}
            }

            attackTimer = 0f
            if (attackIndex == attackScheme.size - 1) attackIndex = 0
            else attackIndex++
        }
    }
}