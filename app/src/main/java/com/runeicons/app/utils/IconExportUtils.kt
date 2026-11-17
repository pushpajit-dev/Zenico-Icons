package com.runeicons.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.RectF
import androidx.core.content.ContextCompat
import org.xmlpull.v1.XmlPullParser
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.PathParser
import androidx.core.content.FileProvider
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.IconStyle
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

object IconExportUtils {

    /**
     * Generates standard W3C compliant SVG markup with viewBox 0 0 24 24,
     * gradient defs, transforms, style mapping and high-precision paths.
     */
    fun vectorResourceToSvg(
        context: Context,
        resId: Int,
        state: CustomizationState,
        style: IconStyle
    ): String? {
        try {
            val parser = context.resources.getXml(resId)
            val paths = mutableListOf<String>()
            var vw = 24f
            var vh = 24f
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name == "vector") {
                        for (i in 0 until parser.attributeCount) {
                            if (parser.getAttributeName(i) == "viewportWidth") {
                                vw = parser.getAttributeValue(i).toFloatOrNull() ?: 24f
                            } else if (parser.getAttributeName(i) == "viewportHeight") {
                                vh = parser.getAttributeValue(i).toFloatOrNull() ?: 24f
                            }
                        }
                    } else if (parser.name == "path") {
                        var d = ""
                        var stroke = ""
                        var fill = ""
                        var strokeWidth = "2"
                        var cap = "round"
                        var join = "round"
                        for (i in 0 until parser.attributeCount) {
                            when (parser.getAttributeName(i)) {
                                "pathData" -> d = parser.getAttributeValue(i)
                                "strokeColor" -> stroke = parser.getAttributeValue(i)
                                "fillColor" -> fill = parser.getAttributeValue(i)
                                "strokeWidth" -> strokeWidth = parser.getAttributeValue(i)
                                "strokeLineCap" -> cap = parser.getAttributeValue(i)
                                "strokeLineJoin" -> join = parser.getAttributeValue(i)
                            }
                        }
                        if (d.isNotEmpty()) {
                            val pathAttrs = mutableListOf<String>()
                            pathAttrs.add("""d="$d""" )
                            if (fill.isNotEmpty() && fill != "none" && fill != "#00000000") {
                                pathAttrs.add("""fill="$fill""" )
                            } else {
                                pathAttrs.add("""fill="none""" )
                            }
                            if (stroke.isNotEmpty() && stroke != "none" && stroke != "#00000000") {
                                pathAttrs.add("""stroke="$stroke""" )
                                pathAttrs.add("""stroke-width="$strokeWidth""" )
                                pathAttrs.add("""stroke-linecap="$cap""" )
                                pathAttrs.add("""stroke-linejoin="$join""" )
                            }
                            paths.add("""    <path ${pathAttrs.joinToString(" ")} />""" )
                        }
                    }
                }
                eventType = parser.next()
            }
            if (paths.isEmpty()) return null
            return buildString {
                appendLine("""<?xml version="1.0" encoding="UTF-8"?>""" )
                appendLine("""<svg xmlns="http://www.w3.org/2000/svg" width="${state.width}" height="${state.height}" viewBox="0 0 ${vw.toInt()} ${vh.toInt()}" fill="none">""" )
                appendLine("""  <!-- Created with Zenico Vector Studio (by NoishiXzen) -->""" )
                if (style == IconStyle.GLASS) {
                    appendLine("""  <defs>""" )
                    appendLine("""    <linearGradient id="glass_specular" x1="0%" y1="0%" x2="0%" y2="100%">""" )
                    appendLine("""      <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.9"/>""" )
                    appendLine("""      <stop offset="100%" stop-color="#FFFFFF" stop-opacity="0.1"/>""" )
                    appendLine("""    </linearGradient>""" )
                    appendLine("""    <linearGradient id="glass_bg" x1="0%" y1="0%" x2="100%" y2="100%">""" )
                    appendLine("""      <stop offset="0%" stop-color="#E3E3E5" stop-opacity="0.6"/>""" )
                    appendLine("""      <stop offset="100%" stop-color="#BBBBC0" stop-opacity="0.6"/>""" )
                    appendLine("""    </linearGradient>""" )
                    appendLine("""  </defs>""" )
                    appendLine("""  <rect x="1.5" y="1.5" width="${vw - 3}" height="${vh - 3}" rx="5" fill="url(#glass_bg)" stroke="url(#glass_specular)" stroke-width="1"/>""" )
                }
                paths.forEach { appendLine(it) }
                appendLine("""</svg>""" )
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun exportSvgMarkup(
        name: String,
        pathData: String,
        state: CustomizationState,
        style: IconStyle = IconStyle.NORMAL
    ): String {
        return exportSvgMarkup(null, name, pathData, state, style, null)
    }

    fun exportSvgMarkup(
        context: Context?,
        name: String,
        pathData: String,
        state: CustomizationState,
        style: IconStyle = IconStyle.NORMAL,
        resId: Int? = null
    ): String {
        if (context != null && resId != null) {
            val svgFromRes = vectorResourceToSvg(context, resId, state, style)
            if (svgFromRes != null) return svgFromRes
        }
        val hex = state.primaryColor.ifBlank { "#141416" }
        val strokeCap = when (state.strokeStyle) {
            "soft", "round" -> "round"
            "medium" -> "square"
            else -> "butt"
        }
        val strokeJoin = when (state.strokeStyle) {
            "soft", "round" -> "round"
            "medium" -> "bevel"
            else -> "miter"
        }

        val hasGradient = state.isGradient && state.gradientStops.isNotEmpty()
        val fillRef = if (hasGradient && state.gradientTarget == "fill") "url(#zenico_grad)" else hex
        val strokeRef = if (hasGradient && state.gradientTarget != "fill") "url(#zenico_grad)" else hex

        val (fillAttr, strokeAttr, strokeWidthAttr, capJoinAttr) = when (style) {
            IconStyle.FILL -> {
                Quadruple("fill=\"$fillRef\"", "stroke=\"none\"", "stroke-width=\"0\"", "")
            }
            IconStyle.DUOTONE -> {
                Quadruple(
                    "fill=\"${hex}47\"",
                    "stroke=\"$strokeRef\"",
                    "stroke-width=\"${state.strokeWidth}\"",
                    "stroke-linecap=\"round\" stroke-linejoin=\"round\""
                )
            }
            IconStyle.GLASS -> {
                Quadruple(
                    "fill=\"url(#zenico_glass_fill)\"",
                    "stroke=\"url(#zenico_glass_specular)\"",
                    "stroke-width=\"${state.strokeWidth}\"",
                    "stroke-linecap=\"round\" stroke-linejoin=\"round\""
                )
            }
            IconStyle.PIXELATED -> {
                val pWidth = (state.strokeWidth * 1.8f).coerceAtLeast(3.2f)
                Quadruple(
                    "fill=\"none\"",
                    "stroke=\"$strokeRef\"",
                    "stroke-width=\"$pWidth\"",
                    "stroke-linecap=\"square\" stroke-linejoin=\"miter\""
                )
            }
            else -> {
                if (state.gradientTarget == "fill") {
                    Quadruple("fill=\"$fillRef\"", "stroke=\"none\"", "stroke-width=\"0\"", "")
                } else {
                    Quadruple(
                        "fill=\"none\"",
                        "stroke=\"$strokeRef\"",
                        "stroke-width=\"${state.strokeWidth}\"",
                        "stroke-linecap=\"$strokeCap\" stroke-linejoin=\"$strokeJoin\""
                    )
                }
            }
        }

        // Transforms
        val scaleX = if (state.flipH) -state.scale else state.scale
        val scaleY = if (state.flipV) -state.scale else state.scale
        val transformList = mutableListOf<String>()
        if (state.rotation != 0f || scaleX != 1f || scaleY != 1f) {
            transformList.add("translate(12 12)")
            if (state.rotation != 0f) transformList.add("rotate(${state.rotation})")
            if (scaleX != 1f || scaleY != 1f) transformList.add("scale($scaleX $scaleY)")
            transformList.add("translate(-12 -12)")
        }
        val transformAttr = if (transformList.isNotEmpty()) " transform=\"${transformList.joinToString(" ")}\"" else ""

        return buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            appendLine("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"${state.width}\" height=\"${state.height}\" viewBox=\"0 0 24 24\" fill=\"none\">")
            appendLine("  <!-- Created with Zenico Vector Studio (by NoishiXzen) -->")

            if (hasGradient || style == IconStyle.GLASS) {
                appendLine("  <defs>")
                if (style == IconStyle.GLASS) {
                    appendLine("    <linearGradient id=\"zenico_glass_specular\" x1=\"0%\" y1=\"0%\" x2=\"0%\" y2=\"100%\">")
                    appendLine("      <stop offset=\"0%\" stop-color=\"#FFFFFF\" stop-opacity=\"0.9\" />")
                    appendLine("      <stop offset=\"50%\" stop-color=\"$hex\" stop-opacity=\"0.55\" />")
                    appendLine("      <stop offset=\"100%\" stop-color=\"$hex\" stop-opacity=\"0.2\" />")
                    appendLine("    </linearGradient>")
                    appendLine("    <linearGradient id=\"zenico_glass_fill\" x1=\"0%\" y1=\"0%\" x2=\"100%\" y2=\"100%\">")
                    appendLine("      <stop offset=\"0%\" stop-color=\"$hex\" stop-opacity=\"0.38\" />")
                    appendLine("      <stop offset=\"100%\" stop-color=\"$hex\" stop-opacity=\"0.18\" />")
                    appendLine("    </linearGradient>")
                }
                if (hasGradient) {
                    if (state.gradientType == "radial") {
                        appendLine("    <radialGradient id=\"zenico_grad\" cx=\"50%\" cy=\"50%\" r=\"50%\">")
                        for (stop in state.gradientStops) {
                            appendLine("      <stop offset=\"${stop.position.toInt()}%\" stop-color=\"${stop.color}\" />")
                        }
                        appendLine("    </radialGradient>")
                    } else {
                        val angleRad = Math.toRadians(state.gradientAngle.toDouble())
                        val x1 = (50 - 50 * cos(angleRad)).toInt().coerceIn(0, 100)
                        val y1 = (50 - 50 * sin(angleRad)).toInt().coerceIn(0, 100)
                        val x2 = (50 + 50 * cos(angleRad)).toInt().coerceIn(0, 100)
                        val y2 = (50 + 50 * sin(angleRad)).toInt().coerceIn(0, 100)
                        appendLine("    <linearGradient id=\"zenico_grad\" x1=\"$x1%\" y1=\"$y1%\" x2=\"$x2%\" y2=\"$y2%\">")
                        for (stop in state.gradientStops) {
                            appendLine("      <stop offset=\"${stop.position.toInt()}%\" stop-color=\"${stop.color}\" />")
                        }
                        appendLine("    </linearGradient>")
                    }
                }
                appendLine("  </defs>")
            }

            if (transformAttr.isNotEmpty()) {
                appendLine("  <g$transformAttr>")
            }
            appendLine("    <path d=\"$pathData\" $fillAttr $strokeAttr $strokeWidthAttr $capJoinAttr opacity=\"${state.opacity / 100f}\" />")
            if (transformAttr.isNotEmpty()) {
                appendLine("  </g>")
            }
            appendLine("</svg>")
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    /**
     * Renders vector icon to high-resolution Android Bitmap with all styles,
     * transforms, colors, and gradients applied.
     */
    fun renderIconToBitmap(
        pathData: String,
        state: CustomizationState,
        style: IconStyle = IconStyle.NORMAL,
        transparent: Boolean = true,
        size: Int = 512
    ): Bitmap {
        return renderIconToBitmap(null, null, pathData, state, style, transparent, size)
    }

    fun renderIconToBitmap(
        context: Context?,
        resId: Int?,
        pathData: String,
        state: CustomizationState,
        style: IconStyle = IconStyle.NORMAL,
        transparent: Boolean = true,
        size: Int = 512
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if (!transparent) {
            canvas.drawColor(AndroidColor.WHITE)
        }

        // Draw Glass Background plate if style == GLASS
        if (style == IconStyle.GLASS) {
            val pad = size * 0.08f
            val rect = RectF(pad, pad, size - pad, size - pad)
            val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.style = Paint.Style.FILL
                this.color = AndroidColor.argb(130, 227, 227, 229)
            }
            canvas.drawRoundRect(rect, size * 0.18f, size * 0.18f, glassPaint)

            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = size * 0.015f
                this.shader = LinearGradient(
                    0f, pad, 0f, size - pad,
                    intArrayOf(AndroidColor.argb(230, 255, 255, 255), AndroidColor.argb(30, 255, 255, 255)),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(rect, size * 0.18f, size * 0.18f, borderPaint)
        }

        if (context != null && resId != null) {
            val drawable = ContextCompat.getDrawable(context, resId)
            if (drawable != null) {
                val inset = if (style == IconStyle.GLASS) (size * 0.22f).toInt() else (size * 0.12f).toInt()
                drawable.setBounds(inset, inset, size - inset, size - inset)
                drawable.draw(canvas)
                return bitmap
            }
        }

        if (pathData.isBlank()) return bitmap

        try {
            val composePath = PathParser().parsePathString(pathData).toPath()
            val androidPath = composePath.asAndroidPath()

            val baseScale = (size * 0.75f) / 24f
            val halfSize = size / 2f

            canvas.save()
            // Translate to center for rotation and flip transforms
            canvas.translate(halfSize, halfSize)

            if (state.rotation != 0f) {
                canvas.rotate(state.rotation)
            }

            val scaleX = (if (state.flipH) -1f else 1f) * state.scale * baseScale
            val scaleY = (if (state.flipV) -1f else 1f) * state.scale * baseScale
            canvas.scale(scaleX, scaleY)

            // Translate back so 0..24 is centered at (-12, -12)
            canvas.translate(-12f, -12f)

            val parsedColor = try {
                AndroidColor.parseColor(state.primaryColor)
            } catch (e: Exception) {
                AndroidColor.BLACK
            }

            val baseAlpha = ((state.opacity / 100f) * 255).toInt().coerceIn(0, 255)

            // Prepare Shader if Gradient is enabled
            var shader: Shader? = null
            if (state.isGradient && state.gradientStops.isNotEmpty()) {
                val sorted = state.gradientStops.sortedBy { it.position }
                val colors = sorted.map {
                    try { AndroidColor.parseColor(it.color) } catch (e: Exception) { AndroidColor.WHITE }
                }.toIntArray()
                val positions = sorted.map { it.position / 100f }.toFloatArray()

                shader = if (state.gradientType == "radial") {
                    RadialGradient(12f, 12f, 12f, colors, positions, Shader.TileMode.CLAMP)
                } else {
                    val angleRad = Math.toRadians(state.gradientAngle.toDouble())
                    val x1 = 12f - 12f * cos(angleRad).toFloat()
                    val y1 = 12f - 12f * sin(angleRad).toFloat()
                    val x2 = 12f + 12f * cos(angleRad).toFloat()
                    val y2 = 12f + 12f * sin(angleRad).toFloat()
                    LinearGradient(x1, y1, x2, y2, colors, positions, Shader.TileMode.CLAMP)
                }
            }

            val strokeWidthPx = (state.strokeWidth / baseScale * (size / 512f * 2.2f)).coerceAtLeast(0.5f)

            when (style) {
                IconStyle.FILL -> {
                    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.FILL
                        this.color = parsedColor
                        this.alpha = baseAlpha
                        if (shader != null) this.shader = shader
                    }
                    canvas.drawPath(androidPath, fillPaint)
                }
                IconStyle.DUOTONE -> {
                    val duoFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.FILL
                        this.color = parsedColor
                        this.alpha = (baseAlpha * 0.28f).toInt()
                    }
                    canvas.drawPath(androidPath, duoFillPaint)

                    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.STROKE
                        this.color = parsedColor
                        this.strokeWidth = strokeWidthPx
                        this.strokeCap = Paint.Cap.ROUND
                        this.strokeJoin = Paint.Join.ROUND
                        this.alpha = baseAlpha
                        if (shader != null) this.shader = shader
                    }
                    canvas.drawPath(androidPath, strokePaint)
                }
                IconStyle.GLASS -> {
                    // 1. Frosted glass translucent fill
                    val glassFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.FILL
                        this.color = parsedColor
                        this.alpha = (baseAlpha * 0.35f).toInt()
                    }
                    canvas.drawPath(androidPath, glassFillPaint)

                    // 2. Specular reflection gradient stroke
                    val red = AndroidColor.red(parsedColor)
                    val green = AndroidColor.green(parsedColor)
                    val blue = AndroidColor.blue(parsedColor)
                    val specularShader = LinearGradient(
                        0f, 0f, 0f, 24f,
                        intArrayOf(
                            AndroidColor.argb((baseAlpha * 0.90f).toInt(), 255, 255, 255),
                            parsedColor,
                            AndroidColor.argb((baseAlpha * 0.22f).toInt(), red, green, blue)
                        ),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    val glassStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.STROKE
                        this.strokeWidth = strokeWidthPx
                        this.strokeCap = Paint.Cap.ROUND
                        this.strokeJoin = Paint.Join.ROUND
                        this.shader = specularShader
                    }
                    canvas.drawPath(androidPath, glassStrokePaint)
                }
                IconStyle.PIXELATED -> {
                    val pixelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        this.style = Paint.Style.STROKE
                        this.color = parsedColor
                        this.strokeWidth = strokeWidthPx * 1.8f
                        this.strokeCap = Paint.Cap.SQUARE
                        this.strokeJoin = Paint.Join.MITER
                        this.alpha = baseAlpha
                        if (shader != null) this.shader = shader
                    }
                    canvas.drawPath(androidPath, pixelPaint)
                }
                else -> {
                    if (state.gradientTarget == "fill") {
                        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            this.style = Paint.Style.FILL
                            this.color = parsedColor
                            this.alpha = baseAlpha
                            if (shader != null) this.shader = shader
                        }
                        canvas.drawPath(androidPath, fillPaint)
                    } else {
                        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            this.style = Paint.Style.STROKE
                            this.color = parsedColor
                            this.strokeWidth = strokeWidthPx
                            this.strokeCap = when (state.strokeStyle) {
                                "round", "soft" -> Paint.Cap.ROUND
                                "medium" -> Paint.Cap.SQUARE
                                else -> Paint.Cap.BUTT
                            }
                            this.strokeJoin = when (state.strokeStyle) {
                                "round", "soft" -> Paint.Join.ROUND
                                "medium" -> Paint.Join.BEVEL
                                else -> Paint.Join.MITER
                            }
                            this.alpha = baseAlpha
                            if (shader != null) this.shader = shader
                        }
                        canvas.drawPath(androidPath, strokePaint)
                    }
                }
            }

            canvas.restore()
        } catch (e: Exception) {
            // Error handling fallback
        }

        return bitmap
    }

    /**
     * Saves rendered Bitmap to Download/Zenico directory.
     */
    fun saveBitmapToDownloads(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        filename: String
    ): File? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zenicoDir = File(downloadsDir, "Zenico")
            if (!zenicoDir.exists()) zenicoDir.mkdirs()
            val file = File(zenicoDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(format, 100, out)
            }
            file
        } catch (e: Exception) {
            try {
                val appDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Zenico")
                if (!appDir.exists()) appDir.mkdirs()
                val appFile = File(appDir, filename)
                FileOutputStream(appFile).use { out ->
                    bitmap.compress(format, 100, out)
                }
                appFile
            } catch (e2: Exception) {
                null
            }
        }
    }

    /**
     * Saves text/JSON/SVG content to Download/Zenico directory.
     */
    fun saveTextToDownloads(
        context: Context,
        text: String,
        subfolder: String,
        filename: String
    ): File? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val targetDir = if (subfolder.isNotEmpty()) File(downloadsDir, subfolder) else downloadsDir
            if (!targetDir.exists()) targetDir.mkdirs()
            val file = File(targetDir, filename)
            file.writeText(text)
            file
        } catch (e: Exception) {
            try {
                val appDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), subfolder)
                if (!appDir.exists()) appDir.mkdirs()
                val appFile = File(appDir, filename)
                appFile.writeText(text)
                appFile
            } catch (e2: Exception) {
                null
            }
        }
    }

    /**
     * Opens SVG directly in external viewer app or SVG View Finder with FileProvider URI.
     */
    fun openSvgInExternalViewer(context: Context, svgMarkup: String, iconName: String): Boolean {
        return try {
            val cleanName = iconName.lowercase().replace(" ", "_").replace("-", "_").ifEmpty { "icon" }
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zenicoDir = File(downloadsDir, "Zenico")
            if (!zenicoDir.exists()) zenicoDir.mkdirs()
            val file = File(zenicoDir, "$cleanName.svg")
            file.writeText(svgMarkup)

            val uri: Uri = try {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/svg+xml")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Open in SVG View Finder / Viewer")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            try {
                val cleanName = iconName.lowercase().replace(" ", "_").replace("-", "_").ifEmpty { "icon" }
                val cacheFile = File(context.cacheDir, "$cleanName.svg")
                cacheFile.writeText(svgMarkup)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/svg+xml"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(shareIntent, "Open / Share SVG")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                true
            } catch (e2: Exception) {
                false
            }
        }
    }

    /**
     * Generates complete, official Sketchware Pro 7.0 block backup JSON.
     */
    fun generateSketchwareBlockJson(name: String, pathData: String): String {
        val cleanName = name.lowercase().replace(" ", "_").replace("-", "_").ifEmpty { "icon" }
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val timestamp = System.currentTimeMillis()

        return """
{
  "format": "Sketchware Pro 7.0 Block Backup",
  "version": "7.0.0",
  "sketchware_version": "7.0.0",
  "date": "$dateStr",
  "timestamp": $timestamp,
  "author": "NoishiXzen",
  "app": "Zenico Vector Studio",
  "blocks": [
    {
      "name": "zenico_$cleanName",
      "type": "c",
      "typeName": "Draw Zenico Icon",
      "spec": "draw zenico $cleanName on %m.canvas at x: %d y: %d size: %d color: %c",
      "code": "/* Zenico Icon: $cleanName */\nandroid.graphics.Path path = androidx.core.graphics.PathParser.createPathFromPathData(\"$pathData\");\nandroid.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);\npaint.setStyle(android.graphics.Paint.Style.STROKE);\npaint.setStrokeWidth(2.5f);\npaint.setColor(%5${'$'}s);\n%1${'$'}s.save();\n%1${'$'}s.translate(%2${'$'}s, %3${'$'}s);\nfloat scale = %4${'$'}s / 24f;\n%1${'$'}s.scale(scale, scale);\n%1${'$'}s.drawPath(path, paint);\n%1${'$'}s.restore();",
      "color": "#3859FD",
      "palette": "9",
      "version": "7.0.0",
      "author": "NoishiXzen",
      "date": "$dateStr",
      "timestamp": $timestamp,
      "icon": "${cleanName}_logo.png",
      "logo": "${cleanName}_logo.png",
      "pathData": "$pathData",
      "compatible": "Sketchware Pro 7.0+"
    }
  ]
}
""".trimIndent()
    }

    /**
     * Exports full Sketchware Pro 7.0 block backup files (backup.json and backup_<date>.json)
     * AND exports the icon as a transparent PNG logo (<name>_logo.png and logo.png)
     * directly into Download/Zenico for instant one-click import into Sketchware Pro 7.0!
     */
    fun exportSketchwarePro7Backup(
        context: Context,
        name: String,
        pathData: String,
        state: CustomizationState
    ): Pair<File?, File?> {
        val cleanName = name.lowercase().replace(" ", "_").replace("-", "_").ifEmpty { "icon" }
        val dateTag = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val jsonContent = generateSketchwareBlockJson(name, pathData)

        // 1. Save backup.json, backup_<date>.json, and block.json
        val backupJsonFile = saveTextToDownloads(context, jsonContent, "Zenico", "backup.json")
        saveTextToDownloads(context, jsonContent, "Zenico", "backup_$dateTag.json")
        saveTextToDownloads(context, jsonContent, "Zenico", "block.json")

        // 2. Render and save block logo PNG (for Sketchware Pro block manager logo import)
        val logoBitmap = renderIconToBitmap(
            pathData = pathData,
            state = state.copy(scale = 1.0f, strokeWidth = 2.5f),
            style = IconStyle.NORMAL,
            transparent = true,
            size = 512
        )
        val logoFile = saveBitmapToDownloads(context, logoBitmap, Bitmap.CompressFormat.PNG, "${cleanName}_logo.png")
        saveBitmapToDownloads(context, logoBitmap, Bitmap.CompressFormat.PNG, "logo.png")

        return Pair(backupJsonFile, logoFile)
    }

    fun generateViteReactComponent(name: String, pathData: String): String {
        val componentName = name.split(" ", "-", "_")
            .filter { it.isNotBlank() }
            .joinToString("") { it.replaceFirstChar { c -> c.uppercase() } } + "Icon"
        return """
import React from 'react';

interface ${componentName}Props extends React.SVGProps<SVGSVGElement> {
  size?: number | string;
  color?: string;
  strokeWidth?: number;
}

export const $componentName: React.FC<${componentName}Props> = ({
  size = 24,
  color = 'currentColor',
  strokeWidth = 2,
  ...props
}) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    stroke={color}
    strokeWidth={strokeWidth}
    strokeLinecap="round"
    strokeLinejoin="round"
    {...props}
  >
    <path d="$pathData" />
  </svg>
);

export default $componentName;
""".trimIndent()
    }

    fun generateNodeJsScript(name: String, pathData: String): String {
        val cleanName = name.lowercase().replace(" ", "-")
        return """
// Node.js SVG generator for $cleanName
const fs = require('fs');
const path = require('path');

const svgMarkup = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="#141416" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="$pathData" />
</svg>`;

const outputPath = path.join(__dirname, '$cleanName.svg');
fs.writeFileSync(outputPath, svgMarkup, 'utf8');
console.log(`Zenico icon exported successfully: ${'$'}{outputPath}`);
""".trimIndent()
    }
}
