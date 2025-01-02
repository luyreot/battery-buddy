package com.teoryul.batterybuddy.domain

import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs
import com.teoryul.batterybuddy.data.SharedPrefs.clearNotifyAtBatteryLvl
import com.teoryul.batterybuddy.data.SharedPrefs.putBatteryLvl
import com.teoryul.batterybuddy.data.SharedPrefs.putBatteryOverheat

object BatteryStatusUseCase {

    const val BATTERY_LEVEL_CHARGED: Int = 80
    const val BATTERY_LEVEL_COULD_CHARGE: Int = 60
    const val BATTERY_LEVEL_MUST_CHARGE: Int = 20

    fun onStatusChanged(
        level: Int,
        scale: Int,
        statusCharging: Boolean,
        statusDischarging: Boolean,
        statusNotCharging: Boolean,
        acCharge: Boolean,
        usbCharge: Boolean,
        wirelessCharge: Boolean,
        dockCharge: Boolean,
        healthOverheat: Boolean
    ): BatteryStatus {
        val batteryLvl = (level * 100 / scale.toFloat()).toInt()
        val didBatteryLvlChange = batteryLvl != SharedPrefs.getBatteryLvl()

        BatteryStats.batteryLvlInt = batteryLvl

        val cache = SharedPrefs.cache()

        // Overheating
        val didOverheat = SharedPrefs.getBatteryOverheat()
        if (healthOverheat && !didOverheat) {
            cache.putBatteryOverheat(true)
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.Overheating
        }
        if (!healthOverheat && didOverheat) {
            cache.putBatteryOverheat(false)
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.StoppedOverheating
        }
        // Skip everything until battery is no longer overheating
        if (healthOverheat) {
            return BatteryStatus.NoStatus
        }

        val isPluggedIn = acCharge || usbCharge || wirelessCharge || dockCharge

        // >= 80%
        if (batteryLvl >= BATTERY_LEVEL_CHARGED) {
            cache.clearNotifyAtBatteryLvl()
            if (isPluggedIn && (statusCharging || statusNotCharging) && didBatteryLvlChange) {
                cache.putBatteryLvl(batteryLvl)
                cache.apply()
                return BatteryStatus.StopCharging
            }
            if (!isPluggedIn && statusDischarging) {
                cache.apply()
                return BatteryStatus.DismissBatteryLvlNotification
            }
        }

        // No notification updates if battery lvl did not change
        if (!didBatteryLvlChange) {
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.NoStatus
        }

        cache.putBatteryLvl(batteryLvl)

        // > 60%
        if (batteryLvl > BATTERY_LEVEL_COULD_CHARGE) {
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.DismissBatteryLvlNotification
        }

        // <= 60%
        if (isPluggedIn && statusCharging) {
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.DismissBatteryLvlNotification
        }

        // <= 20%
        if (batteryLvl <= BATTERY_LEVEL_MUST_CHARGE) {
            cache.clearNotifyAtBatteryLvl()
            cache.apply()
            return BatteryStatus.Charge20
        }

        // > 20% && <= 60%
        if (!isPluggedIn && statusDischarging) {
            val notifyAtBatteryLvl: Int = SharedPrefs.getNotifyAtBatteryLvl()
            if (notifyAtBatteryLvl != -1) {
                if (batteryLvl <= notifyAtBatteryLvl) {
                    cache.clearNotifyAtBatteryLvl()
                    cache.apply()
                    return BatteryStatus.Charge60
                }
            } else {
                cache.clearNotifyAtBatteryLvl()
                cache.apply()
                return BatteryStatus.Charge60
            }
        }

        cache.apply()
        return BatteryStatus.NoStatus
    }

    sealed class BatteryStatus {
        data object NoStatus : BatteryStatus()

        data object Overheating : BatteryStatus()
        data object StoppedOverheating : BatteryStatus()

        data object Charge60 : BatteryStatus()
        data object Charge20 : BatteryStatus()

        data object StopCharging : BatteryStatus()

        data object DismissBatteryLvlNotification : BatteryStatus()
    }
}