package com.tetrea.game.battle.rating

import com.tetrea.game.battle.Enemy
import kotlin.math.log2
import kotlin.math.min
import kotlin.math.pow

private const val MIN_ENEMY_ELO = 400f
private const val MAX_ENEMY_ELO = 3400f
private const val ENEMY_HP_TO_ELO_PERCENT = 0.2f

object Elo {

    fun getRatingChange(
        playerRating: Float,
        enemyRating: Float,
        playerScore: Int,
        enemyScore: Int
    ): Int {
        val playerWon = playerScore > enemyScore
        val expected = 1f / (1f + 10f.pow((enemyRating - playerRating) / 400f))
        val result = 0.7f + 0.3f * min(log2(1f + playerScore - enemyScore) / log2(8f), 1f)
        val actual = if (playerWon) result else 1 - result
        val change = (getKFactor(playerRating) * (actual - expected)).toInt()
        return if (playerWon && change < 0) 0 else change
    }

    fun getRating(enemy: Enemy): Int {
        val statsPercentage = (enemy.attack + enemy.defense + enemy.speed) / 300f
        val elo = (statsPercentage * (MAX_ENEMY_ELO - MIN_ENEMY_ELO)) + MIN_ENEMY_ELO + (ENEMY_HP_TO_ELO_PERCENT * enemy.maxHp)
        return elo.toInt()
    }

    private fun getKFactor(playerRating: Float): Int {
        return when {
            playerRating < 400 -> 340
            playerRating < 800 -> 220
            playerRating < 1000 -> 190
            playerRating < 1350 -> 155
            playerRating < 1750 -> 145
            playerRating < 2100 -> 110
            playerRating < 2300 -> 85
            playerRating < 2600 -> 72
            playerRating < 2700 -> 64
            playerRating < 2800 -> 50
            playerRating < 2900 -> 32
            playerRating < 3000 -> 24
            playerRating < 3200 -> 16
            else -> 12
        }
    }
}