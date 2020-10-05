package com.tetrea.game.desktop

import com.badlogic.gdx.Files
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
            addIcon("desktop_icon128.png", Files.FileType.Internal)
            addIcon("desktop_icon32.png", Files.FileType.Internal)
            addIcon("desktop_icon16.png", Files.FileType.Internal)
        }
        LwjglApplication(TetreaGame(), config)
    }
}