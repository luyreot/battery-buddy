package com.teoryul.batterybuddy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.model.NotificationType
import com.teoryul.batterybuddy.util.NotificationUtil.createNotification

class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action == null) return

        handleActionBatteryChanged(context, intent)
        handleActionSkip10Pct(intent)
    }

    private fun handleActionBatteryChanged(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return

        // Charging status
        val chargeStatus: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val statusUnknown: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_UNKNOWN
        val statusCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_CHARGING
        val statusDischarging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_DISCHARGING
        val statusNotCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING
        val statusFull: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_FULL

        // Charging type
        val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
        val dockCharge: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            chargePlug == BatteryManager.BATTERY_PLUGGED_DOCK
        } else {
            false
        }

        // Health
        /*
        val health: Int = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthUnknown: Boolean = health == BatteryManager.BATTERY_HEALTH_UNKNOWN
        val healthGood: Boolean = health == BatteryManager.BATTERY_HEALTH_GOOD
        val healthOverheat: Boolean = health == BatteryManager.BATTERY_HEALTH_OVERHEAT
        val healthDead: Boolean = health == BatteryManager.BATTERY_HEALTH_DEAD
        val healthOverVoltage: Boolean = health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
        val healthUnspecifiedFailure: Boolean =
            health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
        val healthCold: Boolean = health == BatteryManager.BATTERY_HEALTH_COLD
        */

        val batteryPct: Float = intent.let {
            val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        BatteryStats.batteryLevel = batteryPct

        val batteryPctInt: Int = batteryPct.toInt()
        val isCharging: Boolean = acCharge || usbCharge || wirelessCharge || dockCharge

        val notifyAtPercentage: Int = SharedPrefs.getNotifyAtPercentage()
        if (notifyAtPercentage != -1) {
            if (batteryPctInt > notifyAtPercentage) {
                if (isCharging) {
                    SharedPrefs.clearNotifyAtPercentage()
                    SharedPrefs.clearLastNotifiedPercentage()
                } else {
                    return
                }
            } else {
                SharedPrefs.clearNotifyAtPercentage()
                SharedPrefs.clearLastNotifiedPercentage()
            }
        }

        val lastNotifiedBatteryPct: Int = SharedPrefs.getLastNotifiedPercentage()
        if (batteryPctInt == lastNotifiedBatteryPct) {
            return
        } else {
            SharedPrefs.clearLastNotifiedPercentage()
        }

        if (batteryPctInt >= 80 &&
            (statusCharging || statusNotCharging) &&
            isCharging
        ) {
            if (createNotification(context, NotificationType.ABOVE_80)) {
                SharedPrefs.saveLastNotifiedPercentage(batteryPctInt)
            }
            return
        }

        if (batteryPctInt <= 60 &&
            statusDischarging &&
            !isCharging
        ) {
            val didNotify: Boolean = if (batteryPctInt <= 20) {
                createNotification(context, NotificationType.BELOW_20)
            } else {
                createNotification(context, NotificationType.BELOW_60)
            }
            if (didNotify) {
                SharedPrefs.saveLastNotifiedPercentage(batteryPctInt)
            }
            return
        }
    }

    private fun handleActionSkip10Pct(intent: Intent) {
        if (intent.action != INTENT_ACTION_SKIP_10_PCT ||
            intent.action != INTENT_ACTION_SKIP_5_PCT
        ) {
            return
        }

        val lastNotifiedBatteryPct: Int = SharedPrefs.getLastNotifiedPercentage()

        if (intent.action != INTENT_ACTION_SKIP_10_PCT) {
            SharedPrefs.saveNotifyAtPercentage(lastNotifiedBatteryPct - 10)
            return
        }

        SharedPrefs.saveNotifyAtPercentage(lastNotifiedBatteryPct - 5)
    }

    companion object {
        const val INTENT_ACTION_SKIP_10_PCT = "INTENT_ACTION_SKIP_10_PCT"
        const val INTENT_ACTION_SKIP_5_PCT = "INTENT_ACTION_SKIP_5_PCT"
    }
}