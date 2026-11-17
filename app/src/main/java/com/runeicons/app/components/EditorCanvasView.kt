package com.runeicons.app.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon
import com.runeicons.app.ui.theme.parseHexColor
import com.runeicons.app.utils.IconExportUtils
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun EditorCanvasView(
    state: CustomizationState,
    currentIcon: RuneIcon?,
    variantIcons: List<RuneIcon>,
    selectedStyle: IconStyle = IconStyle.NORMAL,
    onSelectVariant: (RuneIcon) -> Unit,
    onStateChange: (CustomizationState) -> Unit,
    onOpenPropertiesSheet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val nodes = remember { mutableStateListOf<EditorNode>() }
    var selectedNodeIndex by remember { mutableIntStateOf(-1) }
    var currentPathString by remember { mutableStateOf(currentIcon?.pathData ?: "") }

    val undoStack = remember { mutableStateListOf<String>() }
    val redoStack = remember { mutableStateListOf<String>() }

    var canvasScale by remember { mutableFloatStateOf(1.0f) }
    var canvasPan by remember { mutableStateOf(Offset.Zero) }

    var drawStartOffset by remember { mutableStateOf<Offset?>(null) }

    LaunchedEffect(currentIcon) {
        if (currentIcon != null) {
            currentPathString = currentIcon.pathData
            nodes.clear()
            nodes.addAll(SvgPathEditorUtils.parsePathToNodes(currentIcon.pathData))
            undoStack.clear()
            redoStack.clear()
            selectedNodeIndex = -1
        }
    }

    var sizeMenuOpen by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImplementationDialog by remember { mutableStateOf(false) }

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val canvasBg = if (isDark) Color(0xFF09090C) else Color(0xFFFAFAFA)
    val gridColor = if (isDark) Color(0xFF16161B) else Color(0xFFE5E5E8)
    val textPrimary = if (isDark) Color(0xFFF4F4F6) else Color(0xFF141418)
    val textMuted = if (isDark) Color(0xFF71717A) else Color(0xFF8E8E93)
    val brandBlue = Color(0xFF3859FD)

    val strokeCap = when (state.strokeStyle) {
        "soft", "round" -> StrokeCap.Round
        "medium" -> StrokeCap.Square
        else -> StrokeCap.Butt
    }

    val strokeJoin = when (state.strokeStyle) {
        "soft", "round" -> StrokeJoin.Round
        "medium" -> StrokeJoin.Bevel
        else -> StrokeJoin.Miter
    }

    val resId = currentIcon?.getResForStyle(selectedStyle) ?: currentIcon?.normalRes

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBg)
    ) {
        // In "view" mode: render the authentic original icon as it is from the gallery!
        if (state.editorMode == "view" && resId != null) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(canvasScale, canvasPan) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            var previousCentroid = Offset.Zero
                            var previousSpan = 0f
                            while (true) {
                                val event = awaitPointerEvent()
                                val pressed = event.changes.filter { it.pressed }
                                if (pressed.isEmpty()) break
                                if (pressed.size >= 2) {
                                    val p1 = pressed[0].position
                                    val p2 = pressed[1].position
                                    val currentSpan = hypot(p1.x - p2.x, p1.y - p2.y)
                                    val currentCentroid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                                    if (previousSpan > 10f && currentSpan > 10f) {
                                        val zoomDelta = currentSpan / previousSpan
                                        if (zoomDelta in 0.8f..1.25f) {
                                            canvasScale = (canvasScale * zoomDelta).coerceIn(0.5f, 5.0f)
                                        }
                                        canvasPan += currentCentroid - previousCentroid
                                    }
                                    previousSpan = currentSpan
                                    previousCentroid = currentCentroid
                                    event.changes.forEach { it.consume() }
                                } else if (pressed.size == 1) {
                                    val ptr = pressed[0]
                                    if (previousCentroid != Offset.Zero) {
                                        canvasPan += ptr.position - previousCentroid
                                    }
                                    previousCentroid = ptr.position
                                    ptr.consume()
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val iconBoxDp = minOf(maxWidth - 40.dp, maxHeight - 152.dp, 320.dp).coerceAtLeast(180.dp)

                Box(
                    modifier = Modifier
                        .size(iconBoxDp)
                        .graphicsLayer {
                            translationX = canvasPan.x
                            translationY = canvasPan.y
                            scaleX = canvasScale * (if (state.flipH) -1f else 1f) * state.scale
                            scaleY = canvasScale * (if (state.flipV) -1f else 1f) * state.scale
                            rotationZ = state.rotation
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.showGrid) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.dp, gridColor.copy(alpha = 0.8f))
                        )
                    }

                    if (selectedStyle == IconStyle.GLASS) {
                        // Authentic Glassmorphism Card Plate
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.35f))
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            (if (isDark) Color(0xFF262632) else Color(0xFFF2F2F7)).copy(alpha = 0.72f),
                                            (if (isDark) Color(0xFF161622) else Color(0xFFE4E4EC)).copy(alpha = 0.52f)
                                        )
                                    )
                                )
                                .border(
                                    1.5.dp,
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.88f),
                                            Color.White.copy(alpha = 0.15f)
                                        )
                                    ),
                                    RoundedCornerShape(26.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = resId),
                                contentDescription = currentIcon?.name,
                                modifier = Modifier.fillMaxSize(0.65f),
                                tint = Color.Unspecified
                            )
                        }
                    } else {
                        // Duotone, Fill, Pixelated, Normal Original Vector
                        Icon(
                            painter = painterResource(id = resId),
                            contentDescription = currentIcon?.name,
                            modifier = Modifier.fillMaxSize(0.85f),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        } else {
        // Unified Touch Processor: Smooth Pinch-to-Zoom & Pan + Node Drag & Draw
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.editorMode, state.activeTool, nodes.size, canvasScale, canvasPan) {
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val topReserved = 68.dp.toPx()
                    val bottomReserved = 68.dp.toPx()
                    val maxBoxW = canvasWidth - 40.dp.toPx()
                    val maxBoxH = canvasHeight - topReserved - bottomReserved - 16.dp.toPx()
                    val iconBoxSize = minOf(maxBoxW, maxBoxH, 720f).coerceAtLeast(320f)
                    val originX = (canvasWidth - iconBoxSize) / 2f
                    val originY = topReserved + (canvasHeight - topReserved - bottomReserved - iconBoxSize) / 2f
                    val scaleFactor = iconBoxSize / 24f

                    val cx = canvasWidth / 2f
                    val cy = canvasHeight / 2f

                    fun screenToLocal(screenOffset: Offset): Offset {
                        val touchX = (screenOffset.x - canvasPan.x - cx) / canvasScale + cx
                        val touchY = (screenOffset.y - canvasPan.y - cy) / canvasScale + cy
                        val lx = ((touchX - originX) / scaleFactor).coerceIn(0f, 24f)
                        val ly = ((touchY - originY) / scaleFactor).coerceIn(0f, 24f)
                        return Offset(lx, ly)
                    }

                    awaitEachGesture {
                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                        var isPinching = false
                        var activeDraggingNode = -1
                        var previousCentroid = firstDown.position
                        var previousSpan = 0f
                        val startLocal = screenToLocal(firstDown.position)

                        if (state.editorMode == "edit" || state.editorMode == "draw") {
                            var closest = -1
                            var minDst = Float.MAX_VALUE
                            for (i in nodes.indices) {
                                val nx = (originX + nodes[i].x * scaleFactor - cx) * canvasScale + cx + canvasPan.x
                                val ny = (originY + nodes[i].y * scaleFactor - cy) * canvasScale + cy + canvasPan.y
                                val dst = hypot(firstDown.position.x - nx, firstDown.position.y - ny)
                                if (dst < 70f && dst < minDst) {
                                    minDst = dst
                                    closest = i
                                }
                            }

                            if (closest != -1) {
                                selectedNodeIndex = closest
                                activeDraggingNode = closest
                                undoStack.add(currentPathString)
                                redoStack.clear()
                            } else {
                                val midpoints = SvgPathEditorUtils.generateMidpointDots(nodes)
                                var closestMid = -1
                                var minMidDst = Float.MAX_VALUE
                                for (m in midpoints.indices) {
                                    val mx = (originX + midpoints[m].x * scaleFactor - cx) * canvasScale + cx + canvasPan.x
                                    val my = (originY + midpoints[m].y * scaleFactor - cy) * canvasScale + cy + canvasPan.y
                                    val dst = hypot(firstDown.position.x - mx, firstDown.position.y - my)
                                    if (dst < 45f && dst < minMidDst) {
                                        minMidDst = dst
                                        closestMid = m
                                    }
                                }
                                if (closestMid != -1) {
                                    val mid = midpoints[closestMid]
                                    undoStack.add(currentPathString)
                                    redoStack.clear()
                                    val insertIndex = (closestMid + 1).coerceAtMost(nodes.size)
                                    nodes.add(insertIndex, EditorNode(mid.x, mid.y, 'L'))
                                    selectedNodeIndex = insertIndex
                                    activeDraggingNode = insertIndex
                                    currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                }
                            }
                        } else if (state.editorMode == "draw") {
                            drawStartOffset = startLocal
                            undoStack.add(currentPathString)
                            redoStack.clear()
                            if (state.activeTool == "pen") {
                                if (nodes.isEmpty()) {
                                    nodes.add(EditorNode(startLocal.x, startLocal.y, 'M', isMoveTo = true))
                                } else {
                                    nodes.add(EditorNode(startLocal.x, startLocal.y, 'L'))
                                }
                                currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                            }
                        }

                        while (true) {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.filter { it.pressed }
                            if (pressed.isEmpty()) break

                            if (pressed.size >= 2) {
                                isPinching = true
                                activeDraggingNode = -1

                                val p1 = pressed[0].position
                                val p2 = pressed[1].position
                                val currentSpan = hypot(p1.x - p2.x, p1.y - p2.y)
                                val currentCentroid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)

                                if (previousSpan > 10f && currentSpan > 10f) {
                                    val zoomDelta = currentSpan / previousSpan
                                    if (zoomDelta in 0.8f..1.25f) {
                                        canvasScale = (canvasScale * zoomDelta).coerceIn(0.5f, 5.0f)
                                    }
                                    val panDelta = currentCentroid - previousCentroid
                                    canvasPan += panDelta
                                }

                                previousSpan = currentSpan
                                previousCentroid = currentCentroid
                                event.changes.forEach { it.consume() }
                            } else if (pressed.size == 1 && !isPinching) {
                                val ptr = pressed[0]
                                val panDelta = ptr.position - previousCentroid
                                previousCentroid = ptr.position
                                val currentLocal = screenToLocal(ptr.position)

                                if (state.editorMode == "edit" || state.editorMode == "draw") {
                                    if (activeDraggingNode in nodes.indices) {
                                        nodes[activeDraggingNode].x = currentLocal.x
                                        nodes[activeDraggingNode].y = currentLocal.y
                                        currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                        ptr.consume()
                                    } else {
                                        canvasPan += panDelta
                                        ptr.consume()
                                    }
                                } else if (state.editorMode == "draw") {
                                    val start = drawStartOffset ?: currentLocal
                                    when (state.activeTool) {
                                        "rect" -> {
                                            val minX = minOf(start.x, currentLocal.x)
                                            val maxX = maxOf(start.x, currentLocal.x)
                                            val minY = minOf(start.y, currentLocal.y)
                                            val maxY = maxOf(start.y, currentLocal.y)
                                            nodes.clear()
                                            nodes.add(EditorNode(minX, minY, 'M', isMoveTo = true))
                                            nodes.add(EditorNode(maxX, minY, 'L'))
                                            nodes.add(EditorNode(maxX, maxY, 'L'))
                                            nodes.add(EditorNode(minX, maxY, 'L'))
                                            nodes.add(EditorNode(minX, minY, 'L'))
                                            currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                        }
                                        "circle" -> {
                                            val radius = hypot(currentLocal.x - start.x, currentLocal.y - start.y).coerceAtLeast(1f)
                                            nodes.clear()
                                            val count = 8
                                            for (step in 0 until count) {
                                                val angle = (step * 2 * Math.PI / count)
                                                val px = (start.x + radius * cos(angle).toFloat()).coerceIn(0f, 24f)
                                                val py = (start.y + radius * sin(angle).toFloat()).coerceIn(0f, 24f)
                                                nodes.add(EditorNode(px, py, if (step == 0) 'M' else 'L', isMoveTo = step == 0))
                                            }
                                            if (nodes.isNotEmpty()) {
                                                nodes.add(EditorNode(nodes.first().x, nodes.first().y, 'L'))
                                            }
                                            currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                        }
                                        "line" -> {
                                            nodes.clear()
                                            nodes.add(EditorNode(start.x, start.y, 'M', isMoveTo = true))
                                            nodes.add(EditorNode(currentLocal.x, currentLocal.y, 'L'))
                                            currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                        }
                                        "pen" -> {
                                            val last = nodes.lastOrNull()
                                            if (last == null || hypot(currentLocal.x - last.x, currentLocal.y - last.y) > 0.4f) {
                                                nodes.add(EditorNode(currentLocal.x, currentLocal.y, if (nodes.isEmpty()) 'M' else 'L'))
                                                currentPathString = SvgPathEditorUtils.nodesToSvgPath(nodes)
                                            }
                                        }
                                    }
                                    ptr.consume()
                                }
                            }
                        }
                        drawStartOffset = null
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val topReserved = 68.dp.toPx()
            val bottomReserved = 68.dp.toPx()
            val maxBoxW = w - 40.dp.toPx()
            val maxBoxH = h - topReserved - bottomReserved - 16.dp.toPx()
            val iconBoxSize = minOf(maxBoxW, maxBoxH, 720f).coerceAtLeast(320f)
            val originX = (w - iconBoxSize) / 2f
            val originY = topReserved + (h - topReserved - bottomReserved - iconBoxSize) / 2f
            val scaleFactor = iconBoxSize / 24f

            val defaultIconColor = if (isDark) Color(0xFFEEEEF0) else Color(0xFF141418)
            val primaryCol = parseHexColor(state.primaryColor, defaultIconColor)

            val brush = if (state.isGradient && state.gradientStops.isNotEmpty()) {
                val sorted = state.gradientStops.sortedBy { it.position }
                val colors = sorted.map { parseHexColor(it.color, primaryCol) }
                val stops = sorted.map { it.position / 100f }
                when (state.gradientType) {
                    "radial" -> Brush.radialGradient(
                        *stops.zip(colors).map { (pos, col) -> Pair(pos, col) }.toTypedArray(),
                        center = Offset(originX + iconBoxSize / 2f, originY + iconBoxSize / 2f),
                        radius = iconBoxSize / 2f
                    )
                    else -> {
                        val rad = Math.toRadians(state.gradientAngle.toDouble())
                        val cx = originX + iconBoxSize / 2f
                        val cy = originY + iconBoxSize / 2f
                        val len = iconBoxSize / 2f
                        val start = Offset(cx - len * cos(rad).toFloat(), cy - len * sin(rad).toFloat())
                        val end = Offset(cx + len * cos(rad).toFloat(), cy + len * sin(rad).toFloat())
                        Brush.linearGradient(
                            *stops.zip(colors).map { (pos, col) -> Pair(pos, col) }.toTypedArray(),
                            start = start,
                            end = end
                        )
                    }
                }
            } else {
                SolidColor(primaryCol)
            }

            withTransform({
                translate(canvasPan.x, canvasPan.y)
                scale(scaleX = canvasScale, scaleY = canvasScale, pivot = Offset(w / 2f, h / 2f))
            }) {
                if (state.showGrid) {
                    val gridSize = 20f
                    var x = -w
                    while (x < w * 2) {
                        drawLine(gridColor, Offset(x, -h), Offset(x, h * 2), strokeWidth = 1f)
                        x += gridSize
                    }
                    var y = -h
                    while (y < h * 2) {
                        drawLine(gridColor, Offset(-w, y), Offset(w * 2, y), strokeWidth = 1f)
                        y += gridSize
                    }
                }

                drawRect(
                    color = gridColor.copy(alpha = 0.8f),
                    topLeft = Offset(originX, originY),
                    size = Size(iconBoxSize, iconBoxSize),
                    style = Stroke(width = 1.5f)
                )

                try {
                    if (currentPathString.isNotEmpty()) {
                        val parsedPath = PathParser().parsePathString(currentPathString).toPath()
                        val renderPath = Path().apply {
                            addPath(parsedPath)
                            val matrix = Matrix()
                            matrix.translate(originX, originY)
                            if (state.flipH && state.flipV) {
                                matrix.translate(iconBoxSize, iconBoxSize)
                                matrix.scale(-scaleFactor, -scaleFactor)
                            } else if (state.flipH) {
                                matrix.translate(iconBoxSize, 0f)
                                matrix.scale(-scaleFactor, scaleFactor)
                            } else if (state.flipV) {
                                matrix.translate(0f, iconBoxSize)
                                matrix.scale(scaleFactor, -scaleFactor)
                            } else {
                                matrix.scale(scaleFactor, scaleFactor)
                            }
                            transform(matrix)
                        }

                        rotate(state.rotation, pivot = Offset(originX + iconBoxSize / 2f, originY + iconBoxSize / 2f)) {
                            if (state.shadowEnabled) {
                                val shadowAlpha = (state.shadowOpacity / 100f).coerceIn(0f, 1f)
                                drawPath(
                                    path = renderPath,
                                    color = Color.Black.copy(alpha = shadowAlpha),
                                    style = Stroke(width = 4.5f, cap = strokeCap, join = strokeJoin)
                                )
                            }

                            val calculatedStrokeWidth = (state.strokeWidth * 1.5f).coerceIn(1.5f, 12f)
                            when (selectedStyle) {
                                IconStyle.FILL -> {
                                    drawPath(path = renderPath, brush = brush)
                                    drawPath(path = renderPath, brush = brush, style = Stroke(width = 1.2f, cap = strokeCap, join = strokeJoin))
                                }
                                IconStyle.DUOTONE -> {
                                    drawPath(path = renderPath, brush = SolidColor(primaryCol.copy(alpha = 0.28f)))
                                    drawPath(path = renderPath, brush = brush, style = Stroke(width = calculatedStrokeWidth, cap = strokeCap, join = strokeJoin))
                                }
                                IconStyle.PIXELATED -> {
                                    val pixelStroke = (calculatedStrokeWidth * 1.8f).coerceAtLeast(4f)
                                    drawPath(path = renderPath, brush = brush, style = Stroke(width = pixelStroke, cap = StrokeCap.Square, join = StrokeJoin.Miter))
                                }
                                IconStyle.GLASS -> {
                                    // 1. Frosted translucent fill
                                    val frostedFillAlpha = if (isDark) 0.35f else 0.28f
                                    drawPath(
                                        path = renderPath,
                                        brush = SolidColor(primaryCol.copy(alpha = frostedFillAlpha))
                                    )

                                    // 2. Soft glass ambient blur / glow rim
                                    val glowColor = if (isDark) Color.White.copy(alpha = 0.12f) else primaryCol.copy(alpha = 0.15f)
                                    drawPath(
                                        path = renderPath,
                                        brush = SolidColor(glowColor),
                                        style = Stroke(
                                            width = calculatedStrokeWidth + 3.5f,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )

                                    // 3. Specular highlight vertical gradient stroke (Frosted Glass Refraction)
                                    val specularStroke = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = if (isDark) 0.90f else 0.80f),
                                            primaryCol.copy(alpha = 0.50f),
                                            primaryCol.copy(alpha = 0.20f)
                                        ),
                                        startY = originY,
                                        endY = originY + iconBoxSize
                                    )
                                    drawPath(
                                        path = renderPath,
                                        brush = specularStroke,
                                        style = Stroke(
                                            width = calculatedStrokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                                else -> {
                                    if (state.gradientTarget == "fill") {
                                        drawPath(path = renderPath, brush = brush)
                                    } else {
                                        drawPath(path = renderPath, brush = brush, style = Stroke(width = calculatedStrokeWidth, cap = strokeCap, join = strokeJoin))
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }

                if (state.editorMode == "edit" || state.editorMode == "draw") {
                    val midpoints = SvgPathEditorUtils.generateMidpointDots(nodes)
                    for (mid in midpoints) {
                        val mx = originX + mid.x * scaleFactor
                        val my = originY + mid.y * scaleFactor
                        drawCircle(color = Color(0xFFEF4444), radius = 4f, center = Offset(mx, my))
                    }

                    for (i in nodes.indices) {
                        val node = nodes[i]
                        val nx = originX + node.x * scaleFactor
                        val ny = originY + node.y * scaleFactor
                        val isSelected = (i == selectedNodeIndex)

                        if (isSelected) {
                            drawCircle(color = brandBlue.copy(alpha = 0.35f), radius = 22f, center = Offset(nx, ny))
                        }
                        drawCircle(color = brandBlue, radius = if (isSelected) 10f else 6.5f, center = Offset(nx, ny))
                        drawCircle(color = Color.White, radius = if (isSelected) 5f else 3.5f, center = Offset(nx, ny))
                    }
                }
            }
        }
        }
        // Top Mode Switcher (Edit | Draw | Properties)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF18181B) else Color(0xFFE4E4E7),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF27272A) else Color(0xFFD4D4D8)),
                shadowElevation = 6.dp
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    val isView = state.editorMode == "view"
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { onStateChange(state.copy(editorMode = "view")) },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isView) (if (isDark) Color.White else Color.Black) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AspectRatio,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isView) (if (isDark) Color.Black else Color.White) else textMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isView) (if (isDark) Color.Black else Color.White) else textMuted)
                        }
                    }

                    val isDraw = state.editorMode == "draw" || state.editorMode == "edit"
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { onStateChange(state.copy(editorMode = "draw")) },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isDraw) (if (isDark) Color.White else Color.Black) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isDraw) (if (isDark) Color.Black else Color.White) else textMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Draw", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isDraw) (if (isDark) Color.Black else Color.White) else textMuted)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { onOpenPropertiesSheet() },
                        shape = RoundedCornerShape(9.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = textMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Properties", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMuted)
                        }
                    }
                }
            }
        }

        // Floating Draw Tools Selector (Only visible in Draw mode)
        if (state.editorMode == "draw") {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 58.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF16161C) else Color(0xFFFFFFFF),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val drawTools = listOf(
                        Triple("pen", "Pen", Icons.Default.Edit),
                        Triple("rect", "Box", Icons.Default.CropSquare),
                        Triple("circle", "Circle", Icons.Default.Circle),
                        Triple("line", "Line", Icons.Default.Timeline)
                    )
                    drawTools.forEach { (tId, tLabel, tIcon) ->
                        val isTActive = state.activeTool == tId
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onStateChange(state.copy(activeTool = tId)) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTActive) brandBlue else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    tIcon,
                                    contentDescription = tLabel,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isTActive) Color.White else textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    tLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isTActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTActive) Color.White else textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Clean Bottom Floating Island Controller (NO background colors for individual icons)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color(0xFF141418) else Color(0xFFFFFFFF),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 1. Undo
                IconButton(
                    onClick = {
                        if (undoStack.isNotEmpty()) {
                            redoStack.add(currentPathString)
                            val prev = undoStack.removeAt(undoStack.lastIndex)
                            currentPathString = prev
                            nodes.clear()
                            nodes.addAll(SvgPathEditorUtils.parsePathToNodes(prev))
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = undoStack.isNotEmpty()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        modifier = Modifier.size(18.dp),
                        tint = if (undoStack.isNotEmpty()) (if (isDark) Color.White else Color.Black) else textMuted.copy(alpha = 0.35f)
                    )
                }

                // 2. Redo
                IconButton(
                    onClick = {
                        if (redoStack.isNotEmpty()) {
                            undoStack.add(currentPathString)
                            val next = redoStack.removeAt(redoStack.lastIndex)
                            currentPathString = next
                            nodes.clear()
                            nodes.addAll(SvgPathEditorUtils.parsePathToNodes(next))
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = redoStack.isNotEmpty()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        modifier = Modifier.size(18.dp),
                        tint = if (redoStack.isNotEmpty()) (if (isDark) Color.White else Color.Black) else textMuted.copy(alpha = 0.35f)
                    )
                }

                // 3. Reset
                IconButton(
                    onClick = {
                        currentPathString = currentIcon?.pathData ?: ""
                        nodes.clear()
                        if (currentIcon != null) {
                            nodes.addAll(SvgPathEditorUtils.parsePathToNodes(currentIcon.pathData))
                        }
                        undoStack.clear()
                        redoStack.clear()
                        selectedNodeIndex = -1
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDark) Color.White else Color.Black
                    )
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(18.dp).background(if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)))

                // 4. Zoom Out
                IconButton(
                    onClick = { canvasScale = (canvasScale / 1.25f).coerceAtLeast(0.5f) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp), tint = textPrimary)
                }

                // 5. Zoom % Reset Pill (Shows numeric percentage, e.g. 100%)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            canvasScale = 1.0f
                            canvasPan = Offset.Zero
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF1E1E24) else Color(0xFFEDEDF1)
                ) {
                    Text(
                        text = "${(canvasScale * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)
                    )
                }

                // 6. Zoom In
                IconButton(
                    onClick = { canvasScale = (canvasScale * 1.25f).coerceAtMost(5.0f) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp), tint = textPrimary)
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(18.dp).background(if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)))

                // 7. Size Dropdown Pill (Displays e.g. "24px ▾" or "128px ▾")
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { sizeMenuOpen = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) Color(0xFF1E1E24) else Color(0xFFEDEDF1)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${state.width}px ▾",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = sizeMenuOpen,
                        onDismissRequest = { sizeMenuOpen = false }
                    ) {
                        val sizes = listOf(16, 20, 24, 28, 32, 48, 64, 96, 128, 256)
                        sizes.forEach { sz ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${sz}px",
                                        fontSize = 12.sp,
                                        fontWeight = if (state.width == sz) FontWeight.Bold else FontWeight.Normal,
                                        color = if (state.width == sz) brandBlue else textPrimary
                                    )
                                },
                                onClick = {
                                    onStateChange(state.copy(width = sz, height = sz))
                                    sizeMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // 8. Grid Toggle
                IconButton(
                    onClick = { onStateChange(state.copy(showGrid = !state.showGrid)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (state.showGrid) Icons.Default.GridOn else Icons.Default.GridOff,
                        contentDescription = "Grid",
                        modifier = Modifier.size(18.dp),
                        tint = if (state.showGrid) brandBlue else (if (isDark) Color.White else Color.Black)
                    )
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(18.dp).background(if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)))

                // 9. Properties Sheet
                IconButton(
                    onClick = onOpenPropertiesSheet,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Properties",
                        modifier = Modifier.size(18.dp),
                        tint = brandBlue
                    )
                }

                // 10. Open in SVG View Finder / Viewer
                IconButton(
                    onClick = {
                        val svg = IconExportUtils.exportSvgMarkup(currentIcon?.name ?: "icon", currentPathString, state, selectedStyle)
                        IconExportUtils.openSvgInExternalViewer(context, svg, currentIcon?.name ?: "icon")
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.OpenInBrowser,
                        contentDescription = "Open in SVG View Finder",
                        modifier = Modifier.size(18.dp),
                        tint = textPrimary
                    )
                }

                // 11. Code Implementation
                IconButton(
                    onClick = { showImplementationDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = "Implementation Code",
                        modifier = Modifier.size(18.dp),
                        tint = textPrimary
                    )
                }

                // 12. Copy SVG (BEFORE Download!)
                IconButton(
                    onClick = {
                        val rId = if (state.editorMode == "view") currentIcon?.getResForStyle(selectedStyle) else null
                        val svg = IconExportUtils.exportSvgMarkup(context, currentIcon?.name ?: "icon", currentPathString, state, selectedStyle, rId)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("SVG", svg))
                        Toast.makeText(context, "SVG markup copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy SVG",
                        modifier = Modifier.size(18.dp),
                        tint = if (isDark) Color.White else Color.Black
                    )
                }

                // 13. Download / Export Formats (Download is LAST!)
                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Export Formats",
                        modifier = Modifier.size(18.dp),
                        tint = brandBlue
                    )
                }
            }
        }

        if (showExportDialog) {
            ExportDialog(
                iconName = currentIcon?.name ?: "icon",
                pathData = currentPathString,
                state = state,
                style = selectedStyle,
                currentIcon = currentIcon,
                onOpenCode = { showImplementationDialog = true },
                onDismiss = { showExportDialog = false }
            )
        }

        if (showImplementationDialog) {
            ImplementationDialog(
                iconName = currentIcon?.name ?: "icon",
                pathData = currentPathString,
                onDismiss = { showImplementationDialog = false }
            )
        }
    }
}

fun exportSvgMarkup(name: String, pathData: String, state: CustomizationState): String {
    return IconExportUtils.exportSvgMarkup(name, pathData, state)
}
