package com.teoryul.batterybuddy.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmationDialog(
    @DrawableRes
    imageRes: Int,
    imageResWidth: Dp,
    title: String,
    content: String,
    confirmationText: String,
    onConfirmation: () -> Unit,
    onDismissal: (() -> Unit)? = null
) {
    AlertDialog(
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .width(imageResWidth)
                        .wrapContentHeight(),
                    painter = painterResource(imageRes),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = content)
            }
        },
        onDismissRequest = { onDismissal?.invoke() },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(confirmationText)
            }
        }
    )
}