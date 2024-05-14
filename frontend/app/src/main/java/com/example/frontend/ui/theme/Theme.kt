package com.example.frontend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

object UiConfig {
    val sideScreenPadding = 12.dp
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.BrandBlue,
    onPrimary = DarkPalette.BGPrimary,
    secondary = DarkPalette.BrandBlue10,
    onSecondary = DarkPalette.BrandBlue,
    background = DarkPalette.BGPrimary,
    onBackground = DarkPalette.TextPrimary,
    surface = DarkPalette.BGPrimary,
    inverseSurface = DarkPalette.BGSecondary,
    onSurface = DarkPalette.TextPrimary,
    onSurfaceVariant = DarkPalette.TextSecondary,
    inverseOnSurface = DarkPalette.TextBrandBlue,
    outline = DarkPalette.Outline,
    outlineVariant = DarkPalette.Outline,
    onError = DarkPalette.SystemError,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPalette.BrandBlue,
    onPrimary = LightPalette.BGPrimary,
    secondary = LightPalette.BrandBlue10,
    onSecondary = LightPalette.BrandBlue,
    background = LightPalette.BGPrimary,
    onBackground = LightPalette.TextPrimary,
    surface = LightPalette.BGPrimary,
    inverseSurface = LightPalette.BGSecondary,
    onSurface = LightPalette.TextPrimary,
    onSurfaceVariant = LightPalette.TextSecondary,
    inverseOnSurface = LightPalette.TextBrandBlue,
    outline = LightPalette.Outline,
    outlineVariant = LightPalette.Outline,
    onError = LightPalette.SystemError,
)

@Composable
fun FrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoppinsTypography,
        shapes = Shapes,
        content = content
    )
}

object Colors {
    val topBarContainer: Color
        @Composable
        get() {
            return if (isSystemInDarkTheme()) {
                DarkPalette.BrandBlue10
            } else {
                LightPalette.BrandBlue10
            }
        }
    val navBarContainer: Color
        @Composable
        get() {
            return if (isSystemInDarkTheme()) {
                DarkPalette.BrandBlue10
            } else {
                LightPalette.BrandBlue10
            }
        }
}