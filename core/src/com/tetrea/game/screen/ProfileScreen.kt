package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.res.GAME_LIGHT_ORANGE
import com.tetrea.game.res.GAME_LOSS_RED
import com.tetrea.game.res.GAME_VICTORY_GREEN
import com.tetrea.game.scene.component.AnimatedImageBar
import com.tetrea.game.scene.dialog.MessageDialog
import java.util.*

private const val MAX_APM = 150f
private const val MAX_PPS = 4f
private const val MAX_NAME_LENGTH = 7

class ProfileScreen(game: TetreaGame) : BaseScreen(game) {

    private lateinit var parentTable: Table
    private lateinit var contentTable: Table
    private lateinit var editBg: Table
    private lateinit var editTable: Table
    private lateinit var playerNameLabel: Label
    private lateinit var playerAvatar: Image
    private var editAvatar: String? = null

    private val apmBar = AnimatedImageBar(1f, 1f, 0.5f, false, MAX_APM, 200f, 8f, game.res.getTexture("atk"))
    private val ppsBar = AnimatedImageBar(1f, 1f, 0.5f, false, MAX_PPS, 200f, 8f, game.res.getTexture("spd"))

    private val nameLengthDialog = MessageDialog(
        "NAME CANNOT EXCEED $MAX_NAME_LENGTH CHARACTERS",
        {},
        game.res,
        game.soundManager,
        title = "EDIT FAILED",
        windowStyleKey = "orange_bg",
        buttonStyleKey = "orange_button"
    )

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
            onTap {
                navigateTo(HOME_SCREEN)
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(backButton).top().left().size(76f, 28f).padTop(6f)
        val editButton = game.res.getNinePatchTextButton(
            text = "EDIT",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                showEditDialog()
                game.soundManager.onPrimaryButtonClicked()
            }
        }
        parentTable.add(editButton).top().right().size(76f, 28f).padTop(6f).row()

        contentTable = Table()
        createOverviewTable()
        createArcadeStatsTable()
        if (game.player.matchHistory.isNotEmpty()) createMatchHistoryTable()
        createBattleStatsTable()

        val scrollPane = ScrollPane(contentTable).apply {
            setOverscroll(false, false)
            setCancelTouchFocus(false)
            fadeScrollBars = false
            layout()
        }
        parentTable.add(scrollPane).expandY().colspan(2).top().padTop(24f)

        editBg = Table().apply {
            background = TextureRegionDrawable(game.res.getTexture("black_150_opacity"))
            setFillParent(true)
            isVisible = false
            touchable = Touchable.enabled
        }
        stage.addActor(editBg)
        editTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
        }
        editBg.add(editTable).size(196f, 220f)
        createEditTable()

        Gdx.input.inputProcessor = multiplexer
    }

    override fun update(dt: Float) {
        apmBar.update(dt)
        ppsBar.update(dt)
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

        playerAvatar = Image(game.res.getTexture(game.player.avatar))
        playerNameLabel = game.res.getLabel(text = game.player.name,fontScale = 1f)
        val rating = game.res.getLabel(
            text = "RATING: ${game.player.rating.toInt()} (${game.player.maxRating.toInt()} PEAK)",
            color = GAME_LIGHT_ORANGE
        )

        overviewTable.add(playerAvatar).padTop(2f).padLeft(4f).expandX()
        val textTable = Table().apply {
            add(playerNameLabel).top().left().expandX().padBottom(2f).row()
            add(rating).top().left().expandX().padBottom(2f)
        }
        overviewTable.add(textTable).width(170f).padTop(6f).top().left().row()
        overviewTable.add(Image(game.res.getTexture("white"))).width(200f).padTop(4f).colspan(2).row()
        overviewTable.add(game.res.getLabel("MATCHMAKING", color = GAME_LIGHT_ORANGE)).padLeft(8f).padTop(8f).top().left().colspan(2).row()

        val apm = String.format("%.2f", if (game.player.battleStats.getApm() > MAX_APM) MAX_APM else
            game.player.battleStats.getApm())
        val pps = String.format("%.2f", if (game.player.battleStats.getPps() > MAX_PPS) MAX_PPS else
            game.player.battleStats.getPps())

        overviewTable.add(game.res.getLabel("$apm AVERAGE APM")).padLeft(8f).padTop(6f).top().left().colspan(2).row()
        val apmStack = Stack().apply {
            add(Image(game.res.getNinePatch("dark_gray_bg")))
            add(Table().apply { addActor(apmBar.image) })
        }
        overviewTable.add(apmStack).size(200f, 10f).padLeft(8f).padTop(4f).top().left().colspan(2).row()

        overviewTable.add(game.res.getLabel("$pps AVERAGE PPS")).padLeft(8f).padTop(8f).top().left().colspan(2).row()
        val ppsStack = Stack().apply {
            add(Image(game.res.getNinePatch("dark_gray_bg")))
            add(Table().apply { addActor(ppsBar.image) })
        }
        overviewTable.add(ppsStack).size(200f, 10f).padLeft(8f).padTop(4f).top().left().colspan(2).row()

        val matchesTable = Table().apply {
            add(game.res.getLabel("MATCHES PLAYED", color = GAME_LIGHT_ORANGE)).expandX().left()
            add(game.res.getLabel(game.player.battleStats.totalMatches.toString())).expandX().right()
        }
        overviewTable.add(matchesTable).colspan(2).width(200f).padTop(8f).row()

        val allTimeRecordTable = Table().apply {
            add(game.res.getLabel("ALL TIME RECORD", color = GAME_LIGHT_ORANGE)).expandX().left()
            add(game.res.getLabel("${game.player.battleStats.wins}W - ${game.player.battleStats.losses}L")).expandX().right()
        }
        overviewTable.add(allTimeRecordTable).colspan(2).padTop(4f).width(200f).row()

        val winrate = String.format("%.2f", if (game.player.battleStats.totalMatches == 0) 0f else
            game.player.battleStats.wins.toFloat() / game.player.battleStats.totalMatches * 100)
        val winrateTable = Table().apply {
            add(game.res.getLabel("WINRATE", color = GAME_LIGHT_ORANGE)).expandX().left()
            add(game.res.getLabel("$winrate%")).expandX().right()
        }
        overviewTable.add(winrateTable).colspan(2).padTop(4f).padBottom(8f).width(200f)

        contentTable.add(overviewTable).width(220f).padBottom(16f).row()

        apmBar.applyChange(game.player.battleStats.getApm(), false)
        ppsBar.applyChange(game.player.battleStats.getPps(), false)
    }

    private fun createArcadeStatsTable() {
        val statsTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
        }
        statsTable.add(game.res.getLabel("ARCADE STATS", color = GAME_LIGHT_ORANGE, fontScale = 1f))
            .expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        val statsMap = game.player.arcadeStats.getLabeledPairs()
        statsMap.forEach { (label, value) ->
            val labelText = game.res.getLabel(text = label, color = GAME_LIGHT_ORANGE)
            val statText = game.res.getLabel(text = value)
            statsTable.add(labelText).expandX().left().padLeft(8f).padTop(4f)
                .padBottom(if (label == "CHEESE LEAST BLOCKS") 8f else 0f)
            statsTable.add(statText).expandX().right().padRight(8f).padTop(5f)
                .padBottom(if (label == "CHEESE LEAST BLOCKS") 8f else 0f).row()
        }

        contentTable.add(statsTable).width(220f).padBottom(16f).row()
    }

    private fun createMatchHistoryTable() {
        val historyTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
        }

        historyTable.add(game.res.getLabel("MATCH HISTORY", color = GAME_LIGHT_ORANGE, fontScale = 1f))
            .expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        game.player.matchHistory.forEach {
            val recordTable = Table()
            val playerTable = Table().apply {
                add(game.res.getLabel("YOU")).row()
                add(game.res.getLabel(it.playerRating.toString(), fontScale = 0.5f, color = GAME_LIGHT_ORANGE))
            }
            val enemyTable = Table().apply {
                add(game.res.getLabel(it.enemyName)).row()
                add(game.res.getLabel(it.enemyRating.toString(), fontScale = 0.5f, color = GAME_LIGHT_ORANGE))
            }
            recordTable.add(playerTable).width(30f).left()
            recordTable.add(game.res.getLabel(
                "${it.playerScore} - ${it.enemyScore}",
                fontScale = 1f,
                color = if (it.playerScore > it.enemyScore) GAME_VICTORY_GREEN else GAME_LOSS_RED
            )).expandX()
            recordTable.add(enemyTable).width(40f).right()

            historyTable.add(recordTable).width(200f).padBottom(6f).row()
        }

        contentTable.add(historyTable).width(220f).padBottom(16f).row()
    }

    private fun createBattleStatsTable() {
        val statsTable = Table().apply {
            background = NinePatchDrawable(game.res.getNinePatch("orange_bg"))
        }
        statsTable.add(game.res.getLabel("MATCHMAKING STATS", color = GAME_LIGHT_ORANGE, fontScale = 1f))
            .expand().top().left().padTop(8f).padLeft(8f).padBottom(4f).row()

        val statsMap = game.player.battleStats.getLabeledPairs()
        statsMap.forEach { (label, value) ->
            val labelText = game.res.getLabel(text = label, color = GAME_LIGHT_ORANGE)
            val statText = game.res.getLabel(text = value)
            statsTable.add(labelText).expandX().left().padLeft(8f).padTop(4f)
                .padBottom(if (label == "PERFECT CLEARS") 8f else 0f)
            statsTable.add(statText).expandX().right().padRight(8f).padTop(5f)
                .padBottom(if (label == "PERFECT CLEARS") 8f else 0f).row()
        }

        contentTable.add(statsTable).width(220f).padBottom(24f).row()
    }

    private fun showEditDialog() {
        editBg.isVisible = true
        editBg.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f)))
    }

    private fun createEditTable() {
        val title = game.res.getLabel("EDIT PROFILE", color = GAME_LIGHT_ORANGE, fontScale = 1f)
        editTable.add(title).expandX().top().left().padTop(8f).padLeft(8f).padBottom(4f).colspan(2).row()

        val avatar = Image(game.res.getTexture(game.player.avatar))
        val randomAvatarButton = game.res.getNinePatchTextButton(
            text = "RANDOM",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                editAvatar = "${MathUtils.random(0, 24)}_avatar"
                avatar.drawable = TextureRegionDrawable(game.res.getTexture(editAvatar!!))
            }
        }
        editTable.add(avatar).top().padTop(16f).padLeft(8f)
        editTable.add(randomAvatarButton).size(90f, 30f).padTop(12f).row()

        val name = game.res.getLabel(text = game.player.name, fontScale = 1f).apply {
            setAlignment(Align.center)
        }
        val editNameButton = game.res.getNinePatchTextButton(
            text = "EDIT",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap { editName(name) }
        }
        editTable.add(name).top().width(75f).padTop(16f).padLeft(8f)
        editTable.add(editNameButton).top().size(90f, 30f).padTop(10f).row()

        val saveButton = game.res.getNinePatchTextButton(
            text = "SAVE",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                editBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f), Actions.run {
                    editBg.isVisible = false
                    game.player.name = name.text.toString()
                    editAvatar?.let {
                        game.player.avatar = it
                        playerAvatar.drawable = TextureRegionDrawable(game.res.getTexture(it))
                    }
                    game.saveManager.save()
                    playerNameLabel.setText(name.text.toString())

                    editAvatar = null
                }))
            }
        }
        editTable.add(saveButton).expandY().padTop(16f).bottom().size(172f, 30f).colspan(2).row()
        val cancelButton = game.res.getNinePatchTextButton(
            text = "CANCEL",
            key = "profile_orange_button",
            colorUp = GAME_LIGHT_ORANGE,
            colorDown = Color.WHITE
        ).apply {
            onTap {
                editBg.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(0.4f), Actions.run {
                    editBg.isVisible = false
                    editAvatar = null
                    avatar.drawable = TextureRegionDrawable(game.res.getTexture(game.player.avatar))
                    name.setText(game.player.name)
                }))
            }
        }
        editTable.add(cancelButton).expandY().bottom().size(172f, 30f).padBottom(8f).colspan(2)
    }

    private fun editName(nameLabel: Label) {
        Gdx.input.getTextInput(
            object : Input.TextInputListener {
                override fun input(text: String) {
                    if (text.length > MAX_NAME_LENGTH) {
                        nameLengthDialog.show(stage)
                    } else {
                        nameLabel.setText(text.toUpperCase(Locale.ROOT))
                    }
                }

                override fun canceled() {}
            },
            "EDIT NAME",
            nameLabel.text.toString(),
            ""
        )
    }
}