package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudAnnouncement
import com.example.data.cloud.FirebaseCloudSync
import com.example.data.db.AppDatabase
import com.example.data.OverallStats
import com.example.data.StudyPlanData
import com.example.data.StudyRepository
import com.example.data.SubjectProgress
import com.example.data.db.CustomDayTaskEntity
import com.example.data.db.FocusSessionEntity
import com.example.data.db.QuestionEntity
import com.example.data.db.StudyProgressEntity
import com.example.data.models.DayTask
import com.example.data.models.ElevationStat
import com.example.data.models.MonthInfo
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
    FOCUS("وضع التركيز", "lock_clock")
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
    val editingDayTask: DayTask? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(db.studyDao())
    private val audioPlayer = FocusAudioPlayer()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
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

    val mergedDayTasks: StateFlow<List<DayTask>> = repository.allCustomDayTasks
        .combine(MutableStateFlow(Unit)) { customTasks, _ ->
            repository.getMergedDayTasks(customTasks)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudyPlanData.allTasks)

    val overallStats: StateFlow<OverallStats> = combine(
        repository.allProgress,
        repository.totalFocusMinutes,
        repository.allCustomDayTasks
    ) { progressList, totalMinutes, customTasks ->
        val base = repository.getOverallStats(progressList, customTasks)
        base.copy(totalFocusHours = ((totalMinutes ?: 0) / 60f))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        OverallStats(0, 0, 0f, 0, 0f, 1, 1)
    )

    val elevationStats: StateFlow<List<ElevationStat>> = combine(
        repository.allProgress,
        repository.allCustomDayTasks
    ) { progressList, customTasks ->
        repository.getElevationStats(progressList, customTasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectProgressList: StateFlow<List<SubjectProgress>> = combine(
        repository.allProgress,
        repository.allCustomDayTasks
    ) { progressList, customTasks ->
        repository.getSubjectProgressList(progressList, customTasks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.focusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun syncFromCloud() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCloudSyncing = true)
            try {
                val cloudQuestions = FirebaseCloudSync.fetchCloudQuestions()
                if (cloudQuestions.isNotEmpty()) {
                    repository.addQuestions(cloudQuestions)
                }
                val announcements = FirebaseCloudSync.fetchAnnouncements()
                _uiState.value = _uiState.value.copy(
                    cloudAnnouncements = announcements,
                    cloudSyncStatus = if (FirebaseCloudSync.isCloudAvailable) "متزامن مع السحابة 🟢" else "يعمل في الوضع المحلي ⚡",
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

