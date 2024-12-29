package com.teoryul.batterybuddy.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.teoryul.batterybuddy.ui.composable.batterylevel.animating.WaterLevelState
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.WaterDropLayout
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.wave.WaterDropText
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.wave.WaveParams

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val waveGap = 30
    val points = remember { screenWidth / waveGap }
    var waterLevelState by remember { mutableStateOf(WaterLevelState.StartReady) }
    WaterDropLayout(
        modifier = Modifier.fillMaxSize(), // Not using the modifier param in order to fill the whole screen
        waveDurationInMills = 5000L,
        waterLevelState = waterLevelState,
        onWavesClick = {
            waterLevelState = if (waterLevelState == WaterLevelState.Animating) {
                WaterLevelState.StartReady
            } else {
                WaterLevelState.Animating
            }
        }
    ) {
        WaterDropText(
            modifier = Modifier,
            align = Alignment.Center,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
            ),
            waveParams = WaveParams(
                pointsQuantity = points,
                maxWaveHeight = 64f // TODO control the wave height
            )
        )
    }
}