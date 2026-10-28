package ru.vibeschool.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.vibeschool.app.data.Grade

class GradeCalculatorTest {
  @Test
  fun weightedAverage_usesSumOfWeights() {
    val grades = listOf(grade(value = 5, weight = 3), grade(value = 4, weight = 1))

    assertEquals(4.75, GradeCalculator.weightedAverage(grades) ?: 0.0, 0.001)
  }

  @Test
  fun requiredGrades_returnsMinimumNumberOfFives() {
    val grades = listOf(grade(value = 4, weight = 1), grade(value = 4, weight = 1))

    assertEquals(2, GradeCalculator.requiredGrades(grades, targetAverage = 4.5))
  }

  @Test
  fun requiredGrades_returnsZeroWhenTargetAlreadyReached() {
    val grades = listOf(grade(value = 5, weight = 1))

    assertEquals(0, GradeCalculator.requiredGrades(grades, targetAverage = 4.5))
  }

  @Test
  fun requiredGrades_returnsNullForUnreachablePerfectAverage() {
    val grades = listOf(grade(value = 4, weight = 1))

    assertNull(GradeCalculator.requiredGrades(grades, targetAverage = 5.0))
  }

  private fun grade(value: Int, weight: Int) = Grade(
    id = "$value-$weight",
    value = value,
    weight = weight,
    workType = "Тест",
    date = "Сегодня",
  )
}
