package com.tetrea.game.res

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Disposable
import com.tetrea.game.tetris.util.PieceType

const val SQUARE_SIZE = 12

class Resources : Disposable {

    private val assetManager = AssetManager()
    private val atlas: TextureAtlas
    private val texturesCache = mutableMapOf<String, TextureRegion>()
    private val tetrisSheet: Array<Array<TextureRegion>>

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
        tetrisSheet = getTexture("tetris").split(SQUARE_SIZE, SQUARE_SIZE)
    }

    fun getTexture(key: String): TextureRegion = checkNotNull(texturesCache[key])

    fun getLabelStyle(color: Color = Color.WHITE) = Label.LabelStyle(font, color)

    fun getDefaultLabel(text: String = "", color: Color = Color.WHITE) = Label(text, getLabelStyle(color)).apply {
        setFontScale(0.5f)
    }

    fun getSquare(pieceType: PieceType) = tetrisSheet[0][pieceType.index]

    fun getGhost(pieceType: PieceType) = tetrisSheet[1][pieceType.index - 2]

    fun getBoardUnit() = tetrisSheet[2][0]

    private fun loadTexture(key: String) {
        texturesCache[key] = atlas.findRegion(key)
    }

    override fun dispose() {
        assetManager.dispose()
        atlas.dispose()
        font.dispose()
    }
}