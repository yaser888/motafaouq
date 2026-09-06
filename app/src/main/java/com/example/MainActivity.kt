package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.ui.components.AdminPanelDialog
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.components.EditDayTaskDialog
import com.example.ui.components.PlanSetupDialog
import com.example.ui.components.QuestionFeedbackDialog
import com.example.ui.screens.AudioFlashcardsScreen
import com.example.ui.screens.BacSimulatorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.LandingPageScreen
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.DuelBattleScreen
import com.example.ui.screens.ElevationChartScreen
import com.example.ui.screens.FocusModeScreen
import com.example.ui.screens.MistakeVaultScreen
import com.example.ui.screens.QuestionsBankScreen
import com.example.ui.screens.SmartRescuePlannerScreen
import com.example.ui.screens.StudyPlanScreen
import com.example.ui.theme.MutafawweqTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val planConfig by viewModel.planConfig.collectAsStateWithLifecycle()
            val stats by viewModel.overallStats.collectAsStateWithLifecycle()
            val elevationList by viewModel.elevationStats.collectAsStateWithLifecycle()
            val subjectProgressList by viewModel.subjectProgressList.collectAsStateWithLifecycle()
            val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
            val focusSessions by viewModel.focusSessions.collectAsStateWithLifecycle()
            val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
            val feedbacks by viewModel.allQuestionFeedbacks.collectAsStateWithLifecycle()
            val customDayTasks by viewModel.customDayTasks.collectAsStateWithLifecycle()
            val mergedDayTasks by viewModel.mergedDayTasks.collectAsStateWithLifecycle()

            MutafawweqTheme {
                val prefs = remember { getSharedPreferences("student_auth_prefs", MODE_PRIVATE) }
                var isLoggedIn by remember {
                    mutableStateOf(prefs.getBoolean("is_logged_in", false))
                }
                var authRoute by remember { mutableStateOf("landing") }

                if (!isLoggedIn) {
                    when (authRoute) {
                        "landing" -> LandingPageScreen(
                            onNavigateToLogin = { authRoute = "login" },
                            onNavigateToSignUp = { authRoute = "signup" }
                        )
                        "login" -> LoginScreen(
                            initialIsSignUp = false,
                            onLoginSuccess = { _ -> isLoggedIn = true },
                            onBackToLanding = { authRoute = "landing" }
                        )
                        "signup" -> LoginScreen(
                            initialIsSignUp = true,
                            onLoginSuccess = { _ -> isLoggedIn = true },
                            onBackToLanding = { authRoute = "landing" }
                        )
                    }
                } else {
                    // Grayscale Filter Box Wrapper for complete black-and-white visual discipline
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            if (uiState.isGrayscaleEnabled) {
                                val matrix = ColorMatrix().apply { setToSaturation(0f) }
                                val filter = ColorFilter.colorMatrix(matrix)
                                drawIntoCanvas { canvas ->
                                    val paint = Paint().apply {
                                        colorFilter = filter
                                    }
                                    canvas.saveLayer(size.toRect(), paint)
                                    drawContent()
                                    canvas.restore()
                                }
                            } else {
                                drawContent()
                            }
                        }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (!(uiState.isFocusLockActive && uiState.isFocusTimerRunning)) {
                                AppTopBar(
                                    streakDays = stats.streakDays,
                                    isGrayscale = uiState.isGrayscaleEnabled,
                                    stream = planConfig.stream,
                                    onStreamClick = { viewModel.openPlanSetupDialog() },
                                    onToggleGrayscale = { viewModel.toggleGrayscale() },
                                    onAdminClick = { viewModel.setAdminPanelOpen(true) },
                                    onQuickFocusClick = { viewModel.setTab(AppTab.FOCUS) }
                                )
                            }
                        },
                        bottomBar = {
                            if (!(uiState.isFocusLockActive && uiState.isFocusTimerRunning)) {
                                AppBottomNav(
                                    currentTab = uiState.currentTab,
                                    onTabSelected = { viewModel.setTab(it) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.currentTab) {
                                AppTab.DASHBOARD -> {
                                    DashboardScreen(
                                        stats = stats,
                                        elevationList = elevationList,
                                        progressList = allProgress,
                                        planConfig = planConfig,
                                        onChangePlanClick = { viewModel.openPlanSetupDialog() },
                                        onNavigateToTab = { viewModel.setTab(it) },
                                        onSelectMonth = { viewModel.setSelectedMonth(it) },
                                        onStartFocus = { viewModel.setTab(AppTab.FOCUS) },
                                        onTogglePeriod1 = { id, curr -> viewModel.togglePeriod1(id, curr) },
                                        onTogglePeriod2 = { id, curr -> viewModel.togglePeriod2(id, curr) },
                                        onTogglePeriod3 = { id, curr -> viewModel.togglePeriod3(id, curr) },
                                        onToggleCards = { id, curr -> viewModel.toggleCards(id, curr) },
                                        onToggleRecitation = { id, curr -> viewModel.toggleRecitation(id, curr) },
                                        onToggleAllDay = { id, done -> viewModel.toggleAllDay(id, done) }
                                    )
                                }

                                AppTab.PLAN -> {
                                    StudyPlanScreen(
                                        allTasks = mergedDayTasks,
                                        customDayTasks = customDayTasks,
                                        selectedMonth = uiState.selectedMonth,
                                        selectedWeek = uiState.selectedWeek,
                                        selectedSubjectFilter = uiState.selectedSubjectFilter,
                                        searchQuery = uiState.searchQuery,
                                        progressList = allProgress,
                                        planConfig = planConfig,
                                        onChangePlanClick = { viewModel.openPlanSetupDialog() },
                                        onSelectMonth = { viewModel.setSelectedMonth(it) },
                                        onSelectWeek = { viewModel.setSelectedWeek(it) },
                                        onSetSubjectFilter = { viewModel.setSubjectFilter(it) },
                                        onSetSearchQuery = { viewModel.setSearchQuery(it) },
                                        onTogglePeriod1 = { id, curr -> viewModel.togglePeriod1(id, curr) },
                                        onTogglePeriod2 = { id, curr -> viewModel.togglePeriod2(id, curr) },
                                        onTogglePeriod3 = { id, curr -> viewModel.togglePeriod3(id, curr) },
                                        onToggleCards = { id, curr -> viewModel.toggleCards(id, curr) },
                                        onToggleRecitation = { id, curr -> viewModel.toggleRecitation(id, curr) },
                                        onToggleAllDay = { id, done -> viewModel.toggleAllDay(id, done) },
                                        onSaveNote = { id, note -> viewModel.saveDayNote(id, note) },
                                        onOpenEditTask = { task -> viewModel.openEditDayTask(task) },
                                        onResetAllCustomTasks = { viewModel.resetAllCustomTasks() }
                                    )
                                }

                                AppTab.QUESTIONS -> {
                                    QuestionsBankScreen(
                                        questions = allQuestions,
                                        selectedSubject = uiState.selectedQuestionSubject,
                                        selectedTypeFilter = uiState.selectedQuestionTypeFilter,
                                        selectedDifficultyFilter = uiState.selectedDifficultyFilter,
                                        searchQuery = uiState.questionsSearchQuery,
                                        onSelectSubject = { viewModel.setQuestionsSubject(it) },
                                        onSelectTypeFilter = { viewModel.setQuestionTypeFilter(it) },
                                        onSelectDifficultyFilter = { viewModel.setQuestionDifficultyFilter(it) },
                                        onSearchQueryChange = { viewModel.setQuestionsSearchQuery(it) },
                                        onToggleStar = { id, curr -> viewModel.toggleQuestionStar(id, curr) },
                                        onAnswerQuestion = { q, ans -> viewModel.answerQuestion(q, ans) },
                                        onOpenFeedback = { q, ans -> viewModel.openFeedbackDialog(q, ans) },
                                        feedbacks = feedbacks
                                    )
                                }

                                AppTab.ELEVATION -> {
                                    ElevationChartScreen(
                                        stats = stats,
                                        elevationList = elevationList,
                                        subjectProgressList = subjectProgressList
                                    )
                                }

                                AppTab.FOCUS -> {
                                    FocusModeScreen(
                                        isGrayscaleEnabled = uiState.isGrayscaleEnabled,
                                        isFocusLockActive = uiState.isFocusLockActive,
                                        focusRemainingSeconds = uiState.focusRemainingSeconds,
                                        focusTotalSeconds = uiState.focusTotalSeconds,
                                        isTimerRunning = uiState.isFocusTimerRunning,
                                        selectedSound = uiState.selectedFocusSound,
                                        focusSubject = uiState.focusSubject,
                                        focusSessions = focusSessions,
                                        onToggleGrayscale = { viewModel.toggleGrayscale() },
                                        onSetDuration = { viewModel.setFocusDuration(it) },
                                        onSetSubject = { viewModel.setFocusSubject(it) },
                                        onSetSound = { viewModel.setFocusSound(it) },
                                        onStartFocus = { grayscale, lock ->
                                             viewModel.startFocusSession(grayscale, lock)
                                        },
                                        onPauseFocus = { viewModel.pauseFocusSession() },
                                        onStopFocus = { viewModel.stopFocusSession(true) },
                                        onDismissLock = { viewModel.dismissFocusLock() }
                                    )
                                }

                                AppTab.MISTAKE_VAULT -> {
                                    MistakeVaultScreen(
                                        onBack = { viewModel.setTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.BAC_SIMULATOR -> {
                                    BacSimulatorScreen(
                                        onBack = { viewModel.setTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.DUEL_BATTLES -> {
                                    DuelBattleScreen(
                                        onBack = { viewModel.setTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.RESCUE_PLANNER -> {
                                    SmartRescuePlannerScreen(
                                        onBack = { viewModel.setTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.AUDIO_FLASHCARDS -> {
                                    AudioFlashcardsScreen(
                                        onBack = { viewModel.setTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.ACCOUNT -> {
                                    AccountScreen(
                                        onLogout = {
                                            recreate()
                                        }
                                    )
                                }
                            }

                            // Student Edit Day Task Dialog
                            if (uiState.isEditDayTaskDialogOpen && uiState.editingDayTask != null) {
                                val currentTask = uiState.editingDayTask!!
                                EditDayTaskDialog(
                                    task = currentTask,
                                    onDismiss = { viewModel.closeEditDayTaskDialog() },
                                    onSave = { dayId, month, week, dayOfWeek, p1Sub, p1Con, p2Sub, p2Con, p3Con, rest ->
                                        viewModel.saveCustomDayTask(dayId, month, week, dayOfWeek, p1Sub, p1Con, p2Sub, p2Con, p3Con, rest)
                                    },
                                    onResetDayToDefault = { dayId ->
                                        viewModel.resetDayToDefault(dayId)
                                    }
                                )
                            }

                            // Intensive Study Plan Configuration Dialog
                            
                        if (uiState.isAdminPanelOpen) {
                            val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle(initialValue = emptyList())
                            val allFeedbacks by viewModel.allQuestionFeedbacks.collectAsStateWithLifecycle(initialValue = emptyList())
                            
                            AdminPanelDialog(
                                feedbacks = allFeedbacks,
                                questions = allQuestions,
                                planConfig = com.example.data.models.PlanConfig(),
                                onDismiss = { viewModel.setAdminPanelOpen(false) },
                                onUpdateFeedbackStatus = { id, status, reply -> viewModel.updateFeedbackStatus(id, status, reply) },
                                onDeleteFeedback = { id -> viewModel.deleteFeedback(id) },
                                onEditQuestion = { q -> viewModel.updateQuestion(q) },
                                onOpenPlanSetup = { viewModel.setAdminPanelOpen(false); viewModel.openPlanSetupDialog() }
                            )
                        }

                        if (uiState.activeChallengeTest != null) {
                            AlertDialog(
                                onDismissRequest = { viewModel.dismissChallengeTest() },
                                title = { Text(uiState.activeChallengeTest!!.title) },
                                text = { Text(uiState.activeChallengeTest!!.description) },
                                confirmButton = {
                                    Button(onClick = { 
                                        viewModel.dismissChallengeTest()
                                        viewModel.setTab(com.example.ui.AppTab.MISTAKE_VAULT)
                                    }) {
                                        Text("قبول التحدي! 💪")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { viewModel.dismissChallengeTest() }) {
                                        Text("تجاهل حالياً")
                                    }
                                }
                            )
                        }

                        if (uiState.isPlanSetupDialogOpen) {
                                PlanSetupDialog(
                                    initialConfig = planConfig,
                                    canDismiss = planConfig.isConfigured,
                                    onDismissRequest = { viewModel.closePlanSetupDialog() },
                                    onConfirmConfig = { newConfig ->
                                        viewModel.applyPlanConfig(newConfig)
                                    }
                                )
                            }

                            // Question Feedback / Error Report Dialog (Student side note)
                            if (uiState.isFeedbackDialogOpen && uiState.feedbackTargetQuestion != null) {
                                QuestionFeedbackDialog(
                                    question = uiState.feedbackTargetQuestion!!,
                                    currentSelectedAnswer = uiState.feedbackSelectedOption,
                                    onDismiss = { viewModel.closeFeedbackDialog() },
                                    onSubmitFeedback = { reason, note, selectedOption ->
                                        viewModel.submitQuestionFeedback(reason, note, selectedOption)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}

