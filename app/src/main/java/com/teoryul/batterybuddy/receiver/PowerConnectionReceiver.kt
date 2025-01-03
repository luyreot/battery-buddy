package com.teoryul.batterybuddy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.di.Injector.get
import com.teoryul.batterybuddy.domain.BatteryStatusUseCase
import com.teoryul.batterybuddy.model.NotificationType
import com.teoryul.batterybuddy.util.NotificationUtil.dismissNotification
import com.teoryul.batterybuddy.util.NotificationUtil.sendNotification

class PowerConnectionReceiver : BroadcastReceiver() {

    private val sharedPrefs: SharedPrefs by lazy { get() }
    private val batteryStatus: BatteryStatusUseCase by lazy { get() }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action == null) return

        handleActionBatteryChanged(context, intent)
        handleActionSkip10Pct(context, intent)
        handleActionDeleteNotification(context, intent)
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

        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val result = batteryStatus.onStatusChanged(
            level = level,
            scale = scale,
            statusCharging = statusCharging,
            statusDischarging = statusDischarging,
            statusNotCharging = statusNotCharging,
            acCharge = acCharge,
            usbCharge = usbCharge,
            wirelessCharge = wirelessCharge,
            dockCharge = dockCharge,
            healthOverheat = healthOverheat
        )

        when (result) {
            BatteryStatusUseCase.BatteryStatus.NoStatus -> {
                // Not used
            }

            BatteryStatusUseCase.BatteryStatus.Charge20 -> {
                sendNotification(context, NotificationType.BELOW_20)
            }

            BatteryStatusUseCase.BatteryStatus.Charge60 -> {
                sendNotification(context, NotificationType.BELOW_60)
            }

            BatteryStatusUseCase.BatteryStatus.DismissBatteryLvlNotification -> {
                dismissNotification(context, NotificationType.ABOVE_80)
                dismissNotification(context, NotificationType.BELOW_60)
                dismissNotification(context, NotificationType.BELOW_20)
            }

            BatteryStatusUseCase.BatteryStatus.Overheating -> {
                sendNotification(context, NotificationType.OVERHEAT)
            }

            BatteryStatusUseCase.BatteryStatus.StoppedOverheating -> {
                sendNotification(context, NotificationType.OVERHEAT_NOT)
            }

            BatteryStatusUseCase.BatteryStatus.StopCharging -> {
                sendNotification(context, NotificationType.ABOVE_80)
            }
        }
    }

    private fun handleActionSkip10Pct(context: Context, intent: Intent) {
        if (intent.action != INTENT_ACTION_SKIP_10_PCT &&
            intent.action != INTENT_ACTION_SKIP_5_PCT
        ) {
            return
        }

        val cachedBatteryLvl = sharedPrefs.getBatteryLvl()

        if (intent.action == INTENT_ACTION_SKIP_10_PCT) {
            sharedPrefs.putNotifyAtBatteryLvl(cachedBatteryLvl - 10)
            dismissNotification(context, NotificationType.BELOW_60)
            return
        }

        sharedPrefs.putNotifyAtBatteryLvl(cachedBatteryLvl - 5)
        dismissNotification(context, NotificationType.BELOW_60)
    }

    private fun handleActionDeleteNotification(context: Context, intent: Intent) {
        if (intent.action != INTENT_ACTION_DELETE_NOTIFICATION) return

        //val notificationType = intent.getStringExtra(INTENT_EXTRA_NOTIFICATION_TYPE)
    }

    companion object {
        const val INTENT_ACTION_CLICK_NOTIFICATION = "INTENT_ACTION_CLICK_NOTIFICATION"
        const val INTENT_ACTION_DELETE_NOTIFICATION = "INTENT_ACTION_DELETE_NOTIFICATION"

        const val INTENT_ACTION_SKIP_10_PCT = "INTENT_ACTION_SKIP_10_PCT"
        const val INTENT_ACTION_SKIP_5_PCT = "INTENT_ACTION_SKIP_5_PCT"

        const val INTENT_EXTRA_NOTIFICATION_TYPE = "INTENT_EXTRA_NOTIFICATION_TYPE"
    }
}