package com.teoryul.batterybuddy.ui.composable.batterylevel.plottedpoints

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.teoryul.batterybuddy.ui.composable.batterylevel.Parabola
import com.teoryul.batterybuddy.ui.composable.batterylevel.PointF
import com.teoryul.batterybuddy.ui.composable.batterylevel.LevelState

@Composable
fun createParabolaAsState(
    position: Offset,
    elementSize: IntSize,
    batteryLvl: Float,
    buffer: Float,
    levelState: LevelState,
    lvlDropDuration: Int
): State<Parabola> {
    val parabolaHeightMultiplier = animateFloatAsState(
        targetValue = if (levelState == LevelState.LevelIsComing) 0f else -1f,
        animationSpec = tween(
            durationMillis = lvlDropDuration,
            easing = { OvershootInterpolator(6f).getInterpolation(it) }
        ),
        label = "parabolaHeightMultiplier"
    )

    val point1 by remember(position, elementSize, batteryLvl, parabolaHeightMultiplier) {
        mutableStateOf(
            PointF(
                position.x,
                batteryLvl + (elementSize.height / 3f + buffer / 5) * parabolaHeightMultiplier.value
            )
        )
    }

    val point2 by remember(position, elementSize, batteryLvl, parabolaHeightMultiplier) {
        mutableStateOf(
            PointF(
                position.x + elementSize.width,
                batteryLvl + (elementSize.height / 3f + buffer / 5) * parabolaHeightMultiplier.value
            )
        )
    }

    val point3 by remember(position, elementSize, parabolaHeightMultiplier, batteryLvl) {
        mutableStateOf(
            PointF(
                position.x + elementSize.width / 2,
                batteryLvl + (elementSize.height / 3f + buffer) * parabolaHeightMultiplier.value
            )
        )
    }

    return remember(point1, point2, point3) {
        derivedStateOf {
            Parabola(point1, point2, point3)
        }
    }
}