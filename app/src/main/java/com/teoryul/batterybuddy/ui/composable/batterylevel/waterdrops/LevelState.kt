package com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.teoryul.batterybuddy.ui.composable.batterylevel.atElementLevel
import com.teoryul.batterybuddy.ui.composable.batterylevel.isAboveElement
import com.teoryul.batterybuddy.ui.composable.batterylevel.isWaterFalls

@Composable
fun createLevelAsState(
    waterLevelProvider: () -> Int,
    bufferY: Float,
    elementParams: ElementParams
): MutableState<LevelState> {
    return remember(elementParams.position, waterLevelProvider()) {
        when {
            isAboveElement(waterLevelProvider(), bufferY, elementParams.position) -> {
                mutableStateOf(LevelState.PlainMoving)
            }

            atElementLevel(
                waterLevelProvider(),
                bufferY,
                elementParams
            ) -> {
                mutableStateOf(LevelState.FlowsAround)
            }

            isWaterFalls(
                waterLevelProvider(),
                elementParams
            ) -> {
                mutableStateOf(LevelState.WaveIsComing)
            }

            else -> {
                mutableStateOf(LevelState.WaveIsComing)
            }
        }
    }
}

sealed class LevelState {
    data object PlainMoving : LevelState()
    data object FlowsAround : LevelState()
    data object WaveIsComing : LevelState()
}