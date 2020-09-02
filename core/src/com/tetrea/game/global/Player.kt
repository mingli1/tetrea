package com.tetrea.game.global

import com.squareup.moshi.Json
import com.tetrea.game.battle.BattleRecord
import com.tetrea.game.battle.BattleStats
import com.tetrea.game.battle.MatchHistory
import com.tetrea.game.res.NUM_LEVELS
import com.tetrea.game.res.NUM_WORLDS
import com.tetrea.game.util.Int2
import kotlin.math.max

private const val MAX_MATCH_HISTORY_SAVED = 10

data class Player(
    @Json(name = "name") val name: String = "PLAYER",
    @Json(name = "avatar") val avatar: String = "enemy_ittzzi",
    @Json(name = "rating") var rating: Float = 800f,
    @Json(name = "maxRating") var maxRating: Float = 800f,
    @Json(name = "currWorldId") var currWorldId: Int = 0,
    @Json(name = "currLevelId") var currLevelId: Int = 0,
    @Json(name = "battleRecords") private val battleRecords: MutableMap<String, BattleRecord> = mutableMapOf(),
    @Json(name = "matchHistory") val matchHistory: MutableList<MatchHistory> = mutableListOf(),
    @Json(name = "battleStats") val battleStats: BattleStats = BattleStats(),
    @Json(name = "quitDuringBattle") var quitDuringBattle: Boolean = false,
    @Json(name = "dodgedBattle") var dodgedBattle: Boolean = false
) {

    fun getRecord(key: String) = battleRecords[key] ?: BattleRecord()

    fun completeMatchup(
        key: String,
        won: Boolean,
        ratingChange: Float,
        playerScore: Int,
        enemyScore: Int,
        isMatchmaking: Boolean,
        enemyName: String,
        enemyRating: Float
    ) {
        if (isMatchmaking) {
            val history = MatchHistory(
                playerRating = rating.toInt(),
                enemyRating = enemyRating.toInt(),
                enemyName = enemyName,
                playerScore = playerScore,
                enemyScore = enemyScore
            )
            if (matchHistory.size == MAX_MATCH_HISTORY_SAVED) {
                matchHistory.removeAt(matchHistory.lastIndex)
            }
            matchHistory.add(0, history)
        }

        if (rating + ratingChange < 0f) rating = 0f
        rating += ratingChange
        if (rating > maxRating) maxRating = rating

        if (isMatchmaking) return

        if (!battleRecords.containsKey(key)) battleRecords[key] = BattleRecord()

        battleRecords[key]?.let {
            if (won) {
                it.defeated = true
                if (it.bestScore == null) {
                    val maxLevel = NUM_LEVELS[currWorldId]
                    if (currLevelId + 1 < maxLevel) currLevelId++
                    else if (currWorldId + 1 < NUM_WORLDS) {
                        currWorldId++
                        currLevelId = 0
                    }
                }
                it.allTimeRecord.x++
            } else {
                it.allTimeRecord.y++
            }

            if (it.bestScore == null) {
                it.bestScore = Int2(playerScore, enemyScore)
            } else {
                if (playerScore > it.bestScore!!.x) it.bestScore!!.x = playerScore
                if (enemyScore < it.bestScore!!.y) it.bestScore!!.y = enemyScore
            }
            it.attempts++
        }
    }
}