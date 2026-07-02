package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WhatsAppAccent,
    onPrimary = Color.Black,
    secondary = WhatsAppBubbleSent,
    onSecondary = Color.White,
    tertiary = WhatsAppBubbleReceived,
    onTertiary = Color.White,
    background = WhatsAppBg,
    onBackground = WhatsAppTextPrimary,
    surface = WhatsAppSurface,
    onSurface = WhatsAppTextPrimary,
    surfaceVariant = WhatsAppSurface,
    onSurfaceVariant = WhatsAppTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme WhatsApp-style
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve brand style
    content: @Composable () -> Unit
) {
    // We enforce the customized WhatsApp Dark Color Scheme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
