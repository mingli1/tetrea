package com.tetrea.game.util

import com.badlogic.gdx.math.MathUtils

private val letters = listOf("T", "I", "L", "J", "O", "S", "Z")

object NameFactory {

    fun getEnemyName(): String {
        val length = MathUtils.random(3, 7)
        var name = ""
        repeat(length) {
            name += letters.random()
        }
        return name
    }
}