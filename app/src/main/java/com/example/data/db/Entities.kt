package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_progress")
data class StudyProgressEntity(
    @PrimaryKey
    val dayId: String,
    val period1Done: Boolean = false,
    val period2Done: Boolean = false,
    val period3Done: Boolean = false,
    val cardsDone: Boolean = false,
    val recitationDone: Boolean = false,
    val note: String = "",
    val score: Int = -1,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val durationMinutes: Int,
    val sessionType: String, // بومودورو، تركيز عميق، مراجعة
    val subjectName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions_bank")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String, // MATH, PHYSICS, CHEMISTRY, SCIENCE, ARABIC, ENGLISH, FRENCH, ISLAMIC, GENERAL
    val unitOrTopic: String,
    val questionText: String,
    val questionType: String = "MCQ", // MCQ, ESSAY
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = "A", // A, B, C, D or model answer text
    val explanation: String = "",
    val difficulty: String = "متوسط", // سهل، متوسط، متقدم
    val yearOrSource: String = "بنك الأسئلة",
    val isStarred: Boolean = false,
    val userSelectedAnswer: String? = null,
    val isAnsweredCorrectly: Boolean? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_day_tasks")
data class CustomDayTaskEntity(
    @PrimaryKey
    val dayId: String, // e.g. "m1_w1_d1"
    val monthNumber: Int,
    val weekNumber: Int,
    val dayOfWeek: String,
    val period1Subject: String,
    val period1Content: String,
    val period2Subject: String,
    val period2Content: String,
    val period3Content: String,
    val isRestDay: Boolean = false,
    val isCustomized: Boolean = true,
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "question_feedbacks")
data class QuestionFeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val questionText: String,
    val subject: String,
    val selectedOption: String? = null,
    val feedbackReason: String, // e.g. "خطأ في نص السؤال", "خطأ في الإجابة الصحيحة", "شرح غير واضح", "ملاحظة عامة"
    val noteText: String,
    val status: String = "PENDING", // PENDING (قيد المراجعة), REVIEWED (تم الفحص), FIXED (تم التصحيح والاعتماد), REJECTED (مرفوض)
    val adminReply: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


