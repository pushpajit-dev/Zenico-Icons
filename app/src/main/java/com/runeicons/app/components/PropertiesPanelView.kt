package com.runeicons.app.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.CustomUploadedIcon
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.GradientStop
import com.runeicons.app.ui.theme.parseHexColor

@Composable
fun PropertiesPanelView(
    state: CustomizationState,
    onStateChange: (CustomizationState) -> Unit,
    onApplyCustomPath: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    // High contrast theme tokens (crisp in both dark and light modes)
    val panelBg = if (isDark) Color(0xFF101014) else Color(0xFFFFFFFF)
    val cardBg = if (isDark) Color(0xFF18181E) else Color(0xFFF4F4F7)
    val borderColor = if (isDark) Color(0xFF27272F) else Color(0xFFE2E2E8)
    val textPrimary = if (isDark) Color(0xFFF4F4F6) else Color(0xFF141418)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF52525B)
    val brandBlue = Color(0xFF3859FD)

    // Palette of vibrant colors
    val colorPalette = listOf(
        "#FFFFFF", "#141416", "#3859FD", "#6366F1", "#EC4899",
        "#EF4444", "#F59E0B", "#10B981", "#06B6D4", "#8B5CF6",
        "#F97316", "#64748B"
    )

    var activeGradientStopIndex by remember { mutableIntStateOf(0) }
    var customSvgPathInput by remember { mutableStateOf("") }
    var customHexInput by remember { mutableStateOf(state.primaryColor) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "custom-icon.svg"
            val newCustom = CustomUploadedIcon(
                id = "custom-${System.currentTimeMillis()}",
                name = fileName,
                pathData = "M12 2L2 22H22L12 2Z",
                uriString = uri.toString()
            )
            val updated = state.customIcons + newCustom
            onStateChange(state.copy(customIcons = updated))
            onApplyCustomPath?.invoke(newCustom.pathData)
            Toast.makeText(context, "Icon loaded: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .background(panelBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reset Quick Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onStateChange(CustomizationState(primaryColor = if (isDark) "#FFFFFF" else "#141416")) },
                shape = RoundedCornerShape(6.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(13.dp), tint = textSecondary)
                    Text("Reset Defaults", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                }
            }
        }

        // 1. COLOR MODE: SOLID VS GRADIENT
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COLOR STYLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        PillButton(
                            text = "SOLID",
                            selected = !state.isGradient,
                            isDark = isDark,
                            onClick = { onStateChange(state.copy(isGradient = false)) }
                        )
                        PillButton(
                            text = "GRADIENT",
                            selected = state.isGradient,
                            isDark = isDark,
                            onClick = { onStateChange(state.copy(isGradient = true)) }
                        )
                    }
                }
            }

            // COLOR PALETTE SWATCHES
            Text(
                text = "PALETTE SWATCHES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colorPalette.forEach { hexColor ->
                    val isColorActive = if (!state.isGradient) {
                        state.primaryColor.equals(hexColor, ignoreCase = true)
                    } else {
                        val stop = state.gradientStops.getOrNull(activeGradientStopIndex)
                        stop?.color.equals(hexColor, ignoreCase = true)
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(hexColor, Color.Gray))
                            .border(
                                width = if (isColorActive) 2.5.dp else 1.dp,
                                color = if (isColorActive) brandBlue else borderColor,
                                shape = CircleShape
                            )
                            .clickable {
                                customHexInput = hexColor
                                if (!state.isGradient) {
                                    onStateChange(state.copy(primaryColor = hexColor))
                                } else {
                                    val stops = state.gradientStops.toMutableList()
                                    if (activeGradientStopIndex in stops.indices) {
                                        stops[activeGradientStopIndex] = stops[activeGradientStopIndex].copy(color = hexColor)
                                        onStateChange(state.copy(gradientStops = stops))
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isColorActive) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (hexColor.equals("#FFFFFF", ignoreCase = true)) Color.Black else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            if (!state.isGradient) {
                // HEX INPUT
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("HEX CODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(state.primaryColor, Color.White))
                                .border(1.dp, borderColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = customHexInput,
                            onValueChange = {
                                customHexInput = it
                                if (it.startsWith("#") && (it.length == 7 || it.length == 9)) {
                                    onStateChange(state.copy(primaryColor = it))
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            ),
                            cursorBrush = SolidColor(brandBlue),
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            } else {
                // GRADIENT STOPS SECTION
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.gradientStops.forEachIndexed { index, stop ->
                        val isStopSelected = activeGradientStopIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isStopSelected) brandBlue.copy(alpha = 0.12f) else cardBg)
                                .border(
                                    width = if (isStopSelected) 1.5.dp else 1.dp,
                                    color = if (isStopSelected) brandBlue else borderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { activeGradientStopIndex = index }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(parseHexColor(stop.color, Color.White))
                                        .border(1.dp, borderColor, CircleShape)
                                )
                                Text(
                                    text = "STOP ${index + 1} (${stop.position.toInt()}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStopSelected) brandBlue else textPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = stop.color.uppercase(),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                                if (state.gradientStops.size > 2) {
                                    IconButton(
                                        onClick = {
                                            val stops = state.gradientStops.toMutableList()
                                            stops.removeAt(index)
                                            activeGradientStopIndex = 0
                                            onStateChange(state.copy(gradientStops = stops))
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(14.dp), tint = textSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // ADD STOP BUTTON
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val newStop = GradientStop("#F43F5E", 75f)
                                onStateChange(state.copy(gradientStops = state.gradientStops + newStop))
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = brandBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADD GRADIENT STOP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = brandBlue)
                        }
                    }

                    // PROJECTION TYPE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PROJECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("linear", "radial", "angular").forEach { prj ->
                                PillButton(
                                    text = prj.uppercase(),
                                    selected = state.gradientType == prj,
                                    isDark = isDark,
                                    onClick = { onStateChange(state.copy(gradientType = prj)) }
                                )
                            }
                        }
                    }

                    // ANGLE SLIDER
                    if (state.gradientType == "linear" || state.gradientType == "angular") {
                        ScrubberRow(
                            label = "ANGLE",
                            value = state.gradientAngle.toInt(),
                            min = 0,
                            max = 360,
                            onValueChange = { onStateChange(state.copy(gradientAngle = it.toFloat())) },
                            isDark = isDark
                        )
                    }

                    // SURFACE PREVIEW
                    val sorted = state.gradientStops.sortedBy { it.position }
                    val colors = sorted.map { parseHexColor(it.color, Color.White) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.horizontalGradient(colors))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("LIVE GRADIENT PREVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // 2. TRANSFORM & SCALE CONTROLS
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("TRANSFORM & SCALE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)

            // SCALE (0.5x - 3.0x)
            ScrubberRow(
                label = "SCALE",
                value = (state.scale * 100).toInt(),
                min = 50,
                max = 300,
                unit = "%",
                onValueChange = { onStateChange(state.copy(scale = it / 100f)) },
                isDark = isDark
            )

            // ROTATION (0 - 360)
            ScrubberRow(
                label = "ROTATION",
                value = state.rotation.toInt(),
                min = 0,
                max = 360,
                unit = "°",
                onValueChange = { onStateChange(state.copy(rotation = it.toFloat())) },
                isDark = isDark
            )

            // OPACITY (0 - 100)
            ScrubberRow(
                label = "OPACITY",
                value = state.opacity.toInt(),
                min = 0,
                max = 100,
                unit = "%",
                onValueChange = { onStateChange(state.copy(opacity = it.toFloat())) },
                isDark = isDark
            )

            // STROKE WIDTH (1 - 10)
            ScrubberRow(
                label = "STROKE WIDTH",
                value = state.strokeWidth.toInt(),
                min = 1,
                max = 10,
                unit = "px",
                onValueChange = { onStateChange(state.copy(strokeWidth = it.toFloat())) },
                isDark = isDark
            )

            // BLUR (0 - 30)
            ScrubberRow(
                label = "BLUR",
                value = state.blur.toInt(),
                min = 0,
                max = 30,
                unit = "",
                onValueChange = { onStateChange(state.copy(blur = it.toFloat())) },
                isDark = isDark
            )
        }

        // 3. ROUNDNESS / STROKE STYLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ROUNDNESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("soft", "round", "medium", "sharp").forEach { r ->
                    PillButton(
                        text = r.replaceFirstChar { it.uppercase() },
                        selected = state.strokeStyle == r,
                        isDark = isDark,
                        onClick = { onStateChange(state.copy(strokeStyle = r)) }
                    )
                }
            }
        }

        // 4. FLIP AXIS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("FLIP / MIRROR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PillButton(
                    text = "FLIP H",
                    selected = state.flipH,
                    isDark = isDark,
                    onClick = { onStateChange(state.copy(flipH = !state.flipH)) }
                )
                PillButton(
                    text = "FLIP V",
                    selected = state.flipV,
                    isDark = isDark,
                    onClick = { onStateChange(state.copy(flipV = !state.flipV)) }
                )
            }
        }

        // 5. SHADOW & NOISE
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SHADOW EFFECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                Switch(
                    checked = state.shadowEnabled,
                    onCheckedChange = { onStateChange(state.copy(shadowEnabled = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = brandBlue)
                )
            }

            if (state.shadowEnabled) {
                ScrubberRow(
                    label = "SHADOW OPACITY",
                    value = state.shadowOpacity.toInt(),
                    min = 0,
                    max = 100,
                    unit = "%",
                    onValueChange = { onStateChange(state.copy(shadowOpacity = it.toFloat())) },
                    isDark = isDark
                )
                ScrubberRow(
                    label = "SHADOW BLUR",
                    value = state.shadowBlur.toInt(),
                    min = 0,
                    max = 50,
                    unit = "",
                    onValueChange = { onStateChange(state.copy(shadowBlur = it.toFloat())) },
                    isDark = isDark
                )
            }
        }

        // 6. UPLOADS SECTION
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("UPLOAD CUSTOM ICON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = brandBlue,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Upload SVG or Image File",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { filePicker.launch("*/*") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                    ) {
                        Text("Browse Files", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick SVG Path Paste Input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(panelBg)
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = customSvgPathInput,
                            onValueChange = { customSvgPathInput = it },
                            textStyle = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = textPrimary
                            ),
                            cursorBrush = SolidColor(brandBlue),
                            modifier = Modifier.weight(1f)
                        )
                        if (customSvgPathInput.isNotBlank()) {
                            Button(
                                onClick = {
                                    onApplyCustomPath?.invoke(customSvgPathInput)
                                    Toast.makeText(context, "Custom SVG path applied", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Apply", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PillButton(
    text: String,
    selected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = if (selected) Color(0xFF3859FD) else Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else (if (isDark) Color(0xFFA1A1AA) else Color(0xFF52525B))
        )
    }
}

@Composable
fun ScrubberRow(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    unit: String = "",
    onValueChange: (Int) -> Unit,
    isDark: Boolean
) {
    val cardBg = if (isDark) Color(0xFF18181E) else Color(0xFFF4F4F7)
    val textPrimary = if (isDark) Color(0xFFF4F4F6) else Color(0xFF141418)
    val textSecondary = if (isDark) Color(0xFFA1A1AA) else Color(0xFF52525B)
    val brandBlue = Color(0xFF3859FD)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            modifier = Modifier.width(130.dp),
            colors = SliderDefaults.colors(
                thumbColor = brandBlue,
                activeTrackColor = brandBlue,
                inactiveTrackColor = if (isDark) Color(0xFF2A2A32) else Color(0xFFD4D4D8)
            )
        )
        Text(
            "$value$unit",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
    }
}
