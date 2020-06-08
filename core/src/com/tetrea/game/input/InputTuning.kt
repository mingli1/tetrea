package com.tetrea.game.input

import com.tetrea.game.tetris.Tetris
import com.tetrea.game.util.Timer

class InputTuning(
    private val tetris: Tetris,
    private val right: Boolean,
    das: Float,
    private val arr: Float
) {

    var inProgress = false
    private val dasTimer = Timer(das, { startArr = true })
    private var arrTimer = 0f
    private var startArr = false

    fun update(dt: Float) {
        dasTimer.update(dt)
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
        dasTimer.start()
        inProgress = true
    }

    fun end() {
        reset()
        inProgress = false
    }

    fun reset() {
        dasTimer.reset()
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