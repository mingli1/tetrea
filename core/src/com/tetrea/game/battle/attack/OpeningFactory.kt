package com.tetrea.game.battle.attack

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.battle.rating.MAX_ENEMY_ELO
import com.tetrea.game.battle.rating.MIN_ENEMY_ELO

object OpeningFactory {

    fun getOpeningSequence(type: OpeningType, rating: Float): List<Attack> {
        val r = 1f - ((rating - MIN_ENEMY_ELO) / (MAX_ENEMY_ELO - MIN_ENEMY_ELO))
        val timeDiff = type.maxTime - type.minTime
        val opening = mutableListOf<Attack>()

        when (type) {
            OpeningType.TKI -> {
                opening.add(Attack(
                    action = Action.SendLines,
                    time = r * timeDiff + type.minTime,
                    lines = 4
                ))
            }
            OpeningType.PerfectClear -> {
                opening.add(Attack(
                    action = Action.SendLines,
                    time = r * timeDiff + type.minTime,
                    lines = MathUtils.random(10, 12)
                ))
            }
            OpeningType.DTCannon -> {
                opening.add(Attack(
                    action = Action.SendLines,
                    time = r * timeDiff + type.minTime,
                    lines = 4
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = 1.5f,
                    lines = 7
                ))
            }
            OpeningType.CSpin -> {
                opening.add(Attack(
                    action = Action.SendLines,
                    time = r * timeDiff + type.minTime,
                    lines = 6
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = 1.5f,
                    lines = 5
                ))
            }
            OpeningType.MKO -> {
                if (MathUtils.random() <= 0.3f) {
                    opening.add(Attack(
                        action = Action.SendLines,
                        time = 3f,
                        lines = 10
                    ))
                } else {
                    opening.add(Attack(
                        action = Action.SendLines,
                        time = r * timeDiff + type.minTime,
                        lines = 4
                    ))
                }
            }
            OpeningType.FourWide -> {
                opening.add(Attack(
                    action = Action.SendLines,
                    time = r * timeDiff + type.minTime,
                    lines = 1
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 1
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 1
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 2
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 2
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 3
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 3
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 4
                ))
                opening.add(Attack(
                    action = Action.SendLines,
                    time = MathUtils.random(1f, 1.4f),
                    lines = 4
                ))
            }
        }

        return opening
    }
}