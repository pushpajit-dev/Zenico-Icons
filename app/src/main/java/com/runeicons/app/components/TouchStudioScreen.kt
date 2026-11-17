package com.runeicons.app.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.RuneIcon
import com.runeicons.app.ui.theme.AmberYellow
import com.runeicons.app.ui.theme.DarkBackground
import com.runeicons.app.ui.theme.EmeraldGreen
import com.runeicons.app.ui.theme.RoseRed
import com.runeicons.app.ui.theme.RunePrimary
import com.runeicons.app.ui.theme.SkyBlue
import com.runeicons.app.ui.theme.VioletPurple

@Composable
fun TouchStudioScreen(
    currentIcon: RuneIcon,
    allIcons: List<RuneIcon>,
    onSelectIcon: (RuneIcon) -> Unit
) {
    val context = LocalContext.current

    // Touch & Adjustment States
    var strokeWidth by remember { mutableFloatStateOf(2.5f) }
    var iconSizeDp by remember { mutableFloatStateOf(100f) }
    var scale by remember { mutableFloatStateOf(1.0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    var selectedColor by remember { mutableStateOf(RunePrimary) }
    var selectedBackground by remember { mutableStateOf(BackgroundStyle.CARD) }
    var strokeCap by remember { mutableStateOf(StrokeCap.Round) }
    var strokeJoin by remember { mutableStateOf(StrokeJoin.Round) }

    var codeTabIndex by remember { mutableIntStateOf(0) }

    val colors = listOf(
        RunePrimary,
        VioletPurple,
        SkyBlue,
        EmeraldGreen,
        RoseRed,
        AmberYellow,
        Color(0xFF38BDF8),
        Color.White,
        Color(0xFF0F172A)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Studio Instruction Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Interactive Touch Studio",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Drag to reposition · Pinch to zoom & resize · Tweak controls below",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Canvas Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            InteractiveIconCanvas(
                pathData = currentIcon.pathData,
                iconSize = iconSizeDp.dp,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap,
                strokeJoin = strokeJoin,
                tintColor = selectedColor,
                backgroundStyle = selectedBackground,
                scale = scale,
                rotation = rotation,
                offset = panOffset,
                onTransformChange = { pan, zoom, rotationChange ->
                    panOffset += pan
                    scale = (scale * zoom).coerceIn(0.4f, 4.0f)
                    rotation = (rotation + rotationChange) % 360f
                }
            )

            // Reset Gestures Button on Canvas Corner
            IconButton(
                onClick = {
                    panOffset = Offset.Zero
                    scale = 1.0f
                    rotation = 0f
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = "Reset gestures",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Icon Selector Row
        Text(
            text = "CURRENT GLYPH: ${currentIcon.name.uppercase()}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allIcons.take(25)) { icon ->
                val isSelected = icon.id == currentIcon.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectIcon(icon) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = if (isSelected) 4.dp else 1.dp
                ) {
                    Text(
                        text = icon.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Adjustment Sliders Section
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Stroke Width Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Stroke Width", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(String.format("%.1f dp", strokeWidth), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 0.5f..8.0f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Base Icon Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("${iconSizeDp.toInt()} dp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = iconSizeDp,
                    onValueChange = { iconSizeDp = it },
                    valueRange = 32f..180f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rotation Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rotation Angle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("${rotation.toInt()}°", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = rotation,
                    onValueChange = { rotation = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Color Palette
                Text("Accent Tint Color", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(colors) { col ->
                        val isPicked = selectedColor == col
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    if (isPicked) 3.dp else 1.dp,
                                    if (isPicked) MaterialTheme.colorScheme.onSurface else Color.LightGray.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Background Style
                Text("Container Background", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val bgStyles = listOf(
                        BackgroundStyle.CARD,
                        BackgroundStyle.NEUMORPHIC,
                        BackgroundStyle.CIRCLE,
                        BackgroundStyle.SQUARE,
                        BackgroundStyle.NONE
                    )
                    items(bgStyles) { bg ->
                        val isSelected = selectedBackground == bg
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBackground = bg },
                            label = { Text(bg.label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Code Export Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export Adjusted Code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = codeTabIndex,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Tab(
                        selected = codeTabIndex == 0,
                        onClick = { codeTabIndex = 0 },
                        text = { Text("SVG", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = codeTabIndex == 1,
                        onClick = { codeTabIndex = 1 },
                        text = { Text("Vector XML", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = codeTabIndex == 2,
                        onClick = { codeTabIndex = 2 },
                        text = { Text("Compose", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val r = (selectedColor.red * 255).toInt()
                val g = (selectedColor.green * 255).toInt()
                val b = (selectedColor.blue * 255).toInt()
                val hexColor = String.format("#%02X%02X%02X", r, g, b)
                val capName = if (strokeCap == StrokeCap.Round) "round" else if (strokeCap == StrokeCap.Square) "square" else "butt"
                val joinName = if (strokeJoin == StrokeJoin.Round) "round" else if (strokeJoin == StrokeJoin.Bevel) "bevel" else "miter"
                val composeCap = if (strokeCap == StrokeCap.Round) "StrokeCap.Round" else if (strokeCap == StrokeCap.Square) "StrokeCap.Square" else "StrokeCap.Butt"

                val exportedCode = when (codeTabIndex) {
                    0 -> "<svg width=\"${iconSizeDp.toInt()}\" height=\"${iconSizeDp.toInt()}\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                            "  <path d=\"${currentIcon.pathData}\"\n" +
                            "        stroke=\"$hexColor\"\n" +
                            "        stroke-width=\"$strokeWidth\"\n" +
                            "        stroke-linecap=\"$capName\"\n" +
                            "        stroke-linejoin=\"$joinName\"/>\n" +
                            "</svg>"

                    1 -> "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                            "    android:width=\"${iconSizeDp.toInt()}dp\"\n" +
                            "    android:height=\"${iconSizeDp.toInt()}dp\"\n" +
                            "    android:viewportWidth=\"24.0\"\n" +
                            "    android:viewportHeight=\"24.0\">\n" +
                            "    <path\n" +
                            "        android:pathData=\"${currentIcon.pathData}\"\n" +
                            "        android:strokeColor=\"$hexColor\"\n" +
                            "        android:strokeWidth=\"$strokeWidth\"\n" +
                            "        android:strokeLineCap=\"$capName\"\n" +
                            "        android:strokeLineJoin=\"$joinName\"/>\n" +
                            "</vector>"

                    else -> "// Jetpack Compose Custom Drawing\n" +
                            "Canvas(modifier = Modifier.size(${iconSizeDp.toInt()}.dp)) {\n" +
                            "    val path = PathParser().parsePathString(\"${currentIcon.pathData}\").toPath()\n" +
                            "    drawPath(\n" +
                            "        path = path,\n" +
                            "        color = Color(0x$hexColor),\n" +
                            "        style = Stroke(width = ${strokeWidth}f, cap = $composeCap)\n" +
                            "    )\n" +
                            "}"
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkBackground
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = exportedCode,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val label = when (codeTabIndex) {
                            0 -> "SVG Code"
                            1 -> "Vector XML"
                            else -> "Compose Code"
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(label, exportedCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Code to Clipboard")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
