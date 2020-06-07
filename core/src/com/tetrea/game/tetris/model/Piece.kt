package com.tetrea.game.tetris.model

import com.tetrea.game.tetris.*
import com.tetrea.game.tetris.util.*

class Piece(
    private val tetris: Tetris,
    val pieceType: PieceType
) {

    private var rotationIndex = 0
    val squares = Array(4) { Square(tetris, pieceType) }
    val previewSquares = Array(4) { Square(tetris, pieceType) }

    init {
        initSquares(previewSquares, 0, 0)
    }

    fun init(spawnX: Int, spawnY: Int) {
        rotationIndex = 0
        initSquares(squares, spawnX, spawnY)
    }

    fun lock() {
        tetris.getLineClears(squares)
        squares.forEach { it.lock() }
        tetris.canHold = true
        tetris.clearLines()
        tetris.piecesPlaced++
    }

    fun move(x: Int, y: Int): Boolean {
        if (!canMove(x, y)) return false
        squares.forEach { it.move(x, y) }
        return true
    }

    fun canMove(x: Int, y: Int) = squares.all { it.canMoveTo(it.x + x, it.y + y) }

    fun isTwist() = !canMove(-1, 0) && !canMove(1, 0) && !canMove(0, 1)

    fun isToppedOut() = squares.all { tetris.isToppedOut(it.y) }

    fun rotate(rotation: Rotation, performOffsetTests: Boolean = true) {
        if (pieceType == PieceType.O) return

        val prevRotationIndex = rotationIndex
        rotationIndex += when (rotation) {
            Rotation.OneEighty -> 2
            Rotation.Clockwise -> 1
            Rotation.Counterclockwise -> -1
        }
        rotationIndex = (rotationIndex % 4 + 4) % 4

        repeat(if (rotation == Rotation.OneEighty) 2 else 1) {
            squares.forEach { it.rotate(squares[0].x, squares[0].y, rotation) }
        }
        if (!performOffsetTests) return

        val offsetResult = performOffsetTests(prevRotationIndex, rotationIndex, rotation == Rotation.OneEighty)
        if (!offsetResult) rotate(rotation.opposite(), false)
    }

    private fun initSquares(squares: Array<Square>, spawnX: Int, spawnY: Int) {
        squares[0].updatePosition(spawnX, spawnY)

        when (pieceType) {
            PieceType.T -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.I -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 2, spawnY)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.L -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.J -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX - 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.S -> {
                squares[1].updatePosition(spawnX - 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX, spawnY + 1)
            }
            PieceType.Z -> {
                squares[1].updatePosition(spawnX, spawnY + 1)
                squares[2].updatePosition(spawnX - 1, spawnY + 1)
                squares[3].updatePosition(spawnX + 1, spawnY)
            }
            PieceType.O -> {
                squares[1].updatePosition(spawnX + 1, spawnY)
                squares[2].updatePosition(spawnX + 1, spawnY + 1)
                squares[3].updatePosition(spawnX, spawnY + 1)
            }
        }
    }

    private fun performOffsetTests(
        prevRotationIndex: Int,
        newRotationIndex: Int,
        is180: Boolean
    ): Boolean {
        var movePossible = false
        var endX = 0
        var endY = 0

        if (is180) {
            val offsetData = when (pieceType) {
                PieceType.I -> I_180_OFFSET_DATA
                else -> TSZLJ_180_OFFSET_DATA
            }
            val index = when {
                prevRotationIndex == 0 && newRotationIndex == 2 -> 0
                prevRotationIndex == 1 && newRotationIndex == 3 -> 1
                prevRotationIndex == 2 && newRotationIndex == 0 -> 2
                prevRotationIndex == 3 && newRotationIndex == 1 -> 3
                else -> 0
            }

            for (i in 0 until 12) {
                endX = offsetData[index][i].x
                endY = offsetData[index][i].y

                if (canMove(endX, endY)) {
                    movePossible = true
                    break
                }
            }
        } else {
            val offsetData = when (pieceType) {
                PieceType.I -> I_OFFSET_DATA
                else -> TSZLJ_OFFSET_DATA
            }

            for (i in 0 until 5) {
                val offset1 = offsetData[prevRotationIndex][i]
                val offset2 = offsetData[newRotationIndex][i]
                endX = offset1.x - offset2.x
                endY = offset1.y - offset2.y

                if (canMove(endX, endY)) {
                    movePossible = true
                    break
                }
            }
        }

        if (movePossible) move(endX, endY)
        return movePossible
    }
}