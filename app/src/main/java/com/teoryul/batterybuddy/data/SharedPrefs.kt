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
}