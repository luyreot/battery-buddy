package com.teoryul.batterybuddy.domain

import com.teoryul.batterybuddy.data.BatteryStats
import com.teoryul.batterybuddy.data.SharedPrefs

class BatteryStatusUseCase(
    private val sharedPrefs: SharedPrefs,
    private val batteryStats: BatteryStats
) {

    private companion object {
        const val BATTERY_LEVEL_CHARGED: Int = 80
        const val BATTERY_LEVEL_COULD_CHARGE: Int = 60
        const val BATTERY_LEVEL_MUST_CHARGE: Int = 20
    }

    /**
     * Test with [level] being the actual battery level and [scale] of 100.
     */
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
        // Update battery lvl
        val batteryLvl = (level * 100 / scale.toFloat()).toInt()
        val didBatteryLvlChange = batteryLvl != sharedPrefs.getBatteryLvl()
        batteryStats.batteryLvlInt = batteryLvl
        if (didBatteryLvlChange) {
            sharedPrefs.putBatteryLvl(batteryLvl)
        }

        // Update plugged in state
        val isPluggedIn = acCharge || usbCharge || wirelessCharge || dockCharge
        val didPluggedInStatusChange = isPluggedIn != sharedPrefs.gutPhonePluggedIn()
        batteryStats.isPluggedIn = isPluggedIn
        if (didPluggedInStatusChange) {
            sharedPrefs.putPhonePluggedIn(isPluggedIn)
        }

        // Overheating
        val didOverheat = sharedPrefs.getBatteryOverheat()
        if (healthOverheat && !didOverheat) {
            sharedPrefs.putBatteryOverheat(true)
            sharedPrefs.clearNotifyAtBatteryLvl()
            return BatteryStatus.Overheating
        }
        if (!healthOverheat && didOverheat) {
            sharedPrefs.putBatteryOverheat(false)
            sharedPrefs.clearNotifyAtBatteryLvl()
            return BatteryStatus.StoppedOverheating
        }
        // Wait until battery is no longer overheating
        if (healthOverheat) {
            if (didBatteryLvlChange || didPluggedInStatusChange) {
                return BatteryStatus.Overheating
            }
            return BatteryStatus.NoStatus
        }

        // Phone got plugged in
        if (isPluggedIn && didPluggedInStatusChange) {
            sharedPrefs.clearNotifyAtBatteryLvl()
            // >= 80%
            if (batteryLvl >= BATTERY_LEVEL_CHARGED) {
                return BatteryStatus.StopCharging
            }
            // < 80%
            return BatteryStatus.DismissBatteryLvlNotification
        }

        // Phone got unplugged
        if (!isPluggedIn && didPluggedInStatusChange) {
            sharedPrefs.clearNotifyAtBatteryLvl()
            // > 60%
            if (batteryLvl > BATTERY_LEVEL_COULD_CHARGE) {
                return BatteryStatus.DismissBatteryLvlNotification
            }
            // > 20%
            if (batteryLvl > BATTERY_LEVEL_MUST_CHARGE) {
                return BatteryStatus.Charge60
            }
            // <= 20%
            return BatteryStatus.Charge20
        }

        // Wait until battery level changes
        if (!didBatteryLvlChange) {
            return BatteryStatus.NoStatus
        }

        // Battery Charging
        if (isPluggedIn && (statusCharging || statusNotCharging)) {
            sharedPrefs.clearNotifyAtBatteryLvl()
            // >= 80%
            if (batteryLvl >= BATTERY_LEVEL_CHARGED) {
                return BatteryStatus.StopCharging
            }
            // < 80%
            return BatteryStatus.DismissBatteryLvlNotification
        }

        // Battery draining
        if (!isPluggedIn && statusDischarging) {
            // <= 20%
            if (batteryLvl <= BATTERY_LEVEL_MUST_CHARGE) {
                sharedPrefs.clearNotifyAtBatteryLvl()
                return BatteryStatus.Charge20
            }
            // > 60%
            if (batteryLvl > BATTERY_LEVEL_COULD_CHARGE) {
                return BatteryStatus.DismissBatteryLvlNotification
            }
            // <= 60%
            val notifyAtBatteryLvl: Int = sharedPrefs.getNotifyAtBatteryLvl()
            if (notifyAtBatteryLvl == -1) {
                return BatteryStatus.Charge60
            }
            if (batteryLvl <= notifyAtBatteryLvl) {
                sharedPrefs.clearNotifyAtBatteryLvl()
                return BatteryStatus.Charge60
            }
        }

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