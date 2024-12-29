package com.teoryul.batterybuddy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.model.NotificationType
import com.teoryul.batterybuddy.util.NotificationUtil.createNotification

class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Charging status
        val chargeStatus: Int = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val statusUnknown: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_UNKNOWN
        val statusCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_CHARGING
        val statusDischarging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_DISCHARGING
        val statusNotCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING
        val statusFull: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_FULL

        // Charging type
        val chargePlug: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS

        // Health
        val health: Int = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val healthUnknown: Boolean = health == BatteryManager.BATTERY_HEALTH_UNKNOWN
        val healthGood: Boolean = health == BatteryManager.BATTERY_HEALTH_GOOD
        val healthOverheat: Boolean = health == BatteryManager.BATTERY_HEALTH_OVERHEAT
        val healthDead: Boolean = health == BatteryManager.BATTERY_HEALTH_DEAD
        val healthOverVoltage: Boolean = health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
        val healthUnspecifiedFailure: Boolean =
            health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
        val healthCold: Boolean = health == BatteryManager.BATTERY_HEALTH_COLD

        val batteryPct: Float = intent?.let {
            val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        } ?: 0f

        BatteryStats.batteryLevel = batteryPct
        return
        // TODO finish logic
        if (batteryPct > 60f && batteryPct < 80f) {
            context?.let { createNotification(it, NotificationType.ABOVE_80) }
        }
    }
}