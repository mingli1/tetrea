package com.tetrea.game.battle

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.enemy.EnemyFactory

private const val ELO_OFFSET = 65f

object BattleConfigFactory {

    fun findMatch(rating: Float): BattleConfig {
        val elo = rating + (MathUtils.randomSign() * MathUtils.random(ELO_OFFSET))
        val bestOf = getBestOfFromRating(elo)
        val enemy = EnemyFactory.getEnemy(elo)
        // todo: figure out attack patterns and ai patterns
        return BattleConfig(
            bestOf = bestOf,
            enemy = enemy,
            isMatchmaking = true
        )
    }

    private fun getBestOfFromRating(rating: Float): Int {
        return when {
            rating < 1000 -> 1
            rating < 1800 -> 3
            rating < 2400 -> 5
            rating < 3000 -> 7
            else -> 9
        }
    }
}