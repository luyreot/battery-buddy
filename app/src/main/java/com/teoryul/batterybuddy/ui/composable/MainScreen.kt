package com.teoryul.batterybuddy.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.WaterDropLayout
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.wave.WaterDropText
import com.teoryul.batterybuddy.ui.composable.batterylevel.waterdrops.wave.WaveParams

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val waveGap = 30
    val points = remember { screenWidth / waveGap }
    val batteryLvl by remember { BatteryStats.batteryLvl }
    WaterDropLayout(
        modifier = Modifier.fillMaxSize(), // Not using the modifier param in order to fill the whole screen
        waveDurationInMills = 1500L, // TODO calculate the speed based on the missing battery %
        batteryLvl = batteryLvl,
        onWavesClick = {
            // Not used
        }
    ) {
        WaterDropText(
            modifier = Modifier,
            align = Alignment.Center,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            ),
            waveParams = WaveParams(
                pointsQuantity = points,
                maxWaveHeight = 64f // TODO control the wave height
            )
        )
    }
}