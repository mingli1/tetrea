package com.tetrea.game.tetris.util

enum class Rotation {
    Clockwise,
    Counterclockwise,
    OneEighty;

    fun opposite() = when (this) {
        Clockwise -> Counterclockwise
        Counterclockwise -> Clockwise
        OneEighty -> OneEighty
    }
}