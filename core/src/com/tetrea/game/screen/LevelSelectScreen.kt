package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.extension.onClick
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.*
import com.tetrea.game.scene.component.VersusCard
import com.tetrea.game.scene.dialog.MessageDialog
import com.tetrea.game.scene.dialog.SelectionDialog
import com.tetrea.game.scene.dialog.SelectionDialogCallback
import com.tetrea.game.scene.dialog.VersusHelpDialog
import kotlin.math.abs

private const val BUTTON_WIDTH = 76f
private const val BUTTON_HEIGHT = 28f
private const val DIALOG_FADE_DURATION = 0.4f

enum class SelectionState {
    Completed,
    Active,
    Locked
}

class LevelSelectScreen(game: TetreaGame) : BaseScreen(game), LateDisposable, SelectionDialogCallback {

    private lateinit var parentTable: Table
    private lateinit var headerTable: Table
    private lateinit var scrollPane: ScrollPane
    private lateinit var selectionTable: Table
    private lateinit var levelsTable: Table
    private lateinit var selectionBg: Image
    private val selectionDialog = SelectionDialog(game.res, game.soundManager, this, false)
    private var playerVersusCard: VersusCard? = null
    private var enemyVersusCard: VersusCard? = null
    private lateinit var versusTag: Image
    private lateinit var bestOfText: Label
    private lateinit var worldLabel: Label

    private var selectedWorldId = game.player.currWorldId
    private lateinit var leftWorldSelectButton: ImageButton
    private lateinit var rightWorldSelectButton: ImageButton

    private lateinit var helpTable: Table
    private lateinit var helpBg: Image
    private val helpDialog = VersusHelpDialog(game.res, false)

    override fun show() {
        super.show()

        parentTable = Table().apply { setFillParent(true) }
        stage.addActor(parentTable)

        worldLabel = game.res.getLabel("WORLD ${selectedWorldId + 1}", fontScale = 1f)

        headerTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("purple_bg"))
            add(worldLabel).padBottom(2f).row()
            add(game.res.getLabel("YOUR RATING: ${game.player.rating.toInt()}", color = GAME_YELLOW))
        }

        parentTable.add(headerTable).size(220f, 44f).top().padTop(16f).colspan(2).row()
        createBackButton()
        createHelpButton()
        createWorldSelectButtons()

        levelsTable = Table()
        setLevelsTable()
        scrollPane = ScrollPane(levelsTable).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        scrollToCurrentLevel()
        parentTable.add(scrollPane).expandY().colspan(2).top().padTop(16f)

        selectionBg = Image(game.res.getTexture("black_150_opacity")).apply {
            setSize(this@LevelSelectScreen.stage.width, this@LevelSelectScreen.stage.height)
            isVisible = false
            onTap { hideSelectionDialog() }
        }
        stage.addActor(selectionBg)
        selectionTable = Table().apply {
            setFillParent(true)
            isVisible = false
        }
        selectionTable.add(selectionDialog).size(196f, 272f)
        stage.addActor(selectionTable)

        versusTag = Image(game.res.getTexture("versus_tag")).apply {
            setPosition(this@LevelSelectScreen.stage.width / 2 - 76f / 2, this@LevelSelectScreen.stage.height / 2 - 44f / 2)
        }

        helpBg = Image(game.res.getTexture("black_150_opacity")).apply {
            setSize(this@LevelSelectScreen.stage.width, this@LevelSelectScreen.stage.height)
            isVisible = false
            onTap { hideHelpDialog() }
        }
        stage.addActor(helpBg)
        helpTable = Table().apply {
            setFillParent(true)
            isVisible = false
        }
        helpTable.add(helpDialog).size(220f, 350f)
        stage.addActor(helpTable)

        arguments?.let {
            if (it.containsKey(ARG_MATCH_QUIT)) {
                val ratingLost = abs((it[ARG_MATCH_QUIT] as Float).toInt())
                MessageDialog(
                    title = "MATCH QUIT",
                    message = "YOU LOST $ratingLost RATING FOR ABANDONING A MATCH.",
                    res = game.res,
                    soundManager = game.soundManager,
                    dismiss = {},
                    windowStyleKey = "purple_bg",
                    buttonStyleKey = "purple_button"
                ).show(stage)
            }
        }

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        super.update(dt)
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

        game.batch.draw(game.res.getTexture("world_${selectedWorldId}_bg"), 0f, 0f)

        game.batch.end()

        stage.act(dt)
        stage.draw()
    }

    override fun onBattleButtonClicked(battleConfig: BattleConfig) {
        game.musicManager.fadeOutBackgroundMusic()
        game.musicManager.inBattle = true

        playerVersusCard = VersusCard(
            stage = stage,
            onScreen = false,
            isEnemy = false,
            onFinished = { onVersusCardFinished(battleConfig) },
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

    private fun showHelpDialog() {
        helpBg.isVisible = true
        helpBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
        helpTable.isVisible = true
        helpTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))
    }

    private fun hideHelpDialog() {
        helpBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { helpBg.isVisible = false }))
        helpTable.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { helpTable.isVisible = false }))
    }

    private fun onVersusCardFinished(battleConfig: BattleConfig) {
        game.soundManager.onBattleStart()
        bestOfText = game.res.getLabel("BEST OF ${battleConfig.bestOf}", fontScale = 1f)
        bestOfText.setPosition(stage.width / 2 - bestOfText.width / 2, versusTag.y + 52f)

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
    }

    private fun createBackButton() {
        val backButton = game.res.getNinePatchTextButton(
            text = "BACK",
            key = "purple_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                navigateTo(VERSUS_SELECT_SCREEN)
                game.soundManager.onPrimaryButtonClicked()
            }
        }

        parentTable.add(backButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT).padTop(6f)
    }

    private fun createHelpButton() {
        val helpButton = game.res.getNinePatchTextButton(
            text = "HELP",
            key = "purple_button",
            colorUp = GAME_LIGHT_GRAY_BLUE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                showHelpDialog()
                game.soundManager.onPrimaryButtonClicked()
            }
        }

        parentTable.add(helpButton).top().right().size(BUTTON_WIDTH, BUTTON_HEIGHT).padTop(6f).row()
    }

    private fun createWorldSelectButtons() {
        leftWorldSelectButton = getWorldSelectButton(true).apply {
            setPosition(4f, this@LevelSelectScreen.stage.height / 2 - 55f / 2)
            isVisible = selectedWorldId > 0
            onTap { onWorldSelectButtonClicked(true) }
        }
        stage.addActor(leftWorldSelectButton)
        rightWorldSelectButton = getWorldSelectButton(false).apply {
            setPosition(this@LevelSelectScreen.stage.width - 4f - 27f, this@LevelSelectScreen.stage.height / 2 - 55f / 2)
            isVisible = selectedWorldId < NUM_WORLDS - 1
            onTap { onWorldSelectButtonClicked(false) }
        }
        stage.addActor(rightWorldSelectButton)
    }

    private fun setLevelsTable() {
        levelsTable.clear()
        val configs = game.res.getBattleConfigs(selectedWorldId)

        configs.forEachIndexed { levelId, config ->
            val selectionState = when {
                selectedWorldId < game.player.currWorldId || (levelId < game.player.currLevelId && selectedWorldId == game.player.currWorldId) -> SelectionState.Completed
                levelId == game.player.currLevelId && selectedWorldId == game.player.currWorldId -> SelectionState.Active
                else -> SelectionState.Locked
            }

            val enteredBg = NinePatchDrawable(
                when (selectionState) {
                    SelectionState.Completed -> game.res.getNinePatch("light_gray_blue_bg")
                    SelectionState.Active -> game.res.getNinePatch("light_purple_bg")
                    else -> game.res.getNinePatch("light_gray_bg")
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
                    exit = { background = exitedBg }
                )
                onTap {
                    showSelectionDialog(config, selectionState)
                    game.soundManager.onLevelSelectClicked()
                }
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
                    game.player.getRecord(config.compositeKey).bestScore?.let {
                        desc.setText("YOU ${it.x} - ${it.y} ENEMY")
                    }
                }
                SelectionState.Active -> {
                    desc.setText("${game.player.getRecord(config.compositeKey).attempts} ATTEMPT(S)")
                }
                else -> desc.setText("LOCKED")
            }
            textTable.add(desc).top().left().padBottom(2f).row()

            val rating = game.res.getLabel(
                text = if (selectionState == SelectionState.Completed || selectionState == SelectionState.Active)
                    "RATING: ${config.enemy.rating.toInt()}"
                else "RATING: ???",
                color = GAME_ORANGE
            )
            textTable.add(rating).top().left().padBottom(2f)

            table.add(textTable).padTop(6f).padLeft(10f).top().left().expand().row()

            levelsTable.add(table).size(195f, 60f).padBottom(16f).row()
        }
    }

    private fun showSelectionDialog(config: BattleConfig, selectionState: SelectionState) {
        selectionDialog.resetBarAnimations()
        selectionDialog.setConfig(config, selectionState, game.player)

        selectionBg.isVisible = true
        selectionBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION)))

        selectionTable.isVisible = true
        selectionTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(DIALOG_FADE_DURATION),
            Actions.run {
                if (selectionState != SelectionState.Locked) selectionDialog.startBarAnimations(config)
            }))
    }

    private fun hideSelectionDialog() {
        selectionBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { selectionBg.isVisible = false }))
        selectionTable.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(DIALOG_FADE_DURATION),
            Actions.run { selectionTable.isVisible = false }))
    }

    private fun getWorldSelectButton(isLeft: Boolean): ImageButton {
        val key = if (isLeft) "left" else "right"
        val style = ImageButton.ImageButtonStyle().apply {
            imageUp = TextureRegionDrawable(game.res.getTexture("world_select_arrow_${key}_up"))
            imageDown = TextureRegionDrawable(game.res.getTexture("world_select_arrow_${key}_down"))
            imageOver = TextureRegionDrawable(game.res.getTexture("world_select_arrow_${key}_down"))
        }
        return ImageButton(style)
    }

    private fun onWorldSelectButtonClicked(isLeft: Boolean) {
        game.soundManager.onWorldSelectClicked()
        if (isLeft) selectedWorldId--
        else selectedWorldId++

        leftWorldSelectButton.isVisible = selectedWorldId > 0
        rightWorldSelectButton.isVisible = selectedWorldId < NUM_WORLDS - 1

        worldLabel.setText("WORLD ${selectedWorldId + 1}")

        setLevelsTable()
        scrollToCurrentLevel()
    }

    private fun scrollToCurrentLevel() {
        if (selectedWorldId == game.player.currWorldId) {
            scrollPane.scrollTo(0f, 72f * (game.res.getBattleConfigs(selectedWorldId).size - game.player.currLevelId), 195f, 60f)
        } else {
            scrollPane.scrollY = 0f
        }
    }
}