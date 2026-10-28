package ru.vibeschool.app.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ru.vibeschool.app.ui.theme.VibeSchoolTheme

/** UI tests for [ru.vibeschool.app.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun todayScreen_displaysSchedule() {
    composeTestRule.setContent { VibeSchoolTheme { MainScreen() } }

    composeTestRule.onNodeWithText("Расписание на сегодня").assertExists()
  }
}
