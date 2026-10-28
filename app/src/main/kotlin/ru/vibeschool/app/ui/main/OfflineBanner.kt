package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.vibeschool.app.R
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.errorContainer,
  ) {
    Text(
      text = stringResource(R.string.offline_cache_message),
      modifier = Modifier.padding(Spacing.medium),
      color = MaterialTheme.colorScheme.onErrorContainer,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}
