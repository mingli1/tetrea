package com.tetrea.game.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

class BattleKeyInput(private val inputHandler: BattleInputHandler) : InputProcessor {

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> inputHandler.onRight(false)
            Input.Keys.LEFT -> inputHandler.onLeft(false)
            Input.Keys.DOWN -> inputHandler.softDrop(false)
        }
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.RIGHT -> inputHandler.onRight(true)
            Input.Keys.LEFT -> inputHandler.onLeft(true)
            Input.Keys.DOWN -> inputHandler.softDrop(true)
            Input.Keys.SPACE -> inputHandler.hardDrop()
            Input.Keys.UP -> inputHandler.rotateClockwise()
            Input.Keys.Z -> inputHandler.rotateCounterClockwise()
            Input.Keys.X -> inputHandler.rotate180()
            Input.Keys.SHIFT_LEFT -> inputHandler.onHold()
            Input.Keys.R -> inputHandler.onRestart()
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