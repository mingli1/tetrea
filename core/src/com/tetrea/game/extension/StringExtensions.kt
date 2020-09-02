package com.tetrea.game.extension

fun Float.formatMMSS(): String {
    val minutes = ((this % 3600) / 60).toInt()
    val seconds = (this % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

fun Float.formatHours(): String {
    val hours = this / 3600f
    return String.format("%.2f H", hours)
}

fun Int.sign() = if (this >= 0) "+" else ""

fun Float.toMillis() = (this * 1000).toInt().toString()

fun Float.formatPercent() = "${(this * 100f).toInt()}%"