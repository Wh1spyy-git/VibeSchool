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

class DemoSchoolRepository : SchoolRepository {
  override val snapshot: Flow<SchoolSnapshot> = flowOf(demoSchoolSnapshot())
}

fun demoSchoolSnapshot(): SchoolSnapshot =
  SchoolSnapshot(
      studentName = "Ученик",
      className = "10 класс",
      quarterName = "1 четверть",
      lessons = listOf(
        Lesson("1", 1, "Алгебра", "08:00", "08:45", "214", "№ 128–132"),
        Lesson("2", 2, "Русский язык", "08:55", "09:40", "305", "Упр. 74"),
        Lesson("3", 3, "Физика", "10:00", "10:45", "117", null),
        Lesson("4", 4, "История", "11:05", "11:50", "208", "Параграф 12"),
      ),
      subjects = listOf(
        SubjectGrades(
          "math",
          "Алгебра",
          listOf(
            Grade("m1", 5, 1, "Ответ на уроке", "24 окт."),
            Grade("m2", 4, 2, "Контрольная работа", "20 окт."),
            Grade("m3", 5, 1, "Самостоятельная работа", "15 окт."),
          ),
        ),
        SubjectGrades(
          "russian",
          "Русский язык",
          listOf(
            Grade("r1", 4, 1, "Домашняя работа", "23 окт."),
            Grade("r2", 5, 2, "Диктант", "18 окт."),
          ),
        ),
      ),
      isOffline = false,
    )
