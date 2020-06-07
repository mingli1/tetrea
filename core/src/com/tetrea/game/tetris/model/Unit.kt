package com.tetrea.game.tetris.model

import com.tetrea.game.tetris.model.Square

data class Unit(
    var square: Square,
    var x: Int,
    var y: Int,
    var filled: Boolean
)