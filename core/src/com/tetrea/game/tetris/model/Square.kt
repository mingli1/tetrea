package com.tetrea.game.tetris.model

import com.tetrea.game.tetris.Tetris
import com.tetrea.game.tetris.util.CCW_ROTATE_DATA
import com.tetrea.game.tetris.util.CW_ROTATE_DATA
import com.tetrea.game.tetris.util.PieceType
import com.tetrea.game.tetris.util.Rotation

class Square(
    private val tetris: Tetris,
    var pieceType: PieceType
) {

    var x = 0
    var y = 0

    fun move(x: Int, y: Int) = updatePosition(this.x + x, this.y + y)

    fun canMoveTo(x: Int, y: Int): Boolean {
        return tetris.isWithinBounds(x, y) && tetris.isUnitEmpty(x, y)
    }

    fun updatePosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun lock(): Boolean {
        if (!tetris.isWithinHeight(y)) return false
        tetris.addSquare(x, y, this)
        return true
    }

    fun rotate(originX: Int, originY: Int, rotation: Rotation) {
        val relX = x - originX
        val relY = y - originY
        val data = if (rotation == Rotation.Clockwise || rotation == Rotation.OneEighty) CW_ROTATE_DATA else CCW_ROTATE_DATA

        var newX = (data[0].x * relX) + (data[1].x * relY)
        var newY = (data[0].y * relX) + (data[1].y * relY)

        newX += originX
        newY += originY

        updatePosition(newX, newY)
    }
}