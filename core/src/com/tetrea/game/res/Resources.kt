package com.tetrea.game.res

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tetrea.game.battle.BattleConfig
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

private const val BUTTON_UP_KEY = "_up"
private const val BUTTON_DOWN_KEY = "_down"

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val texturesCache = mutableMapOf<String, TextureRegion>()
    private val ninePatchCache = mutableMapOf<String, NinePatch>()
    private val battleConfigCache = mutableMapOf<String, BattleConfig>()
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

        loadTextures()
        loadBattleConfigs()

        tetrisSheet = getTexture("tetris").split(SQUARE_SIZE, SQUARE_SIZE)
        tetrisButtons = getTexture("tetris_buttons").split(TETRIS_BUTTON_SIZE, TETRIS_BUTTON_SIZE)
    }

    fun getTexture(key: String): TextureRegion = checkNotNull(texturesCache[key])

    fun getNinePatch(key: String): NinePatch = checkNotNull(ninePatchCache[key])

    fun getBattleConfig(key: String): BattleConfig = checkNotNull(battleConfigCache[key])

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

    fun getNinePatchTextButton(
        text: String,
        key: String,
        colorUp: Color = Color.WHITE,
        colorDown: Color = Color.GRAY,
        width: Float = 0f,
        height: Float = 0f
    ): TextButton {
        val style = TextButton.TextButtonStyle().apply {
            up = NinePatchDrawable(getNinePatch(key + BUTTON_UP_KEY))
            down = NinePatchDrawable(getNinePatch(key + BUTTON_DOWN_KEY))
            over = NinePatchDrawable(getNinePatch(key + BUTTON_DOWN_KEY))
            font = this@Resources.font
            fontColor = colorUp
            downFontColor = colorDown
            overFontColor = colorDown
        }
        return TextButton(text, style).apply {
            setSize(width, height)
        }
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

    private fun loadNinePatch(key: String) {
        ninePatchCache[key] = atlas.createPatch(key)
    }

    private fun fileString(path: String) = Gdx.files.internal(path).readString()

    private fun loadTextures() {
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
        loadTexture("victory_tag")
        loadTexture("defeat_tag")

        loadTexture("black_100_opacity")

        loadNinePatch("gray_blue_bg")
        loadNinePatch("gray_blue_button_up")
        loadNinePatch("gray_blue_button_down")
        loadNinePatch("red_bg")
        loadNinePatch("red_button_up")
        loadNinePatch("red_button_down")
    }

    private fun loadBattleConfigs() {
        val adapter = moshi.adapter(BattleConfig::class.java)
        val config = adapter.fromJson(fileString("configs/battle/battle_config1.json")) ?: return
        battleConfigCache["test_config"] = config
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
        font.dispose()
    }
}