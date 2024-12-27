package com.teoryul.batterybuddy.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.teoryul.batterybuddy.util.Util.openAppInfoScreen

object NotificationUtil {

    const val CHANNEL_NAME = "Battery Status"
    const val CHANNEL_DESCRIPTION = "Receive battery status notifications"
    const val CHANNEL_ID = "battery_status"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            .apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            .let { channel ->
                getSystemService(context, NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestPushPermission(
        onPermissionGranted: @Composable () -> Unit,
        onShowRationale: @Composable (onClick: () -> Unit) -> Unit,
        onPermissionDenied: @Composable (onClick: () -> Unit) -> Unit
    ) {
        val pushPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        var didRequestPermission by remember { mutableStateOf(false) }
        val context = LocalContext.current

        if (pushPermissionState.status.isGranted) {
            onPermissionGranted()
            return
        }

        if (didRequestPermission) {
            onPermissionDenied { openAppInfoScreen(context) }
            return
        }

        onShowRationale {
            pushPermissionState.launchPermissionRequest()
            // This line immediately triggers the onPermissionDenied logic above
            didRequestPermission = true
        }
    }
}