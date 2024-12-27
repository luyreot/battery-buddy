package com.teoryul.batterybuddy.ui.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.teoryul.batterybuddy.ui.theme.BatteryBuddyTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Gucci Marcucci",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BatteryBuddyTheme {
        MainScreen()
    }
}