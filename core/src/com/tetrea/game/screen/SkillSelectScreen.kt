package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.GAME_LIGHT_PURPLE

class SkillSelectScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private val skillSlots = Array(3) {
        Image(game.res.getTexture("skill_slot"))
    }

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("EDIT SKILLS", fontScale = 1f))
        }
        parentTable.add(headerTable).size(220f, 30f).top().padTop(16f).colspan(2).row()

        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "purple_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap { navigateTo(VERSUS_SELECT_SCREEN) }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f).row()

        createSkillSlotsTable()

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

    private fun createSkillSlotsTable() {
        val table = Table().apply {
            add(skillSlots[0]).left().expandX().padLeft(8f)
            add(skillSlots[1]).expandX()
            add(skillSlots[2]).right().expandX().padRight(8f)
        }
        parentTable.add(table).width(220f).padTop(16f).padBottom(16f).row()
    }

    private fun createSkillsTable() {
        val table = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
        }
        table.add(game.res.getLabel("SKILLS", color = GAME_LIGHT_PURPLE, fontScale = 1f))
            .expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()


    }
}