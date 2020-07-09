package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.battle.MatchState
import com.tetrea.game.extension.onTap
import com.tetrea.game.res.*
import com.tetrea.game.tetris.TetrisStats

private const val BUTTON_WIDTH = 76f
private const val BUTTON_HEIGHT = 36f

class ResultsScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var headerTable: Table
    private lateinit var bodyTable: Table
    private lateinit var buttonTable: Table

    private lateinit var stats: TetrisStats
    private var playerWin = true
    private var playerScore = 0
    private var enemyScore = 0
    private var enemyName = ""

    override fun show() {
        super.show()

        arguments?.let {
            stats = it[ARG_TETRIS_STATS] as TetrisStats
            val matchState = it[ARG_MATCH_STATE] as MatchState
            playerWin = matchState == MatchState.PlayerWin
            playerScore = it[ARG_PLAYER_SCORE] as Int
            enemyScore = it[ARG_ENEMY_SCORE] as Int
            enemyName = it[ARG_ENEMY_NAME] as String
        }

        val bgTable = Table().apply { setFillParent(true) }
        stage.addActor(bgTable)

        headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch(if (playerWin) "gray_blue_bg" else "red_bg"))
        }
        bodyTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch(if (playerWin) "gray_blue_bg" else "red_bg"))
        }
        buttonTable = Table()

        bgTable.add(headerTable).size(247f, 80f).expandY().row()
        bgTable.add(bodyTable).size(247f, 260f).expandY().row()
        bgTable.add(buttonTable).width(247f).expandY()

        createHeader()
        createBody()
        createButtons()

        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(dt: Float) {
        super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        if (transition == Transition.None) game.batch.color = Color.WHITE

        game.batch.draw(game.res.getTexture("battle_bg_sky"), 0f, 0f)
        game.batch.draw(game.res.getTexture("black_100_opacity"), 0f, 0f, stage.width, stage.height)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    private fun createHeader() {
        val matchStateLabel = game.res.getLabel(
            text = if (playerWin) "VICTORY" else "DEFEAT",
            color = if (playerWin) GAME_YELLOW else GAME_DARK_RED,
            fontScale = 3f
        )
        headerTable.add(matchStateLabel).row()
        val finalScoreLabel = game.res.getLabel(
            text = "${game.player.name} $playerScore - $enemyScore $enemyName",
            fontScale = 1f
        )
        headerTable.add(finalScoreLabel)
    }

    private fun createBody() {
        val headerText = game.res.getLabel(
            text = "STATS",
            color = if (playerWin) GAME_DARK_GRAY_BLUE else GAME_DARK_RED,
            fontScale = 1f
        )
        bodyTable.add(headerText).expand().top().left().padTop(8f).padLeft(8f).row()

        val statsTable = Table()
        val statsMap = stats.getLabeledPairs()
        statsMap.forEach { (label, value) ->
            val labelText = game.res.getLabel(text = label, color = if (playerWin) GAME_LIGHT_GRAY_BLUE else GAME_LIGHT_RED, fontScale = 1f)
            val statText = game.res.getLabel(text = value, fontScale = 1f)
            statsTable.add(labelText).expandX().left()
            statsTable.add(statText).expandX().right().row()
        }

        val scrollPane = ScrollPane(statsTable).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }

        bodyTable.add(scrollPane).width(231f).padTop(8f).padBottom(8f)
    }

    private fun createButtons() {
        val backButton = getButton("BACK").apply { onTap { navigateTo(LEVEL_SELECT_SCREEN) } }
        val retryButton = getButton("RETRY")
        val nextButton = getButton("NEXT")

        buttonTable.add(backButton).align(Align.left).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()
        buttonTable.add(retryButton).align(Align.center).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()
        buttonTable.add(nextButton).align(Align.right).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()
    }

    private fun getButton(text: String): TextButton = game.res.getNinePatchTextButton(
        text = text,
        key = if (playerWin) "gray_blue_button" else "red_button",
        colorUp = GAME_LIGHT_GRAY_BLUE,
        colorDown = Color.WHITE
    )
}