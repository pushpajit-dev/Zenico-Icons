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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.utils.IconExportUtils

@Composable
fun ImplementationDialog(
    iconName: String,
    pathData: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val brandBlue = Color(0xFF3859FD)
    val cardBg = if (isDark) Color(0xFF141418) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val codeBg = if (isDark) Color(0xFF0D0D11) else Color(0xFFF4F4F6)
    val textPrimary = if (isDark) Color(0xFFF4F4F5) else Color(0xFF09090B)
    val textMuted = if (isDark) Color(0xFF8E8E93) else Color(0xFF71717A)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sketchware Pro 7.0, 1: Vite / React, 2: Node.js

    val tabs = listOf("Sketchware Pro 7.0", "Vite (React)", "Node.js")

    val codeText = remember(selectedTab, iconName, pathData) {
        when (selectedTab) {
            0 -> IconExportUtils.generateSketchwareBlockJson(iconName, pathData)
            1 -> IconExportUtils.generateViteReactComponent(iconName, pathData)
            else -> IconExportUtils.generateNodeJsScript(iconName, pathData)
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            color = cardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header: Title + Close Icon (X)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Extension,
                            contentDescription = null,
                            tint = brandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Implementation",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }

                    val closeBtnBg = if (isDark) Color(0xFF272730) else Color(0xFFE4E4E8)
                    val closeIconTint = if (isDark) Color.White else Color(0xFF141416)
                    val closeBorder = if (isDark) Color(0xFF3F3F46) else Color(0xFFCBD5E1)

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(closeBtnBg)
                            .border(1.dp, closeBorder, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(18.dp),
                            tint = closeIconTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Three Code Editor Tabs: Sketchware Pro 7.0, Vite, Node.js
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF1C1C22) else Color(0xFFEEEEF2))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        val isTabActive = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTabActive) brandBlue else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabTitle,
                                fontSize = 11.sp,
                                fontWeight = if (isTabActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTabActive) Color.White else textMuted,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle / Note for Sketchware Pro 7.0
                if (selectedTab == 0) {
                    Text(
                        text = "Compatible with Sketchware Pro 7.0+ Block Manager. Export generates backup.json & logo.png.",
                        fontSize = 10.sp,
                        color = textMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Code Editor Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 270.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(codeBg)
                        .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = codeText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isDark) Color(0xFFE0E0E6) else Color(0xFF1E1E24),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Code Button
                    Button(
                        onClick = { copyToClipboard("Code", codeText) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF27272E) else Color(0xFFE4E4E8),
                            contentColor = textPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Export Block Button (For Sketchware Pro 7.0)
                    if (selectedTab == 0) {
                        Button(
                            onClick = {
                                val (jsonFile, logoFile) = IconExportUtils.exportSketchwarePro7Backup(
                                    context = context,
                                    name = iconName,
                                    pathData = pathData,
                                    state = CustomizationState()
                                )
                                copyToClipboard("Sketchware Pro 7.0 Backup JSON", codeText)
                                Toast.makeText(
                                    context,
                                    "Exported to Download/Zenico/backup.json\nand logo.png (Sketchware Pro 7.0 compatible)",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = brandBlue,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Block", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
