package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.vibeschool.app.R
import ru.vibeschool.app.data.Grade
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.data.SubjectGrades
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun DiaryContent(
  snapshot: SchoolSnapshot,
  averagesBySubjectId: Map<String, Double>,
  compactLayout: Boolean,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(Spacing.medium),
    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
  ) {
    item(key = "header") {
      Column {
        Text(
          text = stringResource(R.string.diary_title),
          style = MaterialTheme.typography.headlineMedium,
        )
        if (snapshot.quarterName.isNotBlank()) {
          Text(
            text = snapshot.quarterName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
    if (snapshot.subjects.isEmpty()) {
      item(key = "empty-diary") {
        EmptyStateCard(
          icon = Icons.AutoMirrored.Outlined.MenuBook,
          title = stringResource(R.string.diary_empty_title),
          description = stringResource(R.string.diary_empty_description),
        )
      }
    }
    items(snapshot.subjects, key = SubjectGrades::id) { subject ->
      SubjectGradeCard(
        subject = subject,
        average = averagesBySubjectId[subject.id] ?: 0.0,
        compactLayout = compactLayout,
        modifier = Modifier.animateItem(),
      )
    }
  }
}

@Composable
private fun SubjectGradeCard(
  subject: SubjectGrades,
  average: Double,
  compactLayout: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(if (compactLayout) Spacing.mediumSmall else Spacing.medium),
      verticalArrangement = Arrangement.spacedBy(if (compactLayout) Spacing.small else Spacing.medium),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = subject.subject, style = MaterialTheme.typography.titleLarge)
        Text(
          text = stringResource(R.string.diary_average, average),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
      FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
        subject.grades.forEach { grade -> GradeBadge(grade) }
      }
      subject.grades.forEach { grade ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = grade.workType, style = MaterialTheme.typography.bodyMedium)
            Text(
              text = grade.date,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Text(text = stringResource(R.string.grade_weight, grade.weight))
        }
      }
    }
  }
}

@Composable
private fun GradeBadge(grade: Grade, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.primaryContainer,
  ) {
    Text(
      text = grade.value.toString(),
      modifier = Modifier.padding(horizontal = Spacing.medium, vertical = Spacing.small),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  }
}
