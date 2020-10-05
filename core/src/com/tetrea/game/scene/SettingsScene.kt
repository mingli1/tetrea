package com.tetrea.game.scene

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.*
import com.tetrea.game.global.Settings
import com.tetrea.game.global.isAndroid
import com.tetrea.game.input.TetrisInputType
import com.tetrea.game.res.*
import com.tetrea.game.scene.dialog.MessageDialog
import com.tetrea.game.scene.dialog.MessageDialog2
import com.tetrea.game.util.RelativeTo
import java.util.*

class SettingsScene(
    private val res: Resources,
    private val settings: Settings,
    private val saveManager: SaveManager,
    private val musicManager: MusicManager,
    private val soundManager: SoundManager,
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
        soundManager,
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
        soundManager,
        "OK",
        "SWAP"
    )
    private var waitingToBind = false
    private var currentBinding: TetrisInputType? = null

    // Android button config screen
    private var androidBg: Image? = null
    private var scoreHeader: Image? = null
    private var tetrisBoard: Image? = null
    private var tetrisItemSlots: Image? = null
    private var enemyHpBar: Image? = null
    private val androidButtons = mutableMapOf<TetrisInputType, ImageButton>()
    private var doneButton: TextButton? = null

    init {
        createControlsSection()
        createTuningSection()
        createAudioSection()
        createMiscSection()

        if (isAndroid()) {
            val boardX = parentStage.width / 2 - (10 * SQUARE_SIZE) / 2f + 3
            val boardY = (parentStage.height / 2 - (20 * SQUARE_SIZE) / 2f) - 16f

            androidBg = Image(res.getTexture("home_screen_bg")).apply { setPosition(0f, 0f) }
            scoreHeader = Image(res.getTexture("score_header")).apply { setPosition(6f, parentStage.height - 24f) }
            tetrisBoard = Image(res.getTexture("tetris_board_bg")).apply { setPosition(boardX - 66, boardY -1) }
            enemyHpBar = Image(res.getTexture("enemy_hp_bar")).apply { setPosition(36f, parentStage.height - 54f) }

            TetrisInputType.values().dropLast(1).forEach {
                val button = ImageButton(res.getTetrisButtonStyle(it)).apply {
                    addListener(object : DragListener() {
                        override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                            moveBy(x - width / 2, y - height / 2)
                        }
                    })
                }
                androidButtons[it] = button
            }

            doneButton = res.getNinePatchTextButton(
                text = "DONE",
                key = "gray_blue_button",
                colorUp = GAME_LIGHT_GRAY_BLUE,
                colorDown = Color.WHITE
            ).apply {
                onTap { exitAndroidButtons() }
                setSize(76f, 30f)
                setPosition(8f, parentStage.height - 38f)
            }
        }
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
            ).apply { onTap {
                customizeAndroidButtons()
                soundManager.onPrimaryButtonClicked()
            } }).expandX().size(204f, 36f).padLeft(8f).padRight(8f).padBottom(8f)
        }

        add(controlsTable).width(220f).padBottom(16f).row()
    }

    private fun createTuningSection() {
        val tuningTable = Table().apply {
            background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))
        }

        tuningTable.add(
            res.getLabel(text = "TUNING", color = GAME_DARK_GRAY_BLUE, fontScale = 1f)
        ).expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        val dasLabel = res.getLabel(text = "DAS: ${settings.das.toMillis()}ms")
        tuningTable.add(dasLabel).expandX().left().padLeft(8f).row()
        val dasDesc = res.getLabel(
            text = "THE DELAY BEFORE A PIECE STARTS MOVING",
            fontScale = 0.5f,
            color = GAME_LIGHT_GRAY_BLUE
        )
        tuningTable.add(dasDesc).expand().left().padLeft(8f).padTop(2f).row()

        val dasSlider = res.getSlider(16f, 333f, 1f, "settings_slider_bg").apply {
            value = settings.das * 1000f
            onChange {
                dasLabel.setText("DAS: ${value.toInt()}ms")
                settings.das = value / 1000f
                if (!isDragging) saveManager.save()
            }
        }
        tuningTable.add(dasSlider).size(204f, 24f).padTop(4f).padBottom(4f).row()

        val arrLabel = res.getLabel(text = "ARR: ${settings.arr.toMillis()}ms")
        tuningTable.add(arrLabel).expandX().left().padLeft(8f).row()
        val arrDesc = res.getLabel(
            text = "HOW FAST A PIECE MOVES LEFT OR RIGHT",
            fontScale = 0.5f,
            color = GAME_LIGHT_GRAY_BLUE
        )
        tuningTable.add(arrDesc).expand().left().padLeft(8f).padTop(2f).row()

        val arrSlider = res.getSlider(0f, 83f, 1f, "settings_slider_bg").apply {
            value = settings.arr * 1000f
            onChange {
                arrLabel.setText("ARR: ${value.toInt()}ms")
                settings.arr = value / 1000f
                if (!isDragging) saveManager.save()
            }
        }
        tuningTable.add(arrSlider).size(204f, 24f).padTop(4f).padBottom(4f).row()

        val sdsLabel = res.getLabel(text = "SDS: ${settings.sds.toMillis()}ms")
        tuningTable.add(sdsLabel).expandX().left().padLeft(8f).row()
        val sdsDesc = res.getLabel(
            text = "HOW FAST A PIECE IS SOFT DROPPED",
            fontScale = 0.5f,
            color = GAME_LIGHT_GRAY_BLUE
        )
        tuningTable.add(sdsDesc).expand().left().padLeft(8f).padTop(2f).row()

        val sdsSlider = res.getSlider(0f, 100f, 1f, "settings_slider_bg").apply {
            value = settings.sds * 1000f
            onChange {
                sdsLabel.setText("SDS: ${value.toInt()}ms")
                settings.sds = value / 1000f
                if (!isDragging) saveManager.save()
            }
        }
        tuningTable.add(sdsSlider).size(204f, 24f).padTop(4f).padBottom(4f).row()

        add(tuningTable).width(220f).padBottom(16f).row()
    }

    private fun createAudioSection() {
        val audioTable = Table().apply {
            background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))
        }

        audioTable.add(
            res.getLabel(text = "AUDIO", color = GAME_DARK_GRAY_BLUE, fontScale = 1f)
        ).expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        val musicLabel = res.getLabel(text = "MUSIC VOLUME: ${settings.musicVolume.formatPercent()}")
        audioTable.add(musicLabel).expandX().left().padLeft(8f).row()
        val musicSlider = res.getSlider(0f, 100f, 1f, "settings_slider_bg").apply {
            value = settings.musicVolume * 100f
            onChange {
                musicLabel.setText("MUSIC VOLUME: ${(value / 100f).formatPercent()}")
                settings.musicVolume = value / 100f
                if (!isDragging) {
                    saveManager.save()
                    musicManager.setVolume(settings.musicVolume)
                }
            }
        }
        audioTable.add(musicSlider).size(204f, 24f).padTop(4f).padBottom(4f).colspan(2).row()

        val soundLabel = res.getLabel(text = "SOUND VOLUME: ${settings.soundVolume.formatPercent()}")
        audioTable.add(soundLabel).expandX().left().padLeft(8f).row()
        val soundSlider = res.getSlider(0f, 100f, 1f, "settings_slider_bg").apply {
            value = settings.soundVolume * 100f
            onChange {
                soundLabel.setText("SOUND VOLUME: ${(value / 100f).formatPercent()}")
                settings.soundVolume = value / 100f
                if (!isDragging) saveManager.save()
            }
        }
        audioTable.add(soundSlider).size(204f, 24f).padTop(4f).padBottom(4f).colspan(2).row()

        val muteMusicLabel = res.getLabel("MUTE MUSIC", color = GAME_LIGHT_GRAY_BLUE)
        val muteMusicCheckBox = res.getCheckBox().apply {
            isChecked = settings.muteMusic
            onChange {
                soundManager.onCheckboxClicked()
                settings.muteMusic = this.isChecked
                saveManager.save()
                if (settings.muteMusic) musicManager.mute()
                else musicManager.unmute()
            }
        }
        audioTable.add(muteMusicLabel).expandX().padLeft(8f).left()
        audioTable.add(muteMusicCheckBox).expandX().padRight(8f).padBottom(4f).right().row()

        val muteSoundLabel = res.getLabel("MUTE SOUND", color = GAME_LIGHT_GRAY_BLUE)
        val muteSoundCheckBox = res.getCheckBox().apply {
            isChecked = settings.muteSound
            onChange {
                soundManager.onCheckboxClicked()
                settings.muteSound = this.isChecked
                saveManager.save()
            }
        }
        audioTable.add(muteSoundLabel).expandX().padLeft(8f).left()
        audioTable.add(muteSoundCheckBox).expandX().padRight(8f).padBottom(4f).right().row()

        add(audioTable).width(220f).padBottom(16f).row()
    }

    private fun createMiscSection() {
        val miscTable = Table().apply {
            background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))
        }

        miscTable.add(
            res.getLabel(text = "MISC", color = GAME_DARK_GRAY_BLUE, fontScale = 1f)
        ).expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        val showFpsLabel = res.getLabel("SHOW FPS", color = GAME_LIGHT_GRAY_BLUE)
        val showFpsCheckBox = res.getCheckBox().apply {
            isChecked = settings.showFps
            onChange {
                soundManager.onCheckboxClicked()
                settings.showFps = this.isChecked
                saveManager.save()
            }
        }
        miscTable.add(showFpsLabel).expandX().padLeft(8f).left()
        miscTable.add(showFpsCheckBox).expandX().padRight(8f).padBottom(4f).right()

        add(miscTable).width(220f).padBottom(24f).row()
    }

    private fun addKeyBinding(type: TetrisInputType) {
        val label = res.getLabel(text = type.str, color = GAME_LIGHT_GRAY_BLUE)
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
            soundManager.onPrimaryButtonClicked()

            keyBindDialog.messageLabel.setText("PRESS A KEY TO BIND TO ${type.str}")
            keyBindDialog.show(parentStage)
        }
        controlButtonMap[type] = keyButton

        controlsTable.add(label).expandX().left().padLeft(8f)
            .padBottom(if (type == TetrisInputType.Restart) 8f else 4f)
        controlsTable.add(keyButton).size(80f, 24f).expandX().right().padRight(8f)
            .padBottom(if (type == TetrisInputType.Restart) 8f else 4f).row()
    }

    private fun customizeAndroidButtons() {
        androidBg?.let { parentStage.addActor(it) }
        scoreHeader?.let { parentStage.addActor(it) }
        tetrisBoard?.let { parentStage.addActor(it) }
        tetrisItemSlots?.let { parentStage.addActor(it) }
        enemyHpBar?.let { parentStage.addActor(it) }

        TetrisInputType.values().dropLast(1).forEach {
            androidButtons[it]?.setRelativePosition(settings.androidBindings[it]!!, parentStage)
            parentStage.addActor(androidButtons[it])
        }
        doneButton?.let { parentStage.addActor(it) }
    }

    private fun exitAndroidButtons() {
        androidBg?.remove()
        scoreHeader?.remove()
        tetrisBoard?.remove()
        tetrisItemSlots?.remove()
        enemyHpBar?.remove()

        TetrisInputType.values().dropLast(1).forEach {
            val button = androidButtons[it]!!

            settings.androidBindings[it]!!.relX.relativeTo =
                if (button.x < parentStage.width / 2) RelativeTo.XZero else RelativeTo.StageWidth
            settings.androidBindings[it]!!.relX.value =
                if (button.x < parentStage.width / 2) button.x else -(parentStage.width - button.x)
            settings.androidBindings[it]!!.relY.relativeTo =
                if (button.y < parentStage.height / 2) RelativeTo.YZero else RelativeTo.StageHeight
            settings.androidBindings[it]!!.relY.value =
                if (button.y < parentStage.height / 2) button.y else -(parentStage.height - button.y)

            button.remove()
        }
        doneButton?.remove()

        saveManager.save()
    }
}
