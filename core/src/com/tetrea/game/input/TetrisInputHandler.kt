package com.tetrea.game.input

import com.tetrea.game.extension.default
import com.tetrea.game.res.SoundManager
import com.tetrea.game.screen.BaseScreen
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.Rotation

class TetrisInputHandler(
    private val tetris: Tetris,
    private val screen: BaseScreen,
    private val soundManager: SoundManager,
    das: Float,
    arr: Float,
    private val sds: Float
) {

    private val leftTuning = InputTuning(tetris, false, das, arr)
    private val rightTuning = InputTuning(tetris, true, das, arr)

    private var sdTimer = 0f
    private var startSoftDrop = false

    fun update(dt: Float) {
        if (tetris.started) {
            leftTuning.update(dt)
            rightTuning.update(dt)

            if (startSoftDrop) {
                if (sds == 0f) tetris.instantSoftDrop()
                else {
                    sdTimer += dt
                    if (sdTimer >= sds) {
                        tetris.currPiece?.move(0, -1)
                        sdTimer = 0f
                    }
                }
            }
        }
    }

    fun onPause() {
        screen.notifyPause()
    }

    fun onRight(down: Boolean) {
        if (down) {
            soundManager.onMove()
            tetris.rightHeld = true
            tetris.stats.numInputs++

            if (leftTuning.inProgress) leftTuning.reset()
            rightTuning.start()
            tetris.currPiece?.move(1, 0)
            if (tetris.currPiece?.canMove(1, 0).default(false)) tetris.toggleLockDelay2(true)
        } else {
            tetris.rightHeld = false

            if (leftTuning.inProgress) leftTuning.start()
            rightTuning.end()
            if (!tetris.leftHeld) tetris.toggleLockDelay2(false)
        }
    }

    fun onLeft(down: Boolean) {
        if (down) {
            soundManager.onMove()
            tetris.leftHeld = true
            tetris.stats.numInputs++

            if (rightTuning.inProgress) rightTuning.reset()
            leftTuning.start()
            tetris.currPiece?.move(-1, 0)
            if (tetris.currPiece?.canMove(-1, 0).default(false)) tetris.toggleLockDelay2(true)
        } else {
            tetris.leftHeld = false

            if (rightTuning.inProgress) rightTuning.start()
            leftTuning.end()
            if (!tetris.rightHeld) tetris.toggleLockDelay2(false)
        }
    }

    fun softDrop(down: Boolean) {
        startSoftDrop = down
        if (down) tetris.stats.numInputs++
    }

    fun hardDrop() {
        if (!tetris.started) return
        tetris.hardDrop()
        tetris.stats.numInputs++
    }

    fun rotateClockwise() {
        if (!tetris.started) return
        tetris.stats.numInputs++
        tetris.onRotate()
        tetris.currPiece?.rotate(Rotation.Clockwise)
    }

    fun rotateCounterClockwise() {
        if (!tetris.started) return
        tetris.onRotate()
        tetris.stats.numInputs++
        tetris.currPiece?.rotate(Rotation.Counterclockwise)
    }

    fun rotate180() {
        if (!tetris.started) return
        tetris.onRotate()
        tetris.stats.numInputs++
        tetris.currPiece?.rotate(Rotation.OneEighty)
    }

    fun onHold() {
        if (!tetris.started) return
        tetris.stats.numInputs++
        tetris.holdCurrPiece()
    }

    fun onRestart() {

    }
}