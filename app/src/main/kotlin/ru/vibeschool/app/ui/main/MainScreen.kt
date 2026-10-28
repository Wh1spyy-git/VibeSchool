package ru.vibeschool.app.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.vibeschool.app.R
import ru.vibeschool.app.data.DemoSchoolRepository
import ru.vibeschool.app.data.demoSchoolSnapshot
import ru.vibeschool.app.ui.theme.VibeSchoolTheme

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DemoSchoolRepository()) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  MainScreen(
    state = state,
    onSectionSelected = viewModel::selectSection,
    onCalculatorTargetChanged = viewModel::updateCalculatorTarget,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
  state: MainScreenState,
  onSectionSelected: (MainSection) -> Unit,
  onCalculatorTargetChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) })
    },
    bottomBar = {
      NavigationBar {
        MainSection.entries.forEach { section ->
          NavigationBarItem(
            selected = state.selectedSection == section,
            onClick = { onSectionSelected(section) },
            icon = { Text(stringResource(section.symbolRes)) },
            label = { Text(stringResource(section.labelRes)) },
          )
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      when {
        state.isLoading -> CircularProgressIndicator()
        state.errorMessage != null -> Text(
          text = stringResource(R.string.common_loading_error),
          color = MaterialTheme.colorScheme.error,
        )
        state.snapshot != null -> Column(modifier = Modifier.fillMaxSize()) {
          if (state.snapshot.isOffline) OfflineBanner()
          when (state.selectedSection) {
            MainSection.Today -> TodayContent(state.snapshot)
            MainSection.Diary -> DiaryContent(
              snapshot = state.snapshot,
              averagesBySubjectId = state.averagesBySubjectId,
            )
            MainSection.Calculator -> CalculatorContent(
              snapshot = state.snapshot,
              target = state.calculatorTarget,
              requiredFives = state.requiredFives,
              onTargetChanged = onCalculatorTargetChanged,
            )
          }
        }
      }
    }
  }
}

private val MainSection.labelRes: Int
  get() = when (this) {
    MainSection.Today -> R.string.navigation_today
    MainSection.Diary -> R.string.navigation_diary
    MainSection.Calculator -> R.string.navigation_calculator
  }

private val MainSection.symbolRes: Int
  get() = when (this) {
    MainSection.Today -> R.string.navigation_today_symbol
    MainSection.Diary -> R.string.navigation_diary_symbol
    MainSection.Calculator -> R.string.navigation_calculator_symbol
  }

@Preview(name = "VibeSchool", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MainScreenPreview() {
  VibeSchoolTheme {
    MainScreen(
      state = MainScreenState(snapshot = demoSchoolSnapshot(), isLoading = false),
      onSectionSelected = {},
      onCalculatorTargetChanged = {},
    )
  }
}
