package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.global.isAndroid
import com.tetrea.game.input.TetrisAndroidInput
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.TetrisScene
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.LineClearType

const val ARG_GAME_MODE = "ARG_GAME_MODE"

class TetrisScreen(game: TetreaGame) : BaseScreen(game), TetrisStateManager {

    lateinit var tetris: Tetris
    private lateinit var scene: TetrisScene

    private val inputMultiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput

    override fun show() {
        stage.addActor(game.fpsLabel)

        val gameMode = arguments?.get(ARG_GAME_MODE) as? GameMode ?: GameMode.Sprint
        val tetrisConfig = game.res.getTetrisConfig("arcade")
        val boardX = stage.width / 2 - (tetrisConfig.width * SQUARE_SIZE) / 2f + 3
        val boardY = (stage.height / 2 - (tetrisConfig.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 8f else 16f

        tetris = Tetris(boardX, boardY, tetrisConfig, this, game.soundManager, gameMode)
        inputHandler = TetrisInputHandler(tetris, this, game.soundManager, game.settings.das, game.settings.arr, game.settings.sds, gameMode)
        tetrisKeyInput = TetrisKeyInput(game.settings, inputHandler)
        scene = TetrisScene(
            boardX,
            boardY,
            gameMode,
            tetrisConfig,
            stage,
            game.res,
            game.soundManager,
            this
        )

        if (isAndroid()) TetrisAndroidInput(stage, inputHandler, game.res, game.settings)

        inputMultiplexer.addProcessor(stage)
        if (!isAndroid()) inputMultiplexer.addProcessor(tetrisKeyInput)

        Gdx.input.inputProcessor = inputMultiplexer

        transition = Transition.FadeIn
        stage.addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(FADE_DURATION),
            Actions.run {
                transition = Transition.None
                game.musicManager.startBattleMusic()
                scene.startCountdown()
            }
        ))
    }

    override fun update(dt: Float) {
        super.update(dt)

        if (tetris.started) tetris.update(dt)
        inputHandler.update(dt)
        scene.update(dt)
    }

    override fun render(dt: Float) {
        if (gameState != GameState.Pause) super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        if (transition == Transition.None) game.batch.color = Color.WHITE

        game.batch.draw(game.res.getTexture("home_screen_bg"), 0f, 0f)
        game.batch.draw(game.res.getTexture("home_screen_overlay"), 0f, 0f)

        scene.render(game.batch)

        game.batch.end()

        if (gameState != GameState.Pause) stage.act(dt)
        stage.draw()
    }

    override fun notifyPause() {
        super.notifyPause()
        if (!isAndroid()) inputMultiplexer.removeProcessor(tetrisKeyInput)
        scene.showPauseDialog()
        game.musicManager.pauseBattleMusic()
    }

    override fun notifyResume() {
        super.notifyResume()
        if (!isAndroid() && !inputMultiplexer.processors.contains(tetrisKeyInput)) {
            inputMultiplexer.addProcessor(tetrisKeyInput)
        }
        game.musicManager.resumeBattleMusic()
    }

    fun onQuit() {
        navigateTo(ARCADE_SCREEN)
        game.musicManager.stopBattleMusic()
        game.musicManager.inBattle = false
        game.musicManager.fadeInBackgroundMusic()
    }

    override fun onRestart() {
        scene.onRestart()
    }

    override fun onGameOver() {

    }

    override fun addGarbage(numLines: Int) {

    }

    override fun spawnComboParticle(combo: Int) {

    }

    override fun spawnB2bParticle(b2b: Int) {

    }

    override fun spawnCenterParticle(text: String, color: Color) {

    }

    override fun spawnSpikeParticle(spike: Int) {

    }

    override fun spawnLineClearParticle(type: LineClearType) {

    }

    override fun spawnNumberParticle(lines: Int, x: Float, y: Float, crit: Boolean) {

    }

    override fun attackEnemy(attack: Int): Boolean {
        return false
    }

    override fun resetGarbage() {

    }

    override fun setPlayerWonGame(win: Boolean) {

    }

    override fun cancelGarbage(lines: Int) {

    }
}