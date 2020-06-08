package com.tetrea.game.input

import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.Rotation

class BattleInputHandler(
    private val tetris: Tetris,
    das: Float,
    arr: Float,
    private val sds: Float
) {

    private val leftTuning = InputTuning(tetris, false, das, arr)
    private val rightTuning = InputTuning(tetris, true, das, arr)

    private var sdTimer = 0f
    private var startSoftDrop = false

    fun update(dt: Float) {
        leftTuning.update(dt)
        rightTuning.update(dt)

        if (startSoftDrop) {
            if (sds == 0f) tetris.instantSoftDrop()
            else {
                sdTimer += dt
                if (sdTimer >= sds) {
                    tetris.currPiece.move(0, -1)
                    sdTimer = 0f
                }
            }
        }
    }

    fun onRight(down: Boolean) {
        if (down) {
            tetris.rightHeld = true

            if (leftTuning.inProgress) leftTuning.reset()
            rightTuning.start()
            tetris.currPiece.move(1, 0)
            if (tetris.currPiece.canMove(1, 0)) tetris.toggleLockDelay2(true)
        } else {
            tetris.rightHeld = false

            if (leftTuning.inProgress) leftTuning.start()
            rightTuning.end()
            if (!tetris.leftHeld) tetris.toggleLockDelay2(false)
        }
    }

    fun onLeft(down: Boolean) {
        if (down) {
            tetris.leftHeld = true

            if (rightTuning.inProgress) rightTuning.reset()
            leftTuning.start()
            tetris.currPiece.move(-1, 0)
            if (tetris.currPiece.canMove(-1, 0)) tetris.toggleLockDelay2(true)
        } else {
            tetris.leftHeld = false

            if (rightTuning.inProgress) rightTuning.start()
            leftTuning.end()
            if (!tetris.rightHeld) tetris.toggleLockDelay2(false)
        }
    }

    fun softDrop(down: Boolean) {
        startSoftDrop = down
    }

    fun hardDrop() {
        tetris.hardDrop()
    }

    fun rotateClockwise() {
        tetris.onRotate()
        tetris.currPiece.rotate(Rotation.Clockwise)
    }

    fun rotateCounterClockwise() {
        tetris.onRotate()
        tetris.currPiece.rotate(Rotation.Counterclockwise)
    }

    fun rotate180() {
        tetris.onRotate()
        tetris.currPiece.rotate(Rotation.OneEighty)
    }

    fun onHold() {
        tetris.holdCurrPiece()
    }

    fun onRestart() {
        tetris.reset()
    }
}