package com.tetrea.game.battle.attack

enum class OpeningType(val minTime: Float, val maxTime: Float) {
    TKI(1.2f, 7.7f),
    PerfectClear(3.6f, 10f),
    DTCannon(2f, 13f),
    CSpin(2.4f, 11f),
    MKO(1.7f, 8f),
    FourWide(6.6f, 17f)
}