package com.tetrea.game.tetris

import com.squareup.moshi.Json

data class TetrisConfig(
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int,
    @Json(name = "startDelay") val startDelay: Int,
    @Json(name = "bagSize") val bagSize: Int,
    @Json(name = "numPreviews") val numPreviews: Int,
    @Json(name = "gravity") val gravity: Float,
    // Delays
    @Json(name = "lockDelay1") val lockDelay1: Float,
    @Json(name = "lockDelay2") val lockDelay2: Float,
    @Json(name = "lockDelay3") val lockDelay3: Float,
    @Json(name = "garbageDelay") val garbageDelay: Float,
    // Attack tables
    @Json(name = "critChance") val critChance: Float,
    @Json(name = "critMultiplier") val critMultiplier: Float,
    @Json(name = "attackSingle") val attackSingle: Int,
    @Json(name = "attackDouble") val attackDouble: Int,
    @Json(name = "attackTriple") val attackTriple: Int,
    @Json(name = "attackQuad") val attackQuad: Int,
    @Json(name = "comboTable") val comboTable: AttackTable,
    @Json(name = "b2bTable") val b2bTable: AttackTable,
    @Json(name = "attackTSS") val attackTSS: Int,
    @Json(name = "attackTSD") val attackTSD: Int,
    @Json(name = "attackTST") val attackTST: Int,
    @Json(name = "attackPC") val attackPC: Int,
    @Json(name = "spikeThreshold") val spikeThreshold: Int,
    @Json(name = "scoreTable") val scoreTable: ScoreTable? = null
)