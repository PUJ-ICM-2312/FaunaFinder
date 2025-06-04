package com.example.faunafinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun FloatingLabel(text: String, isActive: Boolean) {
    if (isActive) {
        val isDark = isSystemInDarkTheme()
        val backgroundColor = if (isDark) Color(0xFF4CAF50) else Color(0xFFB2FFCC)
        val borderColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RectangleShape)
                .border(2.dp, borderColor, shape = RectangleShape)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
    }
}
