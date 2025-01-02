package com.teoryul.batterybuddy.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.teoryul.batterybuddy.receiver.PowerConnectionReceiver.Companion.INTENT_ACTION_CLICK_NOTIFICATION
import com.teoryul.batterybuddy.ui.composable.AppUi
import com.teoryul.batterybuddy.ui.theme.BatteryBuddyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()
        setContent {
            BatteryBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppUi()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleActionClickNotification(intent)
    }

    private fun handleActionClickNotification(intent: Intent) {
        if (intent.action != INTENT_ACTION_CLICK_NOTIFICATION) return

        //val notificationType = intent.getStringExtra(INTENT_EXTRA_NOTIFICATION_TYPE)
    }
}