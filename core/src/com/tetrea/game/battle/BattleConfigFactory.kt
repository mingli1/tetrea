package com.tetrea.game.battle

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.attack.Action
import com.tetrea.game.battle.attack.Attack
import com.tetrea.game.battle.attack.AttackPattern
import com.tetrea.game.battle.attack.OpeningFactory
import com.tetrea.game.battle.attack.OpeningType
import com.tetrea.game.battle.enemy.AILevel
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
        val attackPatterns = getAttackPatterns(elo)
        val opening = getOpening(elo, attackPatterns.contains(AttackPattern.FourWider))

        return BattleConfig(
            bestOf = bestOf,
            enemy = enemy,
            isMatchmaking = true,
            aiLevel = getAiLevelFromRating(elo),
            opening = opening,
            attackPatterns = attackPatterns,
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
                if (rating >= Action.Gravity.ratingThreshold) abilities.add(Action.Gravity)
            }
            StatPriority.DefenseSpeed, StatPriority.DefenseAttack -> {
                if (rating >= Action.DamageReduction.ratingThreshold) abilities.add(Action.DamageReduction)
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
        val pool = AttackPattern.values().toMutableList()
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

    private fun getOpening(rating: Float, isFourWider: Boolean): List<Attack> {
        if (isFourWider) return OpeningFactory.getOpeningSequence(OpeningType.FourWide, rating)
        return when {
            rating <= 1000 -> OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
            rating <= 1200 -> {
                if (MathUtils.random() <= 0.7f) OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
                else OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
            }
            rating <= 1500 -> {
                val r = MathUtils.random()
                when {
                    r < 0.6f -> OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
                    r < 0.2f -> OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.DTCannon, rating)
                }
            }
            rating <= 1800 -> {
                val r = MathUtils.random()
                when {
                    r < 0.5f -> OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
                    r < 0.3f -> OpeningFactory.getOpeningSequence(OpeningType.DTCannon, rating)
                    r < 0.15 -> OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                }
            }
            rating <= 2100 -> {
                val r = MathUtils.random()
                when {
                    r < 0.5f -> OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
                    r < 0.3f -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                    r < 0.15 -> OpeningFactory.getOpeningSequence(OpeningType.DTCannon, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
                }
            }
            rating <= 2400 -> {
                val r = MathUtils.random()
                when {
                    r < 0.4f -> OpeningFactory.getOpeningSequence(OpeningType.TKI, rating)
                    r < 0.4f -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                    r < 0.15 -> OpeningFactory.getOpeningSequence(OpeningType.CSpin, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.DTCannon, rating)
                }
            }
            rating <= 2700 -> {
                val r = MathUtils.random()
                when {
                    r < 0.6f -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                    r < 0.3f -> OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.CSpin, rating)
                }
            }
            rating <= 3000 -> {
                val r = MathUtils.random()
                when {
                    r < 0.7f -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                    r < 0.2f -> OpeningFactory.getOpeningSequence(OpeningType.MKO, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.CSpin, rating)
                }
            }
            else -> {
                val r = MathUtils.random()
                when {
                    r < 0.7f -> OpeningFactory.getOpeningSequence(OpeningType.PerfectClear, rating)
                    else -> OpeningFactory.getOpeningSequence(OpeningType.CSpin, rating)
                }
            }
        }
    }
}