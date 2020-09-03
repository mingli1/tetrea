package com.tetrea.game.scene.dialog

import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager

private const val WINDOW_MIN_HEIGHT = 120f
private const val BUTTON_WIDTH = 80f
private const val BUTTON_HEIGHT = 30f
private const val WIDTH = 200f
private const val AFFIRMATIVE_TAG = "AFFIRMATIVE_TAG"
private const val DISMISSIVE_TAG = "DISMISSIVE_TAG"

class ConfirmDialog(
    title: String,
    message: String,
    private val affirmative: () -> Unit,
    private val dismissive: () -> Unit,
    res: Resources,
    soundManager: SoundManager,
    affirmativeText: String = "YES",
    dismissiveText: String = "NO",
    windowStyleKey: String = "gray_blue_bg",
    buttonStyleKey: String = "gray_blue_button"
) : BaseModalDialog(
    title,
    res.getNinePatchWindowStyle(windowStyleKey),
    res,
    soundManager
) {

    init {
        titleLabel.setFontScale(1.5f)
        background.minHeight = WINDOW_MIN_HEIGHT

        buttonTable.defaults().width(BUTTON_WIDTH)
        buttonTable.defaults().height(BUTTON_HEIGHT)

        val messageLabel = res.getLabel(text = message).apply {
            setWrap(true)
            setAlignment(Align.center)
        }
        contentTable.add(messageLabel).width(WIDTH).fill().space(8f).padLeft(12f).padRight(8f).align(Align.center)

        button(getButton(affirmativeText, key = buttonStyleKey), AFFIRMATIVE_TAG)
        button(getButton(dismissiveText, key = buttonStyleKey), DISMISSIVE_TAG)

        buttonTable.pad(16f, 4f, 8f, 4f)
    }

    override fun result(obj: Any) {
        super.result(obj)
        if (obj == AFFIRMATIVE_TAG) affirmative()
        else if (obj == DISMISSIVE_TAG) dismissive()
    }
}