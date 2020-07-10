package com.tetrea.game.util

import com.squareup.moshi.Json

data class Int2(
    @Json(name = "x") var x: Int = 0,
    @Json(name = "y") var y: Int = 0
)