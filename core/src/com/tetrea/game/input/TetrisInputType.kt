package com.tetrea.game.input

import com.squareup.moshi.Json

enum class TetrisInputType(val buttonIndex: Int) {
    @Json(name = "Left") Left(0),
    @Json(name = "Right") Right(1),
    @Json(name = "SoftDrop") SoftDrop(2),
    @Json(name = "HardDrop") HardDrop(3),
    @Json(name = "RotateCW") RotateCW(4),
    @Json(name = "RotateCCW") RotateCCW(5),
    @Json(name = "Rotate180") Rotate180(6),
    @Json(name = "Hold") Hold(7),
    @Json(name = "Pause") Pause(8)
}