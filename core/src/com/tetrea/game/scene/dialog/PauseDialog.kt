package com.tetrea.game.scene.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SoundManager
import com.tetrea.game.screen.BaseScreen
import com.tetrea.game.screen.BattleScreen
import com.tetrea.game.screen.GameMode
import com.tetrea.game.screen.TetrisScreen

private const val BUTTON_WIDTH = 150f
private const val BUTTON_HEIGHT = 36f
private const val BUTTON_PADDING = 16f
private const val TOP_BOTTOM_PADDING = 2f

private const val RESUME = "RESUME"
private const val RESTART = "RESTART"
private const val QUIT = "QUIT"

class PauseDialog(
    res: Resources,
    soundManager: SoundManager,
    private val screen: BaseScreen,
    windowStyleKey: String = "gray_blue_bg",
    buttonStyleKey: String = "gray_blue_button",
    private val gameMode: GameMode
) : BaseModalDialog(
    "PAUSED",
    res.getNinePatchWindowStyle(windowStyleKey),
    res,
    soundManager
) {

    private val confirmDialog: ConfirmDialog

    init {
        background.minHeight = if (gameMode == GameMode.Versus) 140f else 180f

        buttonTable.defaults().width(BUTTON_WIDTH).padLeft(BUTTON_PADDING).padBottom(TOP_BOTTOM_PADDING)
        buttonTable.defaults().height(BUTTON_HEIGHT).padRight(BUTTON_PADDING)

        button(getButton(RESUME, key = buttonStyleKey), RESUME)
        buttonTable.row()
        if (gameMode != GameMode.Versus) {
            button(getButton(RESTART, key = buttonStyleKey), RESTART)
            buttonTable.row()
        }
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
            RESUME -> screen.notifyResume()
            RESTART -> {
                hide(null)
                screen.notifyResume()
                screen.onRestart()
            }
            QUIT -> {
                if (gameMode == GameMode.Versus) {
                    hide(null)
                    currStage?.let { confirmDialog.show(it) }
                } else {
                    exit()
                }
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
        } else if (screen is TetrisScreen) {
            screen.onQuit()
        }
    }
}