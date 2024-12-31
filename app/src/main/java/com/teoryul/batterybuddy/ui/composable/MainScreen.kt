package com.teoryul.batterybuddy.ui.composable

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.service.BatteryUpdatesService
import com.teoryul.batterybuddy.ui.composable.batterylevel.BatteryLvlLayout
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.BatteryLvlText
import com.teoryul.batterybuddy.ui.composable.batterylevel.level.LvlParams

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    context.startForegroundService(Intent(context, BatteryUpdatesService::class.java))

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val levelGap = 30
    val points = remember { screenWidth / levelGap }
    val batteryLvl by remember { BatteryStats.batteryLvl }
    BatteryLvlLayout(
        modifier = Modifier.fillMaxSize(), // Not using the modifier param in order to fill the whole screen
        lvlDropDurationInMills = 1500L, // TODO calculate the speed based on the missing battery %
        batteryLvl = batteryLvl,
        onClick = {
            // Not used
        }
    ) {
        BatteryLvlText(
            modifier = Modifier,
            align = Alignment.Center,
            textStyle = TextStyle(
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            ),
            lvlParams = LvlParams(
                pointsQuantity = points,
                maxHeight = 64f // TODO control the lvl height
            )
        )
    }
}