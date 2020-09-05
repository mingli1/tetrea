package com.tetrea.game.input

import com.badlogic.gdx.InputProcessor
import com.tetrea.game.global.Settings

class TetrisKeyInput(
    private val settings: Settings,
    private val inputHandler: TetrisInputHandler
) : InputProcessor {

    override fun keyUp(keycode: Int): Boolean {
        when (settings.keyBindings[keycode]) {
            TetrisInputType.Left -> inputHandler.onLeft(false)
            TetrisInputType.Right -> inputHandler.onRight(false)
            TetrisInputType.SoftDrop -> inputHandler.softDrop(false)
        }
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (settings.keyBindings[keycode]) {
            TetrisInputType.Left -> inputHandler.onLeft(true)
            TetrisInputType.Right -> inputHandler.onRight(true)
            TetrisInputType.SoftDrop -> inputHandler.softDrop(true)
            TetrisInputType.HardDrop -> inputHandler.hardDrop()
            TetrisInputType.RotateCW -> inputHandler.rotateClockwise()
            TetrisInputType.RotateCCW -> inputHandler.rotateCounterClockwise()
            TetrisInputType.Rotate180 -> inputHandler.rotate180()
            TetrisInputType.Hold -> inputHandler.onHold()
            TetrisInputType.Pause -> inputHandler.onPause()
            TetrisInputType.Restart -> inputHandler.onRestart()
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = true

    override fun keyTyped(character: Char): Boolean = true

    override fun scrolled(amount: Int): Boolean = true

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true
}