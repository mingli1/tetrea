package com.tetrea.game.battle

import com.badlogic.gdx.math.MathUtils
import com.tetrea.game.screen.BattleScreen
import kotlin.math.max

private const val MIN_ATTACK_DELAY = 0.3f
private const val MAX_ATTACK_DELAY = 12f
private const val SPEED_RANGE = 5f
private const val MAX_ATTACK = 8
private const val ATTACK_OFFSET = 2
private const val HEAL_CHANCE_MULTIPLIER = 0.4f
private const val MIN_HEAL_PERCENTAGE = 0.1f
private const val MAX_HEAL_PERCENTAGE = 0.3f

class BattleState(
    private val config: BattleConfig,
    private val screen: BattleScreen
) {

    val firstToText = "FT${config.firstTo}"
    var playerText = ""
    var enemyText = ""

    var enemyHp = config.enemy.maxHp
    val enemyMaxHp = config.enemy.maxHp

    var playerScore = 0
    var enemyScore = 0
    var gameNumber = 0
    var playerWonGame = false

    var attackTimer = 0f
    private var attackIndex = 0
    private var attackDelay = getAttackDelay()
    private var initAction = false
    private var futureAction: () -> Unit = {}
    private var randomMoveCount = 0

    fun update(dt: Float) {
        if (screen.tetris.started) {
            config.attackScheme?.let { handleAttackScheme(dt, it) } ?: handleRandomScheme(dt)
        }

        playerText = "YOU $playerScore"
        enemyText = "$enemyScore ${config.enemy.name}"
    }

    fun attackEnemy(attack: Int): Boolean {
        enemyHp -= attack
        screen.scene.attackEnemyHp(attack)
        if (enemyHp <= 0) {
            enemyHp = 0
            return true
        }
        return false
    }

    fun resetState() {
        enemyHp = enemyMaxHp
        attackIndex = 0
        attackTimer = 0f
        initAction = false
        attackDelay = getAttackDelay()
        randomMoveCount = 0
    }

    fun updateGameNumber() = gameNumber++

    fun updateScores() {
        playerScore += if (playerWonGame) 1 else 0
        enemyScore += if (playerWonGame) 0 else 1
    }

    fun getMatchState() = when {
        playerScore == config.firstTo -> MatchState.PlayerWin
        enemyScore == config.firstTo -> MatchState.EnemyWin
        config.firstTo > 1 && playerScore == config.firstTo - 1 && enemyScore == config.firstTo - 1 -> MatchState.Tiebreaker
        playerScore == config.firstTo - 1 || enemyScore == config.firstTo - 1 -> MatchState.MatchPoint
        else -> MatchState.Ongoing
    }

    private fun healEnemy(heal: Int) {
        enemyHp += heal
        screen.scene.healEnemyHp(heal)
        if (enemyHp > enemyMaxHp) enemyHp = enemyMaxHp
    }

    private fun handleRandomScheme(dt: Float, stopAfter: Int = 0): Boolean {
        if (!initAction) {
            futureAction = if (shouldHeal()) {
                screen.scene.startEnemyCharge(attackDelay, Action.Heal)
                ({ healEnemy(getHeal()) })
            } else {
                val attack = getAttack()
                screen.scene.startEnemyCharge(attackDelay, Action.SendLines)
                ({ screen.tetris.queueGarbage(attack) })
            }
            initAction = true
        }
        attackTimer += dt
        if (attackTimer >= attackDelay) {
            futureAction()
            attackDelay = getAttackDelay()
            attackTimer = 0f
            initAction = false

            randomMoveCount++
            if (randomMoveCount >= stopAfter) {
                randomMoveCount = 0
                return true
            }
        }
        return false
    }

    private fun handleAttackScheme(dt: Float, attackScheme: List<Attack>) {
        val attack = attackScheme[attackIndex]

        when (attack.action) {
            Action.Random -> {
                attack.numMoves?.let {
                    if (handleRandomScheme(dt, it)) {
                        if (attackIndex == attackScheme.size - 1) attackIndex = 0
                        else attackIndex++
                    }
                }
            }
            Action.SendLines, Action.Heal -> screen.scene.startEnemyCharge(attack.time, attack.action)
        }
        if (attack.action != Action.Random) {
            attackTimer += dt
            if (attackTimer >= attack.time) {
                when (attack.action) {
                    Action.SendLines -> attack.lines?.let { screen.tetris.queueGarbage(it) }
                    Action.Heal -> attack.heal?.let { healEnemy(it) }
                }

                attackTimer = 0f
                if (attackIndex == attackScheme.size - 1) attackIndex = 0
                else attackIndex++
            }
        }
    }

    private fun getAttackDelay(): Float {
        val minDelay = (1f - (config.enemy.speed / 100f)) * (MAX_ATTACK_DELAY - MIN_ATTACK_DELAY)
        val maxDelay = minDelay + SPEED_RANGE
        return MathUtils.random(minDelay, maxDelay)
    }

    private fun getAttack(): Int {
        val chance = MathUtils.random()
        if (chance <= 0.8f) {
            val att = ((config.enemy.attack / 100f) * MAX_ATTACK).toInt()
            val minAttack = max(1, att - ATTACK_OFFSET)
            val maxAttack = att + ATTACK_OFFSET
            return MathUtils.random(minAttack, maxAttack)
        }
        return MathUtils.random(1, 4)
    }

    private fun shouldHeal(): Boolean {
        val chance = MathUtils.random()
        return chance <= (config.enemy.defense / 100f) * HEAL_CHANCE_MULTIPLIER
    }

    private fun getHeal(): Int {
        val minHeal = max(1, (MIN_HEAL_PERCENTAGE * config.enemy.maxHp).toInt())
        val maxHeal = max(1, (MAX_HEAL_PERCENTAGE * config.enemy.maxHp).toInt())
        return ((config.enemy.defense / 100f) * (maxHeal - minHeal) + minHeal).toInt()
    }
}