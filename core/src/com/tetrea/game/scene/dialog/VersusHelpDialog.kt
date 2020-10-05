package com.tetrea.game.scene.dialog

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.tetrea.game.res.GAME_LIGHT_PURPLE
import com.tetrea.game.res.GAME_PURPLE
import com.tetrea.game.res.GAME_YELLOW
import com.tetrea.game.res.Resources

class VersusHelpDialog(res: Resources, isMatchmaking: Boolean) : Table() {

    /**
     * ABOUT
     *
     * *MATCHMATCHING* (isMatchmaking = true)
     * Battle against an opponent of similar rating in a set of games. Winning or losing
     * a match changes your rating based on the rating difference between you and the opponent.
     *
     * BEST-OF FOR RATING:
     *     < 1000 = BO1
     *     < 1800 = BO3
     *     < 2400 = BO5
     *     < 3000 = BO7
     *     > 3000 = B09
     *
     * *HOW BATTLES WORK*
     * Enemies all have an HP bar and power bar. To win a game, you must send enough attack
     * to drop the enemy's HP to 0. The enemy can also send attacks in the form of garbage lines and
     * other special abilities when it fills its power bar.
     *
     * POWER BAR COLORS
     *     yellow = garbage lines
     *     blue = healing
     *     gray = solid garbage lines
     *     purple = increase gravity
     *     orange = damage reduction
     *     green = immunity
     *
     * *BATTLE MECHANICS*
     * T-SPINS and QUADS send the most damage. Chaining them gives a back-to-back bonus that scales
     * with the amount of back-to-backs.
     *
     * Large combos also deal a lot of damage. When finishing a combo with a QUAD, T-SPIN DOUBLE,
     * or T-SPIN TRIPLE, the damage is scaled based on the size of the combo.
     *
     * Each attack has a 10% chance of being a critical strike that sends double the damage. Use
     * this to your advantage to create massive damage spikes.
     *
     * Incoming garbage lines can be neutralized by sending lines while they buffer, even if the
     * enemy has applied immunity or damage reduction.
     */

    init {
        touchable = Touchable.enabled
        background = NinePatchDrawable(res.getNinePatch("purple_bg"))

        add(res.getLabel("ABOUT", fontScale = 1f, color = GAME_PURPLE))
            .top().left().padLeft(16f).padTop(8f).row()

        val content = Table()
        val scrollPane = ScrollPane(content).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            layout()
        }
        add(scrollPane).expand().width(205f).top()

        if (isMatchmaking) {
            content.add(res.getLabel("MATCHMAKING", fontScale = 1f, color = GAME_YELLOW))
                .left().padTop(8f).padLeft(10f).expandX().row()
            content.add(
                res.getLabel("BATTLE AGAINST AN OPPONENT OF SIMILAR RATING IN A SET OF GAMES. WINNING OR LOSING A MATCH CHANGES YOUR RATING BASED ON THE RATING DIFFERENCE BETWEEN YOU AND THE OPPONENT.").apply {
                    setWrap(true)
                    setAlignment(Align.left)
                }
            ).left().width(189f).padLeft(10f).padTop(2f).row()

            content.add(
                res.getLabel("BEST-OF FOR RATING:", color = GAME_LIGHT_PURPLE)
            ).left().padLeft(10f).expandX().padTop(8f).row()

            content.add(
                res.getLabel("< 1000 = BO1")
            ).left().padLeft(20f).expandX().padTop(2f).row()
            content.add(
                res.getLabel("< 1800 = BO3")
            ).left().padLeft(20f).expandX().padTop(2f).row()
            content.add(
                res.getLabel("< 2400 = BO5")
            ).left().padLeft(20f).expandX().padTop(2f).row()
            content.add(
                res.getLabel("< 3000 = BO7")
            ).left().padLeft(20f).expandX().padTop(2f).row()
            content.add(
                res.getLabel(">= 3000 = BO9")
            ).left().padLeft(20f).expandX().padTop(2f).row()
        } else {
            content.add(res.getLabel("ADVENTURE", fontScale = 1f, color = GAME_YELLOW))
                .left().padTop(8f).padLeft(10f).expandX().row()
            content.add(
                res.getLabel("BATTLE AGAINST OPPONENTS WITH UNIQUE ATTACK PATTERNS AND ABILITIES. EACH WORLD IS OF INCREASING DIFFICULTY. MATCHES WILL APPLY RATING CHANGES.").apply {
                    setWrap(true)
                    setAlignment(Align.left)
                }
            ).left().width(189f).padLeft(10f).padTop(2f).row()
        }

        content.add(res.getLabel("HOW BATTLES WORK", fontScale = 1f, color = GAME_YELLOW))
            .left().padLeft(10f).padTop(8f).expandX().row()
        content.add(
            res.getLabel("ENEMIES ALL HAVE AN HP BAR AND POWER BAR. TO WIN A GAME, YOU MUST SEND ENOUGH ATTACK TO DROP THE ENEMY'S HP TO 0. THE ENEMY CAN ALSO SEND ATTACKS IN THE FORM OF GARBAGE LINES AND OTHER SPECIAL ABILITIES WHEN IT FILLS ITS POWER BAR.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(
            res.getLabel("POWER BAR COLORS:", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("- YELLOW = GARBAGE LINES")
        ).left().padLeft(10f).expandX().padTop(2f).row()
        content.add(
            res.getLabel("- BLUE = HEALING")
        ).left().padLeft(10f).expandX().padTop(2f).row()
        content.add(
            res.getLabel("- GRAY = SOLID GARBAGE LINES")
        ).left().padLeft(10f).expandX().padTop(2f).row()
        content.add(
            res.getLabel("- PURPLE = INCREASE GRAVITY")
        ).left().padLeft(10f).expandX().padTop(2f).row()
        content.add(
            res.getLabel("- ORANGE = DAMAGE REDUCTION")
        ).left().padLeft(10f).expandX().padTop(2f).row()
        content.add(
            res.getLabel("- GREEN = IMMUNITY")
        ).left().padLeft(10f).expandX().padTop(2f).row()

        content.add(res.getLabel("BATTLE MECHANICS", fontScale = 1f, color = GAME_YELLOW))
            .left().padLeft(10f).padTop(8f).expandX().row()
        content.add(
            res.getLabel("T-SPINS AND QUADS SEND THE MOST DAMAGE. CHAINING THEM GIVES A BACK-TO-BACK BONUS THAT SCALES WITH THE AMOUNT OF BACK-TO-BACKS.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(
            res.getLabel("LARGE COMBOS ALSO DEAL A LOT OF DAMAGE. WHEN FINISHING A COMBO WITH A QUAD, T-SPIN DOUBLE, OR T-SPIN TRIPLE, THE DAMAGE IS SCALED BASED ON THE SIZE OF THE COMBO.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(8f).row()

        content.add(
            res.getLabel("EACH ATTACK HAS A 10% CHANCE OF BEING A CRITICAL STRIKE THAT SENDS DOUBLE THE DAMAGE. USE THIS TO YOUR ADVANTAGE TO CREATE MASSIVE DAMAGE SPIKES.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(8f).row()

        content.add(
            res.getLabel("INCOMING GARBAGE LINES CAN BE NEUTRALIZED BY SENDING LINES WHILE THEY BUFFER, EVEN IF THE ENEMY HAS APPLIED IMMUNITY OR DAMAGE REDUCTION.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(8f).row()

        content.add(res.getLabel("ATTACK PATTERNS", fontScale = 1f, color = GAME_YELLOW))
            .left().padLeft(10f).padTop(8f).expandX().row()
        content.add(
            res.getLabel("ENEMIES MAY HAVE CERTAIN ATTACK PATTERNS THAT DETERMINE TYPE OF ATTACKS SENT.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(10f).padTop(2f).row()

        content.add(
            res.getLabel("RANDOM", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("THE DEFAULT PATTERN. ENEMIES ATTACK BASED ON THEIR STATS.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("SPIKER", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("ENEMIES TEND TO SEND LARGE BURSTS AT A TIME.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("CHEESER", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("ENEMIES TEND TO SEND MESSIER GARBAGE LINES.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("DEFENSIVE", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("ENEMIES WILL HEAL MORE OR USE MORE DEFENSIVE ABILITIES.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).row()

        content.add(
            res.getLabel("FOUR WIDER", color = GAME_LIGHT_PURPLE)
        ).left().padLeft(10f).expandX().padTop(8f).row()
        content.add(
            res.getLabel("ENEMIES SEND A MASSIVE COMBO AS THEIR OPENER.").apply {
                setWrap(true)
                setAlignment(Align.left)
            }
        ).left().width(189f).padLeft(16f).padTop(2f).padBottom(8f).row()
    }
}