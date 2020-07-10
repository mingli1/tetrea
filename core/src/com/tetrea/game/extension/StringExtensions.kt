package com.tetrea.game.extension

fun Float.formatMMSS(): String {
    val minutes = ((this % 3600) / 60).toInt()
    val seconds = (this % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

fun Int.sign() = if (this > 0) "+" else ""