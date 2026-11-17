package com.runeicons.app.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon

@Composable
fun EditorWorkspaceScreen(
    state: CustomizationState,
    currentIcon: RuneIcon?,
    allIcons: List<RuneIcon>,
    selectedStyle: IconStyle = IconStyle.NORMAL,
    onSelectStyle: (IconStyle) -> Unit = {},
    onSelectIcon: (RuneIcon) -> Unit,
    onStateChange: (CustomizationState) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    // Find arrow variants or category variants for the bottom tray
    val variantIcons = remember(currentIcon, allIcons) {
        if (currentIcon != null) {
            val folderMatches = allIcons.filter { it.folder == currentIcon.folder }
            if (folderMatches.size >= 4) folderMatches
            else allIcons.take(6)
        } else {
            emptyList()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 840.dp
        val isMedium = maxWidth in 580.dp..840.dp

        if (isWide) {
            // Full 3-Column Desktop/Tablet Layout Matching Screenshot 3:
            // [ Left Icon Library Panel (300dp) | Center Canvas (Flex) | Right Properties Panel (340dp) ]
            Row(modifier = Modifier.fillMaxSize()) {
                IconLibraryPanelView(
                    icons = allIcons,
                    selectedIcon = currentIcon,
                    selectedStyle = selectedStyle,
                    onSelectStyle = onSelectStyle,
                    onSelectIcon = onSelectIcon,
                    showLeftRail = true,
                    modifier = Modifier
                        .width(310.dp)
                        .fillMaxHeight()
                )

                EditorCanvasView(
                    state = state,
                    currentIcon = currentIcon,
                    variantIcons = variantIcons,
                    onSelectVariant = onSelectIcon,
                    onStateChange = onStateChange,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                PropertiesPanelView(
                    state = state,
                    onStateChange = onStateChange,
                    modifier = Modifier
                        .width(350.dp)
                        .fillMaxHeight()
                )
            }
        } else if (isMedium) {
            // Medium screens (Tablet portrait / Foldables): 2-Column with Icon Library + Canvas & Properties Toggle
            var mediumRightTab by remember { mutableIntStateOf(0) }

            Row(modifier = Modifier.fillMaxSize()) {
                IconLibraryPanelView(
                    icons = allIcons,
                    selectedIcon = currentIcon,
                    selectedStyle = selectedStyle,
                    onSelectStyle = onSelectStyle,
                    onSelectIcon = onSelectIcon,
                    showLeftRail = false,
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Surface(
                        color = if (isDark) Color(0xFF141416) else Color(0xFFEFEFF2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TabRow(
                            selectedTabIndex = mediumRightTab,
                            containerColor = Color.Transparent,
                            contentColor = if (isDark) Color.White else Color.Black
                        ) {
                            Tab(
                                selected = mediumRightTab == 0,
                                onClick = { mediumRightTab = 0 },
                                text = { Text("CANVAS & DRAW", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = mediumRightTab == 1,
                                onClick = { mediumRightTab = 1 },
                                text = { Text("PROPERTIES & UPLOAD", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (mediumRightTab == 0) {
                            EditorCanvasView(
                                state = state,
                                currentIcon = currentIcon,
                                variantIcons = variantIcons,
                                onSelectVariant = onSelectIcon,
                                onStateChange = onStateChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            PropertiesPanelView(
                                state = state,
                                onStateChange = onStateChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Portrait: Seamless 3 Tabs between Icons, Canvas, and Properties
            var mobileTab by remember { mutableIntStateOf(if (currentIcon == null) 0 else 1) }

            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = if (isDark) Color(0xFF141416) else Color(0xFFEFEFF2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = mobileTab,
                        containerColor = Color.Transparent,
                        contentColor = if (isDark) Color.White else Color.Black
                    ) {
                        Tab(
                            selected = mobileTab == 0,
                            onClick = { mobileTab = 0 },
                            text = { Text("ICONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) }
                        )
                        Tab(
                            selected = mobileTab == 1,
                            onClick = { mobileTab = 1 },
                            text = { Text("CANVAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) }
                        )
                        Tab(
                            selected = mobileTab == 2,
                            onClick = { mobileTab = 2 },
                            text = { Text("PROPERTIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (mobileTab) {
                        0 -> {
                            IconLibraryPanelView(
                                icons = allIcons,
                                selectedIcon = currentIcon,
                                selectedStyle = selectedStyle,
                                onSelectStyle = onSelectStyle,
                                onSelectIcon = { icon ->
                                    onSelectIcon(icon)
                                    // Smoothly navigate to canvas upon selecting an icon
                                    mobileTab = 1
                                },
                                showLeftRail = false,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        1 -> {
                            EditorCanvasView(
                                state = state,
                                currentIcon = currentIcon,
                                variantIcons = variantIcons,
                                onSelectVariant = onSelectIcon,
                                onStateChange = onStateChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        2 -> {
                            PropertiesPanelView(
                                state = state,
                                onStateChange = onStateChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
