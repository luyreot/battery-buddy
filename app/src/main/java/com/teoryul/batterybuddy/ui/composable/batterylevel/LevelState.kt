package com.teoryul.batterybuddy.ui.composable.batterylevel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun createLevelAsState(
    batteryLvlProvider: () -> Int,
    bufferY: Float,
    elementParams: ElementParams
): MutableState<LevelState> {
    return remember(elementParams.position, batteryLvlProvider()) {
        when {
            isAboveElement(batteryLvlProvider(), bufferY, elementParams.position) -> {
                mutableStateOf(LevelState.PlainMoving)
            }

            atElementLevel(
                batteryLvlProvider(),
                bufferY,
                elementParams
            ) -> {
                mutableStateOf(LevelState.FlowsAround)
            }

            isLevelDropping(
                batteryLvlProvider(),
                elementParams
            ) -> {
                mutableStateOf(LevelState.LevelIsComing)
            }

            else -> {
                mutableStateOf(LevelState.LevelIsComing)
            }
        }
    }
}

sealed class LevelState {
    data object PlainMoving : LevelState()
    data object FlowsAround : LevelState()
    data object LevelIsComing : LevelState()
}