package com.teoryul.batterybuddy.ui.composable.batterylevel.level

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Stable
data class BatteryLvlText(
    val modifier: Modifier = Modifier,
    val align: Alignment,
    val textStyle: TextStyle,
    val lvlParams: LvlParams
)

@Stable
data class LvlParams(
    val pointsQuantity: Int = 10,
    val maxHeight: Float = 20f,
    val bufferY: Float = 60f,
    val bufferX: Float = 50f
)