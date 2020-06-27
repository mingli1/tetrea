package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tetrea.game.TetreaGame
import com.tetrea.game.res.*

private const val SELECTION_HEIGHT_PERCENT = 0.8f

class LevelSelectScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private lateinit var headerTable: Table

    // temp defeated and locked states
    private val playerWorldId = 0
    private val playerLevelId = 3

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("CHOOSE A MATCHUP", fontScale = 1f))
        }

        parentTable.add(headerTable).size(220f, 32f).top().padTop(16f).expandY().row()
        populateSelections()

        Gdx.input.inputProcessor = stage
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()

        game.batch.draw(game.res.getTexture("battle_bg_sky"), 0f, 0f)
        if (transition != Transition.None) fade.draw(game.batch)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun populateSelections() {
        // todo: implement multiple worlds
        val selectionTable = Table()
        val currWorldId = 0

        val configs = game.res.getBattleConfigs(currWorldId)

        configs.forEachIndexed { levelId, config ->
            val completedState = currWorldId < playerWorldId || levelId < playerLevelId
            val activeState = levelId == playerLevelId

            val table = Table().apply {
                touchable = Touchable.enabled
                background = NinePatchDrawable(
                    when {
                        completedState -> game.res.getNinePatch("gray_blue_bg")
                        activeState -> game.res.getNinePatch("purple_bg")
                        else -> game.res.getNinePatch("dark_gray_bg")
                    }
                )

            }
            val avatar = Image(game.res.getTexture(config.enemy.avatar))
            table.add(avatar).padTop(8f).padLeft(8f).top().left()

            val textTable = Table()
            val title = game.res.getLabel(
                text = when {
                    completedState -> "DEFEATED"
                    activeState -> "VS. ${config.enemy.name}"
                    else -> "VS. ???"
                },
                color = when {
                    completedState -> GAME_WHITE_BLUE
                    activeState -> GAME_YELLOW
                    else -> GAME_LIGHT_GRAY
                },
                fontScale = 1f
            )
            textTable.add(title).top().left().padBottom(2f).row()

            if (completedState) {
                // todo get score from save file
                val score = game.res.getLabel(text = "YOU 3 - 0 ENEMY")
                textTable.add(score).top().left().padBottom(2f).row()
            }

            // todo get rating from enemy
            val rating = game.res.getLabel(
                text = when {
                    completedState || activeState -> "RATING: 1232"
                    else -> "RATING: ???"
                },
                color = GAME_ORANGE
            )
            textTable.add(rating).top().left().padBottom(2f)

            table.add(textTable).padTop(6f).padLeft(10f).top().left().expand().row()

            selectionTable.add(table).size(195f, 60f).padBottom(16f).row()
        }

        val scrollPane = ScrollPane(selectionTable).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        parentTable.add(scrollPane).height(stage.height * SELECTION_HEIGHT_PERCENT)
    }
}