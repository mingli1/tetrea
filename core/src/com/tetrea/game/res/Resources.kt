package com.tetrea.game.res

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tetrea.game.battle.BattleConfig
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.extension.onTap
import com.tetrea.game.input.TetrisInputType
import com.tetrea.game.tetris.TetrisConfig
import com.tetrea.game.tetris.util.PieceType

const val SQUARE_SIZE = 12
const val AVATAR_SIZE = 26f
const val TITLE_LETTER_WIDTH = 36

const val TETRIS_BUTTON_SIZE = 40

private const val BUTTON_UP_KEY = "_up"
private const val BUTTON_DOWN_KEY = "_down"

const val NUM_WORLDS = 3
val NUM_LEVELS = arrayOf(8, 5, 5)

private const val NUM_AVATARS = 25

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val texturesCache = mutableMapOf<String, TextureRegion>()
    private val ninePatchCache = mutableMapOf<String, NinePatch>()
    private val tetrisConfigCache = mutableMapOf<String, TetrisConfig>()
    private val battleConfigCache = mutableListOf<MutableList<BattleConfig>>()

    val sounds = mutableMapOf<String, Sound>()
    val bgMusic = mutableListOf<Music>()
    val battleMusic = mutableListOf<Music>()
    private val tetrisSheet: Array<Array<TextureRegion>>
    private val tetrisButtons: Array<Array<TextureRegion>>
    val titleLetters: Array<TextureRegion>

    private val font: BitmapFont

    init {
        assetManager.load("textures.atlas", TextureAtlas::class.java)

        assetManager.load("music/bg_a.ogg", Music::class.java)
        assetManager.load("music/bg_b.ogg", Music::class.java)
        assetManager.load("music/bg_c.ogg", Music::class.java)
        assetManager.load("music/bg_d.ogg", Music::class.java)
        assetManager.load("music/bg_e.ogg", Music::class.java)
        assetManager.load("music/bg_f.ogg", Music::class.java)
        assetManager.load("music/battle_a.ogg", Music::class.java)
        assetManager.load("music/battle_b.ogg", Music::class.java)
        assetManager.load("music/battle_c.ogg", Music::class.java)
        assetManager.load("music/battle_d.ogg", Music::class.java)

        assetManager.load("sound/std_button_click.ogg", Sound::class.java)
        assetManager.load("sound/sec_button_click.ogg", Sound::class.java)
        assetManager.load("sound/checkbox_click.ogg", Sound::class.java)
        assetManager.load("sound/world_select_click.ogg", Sound::class.java)
        assetManager.load("sound/level_select_click.ogg", Sound::class.java)
        assetManager.load("sound/battle_start.ogg", Sound::class.java)

        for (i in 1 until 17) {
            assetManager.load("sound/combo$i.ogg", Sound::class.java)
        }
        for (i in 1 until 5) {
            assetManager.load("sound/clear$i.ogg", Sound::class.java)
        }
        assetManager.load("sound/b2b.ogg", Sound::class.java)
        assetManager.load("sound/died.ogg", Sound::class.java)
        assetManager.load("sound/hold.ogg", Sound::class.java)
        assetManager.load("sound/lock.ogg", Sound::class.java)
        assetManager.load("sound/move.ogg", Sound::class.java)
        assetManager.load("sound/perfect_clear.ogg", Sound::class.java)
        assetManager.load("sound/rotate.ogg", Sound::class.java)
        assetManager.load("sound/tspin.ogg", Sound::class.java)
        assetManager.load("sound/crit.ogg", Sound::class.java)
        assetManager.load("sound/countdown.ogg", Sound::class.java)
        assetManager.load("sound/go.ogg", Sound::class.java)
        assetManager.load("sound/win.ogg", Sound::class.java)
        assetManager.load("sound/score_change.ogg", Sound::class.java)

        assetManager.finishLoading()

        atlas = assetManager.get("textures.atlas", TextureAtlas::class.java)

        font = BitmapFont(Gdx.files.internal("font.fnt"), atlas.findRegion("font"), false).apply {
            setUseIntegerPositions(false)
        }

        loadTextures()
        loadTetrisConfigs()
        loadBattleConfigs()
        loadMusic()
        loadSounds()

        tetrisSheet = getTexture("tetris").split(SQUARE_SIZE, SQUARE_SIZE)
        tetrisButtons = getTexture("tetris_buttons").split(TETRIS_BUTTON_SIZE, TETRIS_BUTTON_SIZE)
        titleLetters = getTexture("title_letters").split(TITLE_LETTER_WIDTH, 44)[0]
    }

    fun getTexture(key: String): TextureRegion = checkNotNull(texturesCache[key])

    fun getNinePatch(key: String): NinePatch = checkNotNull(ninePatchCache[key])

    fun getTetrisConfig(key: String): TetrisConfig = checkNotNull(tetrisConfigCache[key])

    fun getBattleConfigs(worldId: Int): List<BattleConfig> = battleConfigCache[worldId]

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
        height: Float = 0f,
        disabledKey: String? = null
    ): TextButton {
        val style = TextButton.TextButtonStyle().apply {
            up = NinePatchDrawable(getNinePatch(key + BUTTON_UP_KEY))
            down = NinePatchDrawable(getNinePatch(key + BUTTON_DOWN_KEY))
            over = NinePatchDrawable(getNinePatch(key + BUTTON_DOWN_KEY))
            disabledKey?.let { disabled = NinePatchDrawable(getNinePatch(it)) }
            font = this@Resources.font
            fontColor = colorUp
            downFontColor = colorDown
            overFontColor = colorDown
        }
        return TextButton(text, style).apply {
            setSize(width, height)
        }
    }

    fun getNinePatchImageTextButton(
        text: String,
        ninePatchKey: String,
        imageKey: String,
        colorUp: Color = Color.WHITE,
        colorDown: Color = Color.GRAY,
        width: Float = 0f,
        height: Float = 0f
    ): ImageTextButton {
        val style = ImageTextButton.ImageTextButtonStyle().apply {
            up = NinePatchDrawable(getNinePatch(ninePatchKey + BUTTON_UP_KEY))
            down = NinePatchDrawable(getNinePatch(ninePatchKey + BUTTON_DOWN_KEY))
            over = NinePatchDrawable(getNinePatch(ninePatchKey + BUTTON_DOWN_KEY))
            font = this@Resources.font
            fontColor = colorUp
            downFontColor = colorDown
            overFontColor = colorDown
            imageUp = TextureRegionDrawable(getTexture(imageKey))
        }
        return ImageTextButton(text, style).apply {
            setSize(width, height)
        }
    }

    fun getNinePatchWindowStyle(
        key: String,
        fontColor: Color = Color.WHITE
    ): Window.WindowStyle {
        return Window.WindowStyle(font, fontColor, NinePatchDrawable(getNinePatch(key)))
    }

    fun getSlider(
        min: Float,
        max: Float,
        stepSize: Float,
        backgroundKey: String,
        vertical: Boolean = false
    ): Slider {
        val style = Slider.SliderStyle().apply {
            background = NinePatchDrawable(getNinePatch(backgroundKey))
            knob = NinePatchDrawable(getNinePatch("gray_blue_bg"))
        }
        return Slider(min, max, stepSize, vertical, style).apply {
            style.background.minHeight = 8f
            style.knob.minHeight = 18f
            style.knob.minWidth = 14f
        }
    }

    fun getCheckBox(): CheckBox {
        val style = CheckBox.CheckBoxStyle().apply {
            checkboxOn = TextureRegionDrawable(getTexture("settings_checkbox_on"))
            checkboxOff = TextureRegionDrawable(getTexture("settings_checkbox_off"))
            font = this@Resources.font
        }
        return CheckBox("", style)
    }

    fun getSquare(pieceType: PieceType) = tetrisSheet[0][pieceType.index]

    fun getGhost(pieceType: PieceType) = tetrisSheet[1][pieceType.index - 2]

    fun getBoardUnit() = tetrisSheet[2][0]

    fun getTetrisButtonStyle(type: TetrisInputType): ImageButton.ImageButtonStyle {
        return ImageButton.ImageButtonStyle().apply {
            imageUp = TextureRegionDrawable(tetrisButtons[0][type.buttonIndex])
            imageDown = TextureRegionDrawable(tetrisButtons[1][type.buttonIndex])
            imageOver = TextureRegionDrawable(tetrisButtons[1][type.buttonIndex])
        }
    }

    fun getButtonWithImage(
        text: String,
        ninePatchKey: String,
        imageKey: String,
        colorUp: Color,
        onClick: () -> Unit
    ): ImageTextButton {
        return getNinePatchImageTextButton(
            text = text,
            ninePatchKey = ninePatchKey,
            imageKey = imageKey,
            colorUp = colorUp,
            colorDown = Color.WHITE
        ).apply {
            label.setFontScale(1.5f)
            labelCell.expandX().align(Align.left).padLeft(12f)
            imageCell.padLeft(16f)
            onTap { onClick() }
        }
    }

    private fun fileString(path: String) = Gdx.files.internal(path).readString()

    private fun loadTexture(key: String) {
        texturesCache[key] = atlas.findRegion(key)
    }

    private fun loadNinePatch(key: String) {
        ninePatchCache[key] = atlas.createPatch(key)
    }

    private fun loadTextures() {
        loadTexture("home_screen_bg")
        loadTexture("home_screen_overlay")
        loadTexture("versus_select_overlay")
        loadTexture("arcade_screen_overlay")
        loadTexture("settings_screen_overlay")
        loadTexture("profile_screen_overlay")
        loadTexture("title_letters")

        loadTexture("tetris")
        loadTexture("red")
        loadTexture("yellow")
        loadTexture("tetris_buttons")
        loadTexture("tetris_board_bg")
        loadTexture("apm_icon")
        loadTexture("pps_icon")
        loadTexture("world_0_bg")
        loadTexture("world_1_bg")
        loadTexture("world_2_bg")
        loadTexture("score_header")
        loadTexture("enemy_hp_bar")
        loadTexture("bar_decay")
        loadTexture("bar_restore")
        loadTexture("match_point_tag")
        loadTexture("tiebreaker_tag")
        loadTexture("victory_tag")
        loadTexture("defeat_tag")
        loadTexture("new_record_tag")
        loadTexture("versus_tag")
        loadTexture("versus_button_icon")
        loadTexture("arcade_button_icon")
        loadTexture("profile_button_icon")
        loadTexture("settings_button_icon")
        loadTexture("find_match_button_icon")
        loadTexture("adventure_button_icon")
        loadTexture("ultra_button_icon")
        loadTexture("cheese_button_icon")
        loadTexture("immune_hp_bar")
        loadTexture("settings_checkbox_off")
        loadTexture("settings_checkbox_on")
        loadTexture("world_select_arrow_right_up")
        loadTexture("world_select_arrow_right_down")
        loadTexture("world_select_arrow_left_up")
        loadTexture("world_select_arrow_left_down")
        loadTexture("arcade_header")

        loadTexture("white")
        loadTexture("black")
        loadTexture("black_75_opacity")
        loadTexture("black_100_opacity")
        loadTexture("black_150_opacity")
        loadTexture("transparent")
        loadTexture("atk")
        loadTexture("def")
        loadTexture("spd")
        loadTexture("gravity_bar")
        loadTexture("solid_garbage_bar")
        loadTexture("damage_reduction_bar")
        loadTexture("immune_bar")

        loadNinePatch("dark_gray_bg")
        loadNinePatch("light_gray_bg")
        loadNinePatch("light_gray_blue_bg")
        loadNinePatch("gray_blue_bg")
        loadNinePatch("gray_blue_button_up")
        loadNinePatch("gray_blue_button_down")
        loadNinePatch("purple_button_up")
        loadNinePatch("purple_button_down")
        loadNinePatch("green_button_up")
        loadNinePatch("green_button_down")
        loadNinePatch("red_bg")
        loadNinePatch("red_button_up")
        loadNinePatch("red_button_down")
        loadNinePatch("purple_bg")
        loadNinePatch("green_bg")
        loadNinePatch("orange_bg")
        loadNinePatch("light_purple_bg")
        loadNinePatch("orange_button_up")
        loadNinePatch("orange_button_down")
        loadNinePatch("versus_blue_bg")
        loadNinePatch("versus_orange_bg")
        loadNinePatch("versus_button_up")
        loadNinePatch("versus_button_down")
        loadNinePatch("arcade_button_up")
        loadNinePatch("arcade_button_down")
        loadNinePatch("profile_button_up")
        loadNinePatch("profile_button_down")
        loadNinePatch("settings_button_up")
        loadNinePatch("settings_button_down")
        loadNinePatch("find_match_button_up")
        loadNinePatch("find_match_button_down")
        loadNinePatch("adventure_button_up")
        loadNinePatch("adventure_button_down")
        loadNinePatch("settings_slider_bg")
        loadNinePatch("profile_orange_button_up")
        loadNinePatch("profile_orange_button_down")
        loadNinePatch("arcade_green_button_up")
        loadNinePatch("arcade_green_button_down")
        loadNinePatch("sprint_button_up")
        loadNinePatch("sprint_button_down")
        loadNinePatch("ultra_button_up")
        loadNinePatch("ultra_button_down")
        loadNinePatch("cheese_button_up")
        loadNinePatch("cheese_button_down")

        for (i in 0 until NUM_AVATARS) {
            loadTexture("${i}_avatar")
        }
    }

    private fun loadTetrisConfigs() {
        val adapter = moshi.adapter(TetrisConfig::class.java)
        tetrisConfigCache["default"] = adapter.fromJson(fileString("configs/tetris/default.json")) ?: return
        tetrisConfigCache["arcade"] = adapter.fromJson(fileString("configs/tetris/arcade.json")) ?: return
    }

    private fun loadBattleConfigs() {
        val adapter = moshi.adapter(BattleConfig::class.java)

        for (world in 0 until NUM_WORLDS) {
            val worlds = mutableListOf<BattleConfig>()
            for (level in 0 until NUM_LEVELS[world]) {
                val config = adapter.fromJson(fileString("configs/battle/bc_${world}_$level.json")) ?: return
                val rating = Elo.getRating(config.enemy)
                config.enemy.rating = rating
                worlds.add(config)
            }
            battleConfigCache.add(worlds)
        }
    }

    private fun loadMusic() {
        bgMusic.add(assetManager.get("music/bg_a.ogg", Music::class.java))
        bgMusic.add(assetManager.get("music/bg_b.ogg", Music::class.java))
        bgMusic.add(assetManager.get("music/bg_c.ogg", Music::class.java))
        bgMusic.add(assetManager.get("music/bg_d.ogg", Music::class.java))
        bgMusic.add(assetManager.get("music/bg_e.ogg", Music::class.java))
        bgMusic.add(assetManager.get("music/bg_f.ogg", Music::class.java))

        battleMusic.add(assetManager.get("music/battle_a.ogg", Music::class.java))
        battleMusic.add(assetManager.get("music/battle_b.ogg", Music::class.java))
        battleMusic.add(assetManager.get("music/battle_c.ogg", Music::class.java))
        battleMusic.add(assetManager.get("music/battle_d.ogg", Music::class.java))

        battleMusic.forEach { it.isLooping = true }
    }

    private fun loadSounds() {
        sounds["std_button_click"] = assetManager.get("sound/std_button_click.ogg", Sound::class.java)
        sounds["sec_button_click"] = assetManager.get("sound/sec_button_click.ogg", Sound::class.java)
        sounds["checkbox_click"] = assetManager.get("sound/checkbox_click.ogg", Sound::class.java)
        sounds["world_select_click"] = assetManager.get("sound/world_select_click.ogg", Sound::class.java)
        sounds["level_select_click"] = assetManager.get("sound/level_select_click.ogg", Sound::class.java)
        sounds["battle_start"] = assetManager.get("sound/battle_start.ogg", Sound::class.java)

        for (i in 1 until 17) {
            sounds["combo$i"] = assetManager.get("sound/combo$i.ogg", Sound::class.java)
        }
        for (i in 1 until 5) {
            sounds["clear$i"] = assetManager.get("sound/clear$i.ogg", Sound::class.java)
        }
        sounds["b2b"] = assetManager.get("sound/b2b.ogg", Sound::class.java)
        sounds["died"] = assetManager.get("sound/died.ogg", Sound::class.java)
        sounds["hold"] = assetManager.get("sound/hold.ogg", Sound::class.java)
        sounds["lock"] = assetManager.get("sound/lock.ogg", Sound::class.java)
        sounds["move"] = assetManager.get("sound/move.ogg", Sound::class.java)
        sounds["perfect_clear"] = assetManager.get("sound/perfect_clear.ogg", Sound::class.java)
        sounds["rotate"] = assetManager.get("sound/rotate.ogg", Sound::class.java)
        sounds["tspin"] = assetManager.get("sound/tspin.ogg", Sound::class.java)
        sounds["crit"] = assetManager.get("sound/crit.ogg", Sound::class.java)
        sounds["countdown"] = assetManager.get("sound/countdown.ogg", Sound::class.java)
        sounds["go"] = assetManager.get("sound/go.ogg", Sound::class.java)
        sounds["win"] = assetManager.get("sound/win.ogg", Sound::class.java)
        sounds["score_change"] = assetManager.get("sound/score_change.ogg", Sound::class.java)
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
        font.dispose()
        bgMusic.forEach { it.dispose() }
        battleMusic.forEach { it.dispose() }
        sounds.forEach { (_, sound) -> sound.dispose() }
    }
}