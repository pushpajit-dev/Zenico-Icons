package com.runeicons.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon
import kotlinx.coroutines.launch

/**
 * The official 18 categories from Runeicons web UI screenshot:
 * All, Actions, Accessibility, Commerce, Communication, Dev & Tech, Feedback,
 * Files, Hardware, Layout, Media, Metrics, Misc, Navigation, Time, Users, Weather, Custom
 */
val OFFICIAL_CATEGORIES = listOf(
    "All",
    "Actions",
    "Accessibility",
    "Commerce",
    "Communication",
    "Dev & Tech",
    "Feedback",
    "Files",
    "Hardware",
    "Layout",
    "Media",
    "Metrics",
    "Misc",
    "Navigation",
    "Time",
    "Users",
    "Weather",
    "Custom"
)

data class StyleItem(
    val style: IconStyle,
    val label: String,
    val iconVector: ImageVector
)

@Composable
fun IconLibraryPanelView(
    icons: List<RuneIcon>,
    selectedIcon: RuneIcon?,
    selectedStyle: IconStyle = IconStyle.NORMAL,
    onSelectStyle: (IconStyle) -> Unit = {},
    onSelectIcon: (RuneIcon) -> Unit,
    showLeftRail: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    // Theme Colors
    val panelBg = if (isDark) Color(0xFF0D0D10) else Color(0xFFFAFAFC)
    val railBg = if (isDark) Color(0xFF09090B) else Color(0xFFF0F0F3)
    val borderColor = if (isDark) Color(0xFF222226) else Color(0xFFE4E4E8)
    val cellBorderColor = if (isDark) Color(0xFF1C1C20) else Color(0xFFECECEF)
    val inputBg = if (isDark) Color(0xFF141417) else Color(0xFFFFFFFF)
    val dropdownBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFFFF)
    val textPrimary = if (isDark) Color(0xFFF4F4F5) else Color(0xFF09090B)
    val textMuted = if (isDark) Color(0xFF71717A) else Color(0xFF8E8E93)
    val brandBlue = Color(0xFF3859FD)

    // State for Search and Category filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Styles list for selector
    val stylesList = remember {
        listOf(
            StyleItem(IconStyle.NORMAL, "Normal", Icons.Default.Edit),
            StyleItem(IconStyle.DUOTONE, "Duotone", Icons.Default.AccountCircle),
            StyleItem(IconStyle.FILL, "Fill", Icons.Default.FilterCenterFocus),
            StyleItem(IconStyle.PIXELATED, "Pixelated", Icons.Default.GridView),
            StyleItem(IconStyle.GLASS, "Glass", Icons.Default.AutoAwesome)
        )
    }

    // Filter icons by category, search query, and style availability
    val filteredIcons = remember(icons, selectedCategory, searchQuery, selectedStyle) {
        icons.filter { icon ->
            // Category match
            val catMatch = when (selectedCategory.lowercase()) {
                "all" -> true
                "actions" -> icon.category.equals("action", ignoreCase = true) || icon.folder.equals("tools", ignoreCase = true) || icon.tags.any { it.contains("action", ignoreCase = true) }
                "accessibility" -> icon.tags.any { it.contains("accessibility", ignoreCase = true) || it.contains("vision", ignoreCase = true) }
                "commerce" -> icon.tags.any { it.contains("shop", ignoreCase = true) || it.contains("cart", ignoreCase = true) || it.contains("card", ignoreCase = true) || it.contains("money", ignoreCase = true) }
                "communication" -> icon.tags.any { it.contains("chat", ignoreCase = true) || it.contains("mail", ignoreCase = true) || it.contains("message", ignoreCase = true) || it.contains("phone", ignoreCase = true) }
                "dev & tech" -> icon.category.equals("dev", ignoreCase = true) || icon.folder.equals("code", ignoreCase = true) || icon.tags.any { it.contains("code", ignoreCase = true) || it.contains("server", ignoreCase = true) || it.contains("git", ignoreCase = true) }
                "feedback" -> icon.tags.any { it.contains("star", ignoreCase = true) || it.contains("heart", ignoreCase = true) || it.contains("alert", ignoreCase = true) || it.contains("thumb", ignoreCase = true) }
                "files" -> icon.category.equals("files", ignoreCase = true) || icon.folder.equals("documents", ignoreCase = true) || icon.tags.any { it.contains("file", ignoreCase = true) || it.contains("archive", ignoreCase = true) }
                "hardware" -> icon.tags.any { it.contains("server", ignoreCase = true) || it.contains("cpu", ignoreCase = true) || it.contains("device", ignoreCase = true) || it.contains("terminal", ignoreCase = true) }
                "layout" -> icon.tags.any { it.contains("layout", ignoreCase = true) || it.contains("grid", ignoreCase = true) || it.contains("table", ignoreCase = true) }
                "media" -> icon.tags.any { it.contains("image", ignoreCase = true) || it.contains("music", ignoreCase = true) || it.contains("play", ignoreCase = true) || it.contains("video", ignoreCase = true) }
                "metrics" -> icon.tags.any { it.contains("chart", ignoreCase = true) || it.contains("graph", ignoreCase = true) || it.contains("metric", ignoreCase = true) }
                "misc" -> icon.tags.any { it.contains("sparkle", ignoreCase = true) || it.contains("zap", ignoreCase = true) || it.contains("flag", ignoreCase = true) }
                "navigation" -> icon.category.equals("navigation", ignoreCase = true) || icon.folder.equals("arrows", ignoreCase = true) || icon.tags.any { it.contains("arrow", ignoreCase = true) || it.contains("move", ignoreCase = true) || it.contains("chevron", ignoreCase = true) }
                "time" -> icon.tags.any { it.contains("clock", ignoreCase = true) || it.contains("time", ignoreCase = true) || it.contains("calendar", ignoreCase = true) }
                "users" -> icon.tags.any { it.contains("user", ignoreCase = true) || it.contains("profile", ignoreCase = true) || it.contains("group", ignoreCase = true) }
                "weather" -> icon.tags.any { it.contains("sun", ignoreCase = true) || it.contains("moon", ignoreCase = true) || it.contains("cloud", ignoreCase = true) || it.contains("rain", ignoreCase = true) }
                "custom" -> false
                else -> icon.category.contains(selectedCategory, ignoreCase = true)
            }

            // Search query match
            val searchMatch = searchQuery.isBlank() ||
                    icon.name.contains(searchQuery, ignoreCase = true) ||
                    icon.category.contains(searchQuery, ignoreCase = true) ||
                    icon.tags.any { it.contains(searchQuery, ignoreCase = true) }

            // Style availability match
            val styleMatch = when (selectedStyle) {
                IconStyle.NORMAL -> icon.normalRes != null
                IconStyle.DUOTONE -> icon.duotoneRes != null || icon.normalRes != null
                IconStyle.FILL -> icon.fillRes != null || icon.normalRes != null
                IconStyle.PIXELATED -> icon.pixelatedRes != null || icon.normalRes != null
                IconStyle.GLASS -> icon.duotoneRes != null || icon.normalRes != null
                IconStyle.BRANDS -> true
            }

            catMatch && searchMatch && styleMatch
        }
    }

    Row(
        modifier = modifier
            .background(panelBg)
            .border(BorderStroke(1.dp, borderColor))
    ) {
        // Far Left Vertical Style Rail (Exact Screenshot 2 & 3)
        if (showLeftRail) {
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(railBg)
                    .border(BorderStroke(1.dp, borderColor))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rune Logo Mark at Top
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "R",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (isDark) Color.Black else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // The 5 Style Buttons
                stylesList.forEach { styleItem ->
                    val isSelected = selectedStyle == styleItem.style
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectStyle(styleItem.style) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) brandBlue else (if (isDark) Color(0xFF18181B) else Color(0xFFE4E4E8)),
                        border = if (!isSelected) BorderStroke(1.dp, borderColor) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = styleItem.iconVector,
                                contentDescription = styleItem.label,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) Color.White else textMuted
                            )
                        }
                    }
                }
            }
        }

        // Main Icon Library Content Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // Top Controls: Search Input + [⊞ All ▾] Category Dropdown Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search...",
                            fontSize = 12.sp,
                            color = textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(15.dp),
                            tint = textMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(14.dp),
                                    tint = textMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 12.sp, color = textPrimary),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedBorderColor = brandBlue,
                        unfocusedBorderColor = borderColor,
                        cursorColor = brandBlue
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )

                // Category Dropdown Button
                Box {
                    Surface(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { categoryDropdownExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = inputBg,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.GridView,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = textMuted
                            )
                            Text(
                                text = selectedCategory,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.size(16.dp),
                                tint = textMuted
                            )
                        }
                    }

                    // Dropdown Menu with the exact 18 categories from Screenshot 1
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier
                            .width(170.dp)
                            .heightIn(max = 380.dp)
                            .background(dropdownBg)
                            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
                    ) {
                        OFFICIAL_CATEGORIES.forEach { category ->
                            val isCurrent = selectedCategory.equals(category, ignoreCase = true)
                            DropdownMenuItem(
                                text = {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCurrent) (if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)) else Color.Transparent,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = category,
                                            fontSize = 12.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) (if (isDark) Color.White else Color.Black) else textPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Horizontal Scrollable Style Strip (User requested: types show in horizontal scroll)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(stylesList) { styleItem ->
                    val isSelected = selectedStyle == styleItem.style
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectStyle(styleItem.style) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) brandBlue else (if (isDark) Color(0xFF18181B) else Color(0xFFE4E4E8)),
                        border = if (!isSelected) BorderStroke(1.dp, borderColor) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = styleItem.iconVector,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isSelected) Color.White else textMuted
                            )
                            Text(
                                text = styleItem.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Count Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredIcons.size} ICONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = textMuted
                )
                if (selectedCategory != "All") {
                    Text(
                        text = selectedCategory,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = brandBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Icons Grid with Spring Animation on Select/Hover (Exact Screenshot 3 crosshatch grid)
            if (filteredIcons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No icons found",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 44.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(filteredIcons, key = { it.id }) { icon ->
                        val isSelected = selectedIcon?.id == icon.id
                        AnimatedIconGridCell(
                            icon = icon,
                            style = selectedStyle,
                            isSelected = isSelected,
                            isDark = isDark,
                            borderColor = cellBorderColor,
                            selectedBorderColor = brandBlue,
                            onClick = { onSelectIcon(icon) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Icon Cell with Spring Bounce Physics on tap and selection highlight
 */
@Composable
private fun AnimatedIconGridCell(
    icon: RuneIcon,
    style: IconStyle,
    isSelected: Boolean,
    isDark: Boolean,
    borderColor: Color,
    selectedBorderColor: Color,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Trigger spring bounce when pressed or selected
    LaunchedEffect(isPressed) {
        if (isPressed) {
            scaleAnim.animateTo(
                targetValue = 0.88f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            scaleAnim.animateTo(
                targetValue = 1.1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val resId = icon.getResForStyle(style) ?: icon.normalRes

    val cellBg = when {
        isSelected -> selectedBorderColor.copy(alpha = if (isDark) 0.18f else 0.12f)
        else -> Color.Transparent
    }

    val iconTint = when {
        isSelected -> selectedBorderColor
        style == IconStyle.GLASS -> if (isDark) Color(0xFF93C5FD) else Color(0xFF2563EB)
        isDark -> Color(0xFFF4F4F5)
        else -> Color(0xFF18181B)
    }

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scaleAnim.value)
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    coroutineScope.launch {
                        scaleAnim.animateTo(0.9f)
                        scaleAnim.animateTo(1f)
                    }
                    onClick()
                }
            ),
        shape = RoundedCornerShape(4.dp),
        color = cellBg,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = if (isSelected) selectedBorderColor else borderColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (resId != null) {
                Icon(
                    painter = painterResource(id = resId),
                    contentDescription = icon.name,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            } else {
                Text(
                    text = icon.name.take(2).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconTint
                )
            }
        }
    }
}
