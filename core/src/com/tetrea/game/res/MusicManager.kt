package com.tetrea.game.res

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Interpolation
import com.tetrea.game.global.Settings

private const val DEFAULT_BGM_FADE = 2f
private const val DEFAULT_BATTLE_MUSIC_FADE = 2f

class MusicManager(private val res: Resources, private val settings: Settings) {

    var inBattle = false
    private var currBgMusic: Music? = null
    private var currBattleMusic: Music? = null

    private var currBgmVolume = 0f
    private var fadeOutBgm = false
    private var fadeOutBgmTimer = 0f
    private var fadeInBgm = false
    private var fadeInBgmTimer = 0f

    private var currBattleVolume = 0f
    private var fadeOutBattleMusic = false
    private var fadeOutBattleMusicTimer = 0f

    fun update(dt: Float) {
        if (fadeOutBgm) {
            fadeOutBgmTimer += dt
            currBgmVolume = Interpolation.linear.apply(settings.musicVolume, 0f, fadeOutBgmTimer / DEFAULT_BGM_FADE)
            setBgmVolume(currBgmVolume)
            if (fadeOutBgmTimer >= DEFAULT_BGM_FADE) {
                currBgMusic?.pause()
                fadeOutBgmTimer = 0f
                fadeOutBgm = false
            }
        }
        if (fadeInBgm) {
            fadeInBgmTimer += dt
            currBgmVolume = Interpolation.linear.apply(0f, settings.musicVolume, fadeInBgmTimer / DEFAULT_BGM_FADE)
            setBgmVolume(currBgmVolume)
            if (fadeInBgmTimer >= DEFAULT_BGM_FADE) {
                fadeInBgmTimer = 0f
                fadeInBgm = false
            }
        }
        if (fadeOutBattleMusic) {
            fadeOutBattleMusicTimer += dt
            currBattleVolume = Interpolation.linear.apply(settings.musicVolume, 0f, fadeOutBattleMusicTimer / DEFAULT_BATTLE_MUSIC_FADE)
            setBattleMusicVolume(currBattleVolume)
            if (fadeOutBattleMusicTimer >= DEFAULT_BATTLE_MUSIC_FADE) {
                stopBattleMusic()
                fadeOutBattleMusicTimer = 0f
                fadeOutBattleMusic = false
            }
        }
    }

    fun startBackgroundMusic() {
        if (settings.muteMusic) {
            mute()
        } else {
            setVolume(settings.musicVolume)
        }
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

        fadeInBackgroundMusic()
    }

    fun startBattleMusic() {
        setBattleMusicVolume(settings.musicVolume)
        currBattleMusic = res.battleMusic.random()
        currBattleMusic?.play()
    }

    fun stopBattleMusic() {
        currBattleMusic?.stop()
        currBattleMusic = null
    }

    fun fadeOutBackgroundMusic() {
        if (settings.muteMusic) return
        setBgmVolume(settings.musicVolume)
        fadeOutBgm = true
    }

    fun fadeInBackgroundMusic() {
        if (settings.muteMusic) return
        currBgMusic?.play()
        setBgmVolume(0f)
        fadeInBgm = true
    }

    fun pauseBackgroundMusic() {
        if (!inBattle) currBgMusic?.pause()
    }

    fun resumeBackgroundMusic() {
        if (!inBattle) currBgMusic?.play()
    }

    fun fadeOutBattleMusic() {
        if (settings.muteMusic) return
        setBattleMusicVolume(settings.musicVolume)
        fadeOutBattleMusic = true
    }

    fun pauseBattleMusic() {
        currBattleMusic?.pause()
    }

    fun resumeBattleMusic() {
        currBattleMusic?.play()
    }

    fun setVolume(volume: Float) {
        setBgmVolume(volume)
        setBattleMusicVolume(volume)
    }

    fun mute() = setVolume(0f)

    fun unmute() = setVolume(settings.musicVolume)

    private fun setBattleMusicVolume(volume: Float) {
        if (volume < 0f || volume >= 1f) return
        res.battleMusic.forEach { it.volume = volume }
    }

    private fun setBgmVolume(volume: Float) {
        if (volume < 0f || volume >= 1f) return
        res.bgMusic.forEach { it.volume = volume }
    }
}