package com.tetrea.game.util

import com.squareup.moshi.Json

data class RelativePosition(
    @Json(name = "relX") val relX: RelativeValue,
    @Json(name = "relY") val relY: RelativeValue
)

data class RelativeValue(
    @Json(name = "value") val value: Float,
    @Json(name = "relativeTo") val relativeTo: RelativeTo
)

enum class RelativeTo {
    @Json(name = "XZero") XZero,
    @Json(name = "YZero") YZero,
    @Json(name = "StageWidth") StageWidth,
    @Json(name = "StageHeight") StageHeight
}