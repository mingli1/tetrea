package com.tetrea.game.res

import com.badlogic.gdx.audio.Sound
import com.tetrea.game.global.Settings

class SoundManager(private val res: Resources, private val settings: Settings) {

    fun onPrimaryButtonClicked() = play(res.sounds["std_button_click"])

    fun onSecondaryButtonClicked() = play(res.sounds["sec_button_click"])

    fun onCheckboxClicked() = play(res.sounds["checkbox_click"])

    private fun play(sound: Sound?) {
        if (sound == null) return
        if (!settings.muteSound) {
            sound.play(settings.soundVolume)
        }
    }
}