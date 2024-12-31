package com.teoryul.batterybuddy

import android.app.Application
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.util.NotificationUtil.createNotificationChannel

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPrefs.init(this)
        createNotificationChannel(this)
    }
}