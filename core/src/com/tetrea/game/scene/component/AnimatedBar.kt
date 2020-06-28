package com.tetrea.game.scene.component

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import kotlin.math.max
import kotlin.math.min

open class AnimatedBar(
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

    protected var curr = 0f
    private var currValue = 0f
    protected var start = 0f
    protected var end = 0f
    protected var animate = false
    protected var timer = 0f
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

    open fun update(dt: Float) {
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

    open fun reset() {
        curr = 0f
        currValue = 0f
        start = 0f
        end = 0f
        animate = false
        timer = 0f
        isDecay = false
    }

    private fun getDimen(value: Float) = (value / maxValue) * (if (vertical) maxHeight else maxWidth)

    open fun finishAnimation() {
        timer = 0f
        animate = false
        curr = end
    }
}