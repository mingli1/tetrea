package com.tetrea.game.scene.dialog

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.onTap
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources

open class BaseModalDialog(
    title: String,
    windowStyle: WindowStyle,
    res: Resources
) : Dialog(title, windowStyle) {

    private val bg = Image(res.getTexture("black_150_opacity"))
    protected var dismissable = false

    init {
        isMovable = false
        titleLabel.run {
            setAlignment(Align.center)
            setFontScale(2f)
            color = GAME_YELLOW
            padTop(64f)
        }

        bg.onTap { if (dismissable) hide() }
    }

    override fun show(stage: Stage): Dialog {
        bg.setSize(stage.width, stage.height)
        bg.color.a = 1f
        this.color.a = 1f
        stage.addActor(bg)
        super.show(stage)
        return this
    }

    override fun hide() {
        hide(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f, Interpolation.smoother), Actions.alpha(0f)))
        bg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f, Interpolation.smoother), Actions.removeActor()))
    }
}