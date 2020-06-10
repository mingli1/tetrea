package com.tetrea.game.input

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.tetrea.game.extension.onClick
import com.tetrea.game.res.*

class TetrisAndroidInput(
    stage: Stage,
    tetrisInputHandler: TetrisInputHandler,
    res: Resources
) {

    init {
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_LEFT)).apply {
            onClick(
                { tetrisInputHandler.onLeft(true) },
                { tetrisInputHandler.onLeft(false) }
            )
            setPosition(stage.width - 150f, 16f)
            setPosition(10f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_RIGHT)).apply {
            onClick(
                { tetrisInputHandler.onRight(true) },
                { tetrisInputHandler.onRight(false) }
            )
            setPosition(60f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_SOFTDROP)).apply {
            onClick(
                { tetrisInputHandler.softDrop(true) },
                { tetrisInputHandler.softDrop(false) }
            )
            setPosition(110f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_HARDDROP)).apply {
            onClick { tetrisInputHandler.hardDrop() }
            setPosition(stage.width - 50f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_CW)).apply {
            onClick { tetrisInputHandler.rotateClockwise() }
            setPosition(stage.width - 50f, 66f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_CCW)).apply {
            onClick { tetrisInputHandler.rotateCounterClockwise() }
            setPosition(stage.width - 100f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_180)).apply {
            onClick { tetrisInputHandler.rotate180() }
            setPosition(60f, 66f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_HOLD)).apply {
            onClick { tetrisInputHandler.onHold() }
            setPosition(stage.width - 100f, 66f)
        })
    }
}