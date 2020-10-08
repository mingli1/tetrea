package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.scene.SettingsScene
import com.tetrea.game.scene.dialog.SettingsAboutDialog

private const val DIALOG_FADE_DURATION = 0.4f

class SettingsScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table

    private lateinit var aboutTable: Table
    private lateinit var aboutBg: Image
    private val aboutDialog = SettingsAboutDialog(game.res)

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("gray_blue_bg"))
            add(game.res.getLabel("SETTINGS", fontScale = 1f)).padBottom(2f).row()
        }
        parentTable.add(headerTable).size(220f, 30f).top().padTop(16f).colspan(2).row()

        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "gray_blue_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                navigateTo(HOME_SCREEN)
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f)

        val helpButton = game.res.getNinePatchTextButton(
            text = "ABOUT",
            key = "gray_blue_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                showAboutDialog()
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(helpButton).top().right().size(76f, 28f).padTop(6f).row()

        val settingsTable = SettingsScene(
            game.res,
            game.settings,
            game.saveManager,
            game.musicManager,
            game.soundManager,
            stage
        )
        val scrollPane = ScrollPane(settingsTable).apply {
            setOverscroll(false, false)
            setCancelTouchFocus(false)
            fadeScrollBars = false
            layout()
        }
        parentTable.add(scrollPane).expandY().colspan(2).top().padTop(24f)

        aboutBg = Image(game.res.getTexture("black_150_opacity")).apply {
            setSize(this@SettingsScreen.stage.width, this@SettingsScreen.stage.height)
            isVisible = false
            onTap { hideAboutDialog() }
        }
        stage.addActor(aboutBg)
        aboutTable = Table().apply {
            setFillParent(true)
            isVisible = false
        }
        aboutTable.add(aboutDialog).size(220f, 310f)
        stage.addActor(aboutTable)

        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        if (transition == Transition.None) game.batch.color = Color.WHITE

        game.batch.draw(game.res.getTexture("home_screen_bg"), 0f, 0f)
        game.batch.draw(game.res.getTexture("settings_screen_overlay"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun showAboutDialog() {
        aboutBg.isVisible = true
        aboutBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
        aboutTable.isVisible = true
        aboutTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
    }

    private fun hideAboutDialog() {
        aboutBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { aboutBg.isVisible = false }))
        aboutTable.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { aboutTable.isVisible = false }))
    }
}