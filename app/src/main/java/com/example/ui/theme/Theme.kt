package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldAccent,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = EmeraldDark,
    secondaryContainer = Color(0xFF1E3832),
    onSecondaryContainer = GoldLight,
    background = IslamicBackgroundDark,
    surface = IslamicSurfaceDark,
    surfaceVariant = CardSurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF264039),
    outlineVariant = Color(0xFF1A302A)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Color(0xFF3E2D00),
    background = IslamicBackgroundLight,
    surface = IslamicSurfaceLight,
    surfaceVariant = CardSurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFC0D3CD),
    outlineVariant = Color(0xFFDCE7E3)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep tailored Islamic Emerald & Gold brand identity across light & dark modes
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

