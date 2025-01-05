package com.teoryul.batterybuddy.data

import android.content.SharedPreferences
import com.teoryul.batterybuddy.di.Injector.get

class SharedPrefs {

    companion object {
        const val KEY_BATTERY_LVL = "battery_lvl"
        const val KEY_BATTERY_OVERHEAT = "battery_overheat"
        const val KEY__PHONE_PLUGGED_IN = "phone_plugged_in"
        const val KEY_NOTIFY_AT_BATTERY_LVL = "notify_at_battery_lvl"
    }

    private val sharedPrefs: SharedPreferences by lazy { get() }

    fun putBatteryLvl(value: Int) {
        sharedPrefs.edit().putInt(KEY_BATTERY_LVL, value).apply()
    }

    fun getBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_BATTERY_LVL, default)
    }

    fun putPhonePluggedIn(value: Boolean) {
        sharedPrefs.edit().putBoolean(KEY__PHONE_PLUGGED_IN, value).apply()
    }

    fun gutPhonePluggedIn(default: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(KEY__PHONE_PLUGGED_IN, default)
    }

    fun putBatteryOverheat(value: Boolean) {
        sharedPrefs.edit().putBoolean(KEY_BATTERY_OVERHEAT, value).apply()
    }

    fun getBatteryOverheat(default: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(KEY_BATTERY_OVERHEAT, default)
    }

    fun putNotifyAtBatteryLvl(value: Int) {
        sharedPrefs.edit().putInt(KEY_NOTIFY_AT_BATTERY_LVL, value).apply()
    }

    fun clearNotifyAtBatteryLvl() {
        sharedPrefs.edit().remove(KEY_NOTIFY_AT_BATTERY_LVL).apply()
    }

    fun getNotifyAtBatteryLvl(default: Int = -1): Int {
        return sharedPrefs.getInt(KEY_NOTIFY_AT_BATTERY_LVL, default)
    }
}