package com.tetrea.game.battle.enemy

enum class AILevel(val level: Int) {
    // completely random
    None(0),
    // will not heal if hp is above a certain threshold
    Simple(1),
    // will prioritize healing when low
    Intermediate(2),
    // will prioritize attacking the player if their stack is high and speed up
    Expert(3),
    // will send cheese when low in hp
    Genius(4)
}