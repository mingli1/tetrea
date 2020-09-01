package com.tetrea.game.tetris

import com.tetrea.game.extension.formatMMSS

class TetrisStats {

    var time = 0f
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
    var maxCombo = 0
    var numPiecesPlaced = 0
    var numInputs = 0
    var numCrits = 0
    val attackList = mutableListOf<Int>()
    val apmList = mutableListOf<Float>()
    val ppsList = mutableListOf<Float>()

    fun getLabeledPairs(): Map<String, String> {
        return mapOf(
            "TOTAL TIME" to time.formatMMSS(),
            "PIECES PER SECOND" to String.format("%.2f", numPiecesPlaced / time),
            "ATTACK PER MINUTE" to String.format("%.2f", attack / time * 60),
            "ATTACK" to attack.toString(),
            "LINES SENT" to linesSent.toString(),
            "PIECES PLACED" to numPiecesPlaced.toString(),
            "MAX SPIKE" to maxSpike.toString(),
            "MAX COMBO" to maxCombo.toString(),
            "BACK TO BACKS" to numB2B.toString(),
            "CRITS" to numCrits.toString(),
            "SINGLES" to numSingle.toString(),
            "DOUBLES" to numDouble.toString(),
            "TRIPLES" to numTriple.toString(),
            "QUADS" to numQuad.toString(),
            "T-SPIN SINGLES" to numTSS.toString(),
            "T-SPIN DOUBLES" to numTSD.toString(),
            "T-SPIN TRIPLES" to numTST.toString(),
            "PERFECT CLEARS" to numPC.toString(),
            "INPUTS PER PIECE" to String.format("%.2f", numInputs.toFloat() / numPiecesPlaced),
            "INPUTS" to numInputs.toString()
        )
    }
}