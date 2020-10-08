package com.tetrea.game.scene.dialog

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.GAME_LIGHT_GRAY_BLUE
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources

class SettingsAboutDialog(res: Resources) : Table() {

    init {
        touchable = Touchable.enabled
        background = NinePatchDrawable(res.getNinePatch("gray_blue_bg"))

        add(res.getLabel("CREDITS", fontScale = 1f, color = GAME_LIGHT_GRAY_BLUE))
            .top().left().padLeft(16f).padTop(8f).row()

        val content = Table()
        val scrollPane = ScrollPane(content).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        add(scrollPane).expand().width(205f).top()

        content.add(res.getLabel("GAME", fontScale = 1f, color = GAME_YELLOW))
            .left().padTop(8f).padLeft(10f).expandX().row()
        content.add(
            res.getLabel("DESIGNED AND DEVELOPED BY MING LI 2020.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()
        content.add(
            res.getLabel("BUILT WITH KOTLIN AND THE LIBGDX GAME LIBRARY.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()
        content.add(
            res.getLabel("AVAILABLE FOR WINDOWS, MAC, LINUX, AND ANDROID PLATFORMS.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(res.getLabel("GRAPHICS", fontScale = 1f, color = GAME_YELLOW))
            .left().padTop(8f).padLeft(10f).expandX().row()
        content.add(
            res.getLabel("CREATED BY MING LI.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(res.getLabel("SOUND EFFECTS", fontScale = 1f, color = GAME_YELLOW))
            .left().padTop(8f).padLeft(10f).expandX().row()
        content.add(
            res.getLabel("UI SFX FROM FREESOUND.ORG.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()
        content.add(
            res.getLabel("GAME SFX MOSTLY FROM TETRIO.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(res.getLabel("MUSIC", fontScale = 1f, color = GAME_YELLOW))
            .left().padTop(8f).padLeft(10f).expandX().row()
        content.add(
            res.getLabel("ROYALTY FREE MUSIC FROM HURT RECORD.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()
    }
}