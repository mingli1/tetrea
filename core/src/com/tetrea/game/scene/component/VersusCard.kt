package com.tetrea.game.scene.component

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.battle.Enemy
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.global.Player
import com.tetrea.game.res.AVATAR_SIZE
import com.tetrea.game.res.GAME_VERSUS_BLUE
import com.tetrea.game.res.GAME_VERSUS_ORANGE
import com.tetrea.game.res.Resources

private const val DELAY = 0.6f

class VersusCard(
    private val stage: Stage,
    onScreen: Boolean,
    private val isEnemy: Boolean,
    private val onFinished: () -> Unit,
    res: Resources,
    enemy: Enemy? = null,
    player: Player? = null
) {

    private val enemyYIn = -1f
    private val enemyYOut = -(stage.height / 2 + 1) - 1
    private val playerYIn = stage.height / 2
    private val playerYOut = stage.height + 1

    private var startY = if (onScreen) {
        if (isEnemy) enemyYIn else playerYIn
    } else {
        if (isEnemy) enemyYOut else playerYOut
    }
    private var targetY = if (onScreen) {
        if (isEnemy) enemyYOut else playerYOut
    } else {
        if (isEnemy) enemyYIn else playerYIn
    }
    private var currY = startY

    private val bg = Image(res.getNinePatch(if (isEnemy) "versus_orange_bg" else "versus_blue_bg")).apply {
        setSize(this@VersusCard.stage.width + 2, this@VersusCard.stage.height / 2 + 1)
    }
    private val avatar = Image().apply { setSize(AVATAR_SIZE * 2, AVATAR_SIZE * 2) }
    private val name = res.getLabel(fontScale = 2f).apply { width = 172f }
    private val rating = res.getLabel(fontScale = 1.25f).apply { width = 172f }

    private var start = false
    private var timer = 0f

    init {
        stage.addActor(bg)

        if (isEnemy) {
            enemy?.let {
                avatar.drawable = TextureRegionDrawable(res.getTexture(it.avatar))
                name.setText(it.name)
                name.setAlignment(Align.right)
                rating.setText("RATING: ${Elo.getRating(it)}")
                rating.color = GAME_VERSUS_BLUE
                rating.setAlignment(Align.right)
            }
        } else {
            player?.let {
                avatar.drawable = TextureRegionDrawable(res.getTexture(it.avatar))
                name.setText(it.name)
                name.setAlignment(Align.left)
                rating.setText("RATING: ${it.rating}")
                rating.color = GAME_VERSUS_ORANGE
                rating.setAlignment(Align.left)
            }
        }

        stage.addActor(avatar)
        stage.addActor(name)
        stage.addActor(rating)

        if (!onScreen) start = true
        else updateActorPositions()
    }

    fun start() {
        start = true
    }

    fun update(dt: Float) {
        if (start) {
            timer += dt
            currY = Interpolation.linear.apply(startY, targetY, timer / DELAY)
            updateActorPositions()

            if (timer >= DELAY) {
                currY = targetY
                updateActorPositions()
                onFinished()
                start = false
                timer = 0f
            }
        }
    }

    private fun updateActorPositions() {
        bg.setPosition(-1f, currY)
        if (isEnemy) {
            avatar.setPosition(stage.width - 20f - (AVATAR_SIZE * 2), currY + (stage.height / 4 - AVATAR_SIZE))
            name.setPosition(4f, currY + (stage.height / 4 + 12f))
            rating.setPosition(4f, currY + (stage.height / 4 - 20f))
        } else {
            avatar.setPosition(20f, currY + (stage.height / 4 - AVATAR_SIZE))
            name.setPosition(86f, currY + (stage.height / 4 + 12f))
            rating.setPosition(86f, currY + (stage.height / 4 - 20f))
        }
    }
}