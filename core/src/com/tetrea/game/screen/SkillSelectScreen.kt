package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tetrea.game.extension.onClick
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.NUM_SKILLS
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.GAME_LIGHT_PURPLE
import com.tetrea.game.res.GAME_ORANGE
import com.tetrea.game.res.GAME_YELLOW

class SkillSelectScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private val skillSlots = Array(NUM_SKILLS) {
        Image(game.res.getTexture("skill_slot"))
    }

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("EDIT SKILLS", fontScale = 1f)).padBottom(2f).row()
            add(game.res.getLabel("YOUR RATING: ${game.player.rating.toInt()}", color = GAME_YELLOW))
        }
        parentTable.add(headerTable).size(220f, 44f).top().padTop(16f).colspan(2).row()

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
        createSkillsTable()

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
        game.player.skills.forEachIndexed { index, key ->
            val skill = game.res.getSkill(key)
            skillSlots[index].drawable = TextureRegionDrawable(game.res.getTexture(skill.avatar))
        }

        val table = Table().apply {
            add(skillSlots[0]).left()
            add(skillSlots[1]).padLeft(40f).padRight(40f)
            add(skillSlots[2]).right()
        }
        parentTable.add(table).size(220f, 40f).padTop(16f).padBottom(16f).colspan(2).row()
    }

    private fun createSkillsTable() {
        val table = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
        }
        table.add(game.res.getLabel("SKILLS", fontScale = 1f))
            .top().left().padTop(8f).padLeft(8f).row()
        table.add(game.res.getLabel("DRAG AND DROP TO EDIT SKILLS", color = GAME_LIGHT_PURPLE))
            .expand().top().left().padTop(4f).padLeft(8f).padBottom(8f).row()

        val skillContainer = Table()

        game.res.skillsCache.forEach { (_, skill) ->
            val enteredBg = NinePatchDrawable(
                if (game.player.maxRating >= skill.rating) game.res.getNinePatch("light_purple_bg")
                else game.res.getNinePatch("light_gray_bg")
            )
            val exitedBg = NinePatchDrawable(
                if (game.player.maxRating >= skill.rating) game.res.getNinePatch("purple_bg")
                else game.res.getNinePatch("dark_gray_bg")
            )

            val skillTable = Table().apply {
                touchable = Touchable.enabled
                background = exitedBg
                onClick(
                    enter = { background = enteredBg },
                    exit = { background = exitedBg }
                )
            }

            val avatar = Image(game.res.getTexture(skill.avatar))
            skillTable.add(avatar).size(40f, 40f).padLeft(6f).padRight(6f).padTop(8f).padBottom(8f)

            val textTable = Table().apply {
                add(game.res.getLabel(text = skill.name, fontScale = 1f, color = GAME_YELLOW))
                    .top().left().padTop(4f).padBottom(4f).row()
                add(game.res.getLabel(text = skill.desc, fontScale = 0.5f).apply { setWrap(true) })
                    .width(150f).top().left().padBottom(4f).row()
                add(game.res.getLabel(text = "RATING REQUIRED: ${skill.rating.toInt()}", color = GAME_ORANGE))
                    .top().left().padBottom(4f)
            }
            skillTable.add(textTable).right()

            skillContainer.add(skillTable).width(200f).padBottom(8f).row()
        }
        val scrollPane = ScrollPane(skillContainer).apply {
            setOverscroll(false, false)
            setCancelTouchFocus(false)
            fadeScrollBars = false
            layout()
        }
        table.add(scrollPane)

        parentTable.add(table).width(220f).expandY().colspan(2).padBottom(16f)
    }
}