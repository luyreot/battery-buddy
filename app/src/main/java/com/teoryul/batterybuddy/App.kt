package com.teoryul.batterybuddy

import android.app.Application
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.util.NotificationUtil.createBatteryMonitorNotificationChannel
import com.teoryul.batterybuddy.util.NotificationUtil.createBatteryStatusNotificationChannel

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPrefs.init(this)
        createBatteryMonitorNotificationChannel(this)
        createBatteryStatusNotificationChannel(this)
    }
}