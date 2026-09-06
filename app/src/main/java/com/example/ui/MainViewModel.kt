package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudAnnouncement
import com.example.data.cloud.CloudSyncManager
import com.example.data.cloud.FirebaseCloudSync
import com.example.data.db.AppDatabase
import com.example.data.OverallStats
import com.example.data.PlanPreferences
import com.example.data.StudyPlanData
import com.example.data.StudyRepository
import com.example.data.SubjectProgress
import com.example.data.db.CustomDayTaskEntity
import com.example.data.db.FocusSessionEntity
import com.example.data.db.QuestionEntity
import com.example.data.db.QuestionFeedbackEntity
import com.example.data.db.StudyProgressEntity
import com.example.data.models.DayTask
import com.example.data.models.ElevationStat
import com.example.data.models.MonthInfo
import com.example.data.models.PlanConfig
import com.example.data.models.Subject
import com.example.focus.FocusAudioPlayer
import com.example.focus.FocusSoundType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: String) {
    DASHBOARD("الرئيسية", "dashboard"),
    PLAN("جدول الخطة", "calendar_month"),
    QUESTIONS("بنك الأسئلة", "quiz"),
    ELEVATION("نسبة الارتفاع", "trending_up"),
    FOCUS("وضع التركيز", "lock_clock"),
    ACCOUNT("حسابي", "person"),
    MISTAKE_VAULT("دفتر الأخطاء", "psychology"),
    BAC_SIMULATOR("محاكي البكالوريا", "hourglass_top"),
    DUEL_BATTLES("تحدي الأقران", "flash_on"),
    RESCUE_PLANNER("مخطط الإنقاذ", "auto_graph"),
    AUDIO_FLASHCARDS("كبسولات الحفظ", "headphones")
}

data class UiState(
    val currentTab: AppTab = AppTab.DASHBOARD,
    val selectedMonth: Int = 1,
    val selectedWeek: Int = 1,
    val selectedSubjectFilter: Subject? = null,
    val searchQuery: String = "",
    val isGrayscaleEnabled: Boolean = false,
    val isFocusLockActive: Boolean = false,
    val focusRemainingSeconds: Int = 25 * 60,
    val focusTotalSeconds: Int = 25 * 60,
    val isFocusTimerRunning: Boolean = false,
    val selectedFocusSound: FocusSoundType = FocusSoundType.OFF,
    val focusSubject: String = "الرياضيات",
    val completedFocusSessionsCount: Int = 0,
    // Questions Bank State
    val selectedQuestionSubject: Subject? = null,
    val selectedQuestionTypeFilter: String = "ALL", // ALL, MCQ, ESSAY, STARRED
    val selectedDifficultyFilter: String = "ALL", // ALL, سهل, متوسط, وزاري / متقدم
    val questionsSearchQuery: String = "",
    val isCloudSyncing: Boolean = false,
    val cloudSyncStatus: String = "متصل بالسحابة 🟢",
    val cloudAnnouncements: List<CloudAnnouncement> = emptyList(),
    // Custom Student Study Plan State
    val isEditDayTaskDialogOpen: Boolean = false,
    val editingDayTask: DayTask? = null,
    val isPlanSetupDialogOpen: Boolean = false,
    // Question Feedback & Admin Panel State
    val isFeedbackDialogOpen: Boolean = false,
    val feedbackTargetQuestion: QuestionEntity? = null,
    val feedbackSelectedOption: String? = null,
    val isAdminPanelOpen: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(db.studyDao())
    private val audioPlayer = FocusAudioPlayer()
    private val planPrefs = PlanPreferences(application)

    private val _planConfig = MutableStateFlow(planPrefs.getPlanConfig())
    val planConfig: StateFlow<PlanConfig> = _planConfig.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        CloudSyncManager.init(application)
        // Initialize StudyPlanData with saved student configuration
        val savedConfig = planPrefs.getPlanConfig()
        StudyPlanData.configurePlan(savedConfig)
        if (!savedConfig.isConfigured) {
            _uiState.value = _uiState.value.copy(isPlanSetupDialogOpen = true)
        }

        viewModelScope.launch {
            repository.seedQuestionsIfEmpty()
            syncFromCloud()
        }
    }

    // Data Flows
    val allProgress: StateFlow<List<StudyProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customDayTasks: StateFlow<List<CustomDayTaskEntity>> = repository.allCustomDayTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestionFeedbacks: StateFlow<List<QuestionFeedbackEntity>> = repository.allQuestionFeedbacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mergedDayTasks: StateFlow<List<DayTask>> = combine(
        repository.allCustomDayTasks,
        _planConfig
    ) { customTasks, _ ->
        repository.getMergedDayTasks(customTasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudyPlanData.allTasks)

    val overallStats: StateFlow<OverallStats> = combine(
        repository.allProgress,
        repository.totalFocusMinutes,
        repository.allCustomDayTasks,
        _planConfig
    ) { progressList, totalMinutes, customTasks, _ ->
        val base = repository.getOverallStats(progressList, customTasks)
        base.copy(totalFocusHours = ((totalMinutes ?: 0) / 60f))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        OverallStats(0, 0, 0f, 0, 0f, 1, 1)
    )

    val elevationStats: StateFlow<List<ElevationStat>> = combine(
        repository.allProgress,
        repository.allCustomDayTasks,
        _planConfig
    ) { progressList, customTasks, _ ->
        repository.getElevationStats(progressList, customTasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectProgressList: StateFlow<List<SubjectProgress>> = combine(
        repository.allProgress,
        repository.allCustomDayTasks,
        _planConfig
    ) { progressList, customTasks, _ ->
        repository.getSubjectProgressList(progressList, customTasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.focusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openPlanSetupDialog() {
        _uiState.value = _uiState.value.copy(isPlanSetupDialogOpen = true)
    }

    fun closePlanSetupDialog() {
        _uiState.value = _uiState.value.copy(isPlanSetupDialogOpen = false)
    }

    fun applyPlanConfig(newConfig: PlanConfig) {
        planPrefs.savePlanConfig(newConfig)
        StudyPlanData.configurePlan(newConfig)
        _planConfig.value = newConfig
        _uiState.value = _uiState.value.copy(
            isPlanSetupDialogOpen = false,
            selectedMonth = 1,
            selectedWeek = 1
        )
    }

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun setSelectedMonth(month: Int) {
        val monthInfo = StudyPlanData.months.firstOrNull { it.monthNumber == month }
        val startWeek = monthInfo?.startWeek ?: 1
        _uiState.value = _uiState.value.copy(
            selectedMonth = month,
            selectedWeek = startWeek
        )
    }

    fun setSelectedWeek(week: Int) {
        _uiState.value = _uiState.value.copy(selectedWeek = week)
    }

    fun setSubjectFilter(subject: Subject?) {
        _uiState.value = _uiState.value.copy(selectedSubjectFilter = subject)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleGrayscale() {
        _uiState.value = _uiState.value.copy(isGrayscaleEnabled = !_uiState.value.isGrayscaleEnabled)
    }

    fun setGrayscale(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isGrayscaleEnabled = enabled)
    }

    // Toggle Task Period Items
    fun togglePeriod1(dayId: String, current: Boolean) {
        viewModelScope.launch { repository.togglePeriod1(dayId, current) }
    }

    fun togglePeriod2(dayId: String, current: Boolean) {
        viewModelScope.launch { repository.togglePeriod2(dayId, current) }
    }

    fun togglePeriod3(dayId: String, current: Boolean) {
        viewModelScope.launch { repository.togglePeriod3(dayId, current) }
    }

    fun toggleCards(dayId: String, current: Boolean) {
        viewModelScope.launch { repository.toggleCards(dayId, current) }
    }

    fun toggleRecitation(dayId: String, current: Boolean) {
        viewModelScope.launch { repository.toggleRecitation(dayId, current) }
    }

    fun toggleAllDay(dayId: String, markDone: Boolean) {
        viewModelScope.launch { repository.toggleAllDay(dayId, markDone) }
    }

    fun saveDayNote(dayId: String, note: String) {
        viewModelScope.launch { repository.updateNote(dayId, note) }
    }

    // --- Student Custom Study Plan Control ---
    fun openEditDayTask(task: DayTask) {
        _uiState.value = _uiState.value.copy(
            isEditDayTaskDialogOpen = true,
            editingDayTask = task
        )
    }

    fun openEditDayTaskDialog(task: DayTask) = openEditDayTask(task)

    fun closeEditDayTaskDialog() {
        _uiState.value = _uiState.value.copy(
            isEditDayTaskDialogOpen = false,
            editingDayTask = null
        )
    }

    fun saveCustomDayTask(
        dayId: String,
        monthNumber: Int,
        weekNumber: Int,
        dayOfWeek: String,
        p1Subject: Subject,
        p1Content: String,
        p2Subject: Subject,
        p2Content: String,
        p3Content: String,
        isRestDay: Boolean
    ) {
        viewModelScope.launch {
            repository.saveCustomDayTask(
                CustomDayTaskEntity(
                    dayId = dayId,
                    monthNumber = monthNumber,
                    weekNumber = weekNumber,
                    dayOfWeek = dayOfWeek,
                    period1Subject = p1Subject.name,
                    period1Content = p1Content,
                    period2Subject = p2Subject.name,
                    period2Content = p2Content,
                    period3Content = p3Content,
                    isRestDay = isRestDay,
                    isCustomized = true
                )
            )
            closeEditDayTaskDialog()
        }
    }

    fun resetDayToDefault(dayId: String) {
        viewModelScope.launch {
            repository.resetCustomDayTask(dayId)
            closeEditDayTaskDialog()
        }
    }

    fun resetCustomDayTask(dayId: String) = resetDayToDefault(dayId)

    fun resetAllCustomTasks() {
        viewModelScope.launch {
            repository.resetAllCustomTasks()
        }
    }

    // --- Questions Bank & Quiz Logic ---
    fun setQuestionsSubject(subject: Subject?) {
        _uiState.value = _uiState.value.copy(selectedQuestionSubject = subject)
    }

    fun setQuestionSubject(subject: Subject?) = setQuestionsSubject(subject)

    fun setQuestionTypeFilter(type: String) {
        _uiState.value = _uiState.value.copy(selectedQuestionTypeFilter = type)
    }

    fun setQuestionDifficultyFilter(difficulty: String) {
        _uiState.value = _uiState.value.copy(selectedDifficultyFilter = difficulty)
    }

    fun setQuestionsSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(questionsSearchQuery = query)
    }

    fun toggleQuestionStar(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleQuestionStarred(id, current)
        }
    }

    fun toggleQuestionStarred(id: Long, current: Boolean) = toggleQuestionStar(id, current)

    fun answerQuestion(question: QuestionEntity, selectedOption: String) {
        val isCorrect = question.correctAnswer.trim().equals(selectedOption.trim(), ignoreCase = true)
        viewModelScope.launch {
            repository.answerQuestion(question.id, selectedOption, isCorrect)
        }
    }

    // --- Question Feedback & Student Reports (Side Note) ---
    fun openFeedbackDialog(question: QuestionEntity, selectedOption: String? = null) {
        _uiState.value = _uiState.value.copy(
            isFeedbackDialogOpen = true,
            feedbackTargetQuestion = question,
            feedbackSelectedOption = selectedOption ?: question.userSelectedAnswer
        )
    }

    fun closeFeedbackDialog() {
        _uiState.value = _uiState.value.copy(
            isFeedbackDialogOpen = false,
            feedbackTargetQuestion = null,
            feedbackSelectedOption = null
        )
    }

    fun submitQuestionFeedback(reason: String, note: String, selectedOption: String?) {
        val targetQ = _uiState.value.feedbackTargetQuestion ?: return
        viewModelScope.launch {
            val feedbackEntity = QuestionFeedbackEntity(
                questionId = targetQ.id,
                questionText = targetQ.questionText,
                subject = targetQ.subject,
                selectedOption = selectedOption,
                feedbackReason = reason,
                noteText = note,
                status = "PENDING"
            )
            // 1. Save locally in device database (offline-first)
            repository.submitQuestionFeedback(feedbackEntity)

            // 2. Upload to Vercel/Cloud in background
            CloudSyncManager.submitFeedback(feedbackEntity)

            if (selectedOption != null) {
                answerQuestion(targetQ, selectedOption)
            }
            closeFeedbackDialog()
        }
    }

    // --- Cloud Sync Controller ---
    fun setVercelCloudUrl(url: String) {
        CloudSyncManager.setVercelUrl(getApplication(), url)
        syncFromCloud()
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCloudSyncing = true)
            try {
                // Sync questions from Vercel / Cloud into local Room DB
                val addedCount = CloudSyncManager.syncQuestions(repository)
                val announcements = CloudSyncManager.fetchAnnouncements()

                _uiState.value = _uiState.value.copy(
                    cloudAnnouncements = announcements,
                    cloudSyncStatus = "متزامن سحابياً 🟢",
                    isCloudSyncing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCloudSyncing = false,
                    cloudSyncStatus = "يعمل محلياً (غير متصل) ⚡"
                )
            }
        }
    }

    // Focus Mode Logic
    fun setFocusDuration(minutes: Int) {
        if (!_uiState.value.isFocusTimerRunning) {
            _uiState.value = _uiState.value.copy(
                focusTotalSeconds = minutes * 60,
                focusRemainingSeconds = minutes * 60
            )
        }
    }

    fun setFocusSubject(subject: String) {
        _uiState.value = _uiState.value.copy(focusSubject = subject)
    }

    fun setFocusSound(soundType: FocusSoundType) {
        _uiState.value = _uiState.value.copy(selectedFocusSound = soundType)
        if (_uiState.value.isFocusTimerRunning || soundType == FocusSoundType.OFF) {
            audioPlayer.startSound(soundType, viewModelScope)
        }
    }

    fun startFocusSession(enableGrayscale: Boolean = true, enableLock: Boolean = true) {
        _uiState.value = _uiState.value.copy(
            isFocusTimerRunning = true,
            isFocusLockActive = enableLock,
            isGrayscaleEnabled = if (enableGrayscale) true else _uiState.value.isGrayscaleEnabled
        )

        audioPlayer.startSound(_uiState.value.selectedFocusSound, viewModelScope)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.focusRemainingSeconds > 0 && _uiState.value.isFocusTimerRunning) {
                delay(1000)
                val remaining = _uiState.value.focusRemainingSeconds - 1
                _uiState.value = _uiState.value.copy(focusRemainingSeconds = remaining)

                if (remaining <= 0) {
                    onFocusSessionCompleted()
                }
            }
        }
    }

    fun pauseFocusSession() {
        _uiState.value = _uiState.value.copy(isFocusTimerRunning = false)
        timerJob?.cancel()
        audioPlayer.stopSound()
    }

    fun stopFocusSession(saveProgress: Boolean = true) {
        val elapsedSeconds = _uiState.value.focusTotalSeconds - _uiState.value.focusRemainingSeconds
        val elapsedMinutes = elapsedSeconds / 60

        if (saveProgress && elapsedMinutes >= 1) {
            viewModelScope.launch {
                repository.saveFocusSession(
                    FocusSessionEntity(
                        durationMinutes = elapsedMinutes,
                        sessionType = "جلسة تركيز",
                        subjectName = _uiState.value.focusSubject
                    )
                )
            }
        }

        timerJob?.cancel()
        audioPlayer.stopSound()

        _uiState.value = _uiState.value.copy(
            isFocusTimerRunning = false,
            isFocusLockActive = false,
            focusRemainingSeconds = _uiState.value.focusTotalSeconds
        )
    }

    private fun onFocusSessionCompleted() {
        val totalMinutes = _uiState.value.focusTotalSeconds / 60
        viewModelScope.launch {
            repository.saveFocusSession(
                FocusSessionEntity(
                    durationMinutes = totalMinutes,
                    sessionType = "بومودورو مكتمل",
                    subjectName = _uiState.value.focusSubject
                )
            )
        }
        audioPlayer.stopSound()
        _uiState.value = _uiState.value.copy(
            isFocusTimerRunning = false,
            isFocusLockActive = false,
            focusRemainingSeconds = _uiState.value.focusTotalSeconds,
            completedFocusSessionsCount = _uiState.value.completedFocusSessionsCount + 1
        )
    }

    fun dismissFocusLock() {
        _uiState.value = _uiState.value.copy(isFocusLockActive = false)
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopSound()
        timerJob?.cancel()
    }
}

