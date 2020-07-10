package com.tetrea.game.res

import com.squareup.moshi.Json
import com.tetrea.game.global.Player

data class SaveData(
    @Json(name = "player") val player: Player
)