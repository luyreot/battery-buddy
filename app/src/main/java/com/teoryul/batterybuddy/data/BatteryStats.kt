package com.teoryul.batterybuddy.data

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf

object BatteryStats {
    val batteryLvl: MutableFloatState = mutableFloatStateOf(0.0f)
}