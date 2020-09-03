package com.tetrea.game.scene.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager
import com.tetrea.game.screen.BaseScreen
import com.tetrea.game.screen.BattleScreen

private const val WINDOW_MIN_HEIGHT = 180f
private const val BUTTON_WIDTH = 150f
private const val BUTTON_HEIGHT = 36f
private const val BUTTON_PADDING = 16f
private const val TOP_BOTTOM_PADDING = 2f

private const val RESUME = "RESUME"
private const val SETTINGS = "SETTINGS"
private const val QUIT = "QUIT"

class PauseDialog(
    res: Resources,
    soundManager: SoundManager,
    private val screen: BaseScreen,
    windowStyleKey: String = "gray_blue_bg",
    buttonStyleKey: String = "gray_blue_button"
) : BaseModalDialog(
    "PAUSED",
    res.getNinePatchWindowStyle(windowStyleKey),
    res,
    soundManager
) {

    private val confirmDialog: ConfirmDialog

    init {
        background.minHeight = WINDOW_MIN_HEIGHT

        buttonTable.defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING).padBottom(TOP_BOTTOM_PADDING)
        buttonTable.defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING)

        button(getButton(RESUME, key = buttonStyleKey), RESUME)
        buttonTable.row()
        button(getButton(SETTINGS, key = buttonStyleKey), SETTINGS)
        buttonTable.row()
        button(getButton(QUIT, key = buttonStyleKey), QUIT)

        confirmDialog = ConfirmDialog(
            "QUIT MATCH",
            "ARE YOU SURE YOU WANT TO QUIT THIS MATCH? THIS WILL COUNT AS A LOSS.",
            this::exit,
            this::dismissConfirmDialog,
            res,
            soundManager,
            windowStyleKey = windowStyleKey,
            buttonStyleKey = buttonStyleKey
        )

        buttonTable.padBottom(16f)
    }

    override fun show(stage: Stage): Dialog {
        if (currStage?.actors?.contains(confirmDialog) == true) return this
        return super.show(stage)
    }

    override fun result(obj: Any) {
        super.result(obj)
        when (obj) {
            RESUME, SETTINGS -> screen.notifyResume()
            QUIT -> {
                hide(null)
                currStage?.let { confirmDialog.show(it) }
            }
        }
    }

    private fun dismissConfirmDialog() {
        confirmDialog.hide(null)
        currStage?.let { show(it) }
    }

    private fun exit() {
        screen.notifyResume()
        confirmDialog.hide(null)
        hide(null)

        if (screen is BattleScreen) {
            screen.onBattleQuit()
        }
    }
}