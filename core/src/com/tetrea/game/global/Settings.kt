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
        Input.Keys.ESCAPE to TetrisInputType.Pause,
        Input.Keys.R to TetrisInputType.Restart
    ),
    @Json(name = "keyBindingsInverse") val keyBindingsInverse: MutableMap<TetrisInputType, Int> = mutableMapOf(
        TetrisInputType.Left to Input.Keys.LEFT,
        TetrisInputType.Right to Input.Keys.RIGHT,
        TetrisInputType.SoftDrop to Input.Keys.DOWN,
        TetrisInputType.HardDrop to Input.Keys.SPACE,
        TetrisInputType.RotateCW to Input.Keys.UP,
        TetrisInputType.RotateCCW to Input.Keys.Z,
        TetrisInputType.Rotate180 to Input.Keys.X,
        TetrisInputType.Hold to Input.Keys.SHIFT_LEFT,
        TetrisInputType.Pause to Input.Keys.ESCAPE,
        TetrisInputType.Restart to Input.Keys.R
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
    ),
    @Json(name = "das") var das: Float = 0.167f,
    @Json(name = "arr") var arr: Float = 0.033f,
    @Json(name = "sds") var sds: Float = 0.045f,
    @Json(name = "showFps") var showFps: Boolean = false,
    @Json(name = "musicVolume") var musicVolume: Float = 0.5f,
    @Json(name = "soundVolume") var soundVolume: Float = 0.5f,
    @Json(name = "muteMusic") var muteMusic: Boolean = false,
    @Json(name = "muteSound") var muteSound: Boolean = false
)