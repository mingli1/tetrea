package com.tetrea.game.scene.dialog

import com.tetrea.game.res.Resources

private const val SECONDARY_TAG = "SECONDARY"

class MessageDialog2(
    message: String,
    private val dismiss: () -> Unit,
    var secondary: () -> Unit,
    res: Resources,
    buttonText: String = "OK",
    button2Text: String = "OK"
) : MessageDialog(
    message,
    dismiss,
    res,
    buttonText
) {

    init {
        buttonTable.row()
        button(getButton(button2Text), SECONDARY_TAG)
    }

    override fun result(obj: Any) {
        if (obj == SECONDARY_TAG) secondary()
        else dismiss()
    }
}