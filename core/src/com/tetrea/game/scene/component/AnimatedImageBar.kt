package com.tetrea.game.scene.component

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.Image

class AnimatedImageBar(
    x: Float,
    y: Float,
    private val speed: Float,
    private val vertical: Boolean,
    maxValue: Float,
    maxWidth: Float,
    maxHeight: Float,
    barTexture: TextureRegion,
    private val interpolator: Interpolation = Interpolation.linear
) : AnimatedBar(x, y, speed, vertical, maxValue, maxWidth, maxHeight, barTexture, interpolator) {

    val image = Image(barTexture).apply {
        setPosition(x, y)
        if (vertical) width = maxWidth else height = maxHeight
    }

    override fun update(dt: Float) {
        if (animate) {
            timer += dt
            curr = interpolator.apply(start, end, timer / speed)

            if (vertical) image.height = curr
            else image.width = curr

            if (timer >= speed) {
                finishAnimation()
            }
        }
    }

    override fun reset() {
        super.reset()
        if (vertical) image.height = 0f
        else image.width = 0f
    }

    override fun finishAnimation() {
        super.finishAnimation()
        if (vertical) image.height = curr
        else image.width = curr
    }
}