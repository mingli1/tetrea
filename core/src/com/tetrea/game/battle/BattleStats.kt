package com.tetrea.game.battle

import com.squareup.moshi.Json

const val MAX_GAMES_SAVED = 25

data class BattleStats(
    @Json(name = "totalMatches") var totalMatches: Int = 0,
    @Json(name = "wins") var wins: Int = 0,
    @Json(name = "losses") var losses: Int = 0,
    @Json(name = "averageApm") val averageApm: MutableList<Float> = mutableListOf(),
    @Json(name = "averagePps") val averagePps: MutableList<Float> = mutableListOf()
) {

    fun getApm() = averageApm.sum() / averageApm.size

    fun getPps() = averagePps.sum() / averagePps.size

    fun addApm(apm: Float) {
        if (averageApm.size == MAX_GAMES_SAVED) {
            averageApm.removeAt(0)
        }
        averageApm.add(apm)
    }

    fun addPps(pps: Float) {
        if (averagePps.size == MAX_GAMES_SAVED) {
            averagePps.removeAt(0)
        }
        averagePps.add(pps)
    }
}