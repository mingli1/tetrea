package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.TITLE_LETTER_WIDTH
import com.tetrea.game.scene.dialog.MessageDialog

private const val TITLE_TOTAL_WIDTH = 216f
private const val TITLE_LETTER_DELAY = 0.2f
private const val TITLE_LETTER_DURATION = 0.4f
private const val BUTTON_WIDTH = 205f
private const val BUTTON_HEIGHT = 50f
private const val BUTTON_PADDING = 16f

class HomeScreen(game: TetreaGame) : BaseScreen(game) {

    private val titleInitX = stage.width + 1
    private val titleFinalX = stage.width / 2 - TITLE_TOTAL_WIDTH / 2
    private val titleY = stage.height - 96f
    private val letters = Array(6) {
        Image(game.res.titleLetters[it]).apply {
            setPosition(titleInitX + it * TITLE_LETTER_WIDTH, titleY)
        }
    }
    private val buttonX = stage.width / 2 - BUTTON_WIDTH / 2
    private val buttonTableY = stage.height / 2 + BUTTON_HEIGHT / 2

    override fun show() {
        super.show()

        letters.forEachIndexed { index, image ->
            stage.addActor(image)
            image.addAction(Actions.sequence(
                Actions.delay(TITLE_LETTER_DELAY * index),
                Actions.moveTo(titleFinalX + index * TITLE_LETTER_WIDTH, titleY, TITLE_LETTER_DURATION, Interpolation.pow2In)
            ))
        }

        val versusButton = getButton(
            text = "VERSUS",
            ninePatchKey = "versus_button",
            imageKey = "versus_button_icon",
            colorUp = Color(216 / 255f, 206 / 255f, 1f, 1f),
            y = buttonTableY,
            onClick = { navigateTo(VERSUS_SELECT_SCREEN) }
        )
        stage.addActor(versusButton)

        val arcadeButton = getButton(
            text = "ARCADE",
            ninePatchKey = "arcade_button",
            imageKey = "arcade_button_icon",
            colorUp = Color(206 / 255f, 234 / 255f, 219 / 255f, 1f),
            y = buttonTableY - (BUTTON_HEIGHT + BUTTON_PADDING),
            onClick = {}
        )
        stage.addActor(arcadeButton)

        val profileButton = getButton(
            text = "PROFILE",
            ninePatchKey = "profile_button",
            imageKey = "profile_button_icon",
            colorUp = Color(234 / 255f, 211 / 255f, 204 / 255f, 1f),
            y = buttonTableY - 2 * (BUTTON_HEIGHT + BUTTON_PADDING),
            onClick = {}
        )
        stage.addActor(profileButton)

        val settingsButton = getButton(
            text = "SETTINGS",
            ninePatchKey = "settings_button",
            imageKey = "settings_button_icon",
            colorUp = Color(201 / 255f, 210 / 255f, 234 / 255f, 1f),
            y = buttonTableY - 3 * (BUTTON_HEIGHT + BUTTON_PADDING),
            onClick = { navigateTo(SETTINGS_SCREEN) }
        )
        stage.addActor(settingsButton)

        if (game.player.quitDuringBattle) {
            MessageDialog(
                title = "MATCH EXITED",
                message = "YOU CLOSED THE APP DURING A BATTLE SO THE MATCH WILL COUNT AS A LOSS.",
                res = game.res,
                dismiss = {
                    game.player.quitDuringBattle = false
                    game.saveManager.save()
                }
            ).show(stage)
        }

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
        game.batch.draw(game.res.getTexture("home_screen_overlay"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun getButton(
        text: String,
        ninePatchKey: String,
        imageKey: String,
        colorUp: Color,
        y: Float,
        onClick: () -> Unit
    ): ImageTextButton {
        return game.res.getNinePatchImageTextButton(
            text = text,
            ninePatchKey = ninePatchKey,
            imageKey = imageKey,
            colorUp = colorUp,
            colorDown = Color.WHITE,
            width = BUTTON_WIDTH,
            height = BUTTON_HEIGHT
        ).apply {
            setPosition(buttonX, y)
            label.setFontScale(1.5f)
            labelCell.expandX().align(Align.left).padLeft(12f)
            imageCell.padLeft(16f)
            onTap { onClick() }
            addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(TITLE_LETTER_DURATION)))
        }
    }
}