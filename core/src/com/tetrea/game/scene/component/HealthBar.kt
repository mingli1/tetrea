package com.tetrea.game.scene.component

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import kotlin.math.max
import kotlin.math.min

class HealthBar(
    private val movementDelay: Float,
    private val x: Float,
    private val y: Float,
    private val maxValue: Float,
    private val maxWidth: Float,
    private val height: Float,
    private val barTexture: TextureRegion,
    private val decayTexture: TextureRegion,
    private val restoreTexture: TextureRegion,
    private val interpolator: Interpolation = Interpolation.linear
) {

    private var currBarWidth = maxWidth
    private var prevBarWidth = 0f
    private var finalBarWidth = 0f
    private var prevChangeWidth = 0f
    private var currChangeBarWidth = 0f
    private var currValue = maxValue
    private var animate = false
    private var timer = 0f
    private var isDecay = false

    fun applyChange(value: Float, decay: Boolean) {
        if ((decay && currValue <= 0) || (!decay && currValue >= maxValue)) return
        if (animate) finishCurrAnimation()

        isDecay = decay
        prevChangeWidth = getWidth(if (currValue == maxValue) value else min(maxValue - currValue, value))
        currValue = if (decay) max(0f, currValue - value) else min(maxValue, currValue + value)
        prevBarWidth = currBarWidth
        finalBarWidth = getWidth(currValue)

        if (decay) currBarWidth = finalBarWidth
        currChangeBarWidth = prevChangeWidth
        if (currBarWidth < maxWidth) animate = true
    }

    fun update(dt: Float) {
        if (animate) {
            timer += dt

            if (isDecay) {
                currChangeBarWidth = interpolator.apply(prevChangeWidth, 0f, timer / movementDelay)
            } else {
                currBarWidth = interpolator.apply(prevBarWidth, finalBarWidth, timer / movementDelay)
            }

            if (timer >= movementDelay) {
                finishCurrAnimation()
            }
        }
    }

    fun render(batch: Batch) {
        if (animate) {
            if (isDecay) batch.draw(decayTexture, x + currBarWidth, y, currChangeBarWidth, height)
            else batch.draw(restoreTexture, x + prevBarWidth, y, currChangeBarWidth, height)
        }
        batch.draw(barTexture, x, y, currBarWidth, height)
    }

    fun reset() {
        currBarWidth = maxWidth
        prevBarWidth = 0f
        finalBarWidth = 0f
        prevChangeWidth = 0f
        currChangeBarWidth = 0f
        currValue = maxValue
    }

    private fun getWidth(value: Float) = (value / maxValue) * maxWidth

    private fun finishCurrAnimation() {
        timer = 0f
        animate = false
        if (isDecay) {
            currChangeBarWidth = 0f
        } else {
            currBarWidth = finalBarWidth
        }
    }
}