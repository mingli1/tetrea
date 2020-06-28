package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tetrea.game.TetreaGame
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.extension.onClick
import com.tetrea.game.res.*
import com.tetrea.game.scene.component.SelectionDialog

private const val SELECTION_SCROLL_HEIGHT_PERCENT = 0.78f
private const val BUTTON_WIDTH = 76f
private const val BUTTON_HEIGHT = 28f

enum class SelectionState {
    Completed,
    Active,
    Locked
}

class LevelSelectScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private lateinit var headerTable: Table
    private lateinit var scrollPane: ScrollPane
    private lateinit var selectionTable: Table
    private val selectionDialog = SelectionDialog(game.res)

    // temp defeated and locked states
    private val playerWorldId = 0
    private val playerLevelId = 4

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("CHOOSE A MATCHUP", fontScale = 1f))
        }

        parentTable.add(headerTable).size(220f, 32f).top().padTop(16f).row()
        createBackButton()
        populateSelections()

        selectionTable = Table().apply {
            touchable = Touchable.enabled
            background = TextureRegionDrawable(game.res.getTexture("black_100_opacity"))
            setFillParent(true)
            isVisible = false
            onClick { hideSelectionDialog() }
        }
        selectionTable.add(selectionDialog).size(196f, 272f)
        stage.addActor(selectionTable)

        Gdx.input.inputProcessor = stage
    }

    override fun update(dt: Float) {
        super.update(dt)
        selectionDialog.update(dt)
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

    private fun createBackButton() {
        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "purple_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        )

        parentTable.add(backButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT).padTop(6f).expandY().row()
    }

    private fun populateSelections() {
        // todo: implement multiple worlds
        val selectionTable = Table()
        val currWorldId = 0

        val configs = game.res.getBattleConfigs(currWorldId)

        configs.forEachIndexed { levelId, config ->
            val selectionState = when {
                currWorldId < playerWorldId || levelId < playerLevelId -> SelectionState.Completed
                levelId == playerLevelId -> SelectionState.Active
                else -> SelectionState.Locked
            }

            val enteredBg = NinePatchDrawable(
                when (selectionState) {
                    SelectionState.Completed -> game.res.getNinePatch("light_gray_blue_bg")
                    SelectionState.Active -> game.res.getNinePatch("light_purple_bg")
                    else -> game.res.getNinePatch("dark_gray_bg")
                }
            )
            val exitedBg = NinePatchDrawable(
                when (selectionState) {
                    SelectionState.Completed -> game.res.getNinePatch("gray_blue_bg")
                    SelectionState.Active -> game.res.getNinePatch("purple_bg")
                    else -> game.res.getNinePatch("dark_gray_bg")
                }
            )

            val table = Table().apply {
                touchable = Touchable.enabled
                background = exitedBg
                onClick(
                    enter = { background = enteredBg },
                    exit = { background = exitedBg },
                    up = { showSelectionDialog(config, selectionState) }
                )
            }
            val avatar = Image(game.res.getTexture(config.enemy.avatar))
            table.add(avatar).padTop(16f).padLeft(8f).top().left()

            val textTable = Table()
            val title = game.res.getLabel(
                text = when (selectionState) {
                    SelectionState.Completed -> "DEFEATED"
                    SelectionState.Active -> "VS. ${config.enemy.name}"
                    else -> "VS. ???"
                },
                color = when (selectionState) {
                    SelectionState.Completed -> GAME_WHITE_BLUE
                    SelectionState.Active -> GAME_YELLOW
                    else -> GAME_LIGHT_GRAY
                },
                fontScale = 1f
            )
            textTable.add(title).top().left().padBottom(2f).row()

            val desc = game.res.getLabel()
            when (selectionState) {
                SelectionState.Completed -> {
                    // todo get score from save file
                    desc.setText("YOU 3 - 0 ENEMY")
                }
                SelectionState.Active -> {
                    // todo get attempts from save file
                    desc.setText("3 ATTEMPTS")
                }
                else -> desc.setText("LOCKED")
            }
            textTable.add(desc).top().left().padBottom(2f).row()

            val rating = game.res.getLabel(
                text = if (selectionState == SelectionState.Completed || selectionState == SelectionState.Active)
                    "RATING: ${config.enemy.rating}"
                    else "RATING: ???",
                color = GAME_ORANGE
            )
            textTable.add(rating).top().left().padBottom(2f)

            table.add(textTable).padTop(6f).padLeft(10f).top().left().expand().row()

            selectionTable.add(table).size(195f, 60f).padBottom(16f).row()
        }

        scrollPane = ScrollPane(selectionTable).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        scrollPane.scrollTo(0f, 72f * (configs.size - playerLevelId), 195f, 60f)
        parentTable.add(scrollPane).height(stage.height * SELECTION_SCROLL_HEIGHT_PERCENT)
    }

    private fun showSelectionDialog(config: BattleConfig, selectionState: SelectionState) {
        selectionDialog.setConfig(config, selectionState)
        selectionTable.isVisible = true
    }

    private fun hideSelectionDialog() {
        selectionTable.isVisible = false
    }
}