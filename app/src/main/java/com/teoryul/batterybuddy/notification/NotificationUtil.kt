package com.teoryul.batterybuddy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService

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
}