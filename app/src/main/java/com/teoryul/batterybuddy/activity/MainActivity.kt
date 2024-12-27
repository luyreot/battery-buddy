package com.teoryul.batterybuddy.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teoryul.batterybuddy.R
import com.teoryul.batterybuddy.common.ConfirmationDialog
import com.teoryul.batterybuddy.notification.NotificationUtil.RequestPushPermission
import com.teoryul.batterybuddy.ui.theme.BatteryBuddyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BatteryBuddyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        RequestPushPermission(
                            onPermissionGranted = {
                                Greeting(modifier = Modifier.padding(innerPadding))
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
                        Greeting(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

// TODO Change
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Gucci Marcucci",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BatteryBuddyTheme {
        Greeting()
    }
}