package com.teoryul.batterybuddy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.model.NotificationType
import com.teoryul.batterybuddy.util.NotificationUtil.dismissNotification
import com.teoryul.batterybuddy.util.NotificationUtil.dismissNotifications
import com.teoryul.batterybuddy.util.NotificationUtil.sendNotification

class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action == null) return

        handleActionBatteryChanged(context, intent)
        handleActionSkip10Pct(context, intent)
    }

    private fun handleActionBatteryChanged(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BATTERY_CHANGED) return

        // Charging status
        val chargeStatus: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        //val statusUnknown: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_UNKNOWN
        val statusCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_CHARGING
        val statusDischarging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_DISCHARGING
        val statusNotCharging: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING
        //val statusFull: Boolean = chargeStatus == BatteryManager.BATTERY_STATUS_FULL

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
        val health: Int = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        //val healthUnknown: Boolean = health == BatteryManager.BATTERY_HEALTH_UNKNOWN
        //val healthGood: Boolean = health == BatteryManager.BATTERY_HEALTH_GOOD
        val healthOverheat: Boolean = health == BatteryManager.BATTERY_HEALTH_OVERHEAT
        //val healthDead: Boolean = health == BatteryManager.BATTERY_HEALTH_DEAD
        //val healthOverVoltage: Boolean = health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
        //val healthUnspecifiedFailure: Boolean = health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
        //val healthCold: Boolean = health == BatteryManager.BATTERY_HEALTH_COLD

        val batteryLvl: Float = intent.let {
            val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        BatteryStats.batteryLvl.floatValue = batteryLvl

        val batteryLvlInt: Int = batteryLvl.toInt()
        val isPhonePluggedIn: Boolean = acCharge || usbCharge || wirelessCharge || dockCharge

        if (batteryLvlInt < 80 && statusCharging && isPhonePluggedIn) {
            dismissNotifications(
                context,
                NotificationType.BELOW_60,
                NotificationType.BELOW_20
            )
        }

        val notifyAtBatteryLvl: Int = SharedPrefs.getNotifyAtBatteryLvl()
        // Notify at the calculated battery level if it was previously saved
        if (notifyAtBatteryLvl != -1) {
            if (batteryLvlInt > notifyAtBatteryLvl) {
                if (isPhonePluggedIn) {
                    // Remove cached data if current battery lvl is above the cached one
                    SharedPrefs.clearNotifyAtBatteryLvl()
                    SharedPrefs.clearLastNotifiedLvl()
                } else {
                    // Wait until current battery level reaches the cached one
                    return
                }
            } else {
                // Battery lvl reached the cached one.
                // Clear data and continue with the notifications below.
                SharedPrefs.clearNotifyAtBatteryLvl()
                SharedPrefs.clearLastNotifiedLvl()
            }
        }

        val lastNotifiedBatteryLvl: Int = SharedPrefs.getLastNotifiedLvl()
        // Do not notify if the current battery lvl is the same as the one we previously notified for
        if (batteryLvlInt == lastNotifiedBatteryLvl) {
            return
        } else {
            SharedPrefs.clearLastNotifiedLvl()
            if (healthOverheat) {
                SharedPrefs.setDidNotifyBatteryOverheat(false)
            }
        }

        val didNotifyOverheat: Boolean = SharedPrefs.getDidNotifyBatteryOverheat()
        if (healthOverheat) {
            if (!didNotifyOverheat && sendNotification(context, NotificationType.OVERHEAT)) {
                SharedPrefs.setDidNotifyBatteryOverheat(true)
            }
        } else {
            if (didNotifyOverheat) {
                sendNotification(context, NotificationType.OVERHEAT_NOT)
            }
            SharedPrefs.setDidNotifyBatteryOverheat(false)
        }

        // Notify when:
        // 1. battery lvl is 80 or more
        // 2. battery is charging or not charging
        // 3. phone is plugged in
        if (batteryLvlInt >= 80 &&
            (statusCharging || statusNotCharging) &&
            isPhonePluggedIn
        ) {
            if (sendNotification(context, NotificationType.ABOVE_80)) {
                SharedPrefs.saveLastNotifiedLvl(batteryLvlInt)
            }
            return
        }

        // Notify when:
        // 1. battery lvl is 60 or less
        // 2. battery is charging
        // 3. phone is not plugged in
        if (batteryLvlInt <= 60 &&
            statusDischarging &&
            !isPhonePluggedIn
        ) {
            val didNotify: Boolean = if (batteryLvlInt <= 20) {
                sendNotification(context, NotificationType.BELOW_20)
            } else {
                sendNotification(context, NotificationType.BELOW_60)
            }
            if (didNotify) {
                SharedPrefs.saveLastNotifiedLvl(batteryLvlInt)
            }
            return
        }
    }

    private fun handleActionSkip10Pct(context: Context, intent: Intent) {
        if (intent.action != INTENT_ACTION_SKIP_10_PCT &&
            intent.action != INTENT_ACTION_SKIP_5_PCT
        ) {
            return
        }

        val lastNotifiedBatteryPct: Int = SharedPrefs.getLastNotifiedLvl()

        if (intent.action == INTENT_ACTION_SKIP_10_PCT) {
            SharedPrefs.saveNotifyAtBatteryLvl(lastNotifiedBatteryPct - 10)
            dismissNotification(context, NotificationType.BELOW_60)
            return
        }

        SharedPrefs.saveNotifyAtBatteryLvl(lastNotifiedBatteryPct - 5)
        dismissNotification(context, NotificationType.BELOW_60)
    }

    companion object {
        const val INTENT_ACTION_SKIP_10_PCT = "INTENT_ACTION_SKIP_10_PCT"
        const val INTENT_ACTION_SKIP_5_PCT = "INTENT_ACTION_SKIP_5_PCT"
    }
}