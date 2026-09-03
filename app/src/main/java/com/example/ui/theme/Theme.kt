package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = ObsidianBg,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldAccent,
    onSecondary = ObsidianBg,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    tertiary = PurpleBoost,
    background = ObsidianBg,
    surface = ObsidianSurface,
    surfaceVariant = ObsidianCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = ObsidianBorder,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = ObsidianBg,
    secondary = GoldDark,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = ObsidianBg,
    tertiary = PurpleBoost,
    background = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    surface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    onBackground = androidx.compose.ui.graphics.Color(0xFF0F172A),
    onSurface = androidx.compose.ui.graphics.Color(0xFF0F172A),
    outline = androidx.compose.ui.graphics.Color(0xFFCBD5E1),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to rich modern dark rewards style
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
