package com.runeicons.app.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon
import com.runeicons.app.utils.IconExportUtils

data class ExportOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun ExportDialog(
    iconName: String,
    pathData: String,
    state: CustomizationState,
    style: IconStyle,
    currentIcon: RuneIcon? = null,
    onOpenCode: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val brandBlue = Color(0xFF3859FD)
    val cardBg = if (isDark) Color(0xFF141418) else Color(0xFFFFFFFF)
    val itemBg = if (isDark) Color(0xFF1C1C22) else Color(0xFFF3F3F6)
    val borderColor = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val textPrimary = if (isDark) Color(0xFFF4F4F5) else Color(0xFF09090B)
    val textMuted = if (isDark) Color(0xFF8E8E93) else Color(0xFF71717A)

    val cleanName = iconName.lowercase().replace(" ", "_").replace("-", "_").ifEmpty { "icon" }

    val options = listOf(
        ExportOption("svg", "SVG Vector", "Scalable vector path (.svg)", Icons.Default.Description, "Vector"),
        ExportOption("svg_viewer", "SVG View Finder", "Open in external viewer or browser", Icons.Default.OpenInBrowser, "Viewer"),
        ExportOption("sketchware", "Sketchware Pro 7.0 Block", "Export backup.json & logo PNG", Icons.Default.Extension, "SW 7.0"),
        ExportOption("png", "PNG Image (Transparent)", "512x512 transparent alpha canvas", Icons.Default.Layers, "Alpha"),
        ExportOption("jpg", "JPG Image (Raster)", "Clean solid white canvas", Icons.Default.Image, "Raster"),
        ExportOption("tiff", "TIFF / WebP Image", "Lossless high-res raster format", Icons.Default.PhotoCamera, "Lossless"),
        ExportOption("code", "Code Implementation", "Sketchware, Vite (React), Node.js", Icons.Default.Code, "Code")
    )

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    fun handleExport(optionId: String) {
        val resId = if (state.editorMode == "view") currentIcon?.getResForStyle(style) else null
        val svgMarkup = IconExportUtils.exportSvgMarkup(context, iconName, pathData, state, style, resId)
        when (optionId) {
            "svg" -> {
                copyToClipboard("SVG Markup", svgMarkup)
                val file = IconExportUtils.saveTextToDownloads(context, svgMarkup, "Zenico", "$cleanName.svg")
                Toast.makeText(
                    context,
                    if (file != null) "SVG saved to Download/Zenico/$cleanName.svg" else "SVG copied to clipboard",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
            "svg_viewer" -> {
                val opened = IconExportUtils.openSvgInExternalViewer(context, svgMarkup, iconName)
                if (!opened) {
                    copyToClipboard("SVG Markup", svgMarkup)
                    Toast.makeText(context, "Saved to Download/Zenico/$cleanName.svg", Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }
            "sketchware" -> {
                val (jsonFile, logoFile) = IconExportUtils.exportSketchwarePro7Backup(context, iconName, pathData, state)
                Toast.makeText(
                    context,
                    "Sketchware Pro 7.0 backup.json & logo.png exported to Download/Zenico!",
                    Toast.LENGTH_LONG
                ).show()
                onDismiss()
            }
            "png" -> {
                val bitmap = IconExportUtils.renderIconToBitmap(context, resId, pathData, state, style, transparent = true, size = 512)
                val file = IconExportUtils.saveBitmapToDownloads(context, bitmap, Bitmap.CompressFormat.PNG, "$cleanName.png")
                Toast.makeText(
                    context,
                    if (file != null) "PNG saved to Download/Zenico/$cleanName.png" else "PNG rendered successfully",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
            "jpg" -> {
                val bitmap = IconExportUtils.renderIconToBitmap(context, resId, pathData, state, style, transparent = false, size = 512)
                val file = IconExportUtils.saveBitmapToDownloads(context, bitmap, Bitmap.CompressFormat.JPEG, "$cleanName.jpg")
                Toast.makeText(
                    context,
                    if (file != null) "JPG saved to Download/Zenico/$cleanName.jpg" else "JPG rendered successfully",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
            "tiff" -> {
                val bitmap = IconExportUtils.renderIconToBitmap(context, resId, pathData, state, style, transparent = true, size = 512)
                val format = Bitmap.CompressFormat.WEBP
                val file = IconExportUtils.saveBitmapToDownloads(context, bitmap, format, "$cleanName.webp")
                Toast.makeText(
                    context,
                    if (file != null) "Exported to Download/Zenico/$cleanName.webp" else "Export completed",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
            "code" -> {
                onDismiss()
                onOpenCode()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export Icon",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Choose export or viewing format for $iconName",
                            fontSize = 11.sp,
                            color = textMuted
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

                // Format Options List (Scrollable to ensure no overlapping on small screens)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { opt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { handleExport(opt.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = itemBg,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(brandBlue.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = opt.icon,
                                            contentDescription = null,
                                            tint = brandBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = opt.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = opt.subtitle,
                                            fontSize = 10.sp,
                                            color = textMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = brandBlue.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = opt.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = brandBlue,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
