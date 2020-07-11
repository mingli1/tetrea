package com.tetrea.game.input

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.tetrea.game.extension.onClick
import com.tetrea.game.extension.setRelativePosition
import com.tetrea.game.global.Settings
import com.tetrea.game.res.*

class TetrisAndroidInput(
    stage: Stage,
    tetrisInputHandler: TetrisInputHandler,
    res: Resources,
    settings: Settings
) {

    init {
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.Left)).apply {
            onClick(
                { tetrisInputHandler.onLeft(true) },
                { tetrisInputHandler.onLeft(false) }
            )
            setRelativePosition(settings.androidBindings[TetrisInputType.Left]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.Right)).apply {
            onClick(
                { tetrisInputHandler.onRight(true) },
                { tetrisInputHandler.onRight(false) }
            )
            setRelativePosition(settings.androidBindings[TetrisInputType.Right]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.SoftDrop)).apply {
            onClick(
                { tetrisInputHandler.softDrop(true) },
                { tetrisInputHandler.softDrop(false) }
            )
            setRelativePosition(settings.androidBindings[TetrisInputType.SoftDrop]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.HardDrop)).apply {
            onClick { tetrisInputHandler.hardDrop() }
            setRelativePosition(settings.androidBindings[TetrisInputType.HardDrop]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.RotateCW)).apply {
            onClick { tetrisInputHandler.rotateClockwise() }
            setRelativePosition(settings.androidBindings[TetrisInputType.RotateCW]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.RotateCCW)).apply {
            onClick { tetrisInputHandler.rotateCounterClockwise() }
            setRelativePosition(settings.androidBindings[TetrisInputType.RotateCCW]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.Rotate180)).apply {
            onClick { tetrisInputHandler.rotate180() }
            setRelativePosition(settings.androidBindings[TetrisInputType.Rotate180]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.Hold)).apply {
            onClick { tetrisInputHandler.onHold() }
            setRelativePosition(settings.androidBindings[TetrisInputType.Hold]!!, stage)
        })
        stage.addActor(ImageButton(res.getTetrisButtonStyle(TetrisInputType.Pause)).apply {
            onClick { tetrisInputHandler.onPause() }
            setRelativePosition(settings.androidBindings[TetrisInputType.Pause]!!, stage)
        })
    }
}