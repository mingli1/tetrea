package com.tetrea.game.tetris

import com.squareup.moshi.Json
import com.tetrea.game.extension.formatComma
import com.tetrea.game.extension.formatMMSSDDD

data class ArcadeStats(
    @Json(name = "sprintTime") var sprintTime: Float = 0f,
    @Json(name = "ultraScore") var ultraScore: Int = 0,
    @Json(name = "cheeseTime") var cheeseTime: Float = 0f,
    @Json(name = "cheeseLeastBlocks") var cheeseLeastBlocks: Int = 0
) {

    fun getLabeledPairs(): Map<String, String> {
        return mapOf(
            "SPRINT TIME" to sprintTime.formatMMSSDDD(),
            "ULTRA SCORE" to ultraScore.formatComma(),
            "CHEESE TIME" to cheeseTime.formatMMSSDDD(),
            "CHEESE LEAST BLOCKS" to cheeseLeastBlocks.toString()
        )
    }
}