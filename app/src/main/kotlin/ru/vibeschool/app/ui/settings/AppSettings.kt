package ru.vibeschool.app.ui.settings

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { System, Light, Dark }

enum class AccentPalette { Ocean, Mint, Violet, Sunset }

enum class CornerStyle { Compact, Balanced, Rounded }

data class AppSettings(
  val themeMode: ThemeMode = ThemeMode.System,
  val accentPalette: AccentPalette = AccentPalette.Ocean,
  val cornerStyle: CornerStyle = CornerStyle.Balanced,
  val dynamicColor: Boolean = false,
  val compactLayout: Boolean = false,
  val animationsEnabled: Boolean = true,
  val showRooms: Boolean = true,
  val showHomework: Boolean = true,
)

class SettingsRepository(context: Context) {
  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
  private val _settings = MutableStateFlow(readSettings())
  val settings: StateFlow<AppSettings> = _settings.asStateFlow()

  fun update(transform: (AppSettings) -> AppSettings) {
    val updated = transform(_settings.value)
    _settings.value = updated
    preferences.edit {
      putString(KEY_THEME, updated.themeMode.name)
      putString(KEY_PALETTE, updated.accentPalette.name)
      putString(KEY_CORNERS, updated.cornerStyle.name)
      putBoolean(KEY_DYNAMIC_COLOR, updated.dynamicColor)
      putBoolean(KEY_COMPACT, updated.compactLayout)
      putBoolean(KEY_ANIMATIONS, updated.animationsEnabled)
      putBoolean(KEY_ROOMS, updated.showRooms)
      putBoolean(KEY_HOMEWORK, updated.showHomework)
    }
  }

  private fun readSettings() = AppSettings(
    themeMode = preferences.enumValue(KEY_THEME, ThemeMode.System),
    accentPalette = preferences.enumValue(KEY_PALETTE, AccentPalette.Ocean),
    cornerStyle = preferences.enumValue(KEY_CORNERS, CornerStyle.Balanced),
    dynamicColor = preferences.getBoolean(KEY_DYNAMIC_COLOR, false),
    compactLayout = preferences.getBoolean(KEY_COMPACT, false),
    animationsEnabled = preferences.getBoolean(KEY_ANIMATIONS, true),
    showRooms = preferences.getBoolean(KEY_ROOMS, true),
    showHomework = preferences.getBoolean(KEY_HOMEWORK, true),
  )

  private inline fun <reified T : Enum<T>> android.content.SharedPreferences.enumValue(
    key: String,
    fallback: T,
  ): T = getString(key, null)?.let { stored -> enumValues<T>().firstOrNull { it.name == stored } } ?: fallback

  private companion object {
    const val PREFERENCES_NAME = "vibeschool_appearance"
    const val KEY_THEME = "theme"
    const val KEY_PALETTE = "palette"
    const val KEY_CORNERS = "corners"
    const val KEY_DYNAMIC_COLOR = "dynamic_color"
    const val KEY_COMPACT = "compact"
    const val KEY_ANIMATIONS = "animations"
    const val KEY_ROOMS = "rooms"
    const val KEY_HOMEWORK = "homework"
  }
}
