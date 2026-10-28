package ru.vibeschool.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class Lesson(
  val id: String,
  val number: Int,
  val subject: String,
  val startTime: String,
  val endTime: String,
  val room: String?,
  val homework: String?,
)

data class Grade(
  val id: String,
  val value: Int,
  val weight: Int,
  val workType: String,
  val date: String,
)

data class SubjectGrades(
  val id: String,
  val subject: String,
  val grades: List<Grade>,
)

data class SchoolSnapshot(
  val studentName: String,
  val className: String,
  val quarterName: String,
  val lessons: List<Lesson>,
  val subjects: List<SubjectGrades>,
  val isOffline: Boolean,
)

interface SchoolRepository {
  val snapshot: Flow<SchoolSnapshot>
}

class EmptySchoolRepository : SchoolRepository {
  override val snapshot: Flow<SchoolSnapshot> = flowOf(emptySchoolSnapshot())
}

fun emptySchoolSnapshot(): SchoolSnapshot =
  SchoolSnapshot(
      studentName = "",
      className = "",
      quarterName = "",
      lessons = emptyList(),
      subjects = emptyList(),
      isOffline = false,
    )
