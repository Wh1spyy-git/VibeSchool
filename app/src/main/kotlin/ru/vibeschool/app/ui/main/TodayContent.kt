package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.vibeschool.app.R
import ru.vibeschool.app.data.Lesson
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.ui.theme.Spacing

@Composable
fun TodayContent(snapshot: SchoolSnapshot, modifier: Modifier = Modifier) {
  LazyColumn(
    modifier = modifier,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.medium),
    verticalArrangement = Arrangement.spacedBy(Spacing.small),
  ) {
    item(key = "header") {
      Column(modifier = Modifier.padding(bottom = Spacing.small)) {
        Text(
          text = stringResource(R.string.today_title),
          style = MaterialTheme.typography.headlineMedium,
        )
        Text(
          text = stringResource(R.string.today_class_quarter, snapshot.className, snapshot.quarterName),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    item(key = "timer") { LessonTimerCard() }
    items(snapshot.lessons, key = Lesson::id) { lesson -> LessonCard(lesson) }
  }
}

@Composable
private fun LessonTimerCard(modifier: Modifier = Modifier) {
  Card(modifier = modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(Spacing.medium)) {
      Text(
        text = stringResource(R.string.timer_next_lesson),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
      )
      Text(text = stringResource(R.string.timer_demo_value), style = MaterialTheme.typography.displaySmall)
      Text(
        text = stringResource(R.string.timer_demo_hint),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun LessonCard(lesson: Lesson, modifier: Modifier = Modifier) {
  Card(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(Spacing.medium),
      horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
      Text(text = lesson.number.toString(), style = MaterialTheme.typography.titleLarge)
      Column(modifier = Modifier.weight(1f)) {
        Text(text = lesson.subject, style = MaterialTheme.typography.titleMedium)
        Text(
          text = stringResource(R.string.lesson_time, lesson.startTime, lesson.endTime),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        lesson.homework?.let { homework ->
          Text(text = stringResource(R.string.lesson_homework, homework))
        }
      }
      lesson.room?.let { room -> Text(text = stringResource(R.string.lesson_room, room)) }
    }
  }
}
