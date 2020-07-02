package com.tetrea.game.global

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

const val V_WIDTH = 264
const val V_HEIGHT = 440
const val V_SCALE = 2
const val S_WIDTH = V_WIDTH * V_SCALE
const val S_HEIGHT = V_HEIGHT * V_SCALE

const val TITLE = "Tetrea"

const val V_SYNC_ENABLED = false
const val RESIZABLE = false
const val BG_FPS = 10
const val FG_FPS = 60
const val DELTA_TIME_BOUND = 1 / 30f

const val IS_DEBUG = true

fun isAndroid() = Gdx.app.type == Application.ApplicationType.Android