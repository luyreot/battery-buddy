package com.teoryul.batterybuddy.model

import androidx.annotation.StringRes
import com.teoryul.batterybuddy.R

enum class NotificationType(
    @StringRes
    val titleStringRes: Int,
    @StringRes
    val textStringRes: Int
) {
    ABOVE_80(
        titleStringRes = R.string.notification_title_battery_above_80,
        textStringRes = R.string.notification_text_battery_above_80
    ),

    BELOW_60(
        titleStringRes = R.string.notification_title_battery_below_60,
        textStringRes = R.string.notification_text_battery_below_60
    ),

    BELOW_20(
        titleStringRes = R.string.notification_title_battery_below_20,
        textStringRes = R.string.notification_text_battery_below_20
    )
}