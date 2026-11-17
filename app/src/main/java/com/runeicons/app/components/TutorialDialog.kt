package com.runeicons.app.components

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.runeicons.app.R

data class TutorialStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val dialogBg = if (isDark) Color(0xFF141418) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
    val textPrimary = if (isDark) Color(0xFFF4F4F5) else Color(0xFF09090B)
    val textMuted = if (isDark) Color(0xFF71717A) else Color(0xFF8E8E93)
    val brandBlue = Color(0xFF3859FD)

    val steps = remember {
        listOf(
            TutorialStep(
                title = "Grouped Icon Library",
                subtitle = "Open the left drawer or library panel to search over 900+ vector glyphs organized neatly by category.",
                icon = Icons.Default.GridView,
                badge = "Step 1 of 3"
            ),
            TutorialStep(
                title = "Finger Vector Editing",
                subtitle = "Touch and drag blue anchor nodes and red midpoints directly on the canvas with your finger to tweak any curve.",
                icon = Icons.Default.Gesture,
                badge = "Step 2 of 3"
            ),
            TutorialStep(
                title = "Custom Physics & Export",
                subtitle = "Apply multi-stop gradients, outer/inner shadow, and noise grain, then export crisp SVG or XML files.",
                icon = Icons.Default.AutoAwesome,
                badge = "Step 3 of 3"
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }

    fun finishTutorial() {
        val prefs = context.getSharedPreferences("zenico_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_tutorial", true).apply()
        onDismiss()
    }

    Dialog(onDismissRequest = { finishTutorial() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = dialogBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF000000))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(
                    targetState = currentStepIndex,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "TutorialSlide"
                ) { index ->
                    val step = steps[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = brandBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = step.badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E1E24) else Color(0xFFECECEF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = brandBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = step.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = step.subtitle,
                            fontSize = 13.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { idx ->
                        val active = idx == currentStepIndex
                        Box(
                            modifier = Modifier
                                .size(width = if (active) 20.dp else 6.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(if (active) brandBlue else (if (isDark) Color(0xFF333338) else Color(0xFFD4D4D8)))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { finishTutorial() }) {
                        Text("Skip", color = textMuted, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                            } else {
                                finishTutorial()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                    ) {
                        Text(
                            if (currentStepIndex < steps.size - 1) "Next" else "Get Started",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
