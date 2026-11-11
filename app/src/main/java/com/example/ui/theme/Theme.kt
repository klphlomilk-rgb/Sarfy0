package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SarfyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    paletteName: String = "EMERALD",
    content: @Composable () -> Unit
) {
    val (primaryLight, primaryDark, containerLight, containerDark) = when (paletteName) {
        "OCEAN" -> QuadColor(OceanPrimaryLight, OceanPrimaryDark, OceanPrimaryContainerLight, OceanPrimaryContainerDark)
        "AMBER" -> QuadColor(AmberPrimaryLight, AmberPrimaryDark, AmberPrimaryContainerLight, AmberPrimaryContainerDark)
        "PURPLE" -> QuadColor(PurplePrimaryLight, PurplePrimaryDark, PurplePrimaryContainerLight, PurplePrimaryContainerDark)
        else -> QuadColor(EmeraldPrimaryLight, EmeraldPrimaryDark, EmeraldPrimaryContainerLight, EmeraldPrimaryContainerDark)
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryDark,
            onPrimary = Color.Black,
            primaryContainer = containerDark,
            onPrimaryContainer = Color.White,
            surface = SurfaceDark,
            onSurface = TextPrimaryDark,
            surfaceContainer = SurfaceContainerDark,
            surfaceContainerHigh = SurfaceContainerHighDark,
            background = SurfaceDark,
            onBackground = TextPrimaryDark,
            outline = Color(0xFF3E4844)
        )
    } else {
        lightColorScheme(
            primary = primaryLight,
            onPrimary = Color.White,
            primaryContainer = containerLight,
            onPrimaryContainer = Color(0xFF003730),
            surface = SurfaceLight,
            onSurface = TextPrimaryLight,
            surfaceContainer = SurfaceContainerLight,
            surfaceContainerHigh = SurfaceContainerHighLight,
            background = SurfaceLight,
            onBackground = TextPrimaryLight,
            outline = Color(0xFFCAD3CF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private data class QuadColor(
    val primaryLight: Color,
    val primaryDark: Color,
    val containerLight: Color,
    val containerDark: Color
)
