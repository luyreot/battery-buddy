package com.teoryul.batterybuddy.ui.composable.batterylevel.canvas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.drawText
import com.teoryul.batterybuddy.ui.composable.batterylevel.Paths
import com.teoryul.batterybuddy.ui.composable.batterylevel.text.TextParams
import com.teoryul.batterybuddy.ui.theme.BatteryLvlBackground
import com.teoryul.batterybuddy.ui.theme.BatteryLvlForeground
import com.teoryul.batterybuddy.ui.theme.BatteryLvlText

fun DrawScope.drawLevels(
    paths: Paths
) {
    drawIntoCanvas {
        it.drawPath(
            paths.pathList[1],
            paint.apply {
                color = BatteryLvlBackground
            }
        )
        it.drawPath(
            paths.pathList[0],
            paint.apply {
                color = BatteryLvlForeground
                alpha = 0.9f
            }
        )
    }
}

fun DrawScope.drawTextWithBlendMode(
    mask: Path,
    textParams: TextParams
) {
    drawText(
        textMeasurer = textParams.textMeasurer,
        topLeft = textParams.textOffset,
        text = textParams.text,
        style = textParams.textStyle
    )
    drawText(
        textMeasurer = textParams.textMeasurer,
        topLeft = textParams.unitTextOffset,
        text = "%",
        style = textParams.unitTextStyle
    )
    drawPath(
        path = mask,
        color = BatteryLvlText,
        blendMode = BlendMode.SrcIn
    )
}

val paint = Paint().apply {
    this.color = BatteryLvlBackground
    pathEffect = PathEffect.cornerPathEffect(100f)
}