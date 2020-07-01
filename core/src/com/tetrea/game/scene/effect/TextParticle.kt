package com.tetrea.game.scene.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pool
import com.tetrea.game.res.Resources
import java.util.*

const val DEFAULT_LIFETIME = 1f
const val DEFAULT_FONT_SCALE = 1.5f

const val DEFAULT_INITIAL_Z = 4f
const val DEFAULT_VX_SCALING = 0.1f
const val DEFAULT_VY_SCALING = 0.4f
const val DEFAULT_VZ_SCALING = 0.4f
const val DEFAULT_Z_NEG_VX_SCALING = 0.6f
const val DEFAULT_Z_NEG_VY_SCALING = 0.6f
const val DEFAULT_Z_NEG_VZ_SCALING = -0.5f
const val DEFAULT_Z_POS_VZ_SCALING = 0.15f

class NumberParticle(
    res: Resources,
    private val rand: Random,
    private val stage: Stage
) : Pool.Poolable {

    private val position = Vector3()
    private val velocity = Vector3()

    private var zNegVxScale = 0f
    private var zNegVyScale = 0f
    private var zNegVzScale = 0f
    private var zPosVzScale = 0f

    private var label = res.getLabel().apply { setAlignment(Align.bottom) }

    var shouldRemove = false
    private var stateTime = 0f
    private var lifetime = 0f

    fun create(text: String, color: Color, fontScale: Float = DEFAULT_FONT_SCALE, lifetime: Float = DEFAULT_LIFETIME) {
        label.setText(text)
        label.color = color
        label.setFontScale(fontScale)
        label.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(lifetime, Interpolation.slowFast)))
        this.lifetime = lifetime
        stage.addActor(label)
    }

    fun initVectors(
        originX: Float,
        originY: Float,
        zi: Float = DEFAULT_INITIAL_Z,
        vxScale: Float = DEFAULT_VX_SCALING,
        vyScale: Float = DEFAULT_VY_SCALING,
        vzScale: Float = DEFAULT_VZ_SCALING,
        zNegVxScale: Float = DEFAULT_Z_NEG_VX_SCALING,
        zNegVyScale: Float = DEFAULT_Z_NEG_VY_SCALING,
        zNegVzScale: Float = DEFAULT_Z_NEG_VZ_SCALING,
        zPosVzScale: Float = DEFAULT_Z_POS_VZ_SCALING,
        useGaussian: Boolean = true
    ) {
        position.set(originX, originY, zi)

        val vx = if (useGaussian) rand.nextGaussian().toFloat() * vxScale else vxScale
        val vy = if (useGaussian) rand.nextGaussian().toFloat() * vyScale else vyScale
        val vz = if (useGaussian) rand.nextFloat() * vzScale + zi else vzScale + zi
        velocity.set(vx, vy, vz)

        this.zNegVxScale = zNegVxScale
        this.zNegVyScale = zNegVyScale
        this.zNegVzScale = zNegVzScale
        this.zPosVzScale = zPosVzScale
    }

    fun update(dt: Float) {
        stateTime += dt
        if (stateTime >= lifetime) {
            shouldRemove = true
            stateTime = 0f
        }

        if (!shouldRemove) {
            position.x += velocity.x
            position.y += velocity.y
            position.z += velocity.z

            if (position.z > 0) {
                position.z = 0f
                velocity.z *= zNegVzScale
                velocity.x *= zNegVxScale
                velocity.y *= zNegVyScale
            }
            velocity.z += zPosVzScale

            label.setPosition(position.x, position.y - position.z)
        }
    }

    override fun reset() {
        label.remove()
        label.clearActions()
        position.set(0f, 0f, 0f)
        velocity.set(0f, 0f, 0f)
        shouldRemove = false
        stateTime = 0f
        lifetime = 0f
        zNegVyScale = 0f
        zNegVxScale = 0f
        zNegVzScale = 0f
        zPosVzScale = 0f
    }
}