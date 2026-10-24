package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.GlazeStyle
import com.example.data.VesselShape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CeramicProductIllustration(
    vesselShape: VesselShape,
    glazeStyle: GlazeStyle,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Palette based on selected GlazeStyle
            val bodyBase: Color
            val bodyShade: Color
            val patternColor: Color
            val rimColor: Color
            val bgAura: Color

            when (glazeStyle) {
                GlazeStyle.QINGHUA_BLUE -> {
                    bodyBase = Color(0xFFFDFBF7)
                    bodyShade = Color(0xFFE7EEF8)
                    patternColor = Color(0xFF1E3A5F)
                    rimColor = Color(0xFF284D7E)
                    bgAura = Color(0xFFD9E6F7)
                }
                GlazeStyle.CELADON_JADE -> {
                    bodyBase = Color(0xFFD8EBE3)
                    bodyShade = Color(0xFFA7CEBF)
                    patternColor = Color(0xFF2F5E4E)
                    rimColor = Color(0xFF457866)
                    bgAura = Color(0xFFCCE4DA)
                }
                GlazeStyle.DEHUA_WHITE -> {
                    bodyBase = Color(0xFFFFFCF5)
                    bodyShade = Color(0xFFEDE4D4)
                    patternColor = Color(0xFF9C8B74)
                    rimColor = Color(0xFFC2B29B)
                    bgAura = Color(0xFFF0E8DA)
                }
                GlazeStyle.TERRACOTTA_CLAY -> {
                    bodyBase = Color(0xFFC4694B)
                    bodyShade = Color(0xFF8F432A)
                    patternColor = Color(0xFF4A1F10)
                    rimColor = Color(0xFFE08B6D)
                    bgAura = Color(0xFFF3D5C9)
                }
                GlazeStyle.STARRY_KILN -> {
                    bodyBase = Color(0xFF2A476E)
                    bodyShade = Color(0xFF152740)
                    patternColor = Color(0xFF8CC4FA)
                    rimColor = Color(0xFFB5895A) // Iron-rust rim typical of Yaobian ware
                    bgAura = Color(0xFFD2DFEE)
                }
                GlazeStyle.PASTEL_FLORAL -> {
                    bodyBase = Color(0xFFFDF9F5)
                    bodyShade = Color(0xFFF5E6DF)
                    patternColor = Color(0xFFD1604B)
                    rimColor = Color(0xFF568B78)
                    bgAura = Color(0xFFF9E2DC)
                }
            }

            // Soft circular warm studio backdrop aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(bgAura.copy(alpha = 0.75f), bgAura.copy(alpha = 0.0f)),
                    center = Offset(cx, cy),
                    radius = minOf(w, h) * 0.48f
                ),
                radius = minOf(w, h) * 0.48f,
                center = Offset(cx, cy)
            )

            // Soft contact shadow on wooden/linen surface
            drawOval(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(cx - w * 0.28f, cy + h * 0.24f),
                size = Size(w * 0.56f, h * 0.10f)
            )

            val ceramicBrush = Brush.linearGradient(
                colors = listOf(bodyBase, bodyShade),
                start = Offset(cx - w * 0.25f, cy - h * 0.2f),
                end = Offset(cx + w * 0.25f, cy + h * 0.25f)
            )

            when (vesselShape) {
                VesselShape.RICE_BOWL -> drawRiceBowl(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.NOODLE_BOWL -> drawNoodleBowl(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.PETAL_PLATE -> drawPetalPlate(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.DUMPLING_PLATTER -> drawDumplingPlatter(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.DAILY_MUG -> drawDailyMug(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.GAIWAN_SET -> drawGaiwanSet(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.SPOON_CHOPSTICK -> drawSpoonSet(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
                VesselShape.TEAPOT_SET -> drawTeapotSet(cx, cy, w, h, ceramicBrush, bodyBase, patternColor, rimColor)
            }
        }
    }
}

private fun DrawScope.drawRiceBowl(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val bw = w * 0.56f
    val bh = h * 0.34f
    val topY = cy - bh * 0.35f

    // Foot ring (굽)
    drawRoundRect(
        color = rimColor.copy(alpha = 0.85f),
        topLeft = Offset(cx - bw * 0.24f, topY + bh * 0.88f),
        size = Size(bw * 0.48f, bh * 0.18f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Bowl body curve
    val path = Path().apply {
        moveTo(cx - bw / 2f, topY)
        cubicTo(
            cx - bw * 0.48f, topY + bh * 0.85f,
            cx - bw * 0.25f, topY + bh * 0.96f,
            cx, topY + bh * 0.96f
        )
        cubicTo(
            cx + bw * 0.25f, topY + bh * 0.96f,
            cx + bw * 0.48f, topY + bh * 0.85f,
            cx + bw / 2f, topY
        )
        close()
    }
    drawPath(path = path, brush = ceramicBrush)
    drawPath(path = path, color = rimColor.copy(alpha = 0.45f), style = Stroke(width = 2.5f))

    // Top inner rim oval
    drawOval(
        color = bodyBase,
        topLeft = Offset(cx - bw / 2f, topY - bh * 0.16f),
        size = Size(bw, bh * 0.32f)
    )
    drawOval(
        color = rimColor,
        topLeft = Offset(cx - bw / 2f, topY - bh * 0.16f),
        size = Size(bw, bh * 0.32f),
        style = Stroke(width = 3f)
    )

    // Lotus / floral band on exterior
    for (i in -2..2) {
        val px = cx + i * (bw * 0.15f)
        val py = topY + bh * 0.44f - kotlin.math.abs(i) * 3f
        drawCircle(color = patternColor.copy(alpha = 0.78f), radius = w * 0.025f, center = Offset(px, py))
        drawCircle(color = bodyBase, radius = w * 0.01f, center = Offset(px, py))
    }
    // Classic double cobalt line near rim
    drawArc(
        color = patternColor.copy(alpha = 0.65f),
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(cx - bw * 0.46f, topY + bh * 0.05f),
        size = Size(bw * 0.92f, bh * 0.25f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawNoodleBowl(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val bw = w * 0.66f
    val bh = h * 0.38f
    val topY = cy - bh * 0.32f

    // Foot ring
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(cx - bw * 0.25f, topY + bh * 0.88f),
        size = Size(bw * 0.50f, bh * 0.16f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Wide flared noodle bowl body
    val path = Path().apply {
        moveTo(cx - bw / 2f, topY)
        cubicTo(
            cx - bw * 0.44f, topY + bh * 0.82f,
            cx - bw * 0.26f, topY + bh * 0.94f,
            cx, topY + bh * 0.94f
        )
        cubicTo(
            cx + bw * 0.26f, topY + bh * 0.94f,
            cx + bw * 0.44f, topY + bh * 0.82f,
            cx + bw / 2f, topY
        )
        close()
    }
    drawPath(path = path, brush = ceramicBrush)
    drawPath(path = path, color = rimColor.copy(alpha = 0.5f), style = Stroke(width = 3f))

    // Inner mouth
    drawOval(
        color = bodyBase,
        topLeft = Offset(cx - bw / 2f, topY - bh * 0.18f),
        size = Size(bw, bh * 0.36f)
    )
    drawOval(
        color = rimColor,
        topLeft = Offset(cx - bw / 2f, topY - bh * 0.18f),
        size = Size(bw, bh * 0.36f),
        style = Stroke(width = 3.5f)
    )

    // Wave / spiral band motif
    for (i in -3..3) {
        val wx = cx + i * (bw * 0.11f)
        val wy = topY + bh * 0.48f
        drawCircle(
            color = patternColor.copy(alpha = 0.75f),
            radius = w * 0.028f,
            center = Offset(wx, wy),
            style = Stroke(width = 2.5f)
        )
    }
    // Chopsticks resting on top
    drawLine(
        color = Color(0xFF7A5230),
        start = Offset(cx - bw * 0.38f, topY - bh * 0.12f),
        end = Offset(cx + bw * 0.45f, topY - bh * 0.24f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF7A5230),
        start = Offset(cx - bw * 0.38f, topY - bh * 0.04f),
        end = Offset(cx + bw * 0.45f, topY - bh * 0.15f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawPetalPlate(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val radius = minOf(w, h) * 0.34f
    // Draw 8 subtle petal lobes around plate edge
    for (i in 0 until 8) {
        val angle = (i * 45.0) * PI / 180.0
        val px = cx + (radius * 0.28f * cos(angle)).toFloat()
        val py = cy + (radius * 0.28f * sin(angle)).toFloat()
        drawCircle(
            brush = ceramicBrush,
            radius = radius * 0.78f,
            center = Offset(px, py)
        )
        drawCircle(
            color = rimColor.copy(alpha = 0.45f),
            radius = radius * 0.78f,
            center = Offset(px, py),
            style = Stroke(width = 2.2f)
        )
    }
    // Center well of plate
    drawCircle(
        color = bodyBase,
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = rimColor.copy(alpha = 0.55f),
        radius = radius * 0.65f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )
    // Center floral motif
    for (i in 0 until 5) {
        val angle = (i * 72.0) * PI / 180.0
        val fx = cx + (radius * 0.18f * cos(angle)).toFloat()
        val fy = cy + (radius * 0.18f * sin(angle)).toFloat()
        drawCircle(
            color = patternColor.copy(alpha = 0.75f),
            radius = radius * 0.11f,
            center = Offset(fx, fy)
        )
    }
    drawCircle(color = bodyBase, radius = radius * 0.07f, center = Offset(cx, cy))
}

private fun DrawScope.drawDumplingPlatter(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val pw = w * 0.70f
    val ph = h * 0.44f
    // Outer rounded platter
    drawRoundRect(
        brush = ceramicBrush,
        topLeft = Offset(cx - pw / 2f, cy - ph / 2f),
        size = Size(pw, ph),
        cornerRadius = CornerRadius(ph * 0.42f, ph * 0.42f)
    )
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(cx - pw / 2f, cy - ph / 2f),
        size = Size(pw, ph),
        cornerRadius = CornerRadius(ph * 0.42f, ph * 0.42f),
        style = Stroke(width = 3f)
    )
    // Inner main cavity
    drawRoundRect(
        color = bodyBase,
        topLeft = Offset(cx - pw * 0.44f, cy - ph * 0.38f),
        size = Size(pw * 0.62f, ph * 0.76f),
        cornerRadius = CornerRadius(ph * 0.3f, ph * 0.3f)
    )
    // Built-in dipping sauce compartment on right
    val sauceCenter = Offset(cx + pw * 0.28f, cy)
    drawCircle(
        color = bodyBase,
        radius = ph * 0.32f,
        center = sauceCenter
    )
    drawCircle(
        color = rimColor.copy(alpha = 0.7f),
        radius = ph * 0.32f,
        center = sauceCenter,
        style = Stroke(width = 2.5f)
    )
    // Soy sauce fill inside compartment
    drawCircle(
        color = Color(0xFF5E2C14).copy(alpha = 0.75f),
        radius = ph * 0.22f,
        center = sauceCenter
    )
    // Decorative pattern motif on main platter
    drawCircle(
        color = patternColor.copy(alpha = 0.7f),
        radius = ph * 0.14f,
        center = Offset(cx - pw * 0.14f, cy),
        style = Stroke(width = 2.5f)
    )
}

private fun DrawScope.drawDailyMug(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val mw = w * 0.36f
    val mh = h * 0.48f
    val topY = cy - mh * 0.38f

    // Mug handle
    drawArc(
        color = rimColor,
        startAngle = -65f,
        sweepAngle = 130f,
        useCenter = false,
        topLeft = Offset(cx + mw * 0.15f, topY + mh * 0.18f),
        size = Size(mw * 0.58f, mh * 0.56f),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )

    // Mug cylinder body
    drawRoundRect(
        brush = ceramicBrush,
        topLeft = Offset(cx - mw / 2f, topY),
        size = Size(mw, mh),
        cornerRadius = CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = rimColor.copy(alpha = 0.6f),
        topLeft = Offset(cx - mw / 2f, topY),
        size = Size(mw, mh),
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(width = 2.5f)
    )

    // Infuser lid on top with wooden/ceramic knob
    drawRoundRect(
        color = bodyBase,
        topLeft = Offset(cx - mw * 0.54f, topY - mh * 0.10f),
        size = Size(mw * 1.08f, mh * 0.14f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(cx - mw * 0.54f, topY - mh * 0.10f),
        size = Size(mw * 1.08f, mh * 0.14f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 2.5f)
    )
    drawCircle(
        color = patternColor,
        radius = mw * 0.10f,
        center = Offset(cx, topY - mh * 0.14f)
    )

    // Center motif on mug body
    drawCircle(
        color = patternColor.copy(alpha = 0.72f),
        radius = mw * 0.22f,
        center = Offset(cx, topY + mh * 0.52f),
        style = Stroke(width = 2.5f)
    )
}

private fun DrawScope.drawGaiwanSet(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val gw = w * 0.52f
    val gh = h * 0.32f

    // Bottom saucer (찻받침)
    drawOval(
        brush = ceramicBrush,
        topLeft = Offset(cx - gw * 0.62f, cy + gh * 0.42f),
        size = Size(gw * 1.24f, gh * 0.36f)
    )
    drawOval(
        color = rimColor,
        topLeft = Offset(cx - gw * 0.62f, cy + gh * 0.42f),
        size = Size(gw * 1.24f, gh * 0.36f),
        style = Stroke(width = 2.5f)
    )

    // Gaiwan bowl body
    val path = Path().apply {
        moveTo(cx - gw / 2f, cy - gh * 0.2f)
        cubicTo(
            cx - gw * 0.42f, cy + gh * 0.55f,
            cx - gw * 0.22f, cy + gh * 0.62f,
            cx, cy + gh * 0.62f
        )
        cubicTo(
            cx + gw * 0.22f, cy + gh * 0.62f,
            cx + gw * 0.42f, cy + gh * 0.55f,
            cx + gw / 2f, cy - gh * 0.2f
        )
        close()
    }
    drawPath(path = path, brush = ceramicBrush)
    drawPath(path = path, color = rimColor, style = Stroke(width = 2.5f))

    // Flared rim
    drawOval(
        color = bodyBase,
        topLeft = Offset(cx - gw * 0.55f, cy - gh * 0.32f),
        size = Size(gw * 1.10f, gh * 0.24f)
    )
    drawOval(
        color = rimColor,
        topLeft = Offset(cx - gw * 0.55f, cy - gh * 0.32f),
        size = Size(gw * 1.10f, gh * 0.24f),
        style = Stroke(width = 2.5f)
    )

    // Domed lid with finial button
    drawArc(
        brush = ceramicBrush,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx - gw * 0.42f, cy - gh * 0.62f),
        size = Size(gw * 0.84f, gh * 0.56f)
    )
    drawArc(
        color = rimColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx - gw * 0.42f, cy - gh * 0.62f),
        size = Size(gw * 0.84f, gh * 0.56f),
        style = Stroke(width = 2.5f)
    )
    drawCircle(
        color = patternColor,
        radius = gw * 0.08f,
        center = Offset(cx, cy - gh * 0.66f)
    )
}

private fun DrawScope.drawSpoonSet(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    // Spoon bowl (left-bottom) and handle (right-top)
    val bowlCenter = Offset(cx - w * 0.12f, cy + h * 0.06f)
    drawOval(
        brush = ceramicBrush,
        topLeft = Offset(bowlCenter.x - w * 0.18f, bowlCenter.y - h * 0.12f),
        size = Size(w * 0.36f, h * 0.24f)
    )
    drawOval(
        color = rimColor,
        topLeft = Offset(bowlCenter.x - w * 0.18f, bowlCenter.y - h * 0.12f),
        size = Size(w * 0.36f, h * 0.24f),
        style = Stroke(width = 2.5f)
    )
    // Curved ceramic handle
    val handlePath = Path().apply {
        moveTo(bowlCenter.x + w * 0.10f, bowlCenter.y - h * 0.06f)
        quadraticTo(
            cx + w * 0.14f, cy - h * 0.16f,
            cx + w * 0.28f, cy - h * 0.22f
        )
        lineTo(cx + w * 0.31f, cy - h * 0.15f)
        quadraticTo(
            cx + w * 0.16f, cy - h * 0.08f,
            bowlCenter.x + w * 0.15f, bowlCenter.y + h * 0.02f
        )
        close()
    }
    drawPath(path = handlePath, brush = ceramicBrush)
    drawPath(path = handlePath, color = rimColor, style = Stroke(width = 2.5f))

    // Floral dot inside spoon bowl
    drawCircle(
        color = patternColor.copy(alpha = 0.8f),
        radius = w * 0.04f,
        center = bowlCenter
    )
    drawCircle(
        color = bodyBase,
        radius = w * 0.015f,
        center = bowlCenter
    )
}

private fun DrawScope.drawTeapotSet(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    ceramicBrush: Brush,
    bodyBase: Color,
    patternColor: Color,
    rimColor: Color
) {
    val potCenter = Offset(cx - w * 0.08f, cy + h * 0.02f)
    val pr = minOf(w, h) * 0.22f

    // Spout (left)
    drawLine(
        color = rimColor,
        start = Offset(potCenter.x - pr * 0.85f, potCenter.y),
        end = Offset(potCenter.x - pr * 1.35f, potCenter.y - pr * 0.55f),
        strokeWidth = 7f,
        cap = StrokeCap.Round
    )
    // Handle (right)
    drawArc(
        color = rimColor,
        startAngle = -70f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(potCenter.x + pr * 0.4f, potCenter.y - pr * 0.6f),
        size = Size(pr * 0.9f, pr * 1.2f),
        style = Stroke(width = 5.5f, cap = StrokeCap.Round)
    )
    // Round pot body
    drawCircle(
        brush = ceramicBrush,
        radius = pr,
        center = potCenter
    )
    drawCircle(
        color = rimColor,
        radius = pr,
        center = potCenter,
        style = Stroke(width = 2.5f)
    )
    // Lid button
    drawCircle(
        color = patternColor,
        radius = pr * 0.18f,
        center = Offset(potCenter.x, potCenter.y - pr * 1.05f)
    )

    // Small companion teacup on the right
    val cupCenter = Offset(cx + w * 0.24f, cy + h * 0.12f)
    drawRoundRect(
        brush = ceramicBrush,
        topLeft = Offset(cupCenter.x - w * 0.09f, cupCenter.y - h * 0.08f),
        size = Size(w * 0.18f, h * 0.18f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(cupCenter.x - w * 0.09f, cupCenter.y - h * 0.08f),
        size = Size(w * 0.18f, h * 0.18f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 2f)
    )
    drawCircle(color = bodyBase, radius = 3f, center = cupCenter)
}
