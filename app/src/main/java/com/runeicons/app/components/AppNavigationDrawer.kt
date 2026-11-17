package com.runeicons.app.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.R
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon

data class DiscordCategory(
    val id: String,
    val name: String,
    val channelLabel: String,
    val icon: ImageVector,
    val filterPredicate: (RuneIcon) -> Boolean
)

val DISCORD_CATEGORIES = listOf(
    DiscordCategory("all", "All", "all-icons", Icons.Default.GridView) { true },
    DiscordCategory("arrows", "Arrows", "arrows-nav", Icons.Default.Navigation) {
        it.folder.equals("arrows", ignoreCase = true) || it.category.equals("navigation", ignoreCase = true)
    },
    DiscordCategory("code", "Code", "dev-tech", Icons.Default.Terminal) {
        it.folder.equals("code", ignoreCase = true) || it.category.equals("dev", ignoreCase = true)
    },
    DiscordCategory("documents", "Files", "documents", Icons.Default.Folder) {
        it.folder.equals("documents", ignoreCase = true) || it.category.equals("files", ignoreCase = true)
    },
    DiscordCategory("gadgets", "Devices", "gadgets", Icons.Default.Devices) {
        it.folder.equals("gadgets", ignoreCase = true)
    },
    DiscordCategory("messaging", "Chat", "messaging", Icons.Default.Chat) {
        it.folder.equals("messaging", ignoreCase = true)
    },
    DiscordCategory("playback", "Media", "playback", Icons.Default.PlayCircle) {
        it.folder.equals("playback", ignoreCase = true)
    },
    DiscordCategory("tools", "Tools", "tools-edit", Icons.Default.Build) {
        it.folder.equals("tools", ignoreCase = true)
    },
    DiscordCategory("metrics", "Metrics", "metrics-money", Icons.Default.Timeline) {
        it.folder.equals("metrics", ignoreCase = true) || it.folder.equals("money", ignoreCase = true)
    },
    DiscordCategory("schedule", "Schedule", "schedule-time", Icons.Default.Schedule) {
        it.folder.equals("schedule", ignoreCase = true)
    },
    DiscordCategory("nature", "Nature", "nature-weather", Icons.Default.WbSunny) {
        it.folder.equals("nature", ignoreCase = true)
    }
)

@Composable
fun AppNavigationDrawerContent(
    icons: List<RuneIcon>,
    selectedIcon: RuneIcon?,
    selectedStyle: IconStyle,
    onSelectStyle: (IconStyle) -> Unit = {},
    onSelectIcon: (RuneIcon) -> Unit,
    onOpenInfo: () -> Unit,
    onOpenTutorial: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    // Theme palette
    val railBg = if (isDark) Color(0xFF09090C) else Color(0xFFEBEBEF)
    val paneBg = if (isDark) Color(0xFF101014) else Color(0xFFFFFFFF)
    val cardBg = if (isDark) Color(0xFF16161B) else Color(0xFFF5F5F8)
    val borderColor = if (isDark) Color(0xFF202026) else Color(0xFFE2E2E8)
    val textPrimary = if (isDark) Color(0xFFF4F4F6) else Color(0xFF0A0A0C)
    val textMuted = if (isDark) Color(0xFF71717A) else Color(0xFF8E8E93)
    val brandBlue = Color(0xFF3859FD)

    var activeCategoryId by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val activeCategory = remember(activeCategoryId) {
        DISCORD_CATEGORIES.find { it.id == activeCategoryId } ?: DISCORD_CATEGORIES.first()
    }

    // Filter icons by category and search
    val filteredIcons = remember(icons, activeCategoryId, searchQuery) {
        val catFiltered = icons.filter { activeCategory.filterPredicate(it) }
        if (searchQuery.isBlank()) {
            catFiltered
        } else {
            catFiltered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    ModalDrawerSheet(
        drawerContainerColor = paneBg,
        drawerContentColor = textPrimary,
        modifier = Modifier
            .fillMaxHeight()
            .width(360.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // ==========================================
            // 1. LEFT RAIL (DISCORD STYLE SERVER ICONS)
            // ==========================================
            Column(
                modifier = Modifier
                    .width(68.dp)
                    .fillMaxHeight()
                    .background(railBg)
                    .border(BorderStroke(0.5.dp, borderColor)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Brand Mark at the very top (Zenico Inverted Icon)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF000000))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                        .clickable { activeCategoryId = "all" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Zenico",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Discord-style small horizontal divider pill
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(borderColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable category icon squircles
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DISCORD_CATEGORIES, key = { it.id }) { cat ->
                        val isSelected = activeCategoryId == cat.id

                        val cornerRadius by animateDpAsState(
                            targetValue = if (isSelected) 14.dp else 22.dp,
                            label = "cornerAnimation"
                        )
                        val squircleBg by animateColorAsState(
                            targetValue = if (isSelected) brandBlue else (if (isDark) Color(0xFF16161B) else Color(0xFFFFFFFF)),
                            label = "bgAnimation"
                        )
                        val iconTint by animateColorAsState(
                            targetValue = if (isSelected) Color.White else textMuted,
                            label = "tintAnimation"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left edge Discord pill indicator
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(if (isSelected) 34.dp else 0.dp)
                                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                                    .background(if (isDark) Color.White else Color.Black)
                            )

                            Spacer(modifier = Modifier.width(7.dp))

                            // Squircle icon button
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(cornerRadius))
                                    .background(squircleBg)
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 0.dp else 0.5.dp,
                                            if (isSelected) Color.Transparent else borderColor
                                        ),
                                        RoundedCornerShape(cornerRadius)
                                    )
                                    .clickable {
                                        activeCategoryId = cat.id
                                        searchQuery = ""
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.name,
                                    modifier = Modifier.size(20.dp),
                                    tint = iconTint
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = borderColor)

                // Bottom Rail Actions: About & Feedback
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onOpenInfo,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "About",
                            modifier = Modifier.size(19.dp),
                            tint = textMuted
                        )
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:NoishiXzen@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "[Zenico App Feedback]")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Contact: NoishiXzen@gmail.com", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Feedback",
                            modifier = Modifier.size(19.dp),
                            tint = textMuted
                        )
                    }
                }
            }

            // ==========================================
            // 2. RIGHT PANE (DISCORD CHANNEL / ICONS LIST)
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(paneBg)
            ) {
                // Top Header: # Category Name + Badge + Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "#",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandBlue,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = activeCategory.channelLabel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }

                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(17.dp), tint = textMuted)
                    }
                }

                HorizontalDivider(color = borderColor)

                // Fixed Search Bar Layout (No overlapping or layout glitches)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = textMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search in #${activeCategory.channelLabel}...",
                                        fontSize = 12.sp,
                                        color = textMuted
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        color = textPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(brandBlue),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(14.dp),
                                        tint = textMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Styles Selector Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val styles = listOf(
                        Pair(IconStyle.NORMAL, "Normal"),
                        Pair(IconStyle.DUOTONE, "Duotone"),
                        Pair(IconStyle.FILL, "Fill"),
                        Pair(IconStyle.PIXELATED, "Pixel"),
                        Pair(IconStyle.GLASS, "Glass")
                    )
                    items(styles) { (st, lbl) ->
                        val isStyleActive = selectedStyle == st
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onSelectStyle(st) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isStyleActive) brandBlue else cardBg,
                            border = BorderStroke(
                                0.5.dp,
                                if (isStyleActive) brandBlue else borderColor
                            )
                        ) {
                            Text(
                                text = lbl,
                                fontSize = 10.sp,
                                fontWeight = if (isStyleActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isStyleActive) Color.White else textMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Icon Grid: 3 Columns
                if (filteredIcons.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(28.dp), tint = textMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No icons found", fontSize = 12.sp, color = textMuted)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredIcons, key = { it.id }) { icon ->
                            val isSelected = selectedIcon?.id == icon.id
                            val resId = icon.getResForStyle(selectedStyle) ?: icon.normalRes

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onSelectIcon(icon)
                                        onCloseDrawer()
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) brandBlue.copy(alpha = 0.15f) else cardBg,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 0.5.dp,
                                    if (isSelected) brandBlue else borderColor
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (resId != null) {
                                        val baseColor = if (isSelected) brandBlue else (if (isDark) Color(0xFFF0F0F3) else Color(0xFF141417))
                                        when (selectedStyle) {
                                            IconStyle.NORMAL -> {
                                                Icon(
                                                    painter = painterResource(id = icon.normalRes ?: resId),
                                                    contentDescription = icon.name,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = baseColor
                                                )
                                            }
                                            IconStyle.DUOTONE -> {
                                                Box(
                                                    modifier = Modifier.size(26.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(baseColor.copy(alpha = 0.22f))
                                                    )
                                                    Icon(
                                                        painter = painterResource(id = icon.normalRes ?: resId),
                                                        contentDescription = icon.name,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = baseColor
                                                    )
                                                }
                                            }
                                            IconStyle.FILL -> {
                                                val fillDrawable = icon.fillRes ?: icon.normalRes ?: resId
                                                Icon(
                                                    painter = painterResource(id = fillDrawable),
                                                    contentDescription = icon.name,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = baseColor
                                                )
                                            }
                                            IconStyle.PIXELATED -> {
                                                val pixelRes = icon.pixelatedRes ?: icon.normalRes ?: resId
                                                Icon(
                                                    painter = painterResource(id = pixelRes),
                                                    contentDescription = icon.name,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = baseColor
                                                )
                                            }
                                            IconStyle.GLASS -> {
                                                Box(
                                                    modifier = Modifier.size(26.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f))
                                                            .border(0.8.dp, baseColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                                    )
                                                    Icon(
                                                        painter = painterResource(id = icon.normalRes ?: resId),
                                                        contentDescription = icon.name,
                                                        modifier = Modifier.size(20.dp),
                                                        tint = baseColor
                                                    )
                                                }
                                            }
                                            else -> {
                                                Icon(
                                                    painter = painterResource(id = resId),
                                                    contentDescription = icon.name,
                                                    modifier = Modifier.size(24.dp),
                                                    tint = baseColor
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = icon.name,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) brandBlue else textMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 2.dp)
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
