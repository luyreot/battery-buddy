package com.teoryul.batterybuddy.data

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf

class BatteryStats {

    // Used in UI
    val batteryLvl: MutableFloatState = mutableFloatStateOf(0.0f)

    private val lock = Any()

    var batteryLvlInt: Int = 0
        set(value) {
            synchronized(lock) {
                field = value
                batteryLvl.floatValue = value.toFloat()
            }
        }
        get() {
            synchronized(lock) { return field }
        }

    var isPluggedIn: Boolean = false
        set(value) {
            synchronized(lock) { field = value }
        }
        get() {
            synchronized(lock) { return field }
        }

    var isCharging: Boolean = false
        set(value) {
            synchronized(lock) { field = value }
        }
        get() {
            synchronized(lock) { return field }
        }

    var isDischarging: Boolean = false
        set(value) {
            synchronized(lock) { field = value }
        }
        get() {
            synchronized(lock) { return field }
        }

    var isNotCharging: Boolean = false
        set(value) {
            synchronized(lock) { field = value }
        }
        get() {
            synchronized(lock) { return field }
        }

    var isOverheating: Boolean = false
        set(value) {
            synchronized(lock) { field = value }
        }
        get() {
            synchronized(lock) { return field }
        }
}