package com.tetrea.game.battle.attack

enum class OpeningType(val minTime: Float, val maxTime: Float) {
    TKI(1.9f, 7.7f),
    PerfectClear(3.6f, 10f),
    DTCannon(2f, 13f),
    CSpin(2.4f, 11f),
    MKO(2f, 8f),
    FourWide(7.6f, 18f)
}