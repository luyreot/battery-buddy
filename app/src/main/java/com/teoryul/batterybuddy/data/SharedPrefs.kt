package com.teoryul.batterybuddy.data

import android.content.Context
import android.content.SharedPreferences

object SharedPrefs {

    private lateinit var sharedPrefs: SharedPreferences

    private const val KEY_LAST_NOTIFIED_BATTERY_LVL = "last_notified_battery_lvl"
    private const val KEY_NOTIFY_AT_BATTERY_LVL = "notify_at_battery_lvl"
    private const val KEY_DID_NOTIFY_BATTERY_OVERHEAT = "did_notify_battery_overheat"

    fun init(context: Context): SharedPreferences {
        synchronized(this) {
            if (::sharedPrefs.isInitialized) return sharedPrefs

            sharedPrefs = context.getSharedPreferences("battery_buddy", Context.MODE_PRIVATE)

            return sharedPrefs
        }
    }

    fun saveLastNotifiedLvl(lvl: Int) {
        sharedPrefs.edit().putInt(KEY_LAST_NOTIFIED_BATTERY_LVL, lvl).apply()
    }

    fun clearLastNotifiedLvl() {
        sharedPrefs.edit().remove(KEY_LAST_NOTIFIED_BATTERY_LVL).apply()
    }

    fun getLastNotifiedLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_LAST_NOTIFIED_BATTERY_LVL, default)
    }

    fun saveNotifyAtBatteryLvl(lvl: Int) {
        sharedPrefs.edit().putInt(KEY_NOTIFY_AT_BATTERY_LVL, lvl).apply()
    }

    fun clearNotifyAtBatteryLvl() {
        sharedPrefs.edit().remove(KEY_NOTIFY_AT_BATTERY_LVL).apply()
    }

    fun getNotifyAtBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_NOTIFY_AT_BATTERY_LVL, default)
    }

    fun setDidNotifyBatteryOverheat(didNotify: Boolean) {
        sharedPrefs.edit().putBoolean(KEY_DID_NOTIFY_BATTERY_OVERHEAT, didNotify).apply()
    }

    fun getDidNotifyBatteryOverheat(default: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(KEY_DID_NOTIFY_BATTERY_OVERHEAT, default)
    }
}