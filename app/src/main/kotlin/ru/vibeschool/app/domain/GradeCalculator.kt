package ru.vibeschool.app.domain

import kotlin.math.ceil
import ru.vibeschool.app.data.Grade

object GradeCalculator {
  fun weightedAverage(grades: List<Grade>): Double? {
    val totalWeight = grades.sumOf(Grade::weight)
    if (totalWeight == 0) return null
    val weightedSum = grades.sumOf { it.value * it.weight }
    return weightedSum.toDouble() / totalWeight
  }

  fun requiredGrades(
    grades: List<Grade>,
    targetAverage: Double,
    newGradeValue: Int = 5,
    newGradeWeight: Int = 1,
  ): Int? {
    require(targetAverage in 1.0..5.0)
    require(newGradeValue in 1..5)
    require(newGradeWeight > 0)

    val currentWeight = grades.sumOf(Grade::weight)
    val currentSum = grades.sumOf { it.value * it.weight }
    if (currentWeight == 0) return if (newGradeValue >= targetAverage) 1 else null
    if (currentSum.toDouble() / currentWeight >= targetAverage) return 0

    val gainPerGrade = newGradeWeight * (newGradeValue - targetAverage)
    if (gainPerGrade <= 0.0) return null
    return ceil((targetAverage * currentWeight - currentSum) / gainPerGrade)
      .toInt()
      .coerceAtLeast(0)
  }
}
