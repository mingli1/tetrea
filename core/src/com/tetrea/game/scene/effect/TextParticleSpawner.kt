package com.tetrea.game.scene.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Pool
import com.tetrea.game.res.Resources
import java.util.Random

class TextParticleSpawner(res: Resources, stage: Stage) {

    private val particles = mutableListOf<NumberParticle>()
    private val pool: Pool<NumberParticle>

    init {
        val rand = Random()
        pool = object : Pool<NumberParticle>() {
            override fun newObject(): NumberParticle = NumberParticle(res, rand, stage)
        }
    }

    fun spawn(
        text: String,
        color: Color,
        x: Float,
        y: Float,
        lifetime: Float = DEFAULT_LIFETIME,
        fontScale: Float = DEFAULT_FONT_SCALE,
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
        val particle = pool.obtain().apply {
            create(text, color, fontScale, lifetime)
            initVectors(x, y, zi, vxScale, vyScale, vzScale, zNegVxScale, zNegVyScale, zNegVzScale, zPosVzScale, useGaussian)
        }
        particles.add(particle)
    }

    fun update(dt: Float) {
        for (i in particles.size - 1 downTo 0) {
            with (particles[i]) {
                update(dt)
                if (shouldRemove) {
                    particles.removeAt(i)
                    pool.free(this)
                }
            }
        }
    }
}