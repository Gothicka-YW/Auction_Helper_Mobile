package com.gothickayw.auctionhelper.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = darkColorScheme(
    primary = Color(0xFFFF47BD),
    secondary = Color(0xFF9A67FF),
    tertiary = Color(0xFFFF79D3),
    background = Color(0xFF09070D),
    surface = Color(0xFF181020),
    surfaceVariant = Color(0xFF21162B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF7EFF9),
    onSurface = Color(0xFFF7EFF9),
    onSurfaceVariant = Color(0xFFB8A7C0),
    error = Color(0xFFFF6D86),
)

@Composable
fun AuctionTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
