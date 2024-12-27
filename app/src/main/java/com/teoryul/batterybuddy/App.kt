package com.teoryul.batterybuddy

import android.app.Application
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.notification.NotificationUtil.createNotificationChannel

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
        SharedPrefs.init(this)
    }
}