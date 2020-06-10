package com.tetrea.game.extension

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

fun Actor.onClick(block: () -> Unit) {
    addListener(object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            block()
            return true
        }
    })
}

fun Actor.onClick(down: () -> Unit, up: () -> Unit) {
    addListener(object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            down()
            return true
        }
        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
            up()
        }
    })
}