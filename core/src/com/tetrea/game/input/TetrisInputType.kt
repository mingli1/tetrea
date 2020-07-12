package com.tetrea.game.input

import com.squareup.moshi.Json

enum class TetrisInputType(val buttonIndex: Int, val str: String) {
    @Json(name = "Left") Left(0, "LEFT"),
    @Json(name = "Right") Right(1, "RIGHT"),
    @Json(name = "SoftDrop") SoftDrop(2, "SOFT DROP"),
    @Json(name = "HardDrop") HardDrop(3, "HARD DROP"),
    @Json(name = "RotateCW") RotateCW(4, "ROTATE RIGHT"),
    @Json(name = "RotateCCW") RotateCCW(5, "ROTATE LEFT"),
    @Json(name = "Rotate180") Rotate180(6, "ROTATE 180"),
    @Json(name = "Hold") Hold(7, "HOLD"),
    @Json(name = "Pause") Pause(8, "PAUSE")
}