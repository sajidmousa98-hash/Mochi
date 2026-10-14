package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MochiColorScheme = lightColorScheme(
  primary = MochiSakuraDeep,
  onPrimary = Color.White,
  primaryContainer = MochiSakuraLight,
  onPrimaryContainer = MochiDarkBrown,
  secondary = MochiLavenderDeep,
  onSecondary = Color.White,
  secondaryContainer = MochiLavenderLight,
  onSecondaryContainer = MochiDarkBrown,
  tertiary = MochiMintDeep,
  onTertiary = Color.White,
  tertiaryContainer = MochiMintLight,
  onTertiaryContainer = MochiDarkBrown,
  background = MochiCream,
  onBackground = MochiDarkBrown,
  surface = MochiCardBg,
  onSurface = MochiDarkBrown,
  surfaceVariant = MochiSakuraLight,
  onSurfaceVariant = MochiSoftBrown,
  outline = MochiBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = MochiColorScheme,
    typography = Typography,
    content = content
  )
}
