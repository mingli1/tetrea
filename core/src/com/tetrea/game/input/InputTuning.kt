package com.tetrea.game.input

import com.tetrea.game.tetris.Tetris

class InputTuning(
    private val tetris: Tetris,
    private val right: Boolean,
    private val das: Float,
    private val arr: Float
) {

    var inProgress = false
    private var dasTimer = 0f
    private var arrTimer = 0f
    private var startDas = false
    private var startArr = false

    fun update(dt: Float) {
        if (startDas) {
            dasTimer += dt
            if (dasTimer >= das) {
                startArr = true
                startDas = false
            }
        }
        if (startArr) {
            arrTimer += dt
            if (arr == 0f) {
                tetris.instantDas(right)
            }
            else if (arrTimer >= arr) {
                movePiece()
                arrTimer = 0f
            }
        }
    }

    fun start() {
        startDas = true
        inProgress = true
    }

    fun end() {
        reset()
        inProgress = false
    }

    fun reset() {
        startDas = false
        dasTimer = 0f
        startArr = false
        arrTimer = 0f
    }

    private fun movePiece() {
        when (right) {
            false -> tetris.currPiece.move(-1, 0)
            true -> tetris.currPiece.move(1, 0)
        }
    }
}