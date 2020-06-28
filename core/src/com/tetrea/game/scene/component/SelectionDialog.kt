package com.tetrea.game.scene.component

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.res.*
import com.tetrea.game.screen.SelectionState

class SelectionDialog(private val res: Resources) : Table() {

    private val avatar = Image()
    private val title = res.getLabel(fontScale = 1f)
    private val desc = res.getLabel()
    private val rating = res.getLabel(color = GAME_ORANGE)
    private val attackPatternLabel = res.getLabel(text = "ATTACK PATTERN")
    private val attackPatterns = res.getLabel()

    private val statsLabel = res.getLabel(text = "STATS")
    private val atkBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("atk"))
    private val defBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("def"))
    private val spdBar = AnimatedImageBar(1f, 1f, 0.5f, false, 100f, 142f, 8f, res.getTexture("spd"))

    init {
        touchable = Touchable.enabled
        //debug = true
        add(avatar).right().padTop(6f)
        val textTable = Table().apply {
            add(title).top().left().padBottom(2f).row()
            add(desc).top().left().padBottom(2f).row()
            add(rating).top().left().padBottom(2f)
        }
        add(textTable).padTop(6f).padLeft(10f).top().left().row()
        add(Image(res.getTexture("white"))).width(170f).padTop(4f).colspan(2).row()
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
        add(spdTable).padLeft(12f).padTop(6f).top().left().colspan(2).expand().row()
    }

    fun update(dt: Float) {
        atkBar.update(dt)
        defBar.update(dt)
        spdBar.update(dt)
    }

    fun setConfig(config: BattleConfig, selectionState: SelectionState) {
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
                // todo get score from save file
                desc.setText("YOU 3 - 0 ENEMY")
            }
            SelectionState.Active -> {
                // todo get attempts from save file
                desc.setText("3 ATTEMPTS")
            }
            else -> desc.setText("LOCKED")
        }

        rating.setText(if (selectionState == SelectionState.Completed || selectionState == SelectionState.Active) {
            "RATING: ${config.enemy.rating}"
        } else {
            "RATING: ???"
        })

        var attackPatternText = "- "
        attackPatternText += if (config.attackPatterns.isEmpty()) "RANDOM" else
            config.attackPatterns.joinToString { it.text }
        attackPatterns.setText(attackPatternText)
        attackPatternLabel.color = labelColor

        statsLabel.color = labelColor
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