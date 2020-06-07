package com.tetrea.game.tetris

data class TetrisConfig(
    val width: Int = 10,
    val height: Int = 20,
    val bagSize: Int = 7,
    val numPreviews: Int = 5,
    val gravity: Float = 1f,
    // Delays
    val lockDelay1: Float = 0.5f,
    val lockDelay2: Float = 5f,
    val lockDelay3: Float = 20f,
    val garbageDelay: Float = 0.5f,
    // Attack tables
    val attackSingle: Int = 0,
    val attackDouble: Int = 1,
    val attackTriple: Int = 2,
    val attackQuad: Int = 4,
    val comboTable: (Int) -> Int = { combo ->
        when (combo) {
            1 -> 0
            2, 3, 4 -> 1
            5, 6 -> 2
            7, 8 -> 3
            9, 10, 11 -> 4
            else -> 5
        }
    },
    val attackTSS: Int = 2,
    val attackTSD: Int = 4,
    val attackTST: Int = 6,
    val b2bBonus: Int = 1,
    val attackPC: Int = 10
)