package com.tetrea.game.global

import com.squareup.moshi.Json
import com.tetrea.game.battle.BattleRecord

data class Player(
    @Json(name = "name") val name: String = "PLAYER",
    @Json(name = "avatar") val avatar: String = "enemy_ittzzi",
    @Json(name = "rating") var rating: Float = 800f,
    @Json(name = "currWorldId") var currWorldId: Int = 0,
    @Json(name = "currLevelId") var currLevelId: Int = 0,
    @Json(name = "battleRecords") private val battleRecords: MutableMap<String, BattleRecord> = mutableMapOf()
) {

    fun getRecord(key: String) = battleRecords[key] ?: BattleRecord()
}