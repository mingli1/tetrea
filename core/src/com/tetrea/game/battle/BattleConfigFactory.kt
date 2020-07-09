package com.tetrea.game.battle

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.enemy.EnemyFactory

private const val ELO_OFFSET = 65f

object BattleConfigFactory {

    fun findMatch(rating: Float): BattleConfig {
        val elo = rating + (MathUtils.randomSign() * MathUtils.random(ELO_OFFSET))
        val firstTo = getFirstToFromRating(elo)
        val enemy = EnemyFactory.getEnemy(elo)
        // todo: figure out attack patterns and ai patterns
        return BattleConfig(
            firstTo = firstTo,
            enemy = enemy,
            isMatchmaking = true
        )
    }

    private fun getFirstToFromRating(rating: Float): Int {
        return when {
            rating < 800 -> 1
            rating < 1200 -> 2
            rating < 1500 -> 3
            rating < 1900 -> 5
            rating < 2600 -> 7
            rating < 3000 -> 10
            else -> 13
        }
    }
}