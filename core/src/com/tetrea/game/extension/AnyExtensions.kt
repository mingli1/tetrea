package com.tetrea.game.extension

fun <T> T?.default(default: T): T = this ?: default