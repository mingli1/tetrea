package com.tetrea.game.scene

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.Settings
import com.tetrea.game.global.isAndroid
import com.tetrea.game.input.TetrisInputType
import com.tetrea.game.res.GAME_DARK_GRAY_BLUE
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.Resources
import com.tetrea.game.scene.dialog.MessageDialog
import java.util.*

class SettingsScene(
    private val res: Resources,
    private val settings: Settings,
    private val parentStage: Stage
) : Table() {

    private lateinit var controlsTable: Table
    private val keyBindDialog = MessageDialog(
        "",
        {},
        res,
        "CANCEL"
    )

    init {
        createControlsSection()
        createTuningSection()
    }

    private fun createControlsSection() {
        controlsTable = Table().apply {
            background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))
        }

        controlsTable.add(
            res.getLabel(text = "CONTROLS", color = GAME_DARK_GRAY_BLUE, fontScale = 1f)
        ).expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        if (!isAndroid()) {
            addKeyBinding(TetrisInputType.Left)
            addKeyBinding(TetrisInputType.Right)
            addKeyBinding(TetrisInputType.SoftDrop)
            addKeyBinding(TetrisInputType.HardDrop)
            addKeyBinding(TetrisInputType.RotateCW)
            addKeyBinding(TetrisInputType.RotateCCW)
            addKeyBinding(TetrisInputType.Rotate180)
            addKeyBinding(TetrisInputType.Hold)
            addKeyBinding(TetrisInputType.Pause)
        } else {
            controlsTable.add(res.getNinePatchTextButton(
                text = "MOVE BUTTONS",
                key = "gray_blue_button",
                colorUp = Color.WHITE,
                colorDown = Color.WHITE
            )).expandX().size(204f, 36f).padLeft(8f).padRight(8f).padBottom(8f)
        }

        add(controlsTable).width(220f).row()
    }

    private fun createTuningSection() {

    }

    private fun addKeyBinding(type: TetrisInputType) {
        val label = res.getLabel(text = type.str, color = GAME_LIGHT_GRAY_BLUE, fontScale = 1f)
        val key = Input.Keys.toString(settings.keyBindingsInverse[type] ?: 0).toUpperCase(Locale.ROOT)
        val keyButton = res.getNinePatchTextButton(
            text = key,
            key = "gray_blue_button",
            colorUp = Color.WHITE,
            colorDown = Color.WHITE
        )
        keyButton.onTap {
            keyBindDialog.messageLabel.setText("PRESS A KEY TO BIND TO ${type.str}")
            keyBindDialog.show(parentStage)
        }
        controlsTable.add(label).expandX().left().padLeft(8f)
            .padBottom(if (type == TetrisInputType.Pause) 8f else 4f)
        controlsTable.add(keyButton).size(80f, 24f).expandX().right().padRight(8f)
            .padBottom(if (type == TetrisInputType.Pause) 8f else 4f).row()
    }
}