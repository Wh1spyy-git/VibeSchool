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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import ru.vibeschool.app.R
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun CalculatorContent(
  snapshot: SchoolSnapshot,
  selectedSubjectId: String?,
  target: String,
  requiredFives: Int?,
  onSubjectSelected: (String) -> Unit,
  onTargetChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val subject = snapshot.subjects.firstOrNull { it.id == selectedSubjectId }
    ?: snapshot.subjects.firstOrNull()
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
      if (snapshot.subjects.isEmpty()) {
        EmptyStateCard(
          icon = Icons.Outlined.Calculate,
          title = stringResource(R.string.calculator_empty_title),
          description = stringResource(R.string.calculator_empty_description),
        )
      } else Card(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(Spacing.medium),
          verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
          Text(
            text = stringResource(R.string.calculator_subject_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          SubjectSelector(
            subjects = snapshot.subjects.map { it.id to it.subject },
            selectedSubjectId = subject?.id,
            onSubjectSelected = onSubjectSelected,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectSelector(
  subjects: List<Pair<String, String>>,
  selectedSubjectId: String?,
  onSubjectSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  val selectedName = subjects.firstOrNull { it.first == selectedSubjectId }?.second
    ?: stringResource(R.string.calculator_no_subject)
  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    modifier = modifier.fillMaxWidth(),
  ) {
    OutlinedTextField(
      value = selectedName,
      onValueChange = {},
      readOnly = true,
      modifier = Modifier
        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
        .fillMaxWidth(),
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      singleLine = true,
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      subjects.forEach { (id, name) ->
        DropdownMenuItem(
          text = { Text(name) },
          onClick = {
            onSubjectSelected(id)
            expanded = false
          },
        )
      }
    }
  }
}
