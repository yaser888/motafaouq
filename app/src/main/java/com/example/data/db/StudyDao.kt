package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_progress")
    fun getAllProgress(): Flow<List<StudyProgressEntity>>

    @Query("SELECT * FROM study_progress WHERE dayId = :id LIMIT 1")
    suspend fun getProgressForDay(id: String): StudyProgressEntity?

    @Query("SELECT * FROM study_progress WHERE dayId = :id LIMIT 1")
    fun getProgressFlowForDay(id: String): Flow<StudyProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: StudyProgressEntity)

    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("DELETE FROM study_progress")
    suspend fun resetAllProgress()

    // --- Questions Bank ---
    @Query("SELECT * FROM questions_bank ORDER BY createdTimestamp DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions_bank WHERE subject = :subject ORDER BY createdTimestamp DESC")
    fun getQuestionsBySubject(subject: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions_bank WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions_bank WHERE id = :id")
    suspend fun deleteQuestion(id: Long)

    @Query("UPDATE questions_bank SET isStarred = :isStarred WHERE id = :id")
    suspend fun toggleQuestionStarred(id: Long, isStarred: Boolean)

    @Query("UPDATE questions_bank SET userSelectedAnswer = :answer, isAnsweredCorrectly = :isCorrect WHERE id = :id")
    suspend fun updateUserAnswer(id: Long, answer: String?, isCorrect: Boolean?)

    @Query("SELECT COUNT(*) FROM questions_bank")
    suspend fun getQuestionsCount(): Int

    // --- Custom Day Tasks ---
    @Query("SELECT * FROM custom_day_tasks")
    fun getAllCustomDayTasks(): Flow<List<CustomDayTaskEntity>>

    @Query("SELECT * FROM custom_day_tasks WHERE dayId = :dayId LIMIT 1")
    suspend fun getCustomDayTask(dayId: String): CustomDayTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCustomDayTask(task: CustomDayTaskEntity)

    @Query("DELETE FROM custom_day_tasks WHERE dayId = :dayId")
    suspend fun deleteCustomDayTask(dayId: String)

    @Query("DELETE FROM custom_day_tasks")
    suspend fun resetAllCustomDayTasks()

    // --- Question Feedbacks & Error Reports (Admin Control) ---
    @Query("SELECT * FROM question_feedbacks ORDER BY timestamp DESC")
    fun getAllFeedbacks(): Flow<List<QuestionFeedbackEntity>>

    @Query("SELECT * FROM question_feedbacks WHERE questionId = :questionId ORDER BY timestamp DESC")
    fun getFeedbacksForQuestion(questionId: Long): Flow<List<QuestionFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: QuestionFeedbackEntity): Long

    @Query("UPDATE question_feedbacks SET status = :status, adminReply = :reply WHERE id = :id")
    suspend fun updateFeedbackStatus(id: Long, status: String, reply: String)

    @Query("DELETE FROM question_feedbacks WHERE id = :id")
    suspend fun deleteFeedback(id: Long)
}

