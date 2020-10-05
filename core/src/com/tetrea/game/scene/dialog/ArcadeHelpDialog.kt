package com.tetrea.game.scene.dialog

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.GAME_LIGHT_GREEN
import com.tetrea.game.res.GAME_VERY_LIGHT_GREEN
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources

class ArcadeHelpDialog(res: Resources) : Table() {

    init {
        touchable = Touchable.enabled
        background = NinePatchDrawable(res.getNinePatch("green_bg"))

        add(res.getLabel("ABOUT", fontScale = 1f, color = GAME_LIGHT_GREEN))
            .top().left().padLeft(16f).padTop(8f).row()

        val content = Table()
        val scrollPane = ScrollPane(content).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        add(scrollPane).expand().width(205f).top()

        content.add(res.getLabel("ARCADE", fontScale = 1f, color = GAME_YELLOW))
            .left().padTop(8f).padLeft(10f).expandX().row()
        content.add(
            res.getLabel("PLAY SINGLEPLAYER MODES HERE. YOU CAN SEE YOUR RECORDS ON YOUR PROFILE.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(
            res.getLabel("SPRINT", color = GAME_VERY_LIGHT_GREEN)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("CLEAR 40 LINES AS FAST AS POSSIBLE.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("ULTRA", color = GAME_VERY_LIGHT_GREEN)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("ACHIEVE THE HIGHEST SCORE POSSIBLE WITHIN 2 MINUTES.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("CHEESE", color = GAME_VERY_LIGHT_GREEN)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("CLEAR THROUGH 100 LINES OF GARBAGE AS FAST AS POSSIBLE.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()
    }
}