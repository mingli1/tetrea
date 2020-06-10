package com.tetrea.game.input

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.tetrea.game.V_WIDTH
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
            setPosition(V_WIDTH - 150f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_RIGHT)).apply {
            onClick(
                { tetrisInputHandler.onRight(true) },
                { tetrisInputHandler.onRight(false) }
            )
            setPosition(V_WIDTH - 50f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_SOFTDROP)).apply {
            onClick(
                { tetrisInputHandler.softDrop(true) },
                { tetrisInputHandler.softDrop(false) }
            )
            setPosition(V_WIDTH - 100f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_HARDDROP)).apply {
            onClick { tetrisInputHandler.hardDrop() }
            setPosition(60f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_CW)).apply {
            onClick { tetrisInputHandler.rotateClockwise() }
            setPosition(V_WIDTH - 100f, 66f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_CCW)).apply {
            onClick { tetrisInputHandler.rotateCounterClockwise() }
            setPosition(10f, 16f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_ROTATE_180)).apply {
            onClick { tetrisInputHandler.rotate180() }
            setPosition(60f, 66f)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TETRIS_BUTTON_HOLD)).apply {
            onClick { tetrisInputHandler.onHold() }
            setPosition(10f, 66f)
        })
    }
}