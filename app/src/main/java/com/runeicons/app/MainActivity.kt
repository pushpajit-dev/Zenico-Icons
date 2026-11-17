package com.runeicons.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.runeicons.app.components.AboutScreen
import com.runeicons.app.components.AppNavigationDrawerContent
import com.runeicons.app.components.EditorCanvasView
import com.runeicons.app.components.ExportDialog
import com.runeicons.app.components.ImplementationDialog
import com.runeicons.app.components.PropertiesPanelView
import com.runeicons.app.components.TutorialDialog
import com.runeicons.app.components.exportSvgMarkup
import com.runeicons.app.data.RuneIconsCatalog
import com.runeicons.app.model.CustomizationState
import com.runeicons.app.model.IconStyle
import com.runeicons.app.model.RuneIcon
import com.runeicons.app.ui.theme.RuneIconsTheme
import com.runeicons.app.utils.IconExportUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDark) }

            RuneIconsTheme(darkTheme = darkTheme) {
                ZenicoApp(
                    isDark = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenicoApp(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val icons = remember { RuneIconsCatalog.icons }

    // Selected icon defaults to first available icon in catalog (e.g. arrow-down-left or arrow-right)
    var editorIcon by remember { mutableStateOf<RuneIcon?>(icons.find { it.name.contains("right", ignoreCase = true) } ?: icons.firstOrNull()) }
    var selectedStyle by remember { mutableStateOf(IconStyle.NORMAL) }
    var editorState by remember { mutableStateOf(CustomizationState(primaryColor = if (isDark) "#FFFFFF" else "#141416", editorMode = "view")) }

    androidx.compose.runtime.LaunchedEffect(isDark) {
        if (isDark && editorState.primaryColor.equals("#141416", ignoreCase = true)) {
            editorState = editorState.copy(primaryColor = "#FFFFFF")
        } else if (!isDark && editorState.primaryColor.equals("#FFFFFF", ignoreCase = true)) {
            editorState = editorState.copy(primaryColor = "#141416")
        }
    }

    // Overflow & Dialog States
    var overflowMenuExpanded by remember { mutableStateOf(false) }
    var showPropertiesBottomSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImplementationDialog by remember { mutableStateOf(false) }

    val prefs = remember { context.getSharedPreferences("zenico_prefs", Context.MODE_PRIVATE) }
    var showTutorialDialog by remember {
        mutableStateOf(!prefs.getBoolean("has_seen_tutorial", false))
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    val borderColor = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val navBg = if (isDark) Color(0xFF121215) else Color(0xFFF4F4F6)
    val brandBlue = Color(0xFF3859FD)

    // Variant icons for bottom tray if needed
    val variantIcons = remember(editorIcon, icons) {
        if (editorIcon != null) {
            val folderMatches = icons.filter { it.folder == editorIcon?.folder }
            if (folderMatches.size >= 4) folderMatches else icons.take(6)
        } else {
            emptyList()
        }
    }

    fun copyTextToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawerContent(
                icons = icons,
                selectedIcon = editorIcon,
                selectedStyle = selectedStyle,
                onSelectStyle = { selectedStyle = it },
                onSelectIcon = { icon ->
                    editorIcon = icon
                    editorState = editorState.copy(editorMode = "view")
                },
                onOpenInfo = { showAboutDialog = true },
                onOpenTutorial = { showTutorialDialog = true },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                // CANVAS HEAD (TOP BAR MATCHING WEB WORKSPACE HEADER)
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Drawer Hamburger & Zenico Text (Gap filled cleanly by Zenico text)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                            }

                            Text(
                                text = "Zenico",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        }

                        // Right: Theme Toggle + Three Dots Menu (Spaced properly, no overlapping)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Theme Toggle Button
                            IconButton(
                                onClick = onToggleTheme,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Theme",
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Three Dots Menu (⋮)
                            Box {
                                IconButton(
                                    onClick = { overflowMenuExpanded = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Options", modifier = Modifier.size(18.dp))
                                }

                                DropdownMenu(
                                    expanded = overflowMenuExpanded,
                                    onDismissRequest = { overflowMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("Upload Custom Icon", fontSize = 12.sp) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            showPropertiesBottomSheet = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("Export & Download", fontSize = 12.sp) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            showExportDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("Copy as SVG", fontSize = 12.sp) },
                                        onClick = {
                                            val svg = IconExportUtils.exportSvgMarkup(editorIcon?.name ?: "icon", editorIcon?.pathData ?: "", editorState, selectedStyle)
                                            copyTextToClipboard("SVG Code", svg)
                                            overflowMenuExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("Properties & Tuning", fontSize = 12.sp) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            showPropertiesBottomSheet = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("Implementation (Code)", fontSize = 12.sp) },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            showImplementationDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        text = { Text("About", fontSize = 12.sp) },
                                        onClick = {
                                            showAboutDialog = true
                                            overflowMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            // CANVAS AT THE CENTER OF THE SCREEN (NO EXTRA DASHBOARD)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                EditorCanvasView(
                    state = editorState,
                    currentIcon = editorIcon,
                    variantIcons = variantIcons,
                    selectedStyle = selectedStyle,
                    onSelectVariant = { editorIcon = it },
                    onStateChange = { editorState = it },
                    onOpenPropertiesSheet = { showPropertiesBottomSheet = true },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // ==========================================
        // PROPERTIES BOTTOM SHEET ("OTTO MSEHTT FOR PROETICS")
        // ==========================================
        if (showPropertiesBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPropertiesBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = if (isDark) Color(0xFF101014) else Color(0xFFFFFFFF),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(borderColor)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 620.dp)
                ) {
                    // Bottom Sheet Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = brandBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Properties & Tuning",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        }

                        IconButton(
                            onClick = { showPropertiesBottomSheet = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                        }
                    }

                    HorizontalDivider(color = borderColor)

                    // Scrollable Properties Content with Custom Path Loading
                    PropertiesPanelView(
                        state = editorState,
                        onStateChange = { editorState = it },
                        onApplyCustomPath = { path ->
                            editorIcon = editorIcon?.copy(pathData = path) ?: RuneIcon(
                                id = "custom",
                                name = "Custom Icon",
                                folder = "custom",
                                category = "custom",
                                tags = emptyList(),
                                pathData = path
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // First-Time Tutorial Walkthrough Dialog (One-time with Skip)
        if (showTutorialDialog) {
            TutorialDialog(
                onDismiss = { showTutorialDialog = false }
            )
        }

        // Info & About Screen Dialog (Minimalist, No Icon, Just Close Icon Top Right)
        if (showAboutDialog) {
            Dialog(
                onDismissRequest = { showAboutDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    AboutScreen(onDismiss = { showAboutDialog = false })
                }
            }
        }

        // Export Dialog (SVG, PNG Transparent, JPG, TIFF, Code Wise)
        if (showExportDialog) {
            ExportDialog(
                iconName = editorIcon?.name ?: "icon",
                pathData = editorIcon?.pathData ?: "",
                state = editorState,
                style = selectedStyle,
                currentIcon = editorIcon,
                onOpenCode = { showImplementationDialog = true },
                onDismiss = { showExportDialog = false }
            )
        }

        // Implementation Dialog (Sketchware Pro, Vite, Node.js)
        if (showImplementationDialog) {
            ImplementationDialog(
                iconName = editorIcon?.name ?: "icon",
                pathData = editorIcon?.pathData ?: "",
                onDismiss = { showImplementationDialog = false }
            )
        }
    }
}
