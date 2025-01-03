package com.teoryul.batterybuddy

import android.app.Application
import com.teoryul.batterybuddy.di.Injector.addDependencies
import com.teoryul.batterybuddy.di.Injector.singleton
import com.teoryul.batterybuddy.di.definitions
import com.teoryul.batterybuddy.util.NotificationUtil.createBatteryMonitorNotificationChannel
import com.teoryul.batterybuddy.util.NotificationUtil.createBatteryStatusNotificationChannel

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        diInit()

        createBatteryMonitorNotificationChannel(this)
        createBatteryStatusNotificationChannel(this)
    }

    private fun diInit() {
        singleton { this.applicationContext }
        addDependencies(definitions)
    }
}