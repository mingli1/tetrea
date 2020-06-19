package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.TetreaGame
import com.tetrea.game.res.Color

class ResultsScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var headerTable: Table
    private lateinit var bodyTable: Table
    private lateinit var buttonTable: Table

    override fun show() {
        super.show()

        val bgTable = Table().apply {
            setFillParent(true)
            //debug = true
        }
        stage.addActor(bgTable)

        headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("gray_blue_bg"))
        }
        bodyTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("gray_blue_bg"))
        }
        buttonTable = Table()

        bgTable.add(headerTable).size(247f, 80f).expandY().row()
        bgTable.add(bodyTable).size(247f, 240f).expandY().row()
        bgTable.add(buttonTable).width(247f).expandY()

        createHeader()
        createBody()
        createButtons()

        Gdx.input.inputProcessor = stage
    }

    override fun update(dt: Float) {
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        game.batch.draw(game.res.getTexture("battle_bg_sky"), 0f, 0f)
        game.batch.draw(game.res.getTexture("black_100_opacity"), 0f, 0f, stage.width, stage.height)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun createHeader() {

    }

    private fun createBody() {

    }

    private fun createButtons() {
        val backButton = getButton("BACK")
        val retryButton = getButton("RETRY")
        val nextButton = getButton("NEXT")

        buttonTable.add(backButton).align(Align.left).size(76f, 36f).expandX()
        buttonTable.add(retryButton).align(Align.center).size(76f, 36f).expandX()
        buttonTable.add(nextButton).align(Align.right).size(76f, 36f).expandX()
    }

    private fun getButton(text: String): TextButton = game.res.getNinePatchTextButton(
        text = text,
        key = "gray_blue_button",
        colorUp = Color.GAME_LIGHT_GRAY_BLUE,
        colorDown = Color.GAME_WHITE
    )
}