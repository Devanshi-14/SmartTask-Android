package com.demo.smarttask.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5269B8),
    onPrimary = Color.White,

    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),

    background = Color(0xFFF8F9FD),
    onBackground = Color(0xFF1B1B1F),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),

    surfaceVariant = Color(0xFFE8EAF3),
    onSurfaceVariant = Color(0xFF5F6368),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)


@Composable
fun SmartTaskTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
