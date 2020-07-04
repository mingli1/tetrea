package com.tetrea.game.scene.dialog

import com.badlogic.gdx.graphics.Color
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.Resources
import com.tetrea.game.screen.BaseScreen

private const val WINDOW_MIN_HEIGHT = 200f
private const val BUTTON_WIDTH = 150f
private const val BUTTON_HEIGHT = 36f
private const val BUTTON_PADDING = 16f
private const val TOP_BOTTOM_PADDING = 8f

private const val RESUME = "RESUME"
private const val SETTINGS = "SETTINGS"
private const val QUIT = "QUIT"

class PauseDialog(
    private val res: Resources,
    private val screen: BaseScreen
) : BaseModalDialog(
    "PAUSED",
    res.getNinePatchWindowStyle("gray_blue_bg"),
    res
) {

    init {
        background.minHeight = WINDOW_MIN_HEIGHT

        buttonTable.defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING).padBottom(TOP_BOTTOM_PADDING)
        buttonTable.defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING)

        button(getButton(RESUME), RESUME)
        buttonTable.row()
        button(getButton(SETTINGS), SETTINGS)
        buttonTable.row()
        button(getButton(QUIT), QUIT)
    }

    override fun result(obj: Any) {
        when (obj) {
            RESUME, SETTINGS -> screen.notifyResume()
        }
    }

    private fun getButton(text: String) = res.getNinePatchTextButton(
        text = text,
        key = "gray_blue_button",
        colorUp = GAME_LIGHT_GRAY_BLUE,
        colorDown = Color.WHITE
    )
}