package com.tetrea.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.tetrea.game.global.TetreaGame
import com.tetrea.game.battle.*
import com.tetrea.game.battle.rating.Elo
import com.tetrea.game.input.TetrisInputHandler
import com.tetrea.game.input.TetrisKeyInput
import com.tetrea.game.global.isAndroid
import com.tetrea.game.res.SQUARE_SIZE
import com.tetrea.game.scene.BattleScene
import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.LineClearType

const val ARG_BATTLE_CONFIG = "ARG_BATTLE_CONFIG"
const val ARG_MATCH_STATE = "ARG_MATCH_STATE"
const val ARG_TETRIS_STATS = "ARG_TETRIS_STATS"
const val ARG_PLAYER_SCORE = "ARG_PLAYER_SCORE"
const val ARG_ENEMY_SCORE = "ARG_ENEMY_SCORE"
const val ARG_MATCH_QUIT = "ARG_MATCH_QUIT"

class BattleScreen(game: TetreaGame) : BaseScreen(game), TetrisStateManager {

    lateinit var tetris: Tetris
    lateinit var scene: BattleScene
    lateinit var state: BattleState

    private val inputMultiplexer = InputMultiplexer()
    private lateinit var inputHandler: TetrisInputHandler
    private lateinit var tetrisKeyInput: TetrisKeyInput

    lateinit var battleConfig: BattleConfig
    var isMatchFinished = false

    override fun show() {
        super.show()

        game.musicManager.startBattleMusic()

        arguments?.let {
            battleConfig = it[ARG_BATTLE_CONFIG] as BattleConfig
        }

        val tetrisConfig = game.res.getTetrisConfig(battleConfig.tetrisConfig)
        val boardX = stage.width / 2 - (tetrisConfig.width * SQUARE_SIZE) / 2f + 3
        val boardY = (stage.height / 2 - (tetrisConfig.height * SQUARE_SIZE) / 2f) - if (isAndroid()) 16f else 32f

        tetris = Tetris(boardX, boardY, tetrisConfig, this, game.soundManager, GameMode.Versus)
        inputHandler = TetrisInputHandler(tetris, this, game.soundManager, game.settings.das, game.settings.arr, game.settings.sds, GameMode.Versus)
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
            game.soundManager,
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

        val bgKey = if (battleConfig.isMatchmaking) "home_screen_bg" else "world_${battleConfig.worldId}_bg"
        game.batch.draw(game.res.getTexture(bgKey), 0f, 0f)
        if (battleConfig.isMatchmaking) game.batch.draw(game.res.getTexture("home_screen_overlay"), 0f, 0f)

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

    fun finishBattlePrematurely() {
        val ratingChange = Elo.getRatingChange(
            game.player.rating,
            battleConfig.enemy.rating,
            if (isMatchFinished) state.currPlayerScore else 0,
            if (isMatchFinished) state.currEnemyScore else (battleConfig.bestOf + 1) / 2
        )
        game.player.completeMatchup(
            battleConfig.compositeKey,
            if (isMatchFinished) state.playerWonGame else false,
            ratingChange,
            if (isMatchFinished) state.currPlayerScore else 0,
            if (isMatchFinished) state.currEnemyScore else (battleConfig.bestOf + 1) / 2,
            battleConfig.isMatchmaking,
            battleConfig.enemy.name,
            battleConfig.enemy.rating
        )
        if (battleConfig.isMatchmaking) {
            game.player.battleStats.totalMatches++
            if (if (isMatchFinished) state.playerWonGame else false) {
                game.player.battleStats.wins++
            } else {
                game.player.battleStats.losses++
            }
        }
        game.saveManager.save()
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

        game.musicManager.fadeOutBattleMusic()
        game.musicManager.inBattle = false
        game.musicManager.fadeInBackgroundMusic()
    }

    fun onBattleQuit() {
        val enemyScore = (battleConfig.bestOf + 1) / 2
        val ratingLost = Elo.getRatingChange(
            game.player.rating,
            battleConfig.enemy.rating,
            0,
            enemyScore
        )
        game.player.completeMatchup(
            battleConfig.compositeKey,
            false,
            ratingLost,
            0,
            enemyScore,
            battleConfig.isMatchmaking,
            battleConfig.enemy.name,
            battleConfig.enemy.rating
        )
        if (battleConfig.isMatchmaking) {
            game.player.battleStats.totalMatches++
            game.player.battleStats.losses++
        }
        game.saveManager.save()

        val args = mapOf(ARG_MATCH_QUIT to ratingLost)
        navigateTo(if (battleConfig.isMatchmaking) VERSUS_SELECT_SCREEN else LEVEL_SELECT_SCREEN, args)

        game.musicManager.stopBattleMusic()
        game.musicManager.inBattle = false
        game.musicManager.fadeInBackgroundMusic()
    }

    override fun addGarbage(numLines: Int) {
        scene.addGarbage(numLines)
    }

    override fun spawnComboParticle(combo: Int) {
        scene.spawnComboParticle(combo)
    }

    override fun spawnB2bParticle(b2b: Int) {
        scene.spawnB2BParticle(b2b)
    }

    override fun spawnCenterParticle(text: String, color: Color) {
        scene.spawnCenterParticle(text, color)
    }

    override fun spawnSpikeParticle(spike: Int) {
        scene.spawnSpikeParticle(spike)
    }

    override fun spawnLineClearParticle(type: LineClearType) {
        scene.spawnLineClearParticle(type)
    }

    override fun spawnNumberParticle(lines: Int, x: Float, y: Float, crit: Boolean) {
        scene.spawnNumberParticle(lines, x, y, crit)
    }

    override fun attackEnemy(attack: Int): Boolean {
        return state.attackEnemy(attack)
    }

    override fun resetGarbage() {
        scene.resetGarbage()
    }

    override fun setPlayerWonGame(win: Boolean) {
        state.playerWonGame = win
    }

    override fun onGameOver(toppedOut: Boolean) {
        scene.startGameOverSequence()
    }

    override fun cancelGarbage(lines: Int) {
        scene.cancelGarbage(lines)
    }
}