package com.tetrea.game.res

import com.badlogic.gdx.audio.Sound
import com.tetrea.game.global.Settings

class SoundManager(private val res: Resources, private val settings: Settings) {

    fun onPrimaryButtonClicked() = play(res.sounds["std_button_click"])

    fun onSecondaryButtonClicked() = play(res.sounds["sec_button_click"])

    fun onCheckboxClicked() = play(res.sounds["checkbox_click"])

    fun onWorldSelectClicked() = play(res.sounds["world_select_click"])

    fun onLevelSelectClicked() = play(res.sounds["level_select_click"])

    fun onBattleStart() = play(res.sounds["battle_start"])

    fun onClear(lines: Int) = play(res.sounds["clear$lines"])

    fun onCombo(combo: Int) {
        if (combo - 1 > 16) play(res.sounds["combo16"])
        else play(res.sounds["combo${combo - 1}"])
    }

    fun onB2b() = play(res.sounds["b2b"])

    fun onDead() = play(res.sounds["died"])

    fun onHold() = play(res.sounds["hold"])

    fun onLock() = play(res.sounds["lock"])

    fun onMove() = play(res.sounds["move"])

    fun onPerfectClear() = play(res.sounds["perfect_clear"])

    fun onRotate() = play(res.sounds["rotate"])

    fun onTSpin() = play(res.sounds["tspin"])

    private fun play(sound: Sound?) {
        if (sound == null) return
        if (!settings.muteSound) {
            sound.play(settings.soundVolume)
        }
    }
}