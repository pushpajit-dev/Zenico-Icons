package com.runeicons.app.model

enum class IconStyle(val label: String) {
    NORMAL("Normal"),
    DUOTONE("Duotone"),
    FILL("Fill"),
    PIXELATED("Pixelated"),
    GLASS("Glass"),
    BRANDS("Brands")
}

data class RuneIcon(
    val id: String,
    val name: String,
    val folder: String,
    val category: String,
    val tags: List<String>,
    val normalRes: Int? = null,
    val duotoneRes: Int? = null,
    val fillRes: Int? = null,
    val pixelatedRes: Int? = null,
    val pathData: String = ""
) {
    fun getResForStyle(style: IconStyle): Int? {
        return when (style) {
            IconStyle.NORMAL -> normalRes
            IconStyle.DUOTONE -> duotoneRes ?: normalRes
            IconStyle.FILL -> fillRes ?: normalRes
            IconStyle.PIXELATED -> pixelatedRes ?: normalRes
            IconStyle.GLASS -> duotoneRes ?: normalRes
            IconStyle.BRANDS -> normalRes
        }
    }
}

data class BrandLogo(
    val id: String,
    val name: String,
    val resId: Int,
    val pathData: String = ""
)
