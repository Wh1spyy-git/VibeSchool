package ru.vibeschool.app.ui.settings

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material.icons.outlined.ViewCompact
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ru.vibeschool.app.R
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun SettingsContent(
  settings: AppSettings,
  hasSgoSession: Boolean,
  onSettingsChanged: (AppSettings) -> Unit,
  onConnectSgo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(Spacing.medium),
    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
  ) {
    item("title") {
      Column(verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)) {
        Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
        Text(
          stringResource(R.string.settings_subtitle),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    item("account") {
      SettingsCard(
        icon = Icons.Outlined.AccountCircle,
        title = stringResource(R.string.settings_account_title),
        subtitle = stringResource(
          if (hasSgoSession) R.string.settings_account_connected else R.string.settings_account_demo,
        ),
      ) {
        Button(onClick = onConnectSgo, modifier = Modifier.fillMaxWidth()) {
          Text(
            stringResource(if (hasSgoSession) R.string.auth_login_again else R.string.auth_login_button),
          )
        }
      }
    }
    item("theme") {
      SettingsCard(
        icon = Icons.Outlined.DarkMode,
        title = stringResource(R.string.settings_theme_title),
        subtitle = stringResource(R.string.settings_theme_subtitle),
      ) {
        ChoiceRow(
          choices = ThemeMode.entries,
          selected = settings.themeMode,
          label = { stringResource(it.labelRes) },
          onSelected = { onSettingsChanged(settings.copy(themeMode = it)) },
        )
      }
    }
    item("palette") {
      SettingsCard(
        icon = Icons.Outlined.ColorLens,
        title = stringResource(R.string.settings_palette_title),
        subtitle = stringResource(R.string.settings_palette_subtitle),
      ) {
        ChoiceRow(
          choices = AccentPalette.entries,
          selected = settings.accentPalette,
          label = { stringResource(it.labelRes) },
          onSelected = { onSettingsChanged(settings.copy(accentPalette = it)) },
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          SettingsSwitch(
            title = stringResource(R.string.settings_dynamic_color),
            checked = settings.dynamicColor,
            onCheckedChange = { onSettingsChanged(settings.copy(dynamicColor = it)) },
          )
        }
      }
    }
    item("shape") {
      SettingsCard(
        icon = Icons.Outlined.SpaceDashboard,
        title = stringResource(R.string.settings_shape_title),
        subtitle = stringResource(R.string.settings_shape_subtitle),
      ) {
        ChoiceRow(
          choices = CornerStyle.entries,
          selected = settings.cornerStyle,
          label = { stringResource(it.labelRes) },
          onSelected = { onSettingsChanged(settings.copy(cornerStyle = it)) },
        )
      }
    }
    item("layout") {
      SettingsCard(
        icon = Icons.Outlined.ViewCompact,
        title = stringResource(R.string.settings_layout_title),
        subtitle = stringResource(R.string.settings_layout_subtitle),
      ) {
        SettingsSwitch(
          title = stringResource(R.string.settings_compact),
          checked = settings.compactLayout,
          onCheckedChange = { onSettingsChanged(settings.copy(compactLayout = it)) },
        )
        SettingsSwitch(
          title = stringResource(R.string.settings_show_rooms),
          checked = settings.showRooms,
          onCheckedChange = { onSettingsChanged(settings.copy(showRooms = it)) },
        )
        SettingsSwitch(
          title = stringResource(R.string.settings_show_homework),
          checked = settings.showHomework,
          onCheckedChange = { onSettingsChanged(settings.copy(showHomework = it)) },
        )
      }
    }
    item("motion") {
      SettingsCard(
        icon = Icons.Outlined.Animation,
        title = stringResource(R.string.settings_motion_title),
        subtitle = stringResource(R.string.settings_motion_subtitle),
      ) {
        SettingsSwitch(
          title = stringResource(R.string.settings_animations),
          checked = settings.animationsEnabled,
          onCheckedChange = { onSettingsChanged(settings.copy(animationsEnabled = it)) },
        )
      }
    }
  }
}

@Composable
private fun SettingsCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Card(modifier = modifier.fillMaxWidth().animateContentSize()) {
    Column(
      modifier = Modifier.padding(Spacing.medium),
      verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.weight(1f)) {
          Text(title, style = MaterialTheme.typography.titleMedium)
          Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      content()
    }
  }
}

@Composable
private fun <T> ChoiceRow(
  choices: List<T>,
  selected: T,
  label: @Composable (T) -> String,
  onSelected: (T) -> Unit,
) {
  androidx.compose.foundation.layout.FlowRow(
    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
    verticalArrangement = Arrangement.spacedBy(Spacing.small),
  ) {
    choices.forEach { choice ->
      FilterChip(
        selected = choice == selected,
        onClick = { onSelected(choice) },
        label = { Text(label(choice)) },
      )
    }
  }
}

@Composable
private fun SettingsSwitch(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

private val ThemeMode.labelRes: Int
  get() = when (this) {
    ThemeMode.System -> R.string.settings_theme_system
    ThemeMode.Light -> R.string.settings_theme_light
    ThemeMode.Dark -> R.string.settings_theme_dark
  }

private val AccentPalette.labelRes: Int
  get() = when (this) {
    AccentPalette.Ocean -> R.string.settings_palette_ocean
    AccentPalette.Mint -> R.string.settings_palette_mint
    AccentPalette.Violet -> R.string.settings_palette_violet
    AccentPalette.Sunset -> R.string.settings_palette_sunset
  }

private val CornerStyle.labelRes: Int
  get() = when (this) {
    CornerStyle.Compact -> R.string.settings_shape_compact
    CornerStyle.Balanced -> R.string.settings_shape_balanced
    CornerStyle.Rounded -> R.string.settings_shape_rounded
  }
