package com.tetrea.game.scene.dialog

import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.Resources

private const val WIDTH = 200f
private const val BUTTON_WIDTH = 176f
private const val BUTTON_HEIGHT = 30f

class MessageDialog(
    message: String,
    private val dismiss: () -> Unit,
    res: Resources,
    buttonText: String = "OK"
) : BaseModalDialog(
    "",
    res.getNinePatchWindowStyle("gray_blue_bg"),
    res
) {

    val messageLabel = res.getLabel(text = message, fontScale = 1f).apply {
        setWrap(true)
        setAlignment(Align.center)
    }

    init {
        titleTable.remove()
        buttonTable.defaults().width(BUTTON_WIDTH)
        buttonTable.defaults().height(BUTTON_HEIGHT)

        contentTable.add(messageLabel).width(WIDTH).padBottom(12f).padLeft(12f).padRight(8f).align(Align.center)

        button(getButton(buttonText), "")
        buttonTable.pad(16f, 4f, 8f, 4f)
    }

    override fun result(obj: Any) {
        dismiss()
    }
}