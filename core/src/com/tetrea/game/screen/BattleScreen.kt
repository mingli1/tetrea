package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.battle.*
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.global.isAndroid
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.BattleScene
import com.tetrea.game.tetris.Tetris

const val ARG_BATTLE_CONFIG = "ARG_BATTLE_CONFIG"
const val ARG_MATCH_STATE = "ARG_MATCH_STATE"
const val ARG_TETRIS_STATS = "ARG_TETRIS_STATS"
const val ARG_PLAYER_SCORE = "ARG_PLAYER_SCORE"
const val ARG_ENEMY_SCORE = "ARG_ENEMY_SCORE"

class BattleScreen(game: TetreaGame) : BaseScreen(game) {

    lateinit var tetris: Tetris
    lateinit var scene: BattleScene
    lateinit var state: BattleState

    private val inputMultiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput

    lateinit var battleConfig: BattleConfig

    override fun show() {
        super.show()

        arguments?.let {
            battleConfig = it[ARG_BATTLE_CONFIG] as BattleConfig
        }

        val tetrisConfig = game.res.getTetrisConfig(battleConfig.tetrisConfig)
        val boardX = stage.width / 2 - (tetrisConfig.width * SQUARE_SIZE) / 2f + 3
        val boardY = (stage.height / 2 - (tetrisConfig.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 16f else 32f

        tetris = Tetris(boardX, boardY, tetrisConfig, this)
        inputHandler = TetrisInputHandler(tetris, this, game.settings.das, game.settings.arr, game.settings.sds)
        tetrisKeyInput = TetrisKeyInput(game.settings, inputHandler)
        state = BattleState(battleConfig, this, game.player, game.res)
        scene = BattleScene(
            boardX,
            boardY,
            tetrisConfig,
            game.player,
            battleConfig.enemy,
            stage,
            game.res,
            this,
            inputHandler,
            game.settings
        )

        inputMultiplexer.addProcessor(stage)
        if (!isAndroid()) inputMultiplexer.addProcessor(tetrisKeyInput)

        Gdx.input.inputProcessor = inputMultiplexer
    }

    override fun update(dt: Float) {
        super.update(dt)

        if (tetris.started) tetris.update(dt)
        inputHandler.update(dt)
        scene.update(dt)
        state.update(dt)
    }

    override fun render(dt: Float) {
        if (gameState != GameState.Pause) super.render(dt)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = cam.combined
        game.batch.begin()
        if (transition == Transition.None) game.batch.color = Color.WHITE

        game.batch.draw(game.res.getTexture("battle_bg_sky"), 0f, 0f)
        scene.render(game.batch)

        game.batch.end()

        if (gameState != GameState.Pause) stage.act(dt)
        stage.draw()
    }

    override fun notifyPause() {
        super.notifyPause()
        if (!isAndroid()) inputMultiplexer.removeProcessor(tetrisKeyInput)
        scene.showPauseDialog()
    }

    override fun notifyResume() {
        super.notifyResume()
        if (!isAndroid() && !inputMultiplexer.processors.contains(tetrisKeyInput)) {
            inputMultiplexer.addProcessor(tetrisKeyInput)
        }
    }

    fun onBattleEnd(matchState: MatchState, playerScore: Int, enemyScore: Int) {
        val arguments = mapOf(
            ARG_MATCH_STATE to matchState,
            ARG_TETRIS_STATS to tetris.stats,
            ARG_PLAYER_SCORE to playerScore,
            ARG_ENEMY_SCORE to enemyScore,
            ARG_BATTLE_CONFIG to battleConfig
        )
        navigateTo(RESULTS_SCREEN, arguments)
    }
}