package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CobaltPrimary,
    onPrimary = Color.White,
    primaryContainer = CobaltContainer,
    onPrimaryContainer = OnCobaltContainer,
    secondary = CeladonSecondary,
    onSecondary = Color.White,
    secondaryContainer = CeladonContainer,
    onSecondaryContainer = OnCeladonContainer,
    tertiary = TerracottaAccent,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaContainer,
    onTertiaryContainer = OnTerracottaContainer,
    background = PorcelainCream,
    onBackground = InkCharcoal,
    surface = PorcelainCream,
    onSurface = InkCharcoal,
    surfaceVariant = LinenSurface,
    onSurfaceVariant = GlazeMutedGray,
    outline = ClayOutline,
    outlineVariant = WarmSandCard
)

private val DarkColorScheme = darkColorScheme(
    primary = CobaltLight,
    onPrimary = OnCobaltContainer,
    primaryContainer = CobaltPrimary,
    onPrimaryContainer = CobaltContainer,
    secondary = CeladonLight,
    onSecondary = OnCeladonContainer,
    secondaryContainer = CeladonSecondary,
    onSecondaryContainer = CeladonContainer,
    tertiary = TerracottaLight,
    onTertiary = OnTerracottaContainer,
    tertiaryContainer = TerracottaAccent,
    onTertiaryContainer = TerracottaContainer,
    background = DarkKilnBackground,
    onBackground = DarkPorcelainText,
    surface = DarkKilnSurface,
    onSurface = DarkPorcelainText,
    surfaceVariant = DarkKilnCard,
    onSurfaceVariant = DarkMutedText,
    outline = Color(0xFF3E4756),
    outlineVariant = Color(0xFF2D3643)
)

val CeramicShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun DailyCeramicsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = CeramicShapes,
        content = content
    )
}
