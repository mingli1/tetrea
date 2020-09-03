package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.scene.SettingsScene

class SettingsScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table

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
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f).row()

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
}