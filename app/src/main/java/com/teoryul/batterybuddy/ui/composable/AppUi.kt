package com.teoryul.batterybuddy.ui.composable

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teoryul.batterybuddy.R

@Composable
fun AppUi() {
    Scaffold { innerPadding ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestPushPermission(
                onPermissionGranted = {
                    MainScreen(modifier = Modifier.padding(innerPadding))
                },
                onShowRationale = { onClick: () -> Unit ->
                    ConfirmationDialog(
                        imageRes = R.drawable.ic_bell,
                        imageResWidth = 64.dp,
                        title = stringResource(R.string.dialog_push_permission_title),
                        content = stringResource(R.string.dialog_push_permission_content),
                        confirmationText = stringResource(R.string.dialog_push_permission_button_allow_text),
                        onConfirmation = onClick
                    )
                },
                onPermissionDenied = { onClick: () -> Unit ->
                    ConfirmationDialog(
                        imageRes = R.drawable.ic_bell,
                        imageResWidth = 64.dp,
                        title = stringResource(R.string.dialog_push_permission_title),
                        content = stringResource(R.string.dialog_push_permission_content),
                        confirmationText = stringResource(R.string.dialog_push_permission_button_go_to_settings_text),
                        onConfirmation = onClick
                    )
                }
            )
        } else {
            MainScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}