package com.tetrea.game.scene.dialog

import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager

private const val WIDTH = 200f
private const val BUTTON_WIDTH = 176f
private const val BUTTON_HEIGHT = 30f

open class MessageDialog(
    message: String,
    private val dismiss: () -> Unit,
    res: Resources,
    soundManager: SoundManager,
    buttonText: String = "OK",
    title: String = "",
    windowStyleKey: String = "gray_blue_bg",
    buttonStyleKey: String = "gray_blue_button"
) : BaseModalDialog(
    title,
    res.getNinePatchWindowStyle(windowStyleKey),
    res,
    soundManager
) {

    val messageLabel = res.getLabel(text = message, fontScale = if (title.isEmpty()) 1f else 0.75f).apply {
        setWrap(true)
        setAlignment(Align.center)
    }

    init {
        if (title.isEmpty()) titleTable.remove()
        buttonTable.defaults().width(BUTTON_WIDTH)
        buttonTable.defaults().height(BUTTON_HEIGHT)

        contentTable.add(messageLabel).width(WIDTH).padLeft(12f).padRight(8f).align(Align.center)

        button(getButton(buttonText, key = buttonStyleKey), "")
        buttonTable.pad(16f, 4f, 8f, 4f)
    }

    override fun result(obj: Any) {
        super.result(obj)
        dismiss()
    }
}