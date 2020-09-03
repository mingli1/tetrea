package com.tetrea.game.scene.dialog

import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager

private const val SECONDARY_TAG = "SECONDARY"

class MessageDialog2(
    message: String,
    private val dismiss: () -> Unit,
    var secondary: () -> Unit,
    res: Resources,
    private val soundManager: SoundManager,
    buttonText: String = "OK",
    button2Text: String = "OK"
) : MessageDialog(
    message,
    dismiss,
    res,
    soundManager,
    buttonText
) {

    init {
        buttonTable.row()
        button(getButton(button2Text), SECONDARY_TAG)
    }

    override fun result(obj: Any) {
        soundManager.onSecondaryButtonClicked()
        if (obj == SECONDARY_TAG) secondary()
        else dismiss()
    }
}