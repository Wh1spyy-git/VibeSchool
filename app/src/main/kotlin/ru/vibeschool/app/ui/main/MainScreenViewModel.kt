package ru.vibeschool.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.vibeschool.app.data.SchoolRepository
import ru.vibeschool.app.data.SchoolSnapshot
import ru.vibeschool.app.domain.GradeCalculator

enum class MainSection { Today, Diary, Calculator }

data class MainScreenState(
  val snapshot: SchoolSnapshot? = null,
  val selectedSection: MainSection = MainSection.Today,
  val isLoading: Boolean = true,
  val errorMessage: String? = null,
  val calculatorTarget: String = "4.50",
  val requiredFives: Int? = null,
  val averagesBySubjectId: Map<String, Double> = emptyMap(),
)

class MainScreenViewModel(repository: SchoolRepository) : ViewModel() {
  private val selectedSection = MutableStateFlow(MainSection.Today)
  private val calculatorTarget = MutableStateFlow("4.50")

  val uiState: StateFlow<MainScreenState> = combine(
    repository.snapshot,
    selectedSection,
    calculatorTarget,
  ) { snapshot, section, targetText ->
      val target = targetText.replace(',', '.').toDoubleOrNull()
      val grades = snapshot.subjects.firstOrNull()?.grades.orEmpty()
      val requiredFives = target?.let { value ->
        runCatching { GradeCalculator.requiredGrades(grades, value) }.getOrNull()
      }
      val averages = snapshot.subjects.associate { subject ->
        subject.id to (GradeCalculator.weightedAverage(subject.grades) ?: 0.0)
      }
      MainScreenState(
        snapshot = snapshot,
        selectedSection = section,
        isLoading = false,
        calculatorTarget = targetText,
        requiredFives = requiredFives,
        averagesBySubjectId = averages,
      )
    }
    .catch { throwable ->
      emit(MainScreenState(isLoading = false, errorMessage = throwable.message))
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = MainScreenState(),
    )

  fun selectSection(section: MainSection) {
    selectedSection.value = section
  }

  fun updateCalculatorTarget(value: String) {
    calculatorTarget.value = value.take(4)
  }
}
