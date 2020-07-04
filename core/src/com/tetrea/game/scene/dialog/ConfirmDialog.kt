package com.tetrea.game.scene.dialog

import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.Resources

private const val WINDOW_MIN_HEIGHT = 120f
private const val BUTTON_WIDTH = 80f
private const val BUTTON_HEIGHT = 36f
private const val WIDTH = 220f
private const val AFFIRMATIVE_TAG = "AFFIRMATIVE_TAG"
private const val DISMISSIVE_TAG = "DISMISSIVE_TAG"

class ConfirmDialog(
    title: String,
    message: String,
    private val affirmative: () -> Unit,
    private val dismissive: () -> Unit,
    res: Resources,
    affirmativeText: String = "YES",
    dismissiveText: String = "NO"
) : BaseModalDialog(
    title,
    res.getNinePatchWindowStyle("gray_blue_bg"),
    res
) {

    init {
        titleLabel.setFontScale(1.5f)
        background.minHeight = WINDOW_MIN_HEIGHT

        buttonTable.defaults().width(BUTTON_WIDTH)
        buttonTable.defaults().height(BUTTON_HEIGHT)

        val messageLabel = res.getLabel(text = message, fontScale = 1f).apply {
            setWrap(true)
            setAlignment(Align.center)
        }
        contentTable.add(messageLabel).width(WIDTH).fill().space(8f).padLeft(8f).padRight(8f)

        button(getButton(affirmativeText), AFFIRMATIVE_TAG)
        button(getButton(dismissiveText), DISMISSIVE_TAG)

        buttonTable.pad(16f, 4f, 8f, 4f)
    }

    override fun result(obj: Any) {
        if (obj == AFFIRMATIVE_TAG) affirmative()
        else if (obj == DISMISSIVE_TAG) dismissive()
    }
}