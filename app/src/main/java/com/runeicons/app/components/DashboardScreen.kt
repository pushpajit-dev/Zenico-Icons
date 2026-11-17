package com.runeicons.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon
import com.runeicons.app.ui.theme.EmeraldGreen
import com.runeicons.app.ui.theme.RunePrimary
import com.runeicons.app.ui.theme.RunePrimaryDark
import com.runeicons.app.ui.theme.SkyBlue
import com.runeicons.app.ui.theme.VioletPurple

@Composable
fun DashboardScreen(
    icons: List<RuneIcon>,
    categories: List<String>,
    selectedCategory: String?,
    selectedStyle: IconStyle,
    searchQuery: String,
    favorites: Set<String>,
    onCategoryChange: (String?) -> Unit,
    onStyleChange: (IconStyle) -> Unit,
    onSearchChange: (String) -> Unit,
    onIconClick: (RuneIcon) -> Unit,
    onToggleFavorite: (RuneIcon) -> Unit
) {
    val filteredIcons = remember(icons, selectedCategory, selectedStyle, searchQuery) {
        icons.filter { icon ->
            val matchesCategory = if (selectedCategory == null || selectedCategory.equals("All", ignoreCase = true)) {
                true
            } else {
                when (selectedCategory.lowercase()) {
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
            }

            val matchesSearch = searchQuery.isBlank() ||
                    icon.name.contains(searchQuery, ignoreCase = true) ||
                    icon.category.contains(searchQuery, ignoreCase = true) ||
                    icon.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val hasStyle = when (selectedStyle) {
                IconStyle.NORMAL -> icon.normalRes != null
                IconStyle.DUOTONE -> icon.duotoneRes != null || icon.normalRes != null
                IconStyle.FILL -> icon.fillRes != null || icon.normalRes != null
                IconStyle.PIXELATED -> icon.pixelatedRes != null || icon.normalRes != null
                IconStyle.GLASS -> icon.duotoneRes != null || icon.normalRes != null
                IconStyle.BRANDS -> true
            }
            matchesCategory && matchesSearch && hasStyle
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Hero Dashboard Header Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rune Icons Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${filteredIcons.size} glyphs available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RunePrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "v1.0 Native",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RunePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBadge("220+", "Outline", RunePrimary, Modifier.weight(1f))
                    StatBadge("218", "Duotone", VioletPurple, Modifier.weight(1f))
                    StatBadge("129", "Fill", SkyBlue, Modifier.weight(1f))
                    StatBadge("215", "Pixel", EmeraldGreen, Modifier.weight(1f))
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            placeholder = { Text("Search by name, category, or tag...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        )

        // Style Filter Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val styles = listOf(IconStyle.NORMAL, IconStyle.DUOTONE, IconStyle.FILL, IconStyle.PIXELATED, IconStyle.GLASS)
            items(styles) { style ->
                val isSelected = selectedStyle == style
                FilterChip(
                    selected = isSelected,
                    onClick = { onStyleChange(style) },
                    label = { Text(style.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Category Filter Chips Row with the official 18 categories
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(OFFICIAL_CATEGORIES) { cat ->
                val isSelected = if (cat == "All") (selectedCategory == null || selectedCategory == "All") else selectedCategory.equals(cat, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(if (cat == "All") null else cat) },
                    label = { Text(cat) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Icons Grid
        if (filteredIcons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No icons found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing search or changing the style filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredIcons, key = { it.id }) { icon ->
                    IconCard(
                        icon = icon,
                        style = selectedStyle,
                        isFavorite = favorites.contains(icon.id),
                        onClick = { onIconClick(icon) },
                        onToggleFavorite = { onToggleFavorite(icon) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatBadge(count: String, label: String, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun IconCard(
    icon: RuneIcon,
    style: IconStyle,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val resId = icon.getResForStyle(style) ?: icon.normalRes

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heart Icon on top right
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Icon Graphic
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                if (resId != null) {
                    Icon(
                        painter = painterResource(id = resId),
                        contentDescription = icon.name,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                } else {
                    InteractiveIconCanvas(
                        pathData = icon.pathData,
                        iconSize = 28.dp,
                        tintColor = MaterialTheme.colorScheme.primary,
                        backgroundStyle = BackgroundStyle.NONE
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = icon.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = icon.category,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
