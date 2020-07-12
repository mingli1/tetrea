package com.tetrea.game.scene

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.Settings
import com.tetrea.game.global.isAndroid
import com.tetrea.game.input.TetrisInputType
import com.tetrea.game.res.GAME_DARK_GRAY_BLUE
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.Resources
import com.tetrea.game.res.SaveManager
import com.tetrea.game.scene.dialog.MessageDialog
import com.tetrea.game.scene.dialog.MessageDialog2
import java.util.*

class SettingsScene(
    private val res: Resources,
    private val settings: Settings,
    private val saveManager: SaveManager,
    private val parentStage: Stage
) : Table() {

    private lateinit var controlsTable: Table
    private val controlButtonMap = mutableMapOf<TetrisInputType, TextButton>()
    private val keyBindDialog = MessageDialog(
        "",
        {
            waitingToBind = false
            currentBinding = null
        },
        res,
        "CANCEL"
    ).apply {
        if (!isAndroid()) {
            addListener(object : InputListener() {
                override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                    bind(keycode)
                    return true
                }
            })
        }
    }
    private val keyBindExistsDialog = MessageDialog2(
        "",
        {},
        {},
        res,
        "OK",
        "SWAP"
    )
    private var waitingToBind = false
    private var currentBinding: TetrisInputType? = null

    init {
        createControlsSection()
        createTuningSection()
    }

    fun bind(keycode: Int) {
        if (waitingToBind) {
            keyBindDialog.hide(null)
            currentBinding?.let {
                if (settings.keyBindingsInverse[it] == keycode) return@let
                if (settings.keyBindings.containsKey(keycode)) {
                    val existingBinding = settings.keyBindings[keycode]!!
                    keyBindExistsDialog.messageLabel.setText(
                        "${Input.Keys.toString(keycode).toUpperCase(Locale.ROOT)} IS ALREADY BINDED TO ${existingBinding.str}."
                    )
                    keyBindExistsDialog.secondary = {
                        val swap = settings.keyBindingsInverse[existingBinding]!!
                        settings.keyBindingsInverse[existingBinding] = settings.keyBindingsInverse[it]!!
                        settings.keyBindingsInverse[it] = swap

                        settings.keyBindings[settings.keyBindingsInverse[existingBinding]!!] = existingBinding
                        settings.keyBindings[settings.keyBindingsInverse[it]!!] = it

                        controlButtonMap[existingBinding]?.label?.setText(
                            Input.Keys.toString(settings.keyBindingsInverse[existingBinding] ?: 0).toUpperCase(Locale.ROOT)
                        )
                        controlButtonMap[it]?.label?.setText(
                            Input.Keys.toString(settings.keyBindingsInverse[it] ?: 0).toUpperCase(Locale.ROOT)
                        )
                        saveManager.save()
                    }
                    keyBindExistsDialog.show(parentStage)

                    return@let
                }

                settings.keyBindings.remove(settings.keyBindingsInverse[it])
                settings.keyBindings[keycode] = it
                settings.keyBindingsInverse[it] = keycode

                controlButtonMap[it]?.label?.setText(
                    Input.Keys.toString(settings.keyBindingsInverse[it] ?: 0).toUpperCase(Locale.ROOT)
                )
                saveManager.save()
            }
            waitingToBind = false
            currentBinding = null
        }
    }

    private fun createControlsSection() {
        controlsTable = Table().apply {
            background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))
        }

        controlsTable.add(
            res.getLabel(text = "CONTROLS", color = GAME_DARK_GRAY_BLUE, fontScale = 1f)
        ).expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        if (!isAndroid()) {
            TetrisInputType.values().forEach { addKeyBinding(it) }
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
            waitingToBind = true
            currentBinding = type

            keyBindDialog.messageLabel.setText("PRESS A KEY TO BIND TO ${type.str}")
            keyBindDialog.show(parentStage)
        }
        controlButtonMap[type] = keyButton

        controlsTable.add(label).expandX().left().padLeft(8f)
            .padBottom(if (type == TetrisInputType.Pause) 8f else 4f)
        controlsTable.add(keyButton).size(80f, 24f).expandX().right().padRight(8f)
            .padBottom(if (type == TetrisInputType.Pause) 8f else 4f).row()
    }
}