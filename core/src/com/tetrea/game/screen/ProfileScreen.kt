package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_ORANGE

class ProfileScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private lateinit var contentTable: Table

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
            add(game.res.getLabel("PROFILE", fontScale = 1f)).padBottom(2f).row()
        }
        parentTable.add(headerTable).size(220f, 30f).top().padTop(16f).colspan(2).row()

        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap { navigateTo(HOME_SCREEN) }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f)
        val editButton = game.res.getNinePatchTextButton(
            text = "EDIT",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        )
        parentTable.add(editButton).top().right().size(76f, 28f).padTop(6f).row()

        contentTable = Table()
        createOverviewTable()

        val scrollPane = ScrollPane(contentTable).apply {
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
        game.batch.draw(game.res.getTexture("profile_screen_overlay"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun createOverviewTable() {
        val overviewTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
        }

        val avatar = Image(game.res.getTexture(game.player.avatar))
        val name = game.res.getLabel(text = game.player.name,fontScale = 1f)
        val rating = game.res.getLabel(
            text = "RATING: ${game.player.rating.toInt()}",
            color = GAME_LIGHT_ORANGE
        )

        overviewTable.add(avatar).padTop(6f).padLeft(4f)
        val textTable = Table().apply {
            add(name).top().left().expandX().padBottom(2f).row()
            add(rating).top().left().expandX().padBottom(2f)
        }
        overviewTable.add(textTable).width(160f).padTop(6f).padLeft(14f).top().left().row()
        overviewTable.add(Image(game.res.getTexture("white"))).width(200f).padTop(4f).colspan(2).row()

        contentTable.add(overviewTable).width(220f).padBottom(16f).row()
    }
}