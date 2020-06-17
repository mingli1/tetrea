package com.tetrea.game.res

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.tetrea.game.tetris.util.PieceType

const val SQUARE_SIZE = 12

const val TETRIS_BUTTON_SIZE = 40
const val TETRIS_BUTTON_LEFT = 0
const val TETRIS_BUTTON_RIGHT = 1
const val TETRIS_BUTTON_SOFTDROP = 2
const val TETRIS_BUTTON_HARDDROP = 3
const val TETRIS_BUTTON_ROTATE_CW = 4
const val TETRIS_BUTTON_ROTATE_CCW = 5
const val TETRIS_BUTTON_ROTATE_180 = 6
const val TETRIS_BUTTON_HOLD = 7

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas
    private val texturesCache = mutableMapOf<String, TextureRegion>()
    private val tetrisSheet: Array<Array<TextureRegion>>
    private val tetrisButtons: Array<Array<TextureRegion>>

    private val font: BitmapFont

    init {
        assetManager.load("textures.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        atlas = assetManager.get("textures.atlas", TextureAtlas::class.java)

        font = BitmapFont(Gdx.files.internal("font.fnt"), atlas.findRegion("font"), false).apply {
            setUseIntegerPositions(false)
        }

        loadTexture("tetris")
        loadTexture("red")
        loadTexture("yellow")
        loadTexture("tetris_buttons")
        loadTexture("tetris_board_bg")
        loadTexture("item_slots_bg")
        loadTexture("apm_icon")
        loadTexture("pps_icon")
        loadTexture("battle_bg_sky")
        loadTexture("score_header")
        loadTexture("enemy_hp_bar")
        loadTexture("bar_decay")
        loadTexture("bar_restore")
        loadTexture("match_point_tag")
        loadTexture("tiebreaker_tag")

        tetrisSheet = getTexture("tetris").split(SQUARE_SIZE, SQUARE_SIZE)
        tetrisButtons = getTexture("tetris_buttons").split(TETRIS_BUTTON_SIZE, TETRIS_BUTTON_SIZE)
    }

    fun getTexture(key: String): TextureRegion = checkNotNull(texturesCache[key])

    fun getLabelStyle(color: Color = Color.WHITE) = Label.LabelStyle(font, color)

    fun getLabel(
        text: String = "",
        color: Color = Color.WHITE,
        x: Float = 0f,
        y: Float = 0f,
        fontScale: Float = 0.75f
    ) = Label(text, getLabelStyle(color)).apply {
        setPosition(x, y)
        setFontScale(fontScale)
    }

    fun getSquare(pieceType: PieceType) = tetrisSheet[0][pieceType.index]

    fun getGhost(pieceType: PieceType) = tetrisSheet[1][pieceType.index - 2]

    fun getBoardUnit() = tetrisSheet[2][0]

    fun getTetrisButtonStyle(index: Int): ImageButton.ImageButtonStyle {
        return ImageButton.ImageButtonStyle().apply {
            imageUp = TextureRegionDrawable(tetrisButtons[0][index])
            imageDown = TextureRegionDrawable(tetrisButtons[1][index])
            imageOver = TextureRegionDrawable(tetrisButtons[1][index])
        }
    }

    private fun loadTexture(key: String) {
        texturesCache[key] = atlas.findRegion(key)
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
        font.dispose()
    }
}