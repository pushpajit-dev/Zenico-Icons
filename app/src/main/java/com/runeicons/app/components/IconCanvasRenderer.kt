package com.runeicons.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class BackgroundStyle(val label: String) {
    NONE("Transparent"),
    CARD("Frosted Card"),
    CIRCLE("Circle"),
    NEUMORPHIC("Glass Glow"),
    SQUARE("Rounded Box")
}

@Composable
fun InteractiveIconCanvas(
    pathData: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 120.dp,
    strokeWidth: Float = 2.5f,
    strokeCap: StrokeCap = StrokeCap.Round,
    strokeJoin: StrokeJoin = StrokeJoin.Round,
    tintColor: Color = MaterialTheme.colorScheme.primary,
    backgroundStyle: BackgroundStyle = BackgroundStyle.CARD,
    scale: Float = 1f,
    rotation: Float = 0f,
    offset: Offset = Offset.Zero,
    onTransformChange: ((pan: Offset, zoom: Float, rotationChange: Float) -> Unit)? = null
) {
    val cachedPath = remember(pathData) {
        try {
            if (pathData.isNotBlank()) {
                PathParser().parsePathString(pathData).toPath()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    val bgShape = when (backgroundStyle) {
        BackgroundStyle.NONE -> RoundedCornerShape(0.dp)
        BackgroundStyle.CIRCLE -> CircleShape
        BackgroundStyle.CARD -> RoundedCornerShape(24.dp)
        BackgroundStyle.NEUMORPHIC -> RoundedCornerShape(28.dp)
        BackgroundStyle.SQUARE -> RoundedCornerShape(16.dp)
    }

    val bgModifier = when (backgroundStyle) {
        BackgroundStyle.NONE -> Modifier
        BackgroundStyle.CARD -> Modifier
            .shadow(12.dp, bgShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f), bgShape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), bgShape)
        BackgroundStyle.CIRCLE -> Modifier
            .shadow(10.dp, CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
        BackgroundStyle.NEUMORPHIC -> Modifier
            .shadow(18.dp, bgShape, ambientColor = tintColor.copy(alpha = 0.3f), spotColor = tintColor)
            .background(MaterialTheme.colorScheme.surface, bgShape)
            .border(1.5.dp, tintColor.copy(alpha = 0.4f), bgShape)
        BackgroundStyle.SQUARE -> Modifier
            .shadow(8.dp, bgShape)
            .background(MaterialTheme.colorScheme.surfaceVariant, bgShape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), bgShape)
    }

    Box(
        modifier = modifier
            .size(iconSize + 80.dp)
            .then(bgModifier)
            .clip(bgShape)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotationChange ->
                    onTransformChange?.invoke(pan, zoom, rotationChange)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(iconSize)) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Runeicons default viewport is 24x24
            val scaleFactor = (canvasWidth / 24f) * scale

            translate(offset.x, offset.y) {
                rotate(rotation, pivot = center) {
                    scale(scale = scaleFactor, pivot = Offset(0f, 0f)) {
                        if (cachedPath != null) {
                            drawPath(
                                path = cachedPath,
                                color = tintColor,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = strokeCap,
                                    join = strokeJoin
                                )
                            )
                        } else {
                            // Fallback sample icon if path is empty
                            drawCircle(
                                color = tintColor,
                                radius = 8f,
                                center = Offset(12f, 12f),
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }
                }
            }
        }
    }
}
