package com.teoryul.batterybuddy.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.teoryul.batterybuddy.R
import com.teoryul.batterybuddy.receiver.PowerConnectionReceiver
import com.teoryul.batterybuddy.util.NotificationUtil.CHANNEL_ID

class BatteryUpdatesService : Service() {

    private val binder = LocalBinder()

    private val powerConnectionReceiver = PowerConnectionReceiver()

    override fun onCreate() {
        super.onCreate()
        registerReceiverActionBatteryChanged()
    }

    override fun onDestroy() {
        unregisterReceiver(powerConnectionReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            createNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(this.getString(R.string.service_notification_title))
            .setContentText(this.getString(R.string.service_notification_text))
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiverActionBatteryChanged() {
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(
                    powerConnectionReceiver,
                    intentFilter,
                    RECEIVER_NOT_EXPORTED
                )
            } else {
                registerReceiver(
                    powerConnectionReceiver,
                    intentFilter
                )
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): BatteryUpdatesService = this@BatteryUpdatesService
    }

    companion object {
        const val NOTIFICATION_ID: Int = -123
    }
}