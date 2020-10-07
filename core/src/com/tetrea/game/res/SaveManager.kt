package com.tetrea.game.res

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Base64Coder
import com.tetrea.game.global.Player
import com.tetrea.game.global.Settings

private const val SAVE_EXTENSION = ".tetr"
private const val SAVE_FILE_PATH = "save/save$SAVE_EXTENSION"

class SaveManager(res: Resources) {

    private val saveFile = Gdx.files.local(SAVE_FILE_PATH)
    private val saveAdapter = res.moshi.adapter(SaveData::class.java)
    val saveData: SaveData

    init {
        if (saveFile.exists()) {
            saveData = checkNotNull(saveAdapter.fromJson(
                Base64Coder.decodeString(saveFile.readString())
            ))
        } else {
            saveData = SaveData(Player(), Settings())
            save()
        }
    }

    fun save() {
        saveFile.writeString(
            Base64Coder.encodeString(saveAdapter.toJson(saveData)),
            false
        )
    }
}