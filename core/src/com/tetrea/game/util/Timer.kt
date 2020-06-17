package com.tetrea.game.util

class Timer(
    private val delay: Float,
    private val onComplete: () -> Unit,
    private val continuous: Boolean = false
) {

    private var start = false
    private var timer = 0f

    fun start() {
        if (start) return
        start = true
        timer = 0f
    }

    fun update(dt: Float) {
        if (continuous) {
            timer += dt
            if (timer >= delay) {
                onComplete()
                timer = 0f
            }
        } else {
            if (start) {
                timer += dt
                if (timer >= delay) {
                    onComplete()
                    timer = 0f
                    start = false
                }
            }
        }
    }

    fun reset() {
        start = false
        timer = 0f
    }
}