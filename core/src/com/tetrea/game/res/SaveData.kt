package com.tetrea.game.res

import com.squareup.moshi.Json
import com.tetrea.game.global.Player
import com.tetrea.game.global.Settings

data class SaveData(
    @Json(name = "player") val player: Player,
    @Json(name = "settings") val settings: Settings
)