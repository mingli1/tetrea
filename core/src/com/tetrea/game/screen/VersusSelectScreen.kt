package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.battle.BattleConfigFactory
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.scene.component.VersusCard
import com.tetrea.game.scene.dialog.ConfirmDialog
import com.tetrea.game.scene.dialog.MessageDialog
import com.tetrea.game.scene.dialog.SelectionDialog
import com.tetrea.game.scene.dialog.SelectionDialogCallback
import kotlin.math.abs

private const val DIALOG_FADE_DURATION = 0.4f

class VersusSelectScreen(game: TetreaGame) : BaseScreen(game), LateDisposable, SelectionDialogCallback {

    private lateinit var parentTable: Table
    private val confirmDialog = ConfirmDialog(
        "MATCHMAKING",
        "YOU WILL BE MATCHED WITH AN ENEMY OF SIMILAR RATING. ARE YOU SURE YOU WANT TO PROCEED?",
        this::showSelectionDialog,
        {},
        game.res,
        windowStyleKey = "purple_bg",
        buttonStyleKey = "purple_button"
    )

    private var playerVersusCard: VersusCard? = null
    private var enemyVersusCard: VersusCard? = null
    private lateinit var versusTag: Image
    private lateinit var bestOfText: Label

    private lateinit var selectionTable: Table
    private lateinit var selectionBg: Image
    private val selectionDialog = SelectionDialog(game.res, this, true)
    private lateinit var battleConfig: BattleConfig
    var matchMade = false

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        val headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(game.res.getLabel("SELECT A VERSUS MODE", fontScale = 1f)).padBottom(2f).row()
            add(game.res.getLabel("YOUR RATING: ${game.player.rating.toInt()}", color = GAME_YELLOW))
        }
        parentTable.add(headerTable).size(220f, 44f).top().padTop(16f).colspan(2).row()

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
                onClick = { confirmDialog.show(this@VersusSelectScreen.stage) }
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

        versusTag = Image(game.res.getTexture("versus_tag")).apply {
            setPosition(this@VersusSelectScreen.stage.width / 2 - 76f / 2, this@VersusSelectScreen.stage.height / 2 - 44f / 2)
        }

        selectionBg = Image(game.res.getTexture("black_150_opacity")).apply {
            setSize(this@VersusSelectScreen.stage.width, this@VersusSelectScreen.stage.height)
            isVisible = false
        }
        stage.addActor(selectionBg)
        selectionTable = Table().apply {
            setFillParent(true)
            isVisible = false
        }
        selectionTable.add(selectionDialog).size(196f, 220f)
        stage.addActor(selectionTable)

        arguments?.let {
            if (it.containsKey(ARG_MATCH_QUIT)) {
                val ratingLost = abs((it[ARG_MATCH_QUIT] as Float).toInt())
                MessageDialog(
                    title = "MATCH QUIT",
                    message = "YOU LOST $ratingLost RATING FOR ABANDONING A MATCH.",
                    res = game.res,
                    dismiss = {},
                    windowStyleKey = "purple_bg",
                    buttonStyleKey = "purple_button"
                ).show(stage)
            }
        }

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        selectionDialog.update(dt)
        playerVersusCard?.update(dt)
        enemyVersusCard?.update(dt)
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

    private fun showSelectionDialog() {
        matchMade = true
        battleConfig = BattleConfigFactory.findMatch(game.player.rating)

        selectionDialog.resetBarAnimations()
        selectionDialog.setConfig(battleConfig, SelectionState.Active, game.player)

        selectionBg.isVisible = true
        selectionBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))

        selectionTable.isVisible = true
        selectionTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION),
            Actions.run {
                selectionDialog.startBarAnimations(battleConfig)
            }))
    }

    override fun onBattleButtonClicked(battleConfig: BattleConfig) {
        bestOfText = game.res.getLabel("BEST OF ${battleConfig.bestOf}", fontScale = 1f)
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
                        val args = mapOf(ARG_BATTLE_CONFIG to battleConfig)
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
            enemy = battleConfig.enemy
        )
    }

    fun onDodge() {
        val ratingLoss = Elo.getDodgeLoss(game.player.rating)
        game.player.rating -= ratingLoss
        game.saveManager.save()
    }
}