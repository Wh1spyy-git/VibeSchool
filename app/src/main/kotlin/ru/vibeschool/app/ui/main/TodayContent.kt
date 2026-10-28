package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.vibeschool.app.R
import ru.vibeschool.app.data.Lesson
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.ui.theme.Spacing
import ru.vibeschool.app.ui.settings.AppSettings

@Composable
fun TodayContent(
  snapshot: SchoolSnapshot,
  settings: AppSettings,
  hasSgoSession: Boolean,
  onConnectSgo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val padding = if (settings.compactLayout) Spacing.small else Spacing.medium
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
    if (snapshot.isDemoData) {
      item(key = "demo") {
        DemoDataCard(hasSgoSession = hasSgoSession, onConnectSgo = onConnectSgo)
      }
    }
    item(key = "timer") { LessonTimerCard() }
    items(snapshot.lessons, key = Lesson::id) { lesson ->
      LessonCard(
        lesson = lesson,
        contentPadding = padding,
        showRoom = settings.showRooms,
        showHomework = settings.showHomework,
        modifier = Modifier.animateItem(),
      )
    }
  }
}

@Composable
private fun DemoDataCard(
  hasSgoSession: Boolean,
  onConnectSgo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(modifier = modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(Spacing.medium),
      verticalArrangement = Arrangement.spacedBy(Spacing.mediumSmall),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(Spacing.mediumSmall)) {
        Icon(
          Icons.Outlined.Info,
          contentDescription = null,
          modifier = Modifier.size(24.dp),
          tint = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.weight(1f)) {
          Text(
            stringResource(if (hasSgoSession) R.string.auth_connected_title else R.string.demo_title),
            style = MaterialTheme.typography.titleMedium,
          )
          Text(
            stringResource(
              if (hasSgoSession) R.string.auth_connected_demo_description else R.string.demo_description,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      Button(onClick = onConnectSgo, modifier = Modifier.fillMaxWidth()) {
        Text(
          stringResource(if (hasSgoSession) R.string.auth_login_again else R.string.auth_login_button),
        )
      }
    }
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
private fun LessonCard(
  lesson: Lesson,
  contentPadding: androidx.compose.ui.unit.Dp,
  showRoom: Boolean,
  showHomework: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(contentPadding),
      horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
      Text(text = lesson.number.toString(), style = MaterialTheme.typography.titleLarge)
      Column(modifier = Modifier.weight(1f)) {
        Text(text = lesson.subject, style = MaterialTheme.typography.titleMedium)
        Text(
          text = stringResource(R.string.lesson_time, lesson.startTime, lesson.endTime),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        lesson.homework?.takeIf { showHomework }?.let { homework ->
          Text(text = stringResource(R.string.lesson_homework, homework))
        }
      }
      lesson.room?.takeIf { showRoom }?.let { room -> Text(text = stringResource(R.string.lesson_room, room)) }
    }
  }
}
