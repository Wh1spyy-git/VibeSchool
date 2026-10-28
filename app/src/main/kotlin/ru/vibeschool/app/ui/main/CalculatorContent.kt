package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import ru.vibeschool.app.R
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun CalculatorContent(
  snapshot: SchoolSnapshot,
  target: String,
  requiredFives: Int?,
  onTargetChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val subject = snapshot.subjects.firstOrNull()
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(Spacing.medium),
    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
  ) {
    item(key = "header") {
      Text(
        text = stringResource(R.string.calculator_title),
        style = MaterialTheme.typography.headlineMedium,
      )
    }
    item(key = "content") {
      Card(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(Spacing.medium),
          verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
          Text(
            text = subject?.subject ?: stringResource(R.string.calculator_no_subject),
            style = MaterialTheme.typography.titleLarge,
          )
          OutlinedTextField(
            value = target,
            onValueChange = onTargetChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.calculator_target_label)) },
            supportingText = { Text(stringResource(R.string.calculator_target_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
          )
          Text(
            text = when (requiredFives) {
              null -> stringResource(R.string.calculator_impossible)
              0 -> stringResource(R.string.calculator_already_reached)
              else -> stringResource(R.string.calculator_result, requiredFives)
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
    }
  }
}
