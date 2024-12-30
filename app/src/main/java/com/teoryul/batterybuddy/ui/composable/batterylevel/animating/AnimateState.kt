package com.teoryul.batterybuddy.ui.composable.batterylevel.animating

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember

@Composable
fun waveProgressAsState(
    batteryLvl: Float,
    timerDurationInMillis: Long
): State<Float> {
    val animatable = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(batteryLvl) {
        animatable.animateTo(
            targetValue = (100f - batteryLvl) / 100f, // TODO controls the level
            animationSpec = tween(
                durationMillis = timerDurationInMillis.toInt(),
                easing = LinearEasing
            )
        )
    }

    return produceState(initialValue = animatable.value, key1 = animatable.value) {
        this.value = animatable.value
    }
}