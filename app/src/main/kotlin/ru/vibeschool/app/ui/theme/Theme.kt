package ru.vibeschool.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.vibeschool.app.ui.settings.AccentPalette
import ru.vibeschool.app.ui.settings.AppSettings
import ru.vibeschool.app.ui.settings.CornerStyle
import ru.vibeschool.app.ui.settings.ThemeMode

private data class Palette(val light: Color, val dark: Color, val container: Color)

private fun AccentPalette.colors() = when (this) {
  AccentPalette.Ocean -> Palette(Color(0xFF00658A), Color(0xFF7BD0FA), Color(0xFFC4E7FF))
  AccentPalette.Mint -> Palette(Color(0xFF006B58), Color(0xFF5DDBB8), Color(0xFF7BF8D3))
  AccentPalette.Violet -> Palette(Color(0xFF6750A4), Color(0xFFCFBCFF), Color(0xFFE9DDFF))
  AccentPalette.Sunset -> Palette(Color(0xFF9C4230), Color(0xFFFFB4A3), Color(0xFFFFDAD2))
}

@Composable
fun VibeSchoolTheme(
  settings: AppSettings = AppSettings(),
  content: @Composable () -> Unit,
) {
  val darkTheme = when (settings.themeMode) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
  }
  val context = LocalContext.current
  val accent = settings.accentPalette.colors()
  val colorScheme = when {
    settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
    settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
    darkTheme -> darkColorScheme(
      primary = accent.dark,
      onPrimary = Color(0xFF003548),
      primaryContainer = accent.light,
      onPrimaryContainer = Color(0xFF001F2A),
      surface = Color(0xFF101417),
      surfaceContainer = Color(0xFF1B2024),
      surfaceContainerHigh = Color(0xFF252A2E),
    )
    else -> lightColorScheme(
      primary = accent.light,
      onPrimary = Color.White,
      primaryContainer = accent.container,
      onPrimaryContainer = Color(0xFF001F2A),
      surface = Color(0xFFF7F9FC),
      surfaceContainer = Color(0xFFEDF1F5),
      surfaceContainerHigh = Color(0xFFE6EBEF),
    )
  }
  val corner = when (settings.cornerStyle) {
    CornerStyle.Compact -> 8.dp
    CornerStyle.Balanced -> 16.dp
    CornerStyle.Rounded -> 28.dp
  }
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = Shapes(
      extraSmall = RoundedCornerShape(corner / 2),
      small = RoundedCornerShape(corner),
      medium = RoundedCornerShape(corner),
      large = RoundedCornerShape(corner + 4.dp),
      extraLarge = RoundedCornerShape(corner + 8.dp),
    ),
    content = content,
  )
}

