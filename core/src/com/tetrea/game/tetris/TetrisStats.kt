package com.tetrea.game.tetris

import com.tetrea.game.extension.formatMMSS

class TetrisStats {

    var numGames = 0
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

    // todo
    var maxCombo = 0

    fun getLabeledPairs(): Map<String, String> {
        return mapOf(
            "TOTAL TIME" to time.formatMMSS(),
            "PIECES PER SECOND" to String.format("%.2f", pps / numGames),
            "ATTACK PER MINUTE" to String.format("%.2f", apm / numGames),
            "ATTACK" to attack.toString(),
            "LINES SENT" to linesSent.toString(),
            "MAX SPIKE" to maxSpike.toString(),
            "BACK TO BACKS" to numB2B.toString(),
            "SINGLES" to numSingle.toString(),
            "DOUBLES" to numDouble.toString(),
            "TRIPLES" to numTriple.toString(),
            "QUADS" to numQuad.toString(),
            "T-SPIN SINGLES" to numTSS.toString(),
            "T-SPIN DOUBLES" to numTSD.toString(),
            "T-SPIN TRIPLES" to numTST.toString(),
            "PERFECT CLEARS" to numPC.toString()
        )
    }
}