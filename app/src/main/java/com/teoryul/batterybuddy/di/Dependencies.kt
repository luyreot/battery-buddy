package com.teoryul.batterybuddy.di

import android.content.Context
import android.content.SharedPreferences
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.di.Injector.module
import com.teoryul.batterybuddy.domain.BatteryStatusUseCase

val definitions: List<Definitions> = listOf(
    module {
        singleton { getSharedPreferences(get()) }
        singleton { SharedPrefs() }
        singleton { BatteryStats() }
        singleton { BatteryStatusUseCase(get(), get()) }
    }
)

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("battery_buddy", Context.MODE_PRIVATE)
}