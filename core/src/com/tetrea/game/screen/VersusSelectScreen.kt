package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE

class VersusSelectScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("SELECT A VERSUS MODE", fontScale = 1f))
        }
        parentTable.add(headerTable).size(220f, 32f).top().padTop(16f).colspan(2).row()

        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "purple_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap { navigateTo(HOME_SCREEN) }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f).row()

        val bodyTable = Table().apply {
            add(getButton(
                text = "FIND MATCH",
                ninePatchKey = "find_match_button",
                imageKey = "find_match_button_icon",
                colorUp = Color(216 / 255f, 206 / 255f, 1f, 1f),
                onClick = {}
            )).size(220f, 50f).row()
            add(getButton(
                text = "ADVENTURE",
                ninePatchKey = "adventure_button",
                imageKey = "adventure_button_icon",
                colorUp = Color(216 / 255f, 206 / 255f, 1f, 1f),
                onClick = { navigateTo(LEVEL_SELECT_SCREEN) }
            )).size(220f, 50f).padTop(16f)
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
        game.batch.draw(game.res.getTexture("versus_select_overlay"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun getButton(
        text: String,
        ninePatchKey: String,
        imageKey: String,
        colorUp: Color,
        onClick: () -> Unit
    ): ImageTextButton {
        return game.res.getNinePatchImageTextButton(
            text = text,
            ninePatchKey = ninePatchKey,
            imageKey = imageKey,
            colorUp = colorUp,
            colorDown = Color.WHITE
        ).apply {
            label.setFontScale(1.5f)
            labelCell.expandX().align(Align.left).padLeft(12f)
            imageCell.padLeft(16f)
            onTap { onClick() }
        }
    }
}