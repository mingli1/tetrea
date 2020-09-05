package com.tetrea.game.tetris

import com.squareup.moshi.Json

data class ScoreTable(
    @Json(name = "single") val single: Int,
    @Json(name = "double") val double: Int,
    @Json(name = "triple") val triple: Int,
    @Json(name = "quad") val quad: Int,
    @Json(name = "tss") val tss: Int,
    @Json(name = "tsd") val tsd: Int,
    @Json(name = "tst") val tst: Int,
    @Json(name = "b2bMultiplier") val b2bMultiplier: Float,
    @Json(name = "combo") val combo: Int,
    @Json(name = "pc") val pc: Int
)