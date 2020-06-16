package com.tetrea.game.tetris

class TetrisStats {

    var time = 0f
    var pps = 0f
    var apm = 0f
    var attack = 0
    var linesSent = 0
    var numB2B = 0
    var numTSS = 0
    var numTSD = 0
    var numTST = 0
    var numSingle = 0
    var numDouble = 0
    var numTriple = 0
    var numQuad = 0
    var numPC = 0
    var maxSpike = 0

    fun reset() {
        time = 0f
        pps = 0f
        apm = 0f
        attack = 0
        linesSent = 0
        numB2B = 0
        numTSS = 0
        numTSD = 0
        numTST = 0
        numSingle = 0
        numDouble = 0
        numTriple = 0
        numQuad = 0
        numPC = 0
        maxSpike = 0
    }
}