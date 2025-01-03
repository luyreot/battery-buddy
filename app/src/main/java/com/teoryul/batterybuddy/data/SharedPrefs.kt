package com.teoryul.batterybuddy.data

import android.content.SharedPreferences
import com.teoryul.batterybuddy.data.SharedPrefs.Companion.KEY_BATTERY_LVL
import com.teoryul.batterybuddy.data.SharedPrefs.Companion.KEY_BATTERY_OVERHEAT
import com.teoryul.batterybuddy.data.SharedPrefs.Companion.KEY_NOTIFY_AT_BATTERY_LVL
import com.teoryul.batterybuddy.di.Injector.get

fun SharedPreferences.Editor.putBatteryLvl(value: Int): SharedPreferences.Editor {
    return putInt(KEY_BATTERY_LVL, value)
}

fun SharedPreferences.Editor.putBatteryOverheat(value: Boolean): SharedPreferences.Editor {
    return putBoolean(KEY_BATTERY_OVERHEAT, value)
}

fun SharedPreferences.Editor.putNotifyAtBatteryLvl(value: Int): SharedPreferences.Editor {
    return putInt(KEY_NOTIFY_AT_BATTERY_LVL, value)
}

fun SharedPreferences.Editor.clearNotifyAtBatteryLvl(): SharedPreferences.Editor {
    return remove(KEY_NOTIFY_AT_BATTERY_LVL)
}

class SharedPrefs {

    companion object {
        const val KEY_BATTERY_LVL = "battery_lvl"
        const val KEY_BATTERY_OVERHEAT = "battery_overheat"
        const val KEY_NOTIFY_AT_BATTERY_LVL = "notify_at_battery_lvl"
    }

    private val sharedPrefs: SharedPreferences by lazy { get() }

    fun cache(): SharedPreferences.Editor = sharedPrefs.edit()

    fun getBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_BATTERY_LVL, default)
    }

    fun getBatteryOverheat(default: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(KEY_BATTERY_OVERHEAT, default)
    }

    fun getNotifyAtBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_NOTIFY_AT_BATTERY_LVL, default)
    }
}