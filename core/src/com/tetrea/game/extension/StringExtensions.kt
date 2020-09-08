package com.tetrea.game.extension

import java.text.NumberFormat
import java.util.*

fun Float.formatMMSS(): String {
    val minutes = ((this % 3600) / 60).toInt()
    val seconds = (this % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

fun Float.formatMMSSDDD(): String {
    val minutes = ((this % 3600) / 60).toInt()
    val seconds = this % 60
    return if (minutes > 0) String.format("%02d:%06.3f", minutes, seconds) else String.format("%06.3f", seconds)
}

fun Float.formatHours(): String {
    val hours = this / 3600f
    return String.format("%.2f H", hours)
}

fun Int.sign() = if (this >= 0) "+" else ""

fun Float.toMillis() = (this * 1000).toInt().toString()

fun Float.formatPercent() = "${(this * 100f).toInt()}%"

fun Int.formatComma() = NumberFormat.getNumberInstance(Locale.US).format(this)