package com.teoryul.batterybuddy.data

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {

    private lateinit var sharedPrefs: SharedPreferences

    private const val KEY_LAST_NOTIFIED_BATTERY_PCT = "last_notified_battery_pct"
    private const val KEY_NOTIFY_AT_PERCENTAGE = "notify_at_percentage"

    fun init(context: Context): SharedPreferences {
        synchronized(this) {
            if (::sharedPrefs.isInitialized) return sharedPrefs

            sharedPrefs = context.getSharedPreferences("battery_buddy", Context.MODE_PRIVATE)

            return sharedPrefs
        }
    }

    fun saveLastNotifiedPercentage(pct: Int) {
        sharedPrefs.edit().putInt(KEY_LAST_NOTIFIED_BATTERY_PCT, pct).apply()
    }

    fun clearLastNotifiedPercentage() {
        sharedPrefs.edit().remove(KEY_LAST_NOTIFIED_BATTERY_PCT).apply()
    }

    fun getLastNotifiedPercentage(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_LAST_NOTIFIED_BATTERY_PCT, default)
    }

    fun saveNotifyAtPercentage(pct: Int) {
        sharedPrefs.edit().putInt(KEY_NOTIFY_AT_PERCENTAGE, pct).apply()
    }

    fun clearNotifyAtPercentage() {
        sharedPrefs.edit().remove(KEY_NOTIFY_AT_PERCENTAGE).apply()
    }

    fun getNotifyAtPercentage(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_NOTIFY_AT_PERCENTAGE, default)
    }
}