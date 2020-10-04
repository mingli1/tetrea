package com.tetrea.game.extension

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.tetrea.game.util.RelativePosition
import com.tetrea.game.util.RelativeTo

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

fun Actor.onClick(down: () -> Unit = {}, up: () -> Unit = {}, enter: () -> Unit, exit: () -> Unit) {
    addListener(object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            down()
            return true
        }
        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
            up()
        }
        override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
            enter()
        }
        override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
            exit()
        }
    })
}

fun Actor.onTap(block: () -> Unit) {
    addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            block()
        }
    })
}

fun Actor.setRelativePosition(pos: RelativePosition, stage: Stage) {
    setPosition(
        when (pos.relX.relativeTo) {
            RelativeTo.XZero -> pos.relX.value
            else -> stage.width + pos.relX.value
        },
        when (pos.relY.relativeTo) {
            RelativeTo.YZero -> pos.relY.value
            else -> stage.height + pos.relY.value
        }
    )
}

fun Actor.onChange(block: () -> Unit) {
    addListener(object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            block()
        }
    })
}