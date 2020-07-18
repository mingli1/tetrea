package com.tetrea.game.battle

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.enemy.EnemyFactory
import com.tetrea.game.battle.enemy.StatPriority

private const val ELO_OFFSET = 65f
private const val ONE_ATTACK_PATTERN_ELO = 1500f
private const val TWO_ATTACK_PATTERN_ELO = 2300f

object BattleConfigFactory {

    fun findMatch(rating: Float): BattleConfig {
        val elo = rating + (MathUtils.randomSign() * MathUtils.random(ELO_OFFSET))
        val bestOf = getBestOfFromRating(elo)
        val enemy = EnemyFactory.getEnemy(elo)
        val abilities = getAbilities(rating, enemy.statPriority)

        return BattleConfig(
            bestOf = bestOf,
            enemy = enemy,
            isMatchmaking = true,
            aiLevel = getAiLevelFromRating(elo),
            attackPatterns = getAttackPatterns(elo),
            abilities = abilities
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

    private fun getAiLevelFromRating(rating: Float): AILevel {
        return when {
            rating < 1000 -> AILevel.None
            rating < 1400 -> AILevel.Simple
            rating < 2000 -> AILevel.Intermediate
            rating < 2400 -> AILevel.Expert
            else -> AILevel.Genius
        }
    }

    private fun getAbilities(rating: Float, statPriority: StatPriority): List<Action> {
        val abilities = mutableListOf<Action>()
        if (rating >= Action.SolidGarbage.ratingThreshold) abilities.add(Action.SolidGarbage)
        if (rating >= Action.Gravity.ratingThreshold) abilities.add(Action.Gravity)

        when (statPriority) {
            StatPriority.AttackSpeed, StatPriority.AttackDefense -> {
                if (rating >= Action.SolidGarbage.ratingThreshold) abilities.add(Action.SolidGarbage)
                if (rating >= Action.Shield.ratingThreshold) abilities.add(Action.Shield)
            }
            StatPriority.DefenseSpeed, StatPriority.DefenseAttack -> {
                if (rating >= Action.Shield.ratingThreshold) abilities.add(Action.Shield)
                if (rating >= Action.Immune.ratingThreshold) abilities.add(Action.Immune)
            }
            else -> {
                if (rating >= Action.Gravity.ratingThreshold) abilities.add(Action.Gravity)
                if (rating >= Action.DamageReduction.ratingThreshold) abilities.add(Action.DamageReduction)
            }
        }
        return abilities
    }

    private fun getAttackPatterns(rating: Float): List<AttackPattern> {
        val attackPatterns = mutableListOf<AttackPattern>()
        val pool = mutableListOf<AttackPattern>().apply {
            addAll(AttackPattern.values())
        }
        return when {
            rating < ONE_ATTACK_PATTERN_ELO -> attackPatterns
            rating < TWO_ATTACK_PATTERN_ELO -> {
                if (MathUtils.random() < 0.5f) attackPatterns.add(AttackPattern.values().random())
                attackPatterns
            }
            else -> {
                if (MathUtils.random() < 0.75f) {
                    val a = pool.random()
                    pool.remove(a)
                    attackPatterns.add(a)

                    if (MathUtils.random() < 0.5f) {
                        attackPatterns.add(pool.random())
                    }
                }
                attackPatterns
            }
        }
    }
}