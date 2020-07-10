package com.tetrea.game.scene.dialog

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.extension.onTap
import com.tetrea.game.global.Player
import com.tetrea.game.res.*
import com.tetrea.game.scene.component.AnimatedImageBar
import com.tetrea.game.screen.LevelSelectScreen
import com.tetrea.game.screen.SelectionState

class SelectionDialog(private val res: Resources, private val screen: LevelSelectScreen) : Table() {

    private val avatar = Image()
    private val title = res.getLabel(fontScale = 1f)
    private val desc = res.getLabel()
    private val rating = res.getLabel(color = GAME_ORANGE)
    private val h2hLabel = res.getLabel(text = "ALL TIME RECORD")
    private val h2h = res.getLabel()
    private val attackPatternLabel = res.getLabel(text = "ATTACK PATTERN")
    private val attackPatterns = res.getLabel()

    private val statsLabel = res.getLabel(text = "STATS")
    private val atkBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("atk"))
    private val defBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("def"))
    private val spdBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("spd"))

    private val battleButton = res.getNinePatchTextButton("", "orange_button",
        colorUp = Color.WHITE, colorDown = Color.WHITE, disabledKey = "light_gray_bg")

    private lateinit var battleConfig: BattleConfig

    init {
        touchable = Touchable.enabled
        add(avatar).padTop(6f).padLeft(10f)
        val textTable = Table().apply {
            add(title).top().left().expandX().padBottom(2f).row()
            add(desc).top().left().expandX().padBottom(2f).row()
            add(rating).top().left().expandX().padBottom(2f)
        }
        add(textTable).width(112f).padTop(6f).padLeft(10f).top().left().row()
        add(Image(res.getTexture("white"))).width(170f).padTop(4f).colspan(2).row()

        add(h2hLabel).padLeft(12f).padTop(8f).top().left().colspan(2).row()
        add(h2h).padLeft(12f).padTop(4f).top().left().colspan(2).row()

        add(attackPatternLabel).padLeft(12f).padTop(8f).top().left().colspan(2).row()
        add(attackPatterns).padLeft(12f).padTop(4f).top().left().colspan(2).row()

        add(statsLabel).padLeft(12f).padTop(8f).top().left().colspan(2).row()

        val atkTable = Table().apply {
            add(res.getLabel("ATK")).padRight(6f)
            val stack = Stack()
            stack.add(Image(res.getNinePatch("dark_gray_bg")))
            stack.add(Table().apply { addActor(atkBar.image) })

            add(stack).size(144f, 10f).expandX()
        }
        add(atkTable).padLeft(12f).padTop(6f).top().left().colspan(2).row()

        val defTable = Table().apply {
            add(res.getLabel("DEF")).padRight(6f)
            val stack = Stack()
            stack.add(Image(res.getNinePatch("dark_gray_bg")))
            stack.add(Table().apply { addActor(defBar.image) })

            add(stack).size(144f, 10f).expandX()
        }
        add(defTable).padLeft(12f).padTop(6f).top().left().colspan(2).row()

        val spdTable = Table().apply {
            add(res.getLabel("SPD")).padRight(6f)
            val stack = Stack()
            stack.add(Image(res.getNinePatch("dark_gray_bg")))
            stack.add(Table().apply { addActor(spdBar.image) })

            add(stack).size(144f, 10f).expandX()
        }
        add(spdTable).padLeft(12f).padTop(6f).top().left().colspan(2).row()

        add(battleButton).padTop(6f).size(170f, 35f).colspan(2).expand()
        battleButton.onTap {
            battleButton.touchable = Touchable.disabled
            screen.onBattleButtonClicked(battleConfig)
        }
    }

    fun update(dt: Float) {
        atkBar.update(dt)
        defBar.update(dt)
        spdBar.update(dt)
    }

    fun setConfig(config: BattleConfig, selectionState: SelectionState, player: Player) {
        battleConfig = config

        val labelColor =  when (selectionState) {
            SelectionState.Completed -> GAME_WHITE_BLUE
            SelectionState.Active -> GAME_YELLOW
            else -> GAME_LIGHT_GRAY
        }

        background = NinePatchDrawable(when (selectionState) {
            SelectionState.Completed -> res.getNinePatch("gray_blue_bg")
            SelectionState.Active -> res.getNinePatch("purple_bg")
            else -> res.getNinePatch("dark_gray_bg")
        })
        avatar.drawable = TextureRegionDrawable(res.getTexture(config.enemy.avatar))

        title.run {
            setText(when (selectionState) {
                SelectionState.Completed -> "DEFEATED"
                SelectionState.Active -> "VS. ${config.enemy.name}"
                else -> "VS. ???"
            })
            color = labelColor
        }

        when (selectionState) {
            SelectionState.Completed -> {
                player.getRecord(config.compositeKey).bestScore?.let {
                    desc.setText("YOU ${it.x} - ${it.y} ENEMY")
                }
            }
            SelectionState.Active -> {
                desc.setText("${player.getRecord(config.compositeKey).attempts} ATTEMPT(S)")
            }
            else -> desc.setText("LOCKED")
        }

        rating.setText(if (selectionState == SelectionState.Completed || selectionState == SelectionState.Active) {
            "RATING: ${config.enemy.rating.toInt()}"
        } else {
            "RATING: ???"
        })

        h2hLabel.color = labelColor
        player.getRecord(config.compositeKey).let {
            h2h.setText("YOU ${it.allTimeRecord.x} - ${it.allTimeRecord.y} ENEMY")
        }

        val attackPatternsText = when {
            selectionState == SelectionState.Locked -> "???"
            config.attackPatterns.isEmpty() -> "RANDOM"
            else -> config.attackPatterns.joinToString { it.text }
        }
        attackPatterns.setText(attackPatternsText)
        attackPatternLabel.color = labelColor

        statsLabel.color = labelColor

        battleButton.isDisabled = selectionState == SelectionState.Locked
        battleButton.touchable = if (selectionState == SelectionState.Locked) Touchable.disabled else Touchable.enabled
        battleButton.setText(if (selectionState == SelectionState.Locked) {
            "BATTLE (FT?)"
        } else {
            "BATTLE (FT${config.firstTo})"
        })
    }

    fun resetBarAnimations() {
        atkBar.reset()
        defBar.reset()
        spdBar.reset()
    }

    fun startBarAnimations(config: BattleConfig) {
        atkBar.applyChange(config.enemy.attack.toFloat(), false)
        defBar.applyChange(config.enemy.defense.toFloat(), false)
        spdBar.applyChange(config.enemy.speed.toFloat(), false)
    }
}