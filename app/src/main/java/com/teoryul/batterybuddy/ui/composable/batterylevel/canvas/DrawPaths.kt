package com.teoryul.batterybuddy.ui.composable.batterylevel.canvas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.drawText
import com.teoryul.batterybuddy.ui.composable.batterylevel.Paths
import com.teoryul.batterybuddy.ui.composable.batterylevel.text.TextParams
import com.teoryul.batterybuddy.ui.theme.Blue
import com.teoryul.batterybuddy.ui.theme.BatteryLvlBackground

fun DrawScope.drawLevels(
    paths: Paths
) {
    drawIntoCanvas {
        it.drawPath(
            paths.pathList[1],
            paint.apply {
                color = Blue
            }
        )
        it.drawPath(
            paths.pathList[0],
            paint.apply {
                color = Color.Black
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
        color = BatteryLvlBackground,
        blendMode = BlendMode.SrcIn
    )
}

val paint = Paint().apply {
    this.color = Blue
    pathEffect = PathEffect.cornerPathEffect(100f)
}