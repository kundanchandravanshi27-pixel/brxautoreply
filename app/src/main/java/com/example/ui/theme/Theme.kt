package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BizDarkColorScheme = darkColorScheme(
    primary = BizPrimary,
    secondary = BizSecondary,
    background = BizDarkBackground,
    surface = BizSurfaceCard,
    onPrimary = BizTextPrimary,
    onSecondary = BizTextPrimary,
    onBackground = BizTextPrimary,
    onSurface = BizTextPrimary,
    error = BizError
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BizDarkColorScheme,
        typography = Typography,
        content = content
    )
}
