package com.runeicons.app.ui.theme

import androidx.compose.ui.graphics.Color

// Pure Obsidian & Monochrome Theme (Matching Runeicons official design)
val ObsidianDarkBg = Color(0xFF09090B)
val ObsidianDarkSurface = Color(0xFF121214)
val ObsidianDarkSurfaceVar = Color(0xFF18181B)
val ObsidianDarkBorder = Color(0xFF27272A)
val ObsidianDarkTextPrimary = Color(0xFFFFFFFF)
val ObsidianDarkTextSecondary = Color(0xFFA1A1AA)

// Inverted Theme (Pure Crisp White & High Contrast Black)
val InvertedLightBg = Color(0xFFFFFFFF)
val InvertedLightSurface = Color(0xFFF4F4F5)
val InvertedLightSurfaceVar = Color(0xFFEFEFF2)
val InvertedLightBorder = Color(0xFFE4E4E7)
val InvertedLightTextPrimary = Color(0xFF09090B)
val InvertedLightTextSecondary = Color(0xFF71717A)

// Core Brand Accents
val RunePrimary = Color(0xFF7C5CFF)
val RunePrimaryDark = Color(0xFF5835E5)
val RunePrimaryLight = Color(0xFFA78BFA)
val RuneAccent = Color(0xFFC084FC)

val DarkBackground = Color(0xFF09090B)
val DarkSurface = Color(0xFF121214)
val DarkSurfaceVariant = Color(0xFF18181B)
val DarkBorder = Color(0xFF27272A)

val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF4F4F5)
val LightSurfaceVariant = Color(0xFFEFEFF2)
val LightBorder = Color(0xFFE4E4E7)

val EmeraldGreen = Color(0xFF10B981)
val RoseRed = Color(0xFFF43F5E)
val AmberYellow = Color(0xFFF59E0B)
val SkyBlue = Color(0xFF0EA5E9)
val VioletPurple = Color(0xFF8B5CF6)

val RunePurpleAccent = Color(0xFF7C5CFF)
val RuneBlueAccent = Color(0xFF3859FD)
val RunePinkAccent = Color(0xFFEC4899)
val RuneEmeraldAccent = Color(0xFF10B981)


fun parseHexColor(hex: String, fallback: Color = Color.Black): Color {
    val clean = hex.removePrefix("#").trim()
    return try {
        when (clean.length) {
            6 -> {
                val colorLong = clean.toLong(16)
                Color(0xFF000000 or colorLong)
            }
            8 -> {
                val colorLong = clean.toLong(16)
                Color(colorLong)
            }
            3 -> {
                val r = clean[0].toString().repeat(2).toInt(16)
                val g = clean[1].toString().repeat(2).toInt(16)
                val b = clean[2].toString().repeat(2).toInt(16)
                Color(r, g, b, 255)
            }
            else -> fallback
        }
    } catch (e: Exception) {
        fallback
    }
}
