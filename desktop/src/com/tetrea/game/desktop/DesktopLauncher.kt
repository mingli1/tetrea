package com.tetrea.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.tetrea.game.global.*

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            width = S_WIDTH
            height = S_HEIGHT
            title = TITLE
            vSyncEnabled = V_SYNC_ENABLED
            resizable = RESIZABLE
            backgroundFPS = BG_FPS
            foregroundFPS = FG_FPS
        }
        LwjglApplication(TetreaGame(), config)
    }
}