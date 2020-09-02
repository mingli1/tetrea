package com.tetrea.game.res

import com.badlogic.gdx.audio.Music

class MusicManager(private val res: Resources) {

    private var currBgMusic: Music? = null

    fun startBackgroundMusic() {
        res.bgMusic.shuffle()
        currBgMusic = res.bgMusic[0]

        for (i in res.bgMusic.indices) {
            res.bgMusic[i].setOnCompletionListener {
                if (i + 1 < res.bgMusic.size) {
                    res.bgMusic[i + 1].play()
                } else {
                    res.bgMusic[0].play()
                }
            }
        }

        currBgMusic?.play()
    }

    fun pauseBackgroundMusic() {
        currBgMusic?.pause()
    }

    fun resumeBackgroundMusic() {
        currBgMusic?.play()
    }

    fun setVolume(volume: Float) {
        res.bgMusic.forEach { it.volume = volume }
        res.battleMusic.forEach { it.volume = volume }
    }
}