package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GREEN
import com.tetrea.game.scene.dialog.ArcadeHelpDialog

private const val DIALOG_FADE_DURATION = 0.4f

class ArcadeScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table

    private lateinit var helpTable: Table
    private lateinit var helpBg: Image
    private val helpDialog = ArcadeHelpDialog(game.res)

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
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f)

        val helpButton = game.res.getNinePatchTextButton(
            text = "HELP",
            key = "arcade_green_button",
            colorUp = GAME_LIGHT_GREEN,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                showHelpDialog()
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(helpButton).top().right().size(76f, 28f).padTop(6f).row()

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
                    game.musicManager.inBattle = true
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
                    game.musicManager.inBattle = true
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
                    game.musicManager.inBattle = true
                }
            )).size(220f, 50f).padTop(16f).row()
            add(game.res.getButtonWithImage(
                text = "2ND PC",
                ninePatchKey = "cheese_button",
                imageKey = "cheese_button_icon",
                colorUp = Color(196 / 255f, 234 / 255f, 219 / 255f, 1f),
                onClick = {
                    navigateTo(TETRIS_SCREEN, mapOf(ARG_GAME_MODE to GameMode.SecondPC))
                    game.soundManager.onPrimaryButtonClicked()
                    game.musicManager.fadeOutBackgroundMusic()
                    game.musicManager.inBattle = true
                }
            )).size(220f, 50f).padTop(16f).row()
        }
        parentTable.add(bodyTable).top().padTop(24f).colspan(2).expandY()

        helpBg = Image(game.res.getTexture("black_150_opacity")).apply {
            setSize(this@ArcadeScreen.stage.width, this@ArcadeScreen.stage.height)
            isVisible = false
            onTap { hideHelpDialog() }
        }
        stage.addActor(helpBg)
        helpTable = Table().apply {
            setFillParent(true)
            isVisible = false
        }
        helpTable.add(helpDialog).size(220f, 280f)
        stage.addActor(helpTable)

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

    private fun showHelpDialog() {
        helpBg.isVisible = true
        helpBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
        helpTable.isVisible = true
        helpTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
    }

    private fun hideHelpDialog() {
        helpBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { helpBg.isVisible = false }))
        helpTable.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { helpTable.isVisible = false }))
    }
}