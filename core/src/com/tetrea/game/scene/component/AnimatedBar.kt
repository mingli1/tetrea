package com.tetrea.game.scene.component

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import kotlin.math.max
import kotlin.math.min

class AnimatedBar(
    private val x: Float,
    private val y: Float,
    private val speed: Float,
    private val vertical: Boolean,
    private val maxValue: Float,
    private val maxWidth: Float,
    private val maxHeight: Float,
    private val barTexture: TextureRegion,
    private val interpolator: Interpolation = Interpolation.linear
) {

    private var curr = 0f
    private var currValue = 0f
    private var start = 0f
    private var end = 0f
    private var animate = false
    private var timer = 0f
    private var isDecay = false

    fun applyChange(value: Float, decay: Boolean) {
        if ((decay && currValue <= 0) || (!decay && currValue >= maxValue)) return
        if (animate) finishAnimation()

        start = getDimen(currValue)
        currValue = if (decay) max(0f, currValue - value) else min(maxValue, currValue + value)
        end = getDimen(currValue)

        isDecay = decay
        animate = true
    }

    fun update(dt: Float) {
        if (animate) {
            timer += dt
            curr = interpolator.apply(start, end, timer / speed)
            if (timer >= speed) {
                finishAnimation()
            }
        }
    }

    fun render(batch: Batch) {
        batch.draw(barTexture, x, y, if (vertical) maxWidth else curr, if (vertical) curr else maxHeight)
    }

    private fun getDimen(value: Float) = (value / maxValue) * (if (vertical) maxHeight else maxWidth)

    private fun finishAnimation() {
        timer = 0f
        animate = false
        curr = end
    }
}