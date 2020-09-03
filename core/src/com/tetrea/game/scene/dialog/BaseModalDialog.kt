package com.tetrea.game.scene.dialog

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.onTap
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager

open class BaseModalDialog(
    title: String,
    windowStyle: WindowStyle,
    private val res: Resources,
    private val soundManager: SoundManager
) : Dialog(title, windowStyle) {

    private val bg = Image(res.getTexture("black_150_opacity"))
    protected var dismissable = false
    protected var currStage: Stage? = null

    init {
        isMovable = false
        titleLabel.run {
            setAlignment(Align.center)
            setFontScale(1.5f)
            color = GAME_YELLOW
            padTop(48f)
        }

        bg.onTap { if (dismissable) hide() }
    }

    override fun show(stage: Stage): Dialog {
        bg.setSize(stage.width, stage.height)
        bg.color.a = 1f
        this.color.a = 1f

        currStage = stage
        stage.addActor(bg)
        super.show(stage)
        return this
    }

    override fun hide(action: Action?) {
        super.hide(action)
        if (action == null) {
            bg.remove()
        } else {
            bg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f, Interpolation.smoother), Actions.removeActor()))
        }
    }

    override fun hide() {
        hide(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f, Interpolation.smoother), Actions.alpha(0f)))
    }

    override fun result(obj: Any) {
        soundManager.onSecondaryButtonClicked()
    }

    protected fun getButton(text: String, key: String = "gray_blue_button") = res.getNinePatchTextButton(
        text = text,
        key = key,
        colorUp = GAME_LIGHT_GRAY_BLUE,
        colorDown = Color.WHITE
    )
}