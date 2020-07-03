package com.tetrea.game.global

import com.tetrea.game.battle.BattleRecord
import com.tetrea.game.util.Int2

class Player {
    val name = "PLAYER"
    val avatar = "enemy_ittzzi"
    val rating = 3333
    val currWorldId = 0
    val currLevelId = 4
    val battleRecords = mapOf(
        "0-0" to BattleRecord(true, 3, Int2(3, 2), Int2(1, 0)),
        "0-1" to BattleRecord(true, 1, Int2(3, 0), Int2(14, 2)),
        "0-2" to BattleRecord(true, 1, Int2(3, 2), Int2(1, 0)),
        "0-3" to BattleRecord(true, 6, Int2(3, 2), Int2(1, 0)),
        "0-4" to BattleRecord(false, 7, allTimeRecord = Int2(0, 7)),
        "0-5" to BattleRecord(),
        "0-6" to BattleRecord(),
        "0-7" to BattleRecord()
    )
}