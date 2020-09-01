package com.tetrea.game.battle

import com.squareup.moshi.Json
import com.tetrea.game.extension.formatHours
import com.tetrea.game.tetris.TetrisStats

const val MAX_GAMES_SAVED = 25

data class BattleStats(
    @Json(name = "totalMatches") var totalMatches: Int = 0,
    @Json(name = "wins") var wins: Int = 0,
    @Json(name = "losses") var losses: Int = 0,
    @Json(name = "averageApm") val averageApm: MutableList<Float> = mutableListOf(),
    @Json(name = "averagePps") val averagePps: MutableList<Float> = mutableListOf(),
    @Json(name = "maxApm") var maxApm: Float = 0f,
    @Json(name = "maxCombo") var maxCombo: Int = 0,
    @Json(name = "maxSpike") var maxSpike: Int = 0,
    @Json(name = "maxAttack") var maxAttack: Int = 0,
    @Json(name = "totalLinesSent") var totalLinesSent: Int = 0,
    @Json(name = "totalAttack") var totalAttack: Int = 0,
    @Json(name = "totalB2b") var totalB2b: Int = 0,
    @Json(name = "totalTime") var totalTime: Float = 0f,
    @Json(name = "totalTSS") var totalTSS: Int = 0,
    @Json(name = "totalTSD") var totalTSD: Int = 0,
    @Json(name = "totalTST") var totalTST: Int = 0,
    @Json(name = "totalSingle") var totalSingle: Int = 0,
    @Json(name = "totalDouble") var totalDouble: Int = 0,
    @Json(name = "totalTriple") var totalTriple: Int = 0,
    @Json(name = "totalQuad") var totalQuad: Int = 0,
    @Json(name = "totalPC") var totalPC: Int = 0,
    @Json(name = "totalCrits") var totalCrits: Int = 0
) {

    fun getApm() = if (averageApm.isEmpty()) 0f else averageApm.sum() / averageApm.size

    fun getPps() = if (averagePps.isEmpty()) 0f else averagePps.sum() / averagePps.size

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

    fun updateMatchmakingStats(stats: TetrisStats) {
        val apm = stats.apmList.max() ?: 0f
        if (apm > maxApm) maxApm = apm

        val attack = stats.attackList.max() ?: 0
        if (attack > maxAttack) maxAttack = attack

        if (stats.maxCombo > maxCombo) maxCombo = stats.maxCombo
        if (stats.maxSpike > maxSpike) maxSpike = stats.maxSpike

        totalTime += stats.time
        totalLinesSent += stats.linesSent
        totalAttack += stats.attack
        totalB2b += stats.numB2B
        totalTSS += stats.numTSS
        totalTSD += stats.numTSD
        totalTST += stats.numTST
        totalSingle += stats.numSingle
        totalDouble += stats.numDouble
        totalTriple += stats.numTriple
        totalQuad += stats.numQuad
        totalPC += stats.numPC
        totalCrits += stats.numCrits
    }

    fun getLabeledPairs(): Map<String, String> {
        return mapOf(
            "MAX APM" to String.format("%.2f", maxApm),
            "MAX ATTACK" to maxAttack.toString(),
            "MAX COMBO" to maxCombo.toString(),
            "MAX SPIKE" to maxSpike.toString(),
            "TOTAL TIME" to totalTime.formatHours(),
            "TOTAL LINES SENT" to totalLinesSent.toString(),
            "TOTAL ATTACK" to totalAttack.toString(),
            "TOTAL B2B" to totalB2b.toString(),
            "TOTAL CRITS" to totalCrits.toString(),
            "T-SPIN SINGLES" to totalTSS.toString(),
            "T-SPIN DOUBLES" to totalTSD.toString(),
            "T-SPIN TRIPLES" to totalTST.toString(),
            "SINGLES" to totalSingle.toString(),
            "DOUBLES" to totalDouble.toString(),
            "TRIPLES" to totalTriple.toString(),
            "QUADS" to totalQuad.toString(),
            "PERFECT CLEARS" to totalPC.toString()
        )
    }
}