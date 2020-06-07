package com.tetrea.game.tetris.util

import com.tetrea.game.util.Int2

val CW_ROTATE_DATA = arrayOf(
    Int2(0, -1),
    Int2(1, 0)
)

val CCW_ROTATE_DATA = arrayOf(
    Int2(0, 1),
    Int2(-1, 0)
)

val TSZLJ_OFFSET_DATA = arrayOf(
    arrayOf(Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0)),
    arrayOf(Int2(0, 0), Int2(1, 0), Int2(1, -1), Int2(0, 2), Int2(1, 2)),
    arrayOf(Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0)),
    arrayOf(Int2(0, 0), Int2(-1, 0), Int2(-1, -1), Int2(0, 2), Int2(-1, 2))
)

val I_OFFSET_DATA = arrayOf(
    arrayOf(Int2(0, 0), Int2(-1, 0), Int2(2, 0), Int2(-1, 0), Int2(2, 0)),
    arrayOf(Int2(-1, 0), Int2(0, 0), Int2(0, 0), Int2(0, 1), Int2(0, -2)),
    arrayOf(Int2(-1, 1), Int2(1, 1), Int2(-2, 1), Int2(1, 0), Int2(-2, 0)),
    arrayOf(Int2(0, 1), Int2(0, 1), Int2(0, 1), Int2(0, -1), Int2(0, 2))
)

val TSZLJ_180_OFFSET_DATA = arrayOf(
    arrayOf(Int2(0, 0), Int2(1, 0), Int2(2, 0), Int2(1, 1), Int2(2, 1), Int2(-1, 0), Int2(-2, 0), Int2(-1, 1), Int2(-2, 1), Int2(0, -1), Int2(3, 0), Int2(-3, 0)),
    arrayOf(Int2(0, 0), Int2(0, 1), Int2(0, 2), Int2(-1, 1), Int2(-1, 2), Int2(0, -1), Int2(0, -2), Int2(-1, -1), Int2(-1, -2), Int2(1, 0), Int2(0, 3), Int2(0, -3)),
    arrayOf(Int2(0, 0), Int2(-1, 0), Int2(-2, 0), Int2(-1, -1), Int2(-2, -1), Int2(1, 0), Int2(2, 0), Int2(1, -1), Int2(2, -1), Int2(0, 1), Int2(-3, 0), Int2(3, 0)),
    arrayOf(Int2(0, 0), Int2(0, 1), Int2(0, 2), Int2(1, 1), Int2(1, 2), Int2(0, -1), Int2(0, -2), Int2(1, -1), Int2(1, -2), Int2(-1, 0), Int2(0, 3), Int2(0, -3))
)

val I_180_OFFSET_DATA = arrayOf(
    arrayOf(Int2(0, 0), Int2(-1, 0), Int2(-2, 0), Int2(1, 0), Int2(2, 0), Int2(0, 1), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0)),
    arrayOf(Int2(0, 0), Int2(0, 1), Int2(0, 2), Int2(0, -1), Int2(0, -2), Int2(-1, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0)),
    arrayOf(Int2(0, 0), Int2(1, 0), Int2(2, 0), Int2(-1, 0), Int2(-2, 0), Int2(0, -1), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0)),
    arrayOf(Int2(0, 0), Int2(0, 1), Int2(0, 2), Int2(0, -1), Int2(0, -2), Int2(1, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0), Int2(0, 0))
)