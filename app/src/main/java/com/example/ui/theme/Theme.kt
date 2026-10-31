package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryLight,
  onPrimary = PrimaryNavy,
  primaryContainer = PrimaryBlue,
  onPrimaryContainer = Color.White,
  secondary = SecondaryDark,
  onSecondary = Color.Black,
  secondaryContainer = SecondaryContainerOrange,
  onSecondaryContainer = Color.White,
  tertiary = TertiaryVariant,
  onTertiary = Color.Black,
  tertiaryContainer = TertiaryContainerGreen,
  onTertiaryContainer = TertiaryFixedLight,
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceContainerDark,
  onSurfaceVariant = TextSecondaryDark,
  surfaceContainerLow = SurfaceContainerLowDark,
  surfaceContainer = SurfaceContainerDark,
  surfaceContainerHigh = SurfaceContainerHighDark,
  outline = OutlineVariantBorder,
  outlineVariant = OutlineBorder
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryNavy,
  onPrimary = Color.White,
  primaryContainer = PrimaryBlue,
  onPrimaryContainer = Color.White,
  secondary = SecondaryAmber,
  onSecondary = Color.White,
  secondaryContainer = SecondaryContainerOrange,
  onSecondaryContainer = Color.White,
  tertiary = TertiaryGreen,
  onTertiary = Color.White,
  tertiaryContainer = TertiaryContainerGreen,
  onTertiaryContainer = TertiaryFixedLight,
  background = BackgroundLight,
  onBackground = TextPrimary,
  surface = SurfaceLight,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceContainerHigh,
  onSurfaceVariant = TextSecondary,
  surfaceContainerLowest = SurfaceContainerLowest,
  surfaceContainerLow = SurfaceContainerLow,
  surfaceContainer = SurfaceContainerDefault,
  surfaceContainerHigh = SurfaceContainerHigh,
  surfaceContainerHighest = SurfaceContainerHighest,
  outline = OutlineBorder,
  outlineVariant = OutlineVariantBorder
)

@Composable
fun JanaspandanaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
