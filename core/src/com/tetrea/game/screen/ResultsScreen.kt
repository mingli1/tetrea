package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.battle.MatchState
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.extension.onTap
import com.tetrea.game.extension.sign
import com.tetrea.game.res.*
import com.tetrea.game.scene.component.VersusCard
import com.tetrea.game.scene.dialog.ConfirmDialog
import com.tetrea.game.tetris.TetrisStats

private const val BUTTON_WIDTH = 76f
private const val BUTTON_HEIGHT = 36f
private const val RATING_ANIMATION_TIME = 2f

class ResultsScreen(game: TetreaGame) : BaseScreen(game), LateDisposable {

    private lateinit var headerTable: Table
    private lateinit var bodyTable: Table
    private lateinit var buttonTable: Table

    private lateinit var stats: TetrisStats
    private var playerWin = true
    private var playerScore = 0
    private var enemyScore = 0
    private lateinit var config: BattleConfig

    private var playerVersusCard: VersusCard? = null
    private var enemyVersusCard: VersusCard? = null
    private lateinit var versusTag: Image
    private lateinit var bestOfText: Label
    private val retryConfirmDialog = ConfirmDialog(
        "REMATCH",
        "ARE YOU SURE YOU WANT A REMATCH?",
        this::onRetry,
        {},
        game.res,
        windowStyleKey = "purple_bg",
        buttonStyleKey = "purple_button"
    )

    private lateinit var ratingLabel: Label
    private var oldRating = game.player.rating
    private var newRating = 0f
    private var currRating = 0f
    private var ratingTimer = 0f
    private var startRatingAnim = false

    override fun show() {
        super.show()

        arguments?.let {
            stats = it[ARG_TETRIS_STATS] as TetrisStats
            val matchState = it[ARG_MATCH_STATE] as MatchState
            playerWin = matchState == MatchState.PlayerWin
            playerScore = it[ARG_PLAYER_SCORE] as Int
            enemyScore = it[ARG_ENEMY_SCORE] as Int
            config = it[ARG_BATTLE_CONFIG] as BattleConfig
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

        versusTag = Image(game.res.getTexture("versus_tag")).apply {
            setPosition(this@ResultsScreen.stage.width / 2 - 76f / 2, this@ResultsScreen.stage.height / 2 - 44f / 2)
        }

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        playerVersusCard?.update(dt)
        enemyVersusCard?.update(dt)

        if (startRatingAnim) {
            ratingTimer += dt
            currRating = Interpolation.slowFast.apply(oldRating, newRating, ratingTimer / RATING_ANIMATION_TIME)
            ratingLabel.setText("RATING: ${currRating.toInt()}")
            if (ratingTimer >= RATING_ANIMATION_TIME) {
                ratingLabel.setText("RATING: ${newRating.toInt()}")
                ratingTimer = 0f
                startRatingAnim = false
            }
        }
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
            fontScale = 2f
        )
        headerTable.add(matchStateLabel).colspan(2).row()
        val finalScoreLabel = game.res.getLabel(
            text = "${game.player.name} $playerScore - $enemyScore ${config.enemy.name}",
            fontScale = 1f
        )
        headerTable.add(finalScoreLabel).colspan(2).row()

        val ratingChange = Elo.getRatingChange(game.player.rating, config.enemy.rating, playerScore, enemyScore)
        val newRating = (game.player.rating + ratingChange).toInt()
        this.newRating = game.player.rating + ratingChange
        val change = newRating - game.player.rating.toInt()
        ratingLabel = game.res.getLabel(fontScale = 1f, color = GAME_ORANGE)
        startRatingAnim = true
        headerTable.add(ratingLabel).align(Align.left).padLeft(12f)
        val ratingChangeLabel = game.res.getLabel(
            text = "(${change.sign()}$change)",
            fontScale = 1f,
            color = when {
                ratingChange < 0 -> Color.RED
                ratingChange == 0f -> Color.WHITE
                else -> Color.GREEN
            }
        )
        headerTable.add(ratingChangeLabel).align(Align.left)

        game.player.completeMatchup(
            key = config.compositeKey,
            won = playerWin,
            ratingChange = ratingChange,
            playerScore = playerScore,
            enemyScore = enemyScore,
            isMatchmaking = config.isMatchmaking
        )
        game.saveManager.save()
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
        val backButton = getButton("BACK").apply { onTap { onBackButtonClicked() } }
        buttonTable.add(backButton).align(Align.left).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()

        if (!config.isMatchmaking) {
            val retryButton = getButton("RETRY").apply { onTap { retryConfirmDialog.show(stage) } }
            buttonTable.add(retryButton).align(Align.center).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()
        }
        val homeButton = getButton("HOME").apply { onTap { navigateTo(HOME_SCREEN) } }
        buttonTable.add(homeButton).align(Align.right).size(BUTTON_WIDTH, BUTTON_HEIGHT).expandX()
    }

    private fun getButton(text: String): TextButton = game.res.getNinePatchTextButton(
        text = text,
        key = if (playerWin) "gray_blue_button" else "red_button",
        colorUp = GAME_LIGHT_GRAY_BLUE,
        colorDown = Color.WHITE
    )

    private fun onBackButtonClicked() {
        navigateTo(if (config.isMatchmaking) VERSUS_SELECT_SCREEN else LEVEL_SELECT_SCREEN)
    }

    private fun onRetry() {
        bestOfText = game.res.getLabel("BEST OF ${config.bestOf}", fontScale = 1f)
        bestOfText.setPosition(stage.width / 2 - bestOfText.width / 2, versusTag.y + 52f)

        playerVersusCard = VersusCard(
            stage = stage,
            onScreen = false,
            isEnemy = false,
            onFinished = {
                stage.addActor(versusTag)
                stage.addActor(bestOfText)

                bestOfText.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
                versusTag.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.fadeIn(1f),
                    Actions.delay(1f),
                    Actions.run {
                        val args = mapOf(ARG_BATTLE_CONFIG to config)
                        navigateTo(BATTLE_SCREEN, args, shouldFade = false)
                    }
                ))
            },
            res = game.res,
            player = game.player
        )
        enemyVersusCard = VersusCard(
            stage = stage,
            onScreen = false,
            isEnemy = true,
            onFinished = {},
            res = game.res,
            enemy = config.enemy
        )
    }
}