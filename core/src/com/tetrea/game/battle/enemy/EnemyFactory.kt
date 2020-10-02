package com.tetrea.game.battle.enemy

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.rating.ENEMY_HP_TO_ELO_PERCENT
import com.tetrea.game.battle.rating.MAX_ENEMY_ELO
import com.tetrea.game.battle.rating.MIN_ENEMY_ELO
import com.tetrea.game.util.NameFactory
import kotlin.math.min
import kotlin.math.pow

private const val MIN_HP = 4f
private const val MAX_HP = 200f

object EnemyFactory {

    fun getEnemy(rating: Float): Enemy {
        val maxHp = getHpFromRange(rating)
        val newRating = rating - (maxHp * ENEMY_HP_TO_ELO_PERCENT)
        val totalStats = (((newRating - MIN_ENEMY_ELO) / (MAX_ENEMY_ELO - MIN_ENEMY_ELO)) * 300f).toInt()

        val r = MathUtils.random()
        val statPriority = when {
            r < 0.4f -> StatPriority.AttackSpeed
            r < 0.7f -> StatPriority.SpeedAttack
            r < 0.85f -> StatPriority.AttackDefense
            r < 0.93f -> StatPriority.DefenseAttack
            r < 0.97f -> StatPriority.SpeedDefense
            else -> StatPriority.DefenseSpeed
        }
        statPriority.reset()
        repeat(totalStats) {
            val p = MathUtils.random()
            when {
                p < 0.45f -> statPriority.increment(0)
                p < 0.75f -> statPriority.increment(1)
                else -> statPriority.increment(2)
            }
        }
        return Enemy(
            name = NameFactory.getEnemyName(),
            avatar = "${MathUtils.random(0, 24)}_avatar",
            maxHp = maxHp.toInt(),
            attack = min(statPriority.getAtk(), 100),
            defense = min(statPriority.getDef(), 100),
            speed = min(statPriority.getSpd(), 100),
            rating = rating,
            statPriority = statPriority
        )
    }

    private fun getHpFromRange(rating: Float): Float {
        return (((rating - MIN_ENEMY_ELO) / (MAX_ENEMY_ELO - MIN_ENEMY_ELO)).pow(1.4f) * (MAX_HP - MIN_HP)) + MIN_HP
    }
}

enum class StatPriority {
    AttackSpeed,
    SpeedAttack,
    AttackDefense,
    DefenseAttack,
    SpeedDefense,
    DefenseSpeed;

    private var first = 0
    private var second = 0
    private var third = 0

    fun reset() {
        first = 0
        second = 0
        third = 0
    }

    fun increment(split: Int) {
        when (split) {
            0 -> {
                when {
                    first < 100 -> first++
                    second < 100 -> second++
                    else -> third++
                }
            }
            1 -> {
                when {
                    second < 100 -> second++
                    first < 100 -> first++
                    else -> third++
                }
            }
            else -> {
                when {
                    third < 100 -> third++
                    first < 100 -> first++
                    else -> second++
                }
            }
        }
    }

    fun getAtk() = when (this) {
        AttackSpeed, AttackDefense -> first
        SpeedAttack, DefenseAttack -> second
        else -> third
    }

    fun getDef() = when (this) {
        DefenseAttack, DefenseSpeed -> first
        SpeedDefense, AttackDefense -> second
        else -> third
    }

    fun getSpd() = when (this) {
        SpeedDefense, SpeedAttack -> first
        AttackSpeed, DefenseSpeed -> second
        else -> third
    }
}