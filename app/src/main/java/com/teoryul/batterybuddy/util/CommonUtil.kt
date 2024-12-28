package com.teoryul.batterybuddy.util

import android.annotation.SuppressLint
import android.app.Activity.RECEIVER_NOT_EXPORTED
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.teoryul.batterybuddy.receiver.PowerConnectionReceiver

object CommonUtil {

    fun openAppInfoScreen(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.setData(uri)
        context.startActivity(intent)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiverActionBatteryChanged(context: Context) {
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    PowerConnectionReceiver(),
                    intentFilter,
                    RECEIVER_NOT_EXPORTED
                )
            } else {
                context.registerReceiver(
                    PowerConnectionReceiver(),
                    intentFilter
                )
            }
        }
    }
}