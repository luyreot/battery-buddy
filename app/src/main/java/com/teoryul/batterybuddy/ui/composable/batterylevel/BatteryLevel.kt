package com.teoryul.batterybuddy.ui.composable.batterylevel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntSize
import com.teoryul.batterybuddy.ui.composable.batterylevel.canvas.drawLevels
import com.teoryul.batterybuddy.ui.composable.batterylevel.canvas.drawTextWithBlendMode
import com.teoryul.batterybuddy.ui.composable.batterylevel.text.createTextParamsAsState
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.BatteryLvlText
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.LvlParams
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.createAnimationsAsState
import com.teoryul.batterybuddy.ui.theme.BatteryLvlBackground

@Composable
fun BatteryLvlLayout(
    modifier: Modifier = Modifier,
    lvlDropDurationInMills: Long = 6000L,
    batteryLvl: Float,
    onClick: () -> Unit,
    content: () -> BatteryLvlText
) {
    val lvlParams = remember { content().lvlParams }
    val animations = createAnimationsAsState(pointsQuantity = lvlParams.pointsQuantity)
    BatteryLvlDrawing(
        modifier = modifier,
        lvlDropDurationInMills = lvlDropDurationInMills,
        lvlParams = lvlParams,
        animations = animations,
        batteryLvl = batteryLvl,
        onClick = onClick,
        content = content
    )
}

@Composable
fun BatteryLvlDrawing(
    modifier: Modifier = Modifier,
    lvlDropDurationInMills: Long,
    lvlParams: LvlParams,
    animations: MutableList<State<Float>>,
    batteryLvl: Float,
    onClick: () -> Unit,
    content: () -> BatteryLvlText
) {
    val lvlDropDuration by rememberSaveable { mutableLongStateOf(lvlDropDurationInMills) }
    val lvlProgress by lvlProgressAsState(
        batteryLvl = batteryLvl,
        timerDurationInMillis = lvlDropDuration
    )
    LvlDrawing(
        modifier = modifier,
        lvlDuration = lvlDropDuration,
        animations = animations,
        lvlProgress = lvlProgress,
        lvlParams = lvlParams,
        onClick = onClick,
        content = content
    )
}

@Composable
fun LvlDrawing(
    modifier: Modifier = Modifier,
    lvlDuration: Long,
    lvlParams: LvlParams,
    animations: MutableList<State<Float>>,
    lvlProgress: Float,
    onClick: () -> Unit,
    content: () -> BatteryLvlText
) {
    val elementParams by remember { mutableStateOf(ElementParams()) }
    var containerSize by remember { mutableStateOf(IntSize(0, 0)) }

    val lvlDropDuration = rememberBatteryLvlDuration(
        elementSize = elementParams.size,
        containerSize = containerSize,
        duration = lvlDuration
    )

    val batteryLvl by remember(lvlProgress, containerSize.height) {
        derivedStateOf { (lvlProgress * containerSize.height).toInt() }
    }

    val levelState = createLevelAsState(
        batteryLvlProvider = { batteryLvl },
        bufferY = lvlParams.bufferY,
        elementParams = elementParams
    )

    val paths = createPathsAsState(
        containerSize = containerSize,
        elementParams = elementParams,
        levelState = levelState.value,
        batteryLvlProvider = { batteryLvl.toFloat() },
        lvlDropDuration = lvlDropDuration,
        animations = animations,
        lvlParams = lvlParams
    )

    val textParams = createTextParamsAsState(
        textStyle = content().textStyle,
        lvlProgress = lvlProgress,
        elementParams = elementParams
    )

    Canvas(
        modifier = Modifier
            .background(BatteryLvlBackground)
            .fillMaxSize()
    ) {
        drawLevels(paths)
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .onGloballyPositioned { containerSize = IntSize(it.size.width, it.size.height) }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawTextWithBlendMode(mask = paths.pathList[0], textParams = textParams.value)
            }
    ) {
        Text(
            modifier = content().modifier
                .align(content().align)
                .onGloballyPositioned {
                    elementParams.position = it.positionInParent()
                    elementParams.size = it.size
                },
            text = "60%",
            style = content().textStyle
        )
    }
}

@Stable
data class ElementParams(
    var size: IntSize = IntSize.Zero,
    var position: Offset = Offset(0f, 0f)
)

data class Paths(
    val pathList: MutableList<Path> = mutableListOf(Path(), Path())
)