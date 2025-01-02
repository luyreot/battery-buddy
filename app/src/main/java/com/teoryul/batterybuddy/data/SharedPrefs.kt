package com.teoryul.batterybuddy.data

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context): SharedPreferences {
        synchronized(this) {
            if (::sharedPrefs.isInitialized) return sharedPrefs

            sharedPrefs = context.getSharedPreferences("battery_buddy", Context.MODE_PRIVATE)

            return sharedPrefs
        }
    }

    private const val KEY_BATTERY_LVL = "battery_lvl"
    private const val KEY_BATTERY_OVERHEAT = "battery_overheat"
    private const val KEY_NOTIFY_AT_BATTERY_LVL = "notify_at_battery_lvl"

    fun cache(): SharedPreferences.Editor = sharedPrefs.edit()

    fun SharedPreferences.Editor.putBatteryLvl(value: Int): SharedPreferences.Editor {
        return putInt(KEY_BATTERY_LVL, value)
    }

    fun getBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_BATTERY_LVL, default)
    }

    fun SharedPreferences.Editor.putBatteryOverheat(value: Boolean): SharedPreferences.Editor {
        return putBoolean(KEY_BATTERY_OVERHEAT, value)
    }

    fun getBatteryOverheat(default: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(KEY_BATTERY_OVERHEAT, default)
    }

    fun SharedPreferences.Editor.putNotifyAtBatteryLvl(value: Int): SharedPreferences.Editor {
        return putInt(KEY_NOTIFY_AT_BATTERY_LVL, value)
    }

    fun getNotifyAtBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_NOTIFY_AT_BATTERY_LVL, default)
    }

    fun SharedPreferences.Editor.clearNotifyAtBatteryLvl(): SharedPreferences.Editor {
        return remove(KEY_NOTIFY_AT_BATTERY_LVL)
    }
}