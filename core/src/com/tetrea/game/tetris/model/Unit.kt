package com.tetrea.game.tetris.model

data class Unit(
    var square: Square,
    var x: Int,
    var y: Int,
    var filled: Boolean
)