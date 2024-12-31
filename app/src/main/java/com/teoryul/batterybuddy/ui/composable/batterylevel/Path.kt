package com.teoryul.batterybuddy.ui.composable.batterylevel

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.LvlParams
import com.teoryul.batterybuddy.ui.composable.batterylevel.plottedpoints.createInitialMultipliersAsState
import com.teoryul.batterybuddy.ui.composable.batterylevel.plottedpoints.createParabolaAsState

@Composable
fun createPathsAsState(
    levelState: LevelState,
    containerSize: IntSize,
    batteryLvlProvider: () -> Float,
    lvlDropDuration: Int,
    animations: MutableList<State<Float>>,
    lvlParams: LvlParams,
    elementParams: ElementParams
): Paths {
    val parabola = createParabolaAsState(
        position = elementParams.position,
        elementSize = elementParams.size,
        batteryLvl = batteryLvlProvider(),
        buffer = lvlParams.bufferY,
        lvlDropDuration = lvlDropDuration,
        levelState = levelState
    )

    val plottedPoints = createPlottedPointsAsState(
        batteryLvl = batteryLvlProvider(),
        containerSize = containerSize,
        levelState = levelState,
        position = elementParams.position,
        buffer = lvlParams.bufferY,
        elementSize = elementParams.size,
        parabola = parabola.value,
        pointsQuantity = lvlParams.pointsQuantity
    )

    val initialMultipliers =
        createInitialMultipliersAsState(pointsQuantity = lvlParams.pointsQuantity)

    val levelMultiplier = animateFloatAsState(
        targetValue = if (levelState == LevelState.LevelIsComing) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = lvlDropDuration
            (0.7f).at((0.2f * lvlDropDuration).toInt())
            (0.8f).at((0.4f * lvlDropDuration).toInt())
        },
        label = "levelMultiplier"
    )

    val paths by remember {
        mutableStateOf(Paths())
    }

    createPaths(
        animations,
        initialMultipliers,
        lvlParams.maxHeight,
        levelState,
        lvlParams.bufferX,
        parabolaInterpolation(levelMultiplier.value),
        containerSize,
        plottedPoints,
        paths,
        elementParams
    )
    return paths
}

fun createPaths(
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    levelState: LevelState,
    bufferX: Float,
    levelMultiplier: Float = 1f,
    containerSize: IntSize,
    points: List<PointF>,
    paths: Paths,
    elementParams: ElementParams
): Paths {

    for (i in 0..1) {
        var levelPoints = points.copy()
        val divider = i % 2
        levelPoints = addLevel(
            points = levelPoints,
            animations = animations,
            initialMultipliers = initialMultipliers,
            maxHeight = maxHeight,
            pointsInversion = divider.toBoolean(),
            levelState = levelState,
            position = elementParams.position,
            elementSize = elementParams.size,
            levelMultiplier = if (divider == 0) levelMultiplier / 2 else levelMultiplier,
            bufferX = bufferX,
        )
        paths.pathList[i].reset()
        paths.pathList[i] = createPath(containerSize, levelPoints, paths.pathList[i])
    }
    return paths
}

fun createPath(
    containerSize: IntSize,
    levelPoints: List<PointF>,
    path: Path
): Path {
    path.moveTo(0f, containerSize.height.toFloat())
    levelPoints.forEach {
        path.lineTo(it.x, it.y)
    }
    path.lineTo(containerSize.width.toFloat(), containerSize.height.toFloat())
    return path
}

fun addLevel(
    points: List<PointF>,
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    pointsInversion: Boolean,
    levelState: LevelState,
    position: Offset,
    elementSize: IntSize,
    bufferX: Float,
    levelMultiplier: Float
): List<PointF> {
    val elementRangeX = (position.x - bufferX)..(position.x + elementSize.width + bufferX)
    points.forEachIndexed { index, pointF ->
        val newIndex = if (pointsInversion) {
            index % animations.size
        } else {
            (animations.size - index % animations.size) - 1
        }
        val initialMultipliersNewIndex = if (pointsInversion) {
            index
        } else {
            initialMultipliers.size - index - 1
        }
        var levelHeight = calculateLevelHeight(
            animations[newIndex].value,
            initialMultipliers[initialMultipliersNewIndex],
            maxHeight
        )

        if (levelState is LevelState.LevelIsComing && pointF.x in elementRangeX) {
            levelHeight *= levelMultiplier
        }

        pointF.y -= levelHeight
    }
    return points
}

private fun calculateLevelHeight(
    currentSize: Float,
    initialMultipliers: Float,
    maxHeight: Float
): Float {
    var levelHeightPercent = initialMultipliers + currentSize
    if (levelHeightPercent > 1.0f) {
        val diff = levelHeightPercent - 1.0f
        levelHeightPercent = 1.0f - diff
    }

    return lerpF(maxHeight, 0f, levelHeightPercent)
}