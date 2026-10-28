package ru.vibeschool.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import ru.vibeschool.app.ui.settings.SettingsRepository
import ru.vibeschool.app.ui.theme.VibeSchoolTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    val settingsRepository = SettingsRepository(applicationContext)
    setContent {
      val settings by settingsRepository.settings.collectAsStateWithLifecycle()
      VibeSchoolTheme(settings = settings) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation(settingsRepository = settingsRepository)
        }
      }
    }
  }
}
