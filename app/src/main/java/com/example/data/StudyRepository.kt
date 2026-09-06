package com.example.data

import com.example.data.db.CustomDayTaskEntity
import com.example.data.db.FocusSessionEntity
import com.example.data.db.QuestionEntity
import com.example.data.db.StudyDao
import com.example.data.db.StudyProgressEntity
import com.example.data.models.DayTask
import com.example.data.models.ElevationStat
import com.example.data.models.MonthInfo
import com.example.data.models.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class OverallStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val overallPercentage: Float,
    val streakDays: Int,
    val totalFocusHours: Float,
    val currentMonth: Int,
    val currentWeek: Int
)

data class SubjectProgress(
    val subject: Subject,
    val totalSlots: Int,
    val completedSlots: Int,
    val percentage: Float
)

class StudyRepository(private val dao: StudyDao) {

    val allProgress: Flow<List<StudyProgressEntity>> = dao.getAllProgress()
    val focusSessions: Flow<List<FocusSessionEntity>> = dao.getAllFocusSessions()
    val totalFocusMinutes: Flow<Int?> = dao.getTotalFocusMinutes()
    val allQuestions: Flow<List<QuestionEntity>> = dao.getAllQuestions()
    val allCustomDayTasks: Flow<List<CustomDayTaskEntity>> = dao.getAllCustomDayTasks()

    suspend fun seedQuestionsIfEmpty() {
        val count = dao.getQuestionsCount()
        if (count == 0) {
            dao.insertAllQuestions(QuestionsData.initialQuestions)
        }
    }

    /**
     * Merges the static base study plan with any customizations made by the student.
     */
    fun getMergedDayTasks(customTasks: List<CustomDayTaskEntity>): List<DayTask> {
        val customMap = customTasks.associateBy { it.dayId }
        return StudyPlanData.allTasks.map { defaultTask ->
            val custom = customMap[defaultTask.id]
            if (custom != null) {
                val p1Subject = try {
                    Subject.valueOf(custom.period1Subject)
                } catch (e: Exception) {
                    defaultTask.period1Subject
                }
                val p2Subject = try {
                    Subject.valueOf(custom.period2Subject)
                } catch (e: Exception) {
                    defaultTask.period2Subject
                }
                defaultTask.copy(
                    period1Subject = p1Subject,
                    period1Content = custom.period1Content,
                    period2Subject = p2Subject,
                    period2Content = custom.period2Content,
                    period3Content = custom.period3Content,
                    isRestDay = custom.isRestDay
                )
            } else {
                defaultTask
            }
        }
    }

    fun getOverallStats(
        progressList: List<StudyProgressEntity>,
        customTasks: List<CustomDayTaskEntity> = emptyList()
    ): OverallStats {
        val tasks = getMergedDayTasks(customTasks)
        val progressMap = progressList.associateBy { it.dayId }

        var totalCheckableSlots = 0
        var completedSlots = 0

        for (task in tasks) {
            if (task.isRestDay) continue
            // 5 slots per active day: period1, period2, period3, cards, recitation
            totalCheckableSlots += 5
            val prog = progressMap[task.id]
            if (prog != null) {
                if (prog.period1Done) completedSlots++
                if (prog.period2Done) completedSlots++
                if (prog.period3Done) completedSlots++
                if (prog.cardsDone) completedSlots++
                if (prog.recitationDone) completedSlots++
            }
        }

        val percentage = if (totalCheckableSlots > 0) {
            (completedSlots.toFloat() / totalCheckableSlots.toFloat()) * 100f
        } else 0f

        // Calculate consecutive active days streak
        val daysWithProgress = progressList.filter {
            it.period1Done || it.period2Done || it.period3Done || it.cardsDone || it.recitationDone
        }.size

        return OverallStats(
            totalTasks = totalCheckableSlots,
            completedTasks = completedSlots,
            overallPercentage = percentage,
            streakDays = daysWithProgress,
            totalFocusHours = 0f,
            currentMonth = 1,
            currentWeek = 1
        )
    }

    fun getElevationStats(
        progressList: List<StudyProgressEntity>,
        customTasks: List<CustomDayTaskEntity> = emptyList()
    ): List<ElevationStat> {
        val progressMap = progressList.associateBy { it.dayId }
        val tasks = getMergedDayTasks(customTasks)

        return StudyPlanData.months.map { month ->
            val monthTasks = tasks.filter { it.monthNumber == month.monthNumber && !it.isRestDay }
            val totalMonthSlots = monthTasks.size * 5
            var completedMonthSlots = 0

            for (task in monthTasks) {
                val prog = progressMap[task.id]
                if (prog != null) {
                    if (prog.period1Done) completedMonthSlots++
                    if (prog.period2Done) completedMonthSlots++
                    if (prog.period3Done) completedMonthSlots++
                    if (prog.cardsDone) completedMonthSlots++
                    if (prog.recitationDone) completedMonthSlots++
                }
            }

            val currentRate = if (totalMonthSlots > 0) {
                (completedMonthSlots.toFloat() / totalMonthSlots.toFloat()) * 100f
            } else 0f

            val plannedRate = 100f
            val status = when {
                currentRate >= 100f -> "تم الإنجاز بالكامل 🌟"
                currentRate >= 75f -> "متقدم جداً 🚀"
                currentRate >= 50f -> "في المسار الصحيح 📈"
                currentRate > 0f -> "قيد التقدم ⏳"
                else -> "لم تبدأ بعد"
            }

            ElevationStat(
                monthNumber = month.monthNumber,
                monthName = month.name,
                plannedRate = plannedRate,
                currentRate = currentRate,
                completedTasks = completedMonthSlots,
                totalTasks = totalMonthSlots,
                status = status
            )
        }
    }

    fun getSubjectProgressList(
        progressList: List<StudyProgressEntity>,
        customTasks: List<CustomDayTaskEntity> = emptyList()
    ): List<SubjectProgress> {
        val progressMap = progressList.associateBy { it.dayId }
        val tasks = getMergedDayTasks(customTasks)

        val subjectTotals = mutableMapOf<Subject, Int>()
        val subjectCompleted = mutableMapOf<Subject, Int>()

        for (task in tasks) {
            if (task.isRestDay) continue
            val prog = progressMap[task.id]

            // Period 1
            subjectTotals[task.period1Subject] = (subjectTotals[task.period1Subject] ?: 0) + 1
            if (prog?.period1Done == true) {
                subjectCompleted[task.period1Subject] = (subjectCompleted[task.period1Subject] ?: 0) + 1
            }

            // Period 2
            subjectTotals[task.period2Subject] = (subjectTotals[task.period2Subject] ?: 0) + 1
            if (prog?.period2Done == true) {
                subjectCompleted[task.period2Subject] = (subjectCompleted[task.period2Subject] ?: 0) + 1
            }
        }

        return Subject.entries.filter { it != Subject.GENERAL }.map { subj ->
            val total = subjectTotals[subj] ?: 0
            val done = subjectCompleted[subj] ?: 0
            val pct = if (total > 0) (done.toFloat() / total.toFloat()) * 100f else 0f
            SubjectProgress(
                subject = subj,
                totalSlots = total,
                completedSlots = done,
                percentage = pct
            )
        }.sortedByDescending { it.percentage }
    }

    suspend fun togglePeriod1(dayId: String, current: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(period1Done = !current, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun togglePeriod2(dayId: String, current: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(period2Done = !current, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun togglePeriod3(dayId: String, current: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(period3Done = !current, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun toggleCards(dayId: String, current: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(cardsDone = !current, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun toggleRecitation(dayId: String, current: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(recitationDone = !current, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun toggleAllDay(dayId: String, markAllDone: Boolean) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(
            existing.copy(
                period1Done = markAllDone,
                period2Done = markAllDone,
                period3Done = markAllDone,
                cardsDone = markAllDone,
                recitationDone = markAllDone,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateNote(dayId: String, note: String) {
        val existing = dao.getProgressForDay(dayId) ?: StudyProgressEntity(dayId = dayId)
        dao.insertOrUpdateProgress(existing.copy(note = note, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun saveFocusSession(session: FocusSessionEntity) {
        dao.insertFocusSession(session)
    }

    suspend fun resetAll() {
        dao.resetAllProgress()
    }

    // --- Questions Bank Operations ---
    suspend fun addQuestion(question: QuestionEntity): Long {
        return dao.insertQuestion(question)
    }

    suspend fun addQuestions(questions: List<QuestionEntity>) {
        dao.insertAllQuestions(questions)
    }

    suspend fun updateQuestion(question: QuestionEntity) {
        dao.updateQuestion(question)
    }

    suspend fun deleteQuestion(id: Long) {
        dao.deleteQuestion(id)
    }

    suspend fun toggleQuestionStarred(id: Long, currentStarred: Boolean) {
        dao.toggleQuestionStarred(id, !currentStarred)
    }

    suspend fun answerQuestion(id: Long, selectedAnswer: String, isCorrect: Boolean) {
        dao.updateUserAnswer(id, selectedAnswer, isCorrect)
    }

    suspend fun resetQuestionsToDefault() {
        dao.insertAllQuestions(QuestionsData.initialQuestions)
    }

    // --- Custom Day Plan Operations for Students ---
    suspend fun saveCustomDayTask(customTask: CustomDayTaskEntity) {
        dao.insertOrUpdateCustomDayTask(customTask)
    }

    suspend fun resetCustomDayTask(dayId: String) {
        dao.deleteCustomDayTask(dayId)
    }

    suspend fun resetAllCustomTasks() {
        dao.resetAllCustomDayTasks()
    }
}

