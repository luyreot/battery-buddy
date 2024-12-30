package com.teoryul.batterybuddy.model

import androidx.annotation.StringRes
import com.teoryul.batterybuddy.R

enum class NotificationType(
    @StringRes
    val titleStringRes: Int,
    @StringRes
    val textStringRes: Int,
    val notificationId: Int
) {
    ABOVE_80(
        titleStringRes = R.string.notification_title_battery_above_80,
        textStringRes = R.string.notification_text_battery_above_80,
        notificationId = Int.MIN_VALUE
    ),

    BELOW_60(
        titleStringRes = R.string.notification_title_battery_below_60,
        textStringRes = R.string.notification_text_battery_below_60,
        notificationId = Int.MIN_VALUE
    ),

    BELOW_20(
        titleStringRes = R.string.notification_title_battery_below_20,
        textStringRes = R.string.notification_text_battery_below_20,
        notificationId = Int.MIN_VALUE
    ),

    OVERHEAT(
        titleStringRes = R.string.notification_title_battery_health_overheat,
        textStringRes = R.string.notification_text_battery_health_overheat,
        notificationId = Int.MIN_VALUE + 1
    ),

    OVERHEAT_NOT(
        titleStringRes = R.string.notification_title_battery_health_overheat_not,
        textStringRes = R.string.notification_text_battery_health_overheat_not,
        notificationId = Int.MIN_VALUE + 1
    )
}