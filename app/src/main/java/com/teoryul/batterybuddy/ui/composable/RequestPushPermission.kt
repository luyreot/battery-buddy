package com.teoryul.batterybuddy.ui.composable

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.teoryul.batterybuddy.util.CommonUtil.openAppInfoScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPushPermission(
    onPermissionGranted: @Composable () -> Unit,
    onShowRationale: @Composable (onClick: () -> Unit) -> Unit,
    onPermissionDenied: @Composable (onClick: () -> Unit) -> Unit
) {
    val pushPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    var didRequestPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (pushPermissionState.status.isGranted) {
        onPermissionGranted()
        return
    }

    if (didRequestPermission) {
        onPermissionDenied { openAppInfoScreen(context) }
        return
    }

    onShowRationale {
        pushPermissionState.launchPermissionRequest()
        // TODO - This line immediately triggers the onPermissionDenied logic above
        didRequestPermission = true
    }
}