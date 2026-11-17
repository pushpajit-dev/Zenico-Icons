package com.runeicons.app.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PackageItem(
    val name: String,
    val description: String,
    val installCommand: String,
    val version: String = "1.0.0"
)

@Composable
fun PackagesScreen() {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val packages = listOf(
        PackageItem("runeicons", "Core vector icon definitions, sprites, and SVG assets for all 5 styles.", "pnpm add runeicons"),
        PackageItem("runeicons-react", "React and Next.js icon components with dynamic style and size props.", "pnpm add runeicons-react"),
        PackageItem("runeicons-flutter", "Official Flutter package providing crisp vector widgets for mobile.", "flutter pub add runeicons_flutter"),
        PackageItem("runeicons-vue", "Vue 3 components for Rune Icons with full TypeScript support.", "pnpm add runeicons-vue"),
        PackageItem("runeicons-svelte", "Native Svelte 5 components with reactive icon properties.", "pnpm add runeicons-svelte"),
        PackageItem("runeicons-astro", "Optimized Astro icon components for blazing fast static websites.", "pnpm add runeicons-astro"),
        PackageItem("runeicons-figma", "Official Figma plugin to insert and customize glyphs directly.", "figma.com/community/plugin/runeicons"),
        PackageItem("runeicons-vscode", "VS Code extension for auto-completing icon names in your IDE.", "code --install-extension runeicons"),
        PackageItem("runeicons-mcp", "Model Context Protocol server for AI coding assistants.", "npx runeicons-mcp")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "RUNE ICONS PACKAGES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Official SDKs, Libraries & Framework Plugins",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(packages) { pkg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pkg.name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
                            ) {
                                Text(
                                    "v${pkg.version}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            pkg.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Terminal install snippet
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF0F0F11) else Color(0xFFF1F1F4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    pkg.installCommand,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isDark) Color(0xFFE4E4E7) else Color(0xFF18181B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
