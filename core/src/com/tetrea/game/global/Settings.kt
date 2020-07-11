package com.tetrea.game.global

import com.badlogic.gdx.Input
import com.squareup.moshi.Json
import com.tetrea.game.input.TetrisInputType
import com.tetrea.game.util.RelativePosition
import com.tetrea.game.util.RelativeTo
import com.tetrea.game.util.RelativeValue

data class Settings(
    @Json(name = "keyBindings") val keyBindings: MutableMap<Int, TetrisInputType> = mutableMapOf(
        Input.Keys.LEFT to TetrisInputType.Left,
        Input.Keys.RIGHT to TetrisInputType.Right,
        Input.Keys.DOWN to TetrisInputType.SoftDrop,
        Input.Keys.SPACE to TetrisInputType.HardDrop,
        Input.Keys.UP to TetrisInputType.RotateCW,
        Input.Keys.Z to TetrisInputType.RotateCCW,
        Input.Keys.X to TetrisInputType.Rotate180,
        Input.Keys.SHIFT_LEFT to TetrisInputType.Hold,
        Input.Keys.ESCAPE to TetrisInputType.Pause
    ),
    @Json(name = "androidBindings") val androidBindings: MutableMap<TetrisInputType, RelativePosition> = mutableMapOf(
        TetrisInputType.Left to RelativePosition(RelativeValue(10f, RelativeTo.XZero), RelativeValue(16f, RelativeTo.YZero)),
        TetrisInputType.Right to RelativePosition(RelativeValue(60f, RelativeTo.XZero), RelativeValue(16f, RelativeTo.YZero)),
        TetrisInputType.SoftDrop to RelativePosition(RelativeValue(110f, RelativeTo.XZero), RelativeValue(16f, RelativeTo.YZero)),
        TetrisInputType.HardDrop to RelativePosition(RelativeValue(-50f, RelativeTo.StageWidth), RelativeValue(16f, RelativeTo.YZero)),
        TetrisInputType.RotateCW to RelativePosition(RelativeValue(-50f, RelativeTo.StageWidth), RelativeValue(66f, RelativeTo.YZero)),
        TetrisInputType.RotateCCW to RelativePosition(RelativeValue(-100f, RelativeTo.StageWidth), RelativeValue(16f, RelativeTo.YZero)),
        TetrisInputType.Rotate180 to RelativePosition(RelativeValue(60f, RelativeTo.XZero), RelativeValue(66f, RelativeTo.YZero)),
        TetrisInputType.Hold to RelativePosition(RelativeValue(-100f, RelativeTo.StageWidth), RelativeValue(66f, RelativeTo.YZero)),
        TetrisInputType.Pause to RelativePosition(RelativeValue(-50f, RelativeTo.StageWidth), RelativeValue(-100f, RelativeTo.StageHeight))
    )
)