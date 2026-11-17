package com.runeicons.app.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    val textPrimary = if (isDark) Color(0xFFF4F4F5) else Color(0xFF09090B)
    val textMuted = if (isDark) Color(0xFF8E8E93) else Color(0xFF71717A)
    val cardBg = if (isDark) Color(0xFF16161C) else Color(0xFFF5F5F8)
    val borderColor = if (isDark) Color(0xFF272730) else Color(0xFFE2E2E8)
    val brandBlue = Color(0xFF3859FD)

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link: $url", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header: Zenico + Close Icon at Top Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Zenico",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "Vector Studio & Icon Engine v1.0",
                    fontSize = 11.sp,
                    color = textMuted
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF222228) else Color(0xFFEDEDF2))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(15.dp),
                    tint = textPrimary
                )
            }
        }

        // 2. Developer Information Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Developed by",
                        fontSize = 11.sp,
                        color = textMuted
                    )
                    Text(
                        text = "NoishiXzen",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandBlue
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Email",
                        fontSize = 11.sp,
                        color = textMuted
                    )
                    Text(
                        text = "NoishiXzen@gmail.com",
                        fontSize = 11.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 3. Material 3 Action Buttons linking to GitHub, Website, and Discord
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // GitHub Button
            OutlinedButton(
                onClick = { openUrl("https://github.com/pushpajit-dev") },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = cardBg,
                    contentColor = textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp), tint = brandBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GitHub: pushpajit-dev",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp), tint = textMuted)
            }

            // Website Button
            OutlinedButton(
                onClick = { openUrl("https://aivorygen.netlify.app") },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = cardBg,
                    contentColor = textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = brandBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Website: aivorygen.netlify.app",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp), tint = textMuted)
            }

            // Discord Button
            OutlinedButton(
                onClick = { openUrl("https://discord.gg/Dcg2GreJ") },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = cardBg,
                    contentColor = textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF5865F2))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Discord: Join Server",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp), tint = textMuted)
            }

            // Send Feedback Button
            Button(
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:NoishiXzen@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "[Zenico App Feedback]")
                    }
                    try {
                        context.startActivity(emailIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Contact: NoishiXzen@gmail.com", Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Send Feedback",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
