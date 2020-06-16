package com.tetrea.game.tetris.util

import com.badlogic.gdx.graphics.Color
import com.tetrea.game.res.Color.GAME_ORANGE
import com.tetrea.game.res.Color.GAME_PURPLE
import com.tetrea.game.res.Color.GAME_WHITE

enum class LineClearType(val desc: String, val color: Color) {
    Double("DOUBLE", GAME_WHITE),
    Triple("TRIPLE", GAME_WHITE),
    Quad("QUAD", GAME_WHITE),
    TSS("T-SPIN SINGLE", GAME_PURPLE),
    TSD("T-SPIN DOUBLE", GAME_PURPLE),
    TST("T-SPIN TRIPLE", GAME_PURPLE),
    PerfectClear("PERFECT CLEAR", GAME_ORANGE),
    None("", GAME_WHITE)
}