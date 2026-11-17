package com.runeicons.app.model

data class GradientStop(
    val color: String,
    val position: Float
)

data class CustomUploadedIcon(
    val id: String,
    val name: String,
    val pathData: String = "",
    val uriString: String? = null
)

data class CustomizationState(
    val primaryColor: String = "#FFFFFF",
    val secondaryColor: String = "#3859FD",
    val isGradient: Boolean = false,
    val gradientStops: List<GradientStop> = listOf(
        GradientStop("#6366F1", 0f),
        GradientStop("#A855F7", 50f),
        GradientStop("#EC4899", 100f)
    ),
    val gradientType: String = "linear", // "linear", "radial", "angular"
    val gradientAngle: Float = 135f,
    val gradientSpread: String = "pad", // "pad", "repeat", "reflect"
    val gradientTarget: String = "stroke", // "stroke", "fill", "both"
    val blur: Float = 0f,
    val scale: Float = 1.0f,
    val opacity: Float = 100f,
    val strokeWidth: Float = 2.0f,
    val animationEnabled: Boolean = false,
    val strokeStyle: String = "round", // "soft", "round", "medium", "sharp"
    val flipH: Boolean = false,
    val flipV: Boolean = false,
    val rotation: Float = 0f,
    val shadowEnabled: Boolean = false,
    val shadowInner: Boolean = false,
    val shadowOpacity: Float = 0f,
    val shadowBlur: Float = 10f,
    val shadowOffsetX: Float = 0f,
    val shadowOffsetY: Float = 4f,
    val noiseIntensity: Float = 20f,
    val texture: String = "none", // "none", "paper", "canvas", "grid"
    val width: Int = 128,
    val height: Int = 128,
    val showGrid: Boolean = true,
    val editorMode: String = "view", // "view", "draw"
    val activeTool: String = "pen", // "pen", "rect", "circle", "line", "fill", "eraser"
    val customIcons: List<CustomUploadedIcon> = emptyList()
)
