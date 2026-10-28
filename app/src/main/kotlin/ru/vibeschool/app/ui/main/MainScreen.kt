package ru.vibeschool.app.ui.main

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.net.toUri
import ru.vibeschool.app.R
import ru.vibeschool.app.data.DemoSchoolRepository
import ru.vibeschool.app.data.demoSchoolSnapshot
import ru.vibeschool.app.ui.settings.AppSettings
import ru.vibeschool.app.ui.settings.SettingsContent
import ru.vibeschool.app.ui.settings.SettingsRepository
import ru.vibeschool.app.ui.theme.VibeSchoolTheme

private const val SGO_URL = "https://netschool.edu22.info/"

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  settingsRepository: SettingsRepository? = null,
) {
  val context = LocalContext.current
  val repository = remember(settingsRepository, context) {
    settingsRepository ?: SettingsRepository(context.applicationContext)
  }
  val viewModel: MainScreenViewModel = viewModel {
    MainScreenViewModel(DemoSchoolRepository(), repository)
  }
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val openSgo = remember(context) {
    { context.startActivity(Intent(Intent.ACTION_VIEW, SGO_URL.toUri())) }
  }
  MainScreen(
    state = state,
    onSectionSelected = viewModel::selectSection,
    onCalculatorTargetChanged = viewModel::updateCalculatorTarget,
    onCalculatorSubjectSelected = viewModel::selectCalculatorSubject,
    onSettingsChanged = viewModel::updateSettings,
    onConnectSgo = openSgo,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
  state: MainScreenState,
  onSectionSelected: (MainSection) -> Unit,
  onCalculatorTargetChanged: (String) -> Unit,
  onCalculatorSubjectSelected: (String) -> Unit,
  onSettingsChanged: (AppSettings) -> Unit,
  onConnectSgo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text(stringResource(state.selectedSection.titleRes)) },
        actions = {
          IconButton(onClick = onConnectSgo) {
            Icon(
              Icons.Outlined.AccountCircle,
              contentDescription = stringResource(R.string.auth_login_button),
            )
          }
        },
      )
    },
    bottomBar = {
      NavigationBar {
        MainSection.entries.forEach { section ->
          NavigationBarItem(
            selected = state.selectedSection == section,
            onClick = { onSectionSelected(section) },
            icon = {
              Icon(
                imageVector = section.icon,
                contentDescription = null,
              )
            },
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
          AnimatedContent(
            targetState = state.selectedSection,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
              if (!state.settings.animationsEnabled) {
                EnterTransition.None togetherWith ExitTransition.None
              } else {
                val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                (slideInHorizontally { it / 7 * direction } + fadeIn()) togetherWith
                  (slideOutHorizontally { -it / 7 * direction } + fadeOut())
              }
            },
            label = "main-section",
          ) { section ->
            when (section) {
              MainSection.Today -> TodayContent(
                snapshot = state.snapshot,
                settings = state.settings,
                onConnectSgo = onConnectSgo,
              )
              MainSection.Diary -> DiaryContent(
                snapshot = state.snapshot,
                averagesBySubjectId = state.averagesBySubjectId,
                compactLayout = state.settings.compactLayout,
              )
              MainSection.Calculator -> CalculatorContent(
                snapshot = state.snapshot,
                selectedSubjectId = state.calculatorSubjectId,
                target = state.calculatorTarget,
                requiredFives = state.requiredFives,
                onSubjectSelected = onCalculatorSubjectSelected,
                onTargetChanged = onCalculatorTargetChanged,
              )
              MainSection.Settings -> SettingsContent(
                settings = state.settings,
                onSettingsChanged = onSettingsChanged,
                onConnectSgo = onConnectSgo,
              )
            }
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
    MainSection.Settings -> R.string.navigation_settings
  }

private val MainSection.titleRes: Int
  get() = when (this) {
    MainSection.Today -> R.string.today_title_short
    MainSection.Diary -> R.string.diary_title
    MainSection.Calculator -> R.string.calculator_title_short
    MainSection.Settings -> R.string.settings_title
  }

private val MainSection.icon: ImageVector
  get() = when (this) {
    MainSection.Today -> Icons.Outlined.CalendarToday
    MainSection.Diary -> Icons.AutoMirrored.Outlined.MenuBook
    MainSection.Calculator -> Icons.Outlined.Calculate
    MainSection.Settings -> Icons.Outlined.Settings
  }

@Preview(name = "VibeSchool", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MainScreenPreview() {
  VibeSchoolTheme {
    MainScreen(
      state = MainScreenState(snapshot = demoSchoolSnapshot(), isLoading = false),
      onSectionSelected = {},
      onCalculatorTargetChanged = {},
      onCalculatorSubjectSelected = {},
      onSettingsChanged = {},
      onConnectSgo = {},
    )
  }
}

@Preview(name = "Calculator", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CalculatorScreenPreview() {
  VibeSchoolTheme {
    MainScreen(
      state = MainScreenState(
        snapshot = demoSchoolSnapshot(),
        selectedSection = MainSection.Calculator,
        calculatorSubjectId = "russian",
        isLoading = false,
      ),
      onSectionSelected = {},
      onCalculatorTargetChanged = {},
      onCalculatorSubjectSelected = {},
      onSettingsChanged = {},
      onConnectSgo = {},
    )
  }
}

@Preview(name = "Settings dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingsScreenPreview() {
  val settings = AppSettings(themeMode = ru.vibeschool.app.ui.settings.ThemeMode.Dark)
  VibeSchoolTheme(settings = settings) {
    MainScreen(
      state = MainScreenState(
        snapshot = demoSchoolSnapshot(),
        selectedSection = MainSection.Settings,
        settings = settings,
        isLoading = false,
      ),
      onSectionSelected = {},
      onCalculatorTargetChanged = {},
      onCalculatorSubjectSelected = {},
      onSettingsChanged = {},
      onConnectSgo = {},
    )
  }
}
