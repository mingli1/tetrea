package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GREEN

class ArcadeScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("green_bg"))
            add(game.res.getLabel("SELECT A GAME MODE", fontScale = 1f))
        }
        parentTable.add(headerTable).size(220f, 30f).top().padTop(16f).colspan(2).row()

        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "arcade_green_button",
            colorUp = GAME_LIGHT_GREEN,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                navigateTo(HOME_SCREEN)
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f).row()

        val bodyTable = Table().apply {
            add(game.res.getButtonWithImage(
                text = "SPRINT",
                ninePatchKey = "sprint_button",
                imageKey = "arcade_button_icon",
                colorUp = Color(206 / 255f, 234 / 255f, 219 / 255f, 1f),
                onClick = {
                    navigateTo(TETRIS_SCREEN, mapOf(ARG_GAME_MODE to GameMode.Sprint))
                    game.soundManager.onPrimaryButtonClicked()
                    game.musicManager.fadeOutBackgroundMusic()
                }
            )).size(220f, 50f).row()
            add(game.res.getButtonWithImage(
                text = "ULTRA",
                ninePatchKey = "ultra_button",
                imageKey = "ultra_button_icon",
                colorUp = Color(206 / 255f, 234 / 255f, 219 / 255f, 1f),
                onClick = {
                    navigateTo(TETRIS_SCREEN, mapOf(ARG_GAME_MODE to GameMode.Ultra))
                    game.soundManager.onPrimaryButtonClicked()
                    game.musicManager.fadeOutBackgroundMusic()
                }
            )).size(220f, 50f).padTop(16f).row()
            add(game.res.getButtonWithImage(
                text = "CHEESE",
                ninePatchKey = "cheese_button",
                imageKey = "cheese_button_icon",
                colorUp = Color(206 / 255f, 234 / 255f, 219 / 255f, 1f),
                onClick = {
                    navigateTo(TETRIS_SCREEN, mapOf(ARG_GAME_MODE to GameMode.Cheese))
                    game.soundManager.onPrimaryButtonClicked()
                    game.musicManager.fadeOutBackgroundMusic()
                }
            )).size(220f, 50f).padTop(16f).row()
        }
        parentTable.add(bodyTable).top().padTop(24f).colspan(2).expandY()

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
        game.batch.draw(game.res.getTexture("arcade_screen_overlay"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }
}