package com.tetrea.game.scene.effect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Pool
import com.tetrea.game.res.Resources
import java.util.*

class NumberParticleSpawner(res: Resources, stage: Stage) {

    private val particles = mutableListOf<NumberParticle>()
    private val pool: Pool<NumberParticle>

    init {
        val rand = Random()
        pool = object : Pool<NumberParticle>() {
            override fun newObject(): NumberParticle = NumberParticle(res, rand, stage)
        }
    }

    fun spawn(number: Int, color: Color, x: Float, y: Float) {
        val particle = pool.obtain().apply {
            create(number, color)
            initVectors(x, y)
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

    fun reset() {
        particles.clear()
        pool.clear()
    }
}