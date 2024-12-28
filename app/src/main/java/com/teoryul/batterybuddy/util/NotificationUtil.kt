package com.teoryul.batterybuddy.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.teoryul.batterybuddy.R
import com.teoryul.batterybuddy.model.NotificationType
import com.teoryul.batterybuddy.ui.activity.MainActivity


object NotificationUtil {

    const val CHANNEL_NAME = "Battery Status"
    const val CHANNEL_DESCRIPTION = "Receive battery status notifications"
    const val CHANNEL_ID = "battery_status"

    private var notificationId: Int = Int.MIN_VALUE

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

    @SuppressLint("MissingPermission")
    fun createNotification(
        context: Context,
        notificationType: NotificationType
    ) {
        if (!hasNotificationPermission(context)) return

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(notificationType.titleStringRes))
            .setContentText(context.getString(notificationType.textStringRes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId++, builder)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}