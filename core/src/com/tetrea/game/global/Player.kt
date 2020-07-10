package com.tetrea.game.global

import com.squareup.moshi.Json
import com.tetrea.game.battle.BattleRecord
import com.tetrea.game.util.Int2

data class Player(
    @Json(name = "name") val name: String = "PLAYER",
    @Json(name = "avatar") val avatar: String = "enemy_ittzzi",
    @Json(name = "rating") var rating: Float = 800f,
    @Json(name = "currWorldId") var currWorldId: Int = 0,
    @Json(name = "currLevelId") var currLevelId: Int = 0,
    @Json(name = "battleRecords") private val battleRecords: MutableMap<String, BattleRecord> = mutableMapOf()
) {

    fun getRecord(key: String) = battleRecords[key] ?: BattleRecord()

    fun completeMatchup(
        key: String,
        won: Boolean,
        ratingChange: Float,
        playerScore: Int,
        enemyScore: Int
    ) {
        if (rating + ratingChange < 0f) rating = 0f
        rating += ratingChange

        if (!battleRecords.containsKey(key)) battleRecords[key] = BattleRecord()

        battleRecords[key]?.let {
            if (won) {
                it.defeated = true
                if (it.bestScore == null) {
                    it.bestScore = Int2(playerScore, enemyScore)
                    currLevelId++
                }
                else if (enemyScore < it.bestScore!!.y) it.bestScore!!.y = enemyScore
                it.allTimeRecord.x++
            } else {
                it.allTimeRecord.y++
            }
            it.attempts++
        }
        // todo: increment world id
    }
}