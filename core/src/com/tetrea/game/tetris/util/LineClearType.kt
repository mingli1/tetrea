package com.tetrea.game.tetris.util

import com.badlogic.gdx.graphics.Color
import com.tetrea.game.res.GAME_ORANGE
import com.tetrea.game.res.GAME_PURPLE

enum class LineClearType(val desc: String, val color: Color) {
    Double("DOUBLE", Color.WHITE),
    Triple("TRIPLE", Color.WHITE),
    Quad("QUAD", Color.WHITE),
    TSS("T-SPIN SINGLE", GAME_PURPLE),
    TSD("T-SPIN DOUBLE", GAME_PURPLE),
    TST("T-SPIN TRIPLE", GAME_PURPLE),
    PerfectClear("PERFECT CLEAR", GAME_ORANGE),
    None("", Color.WHITE)
}