package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.QuestionEntity
import com.example.data.db.QuestionFeedbackEntity
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500 as EmeraldSuccess
import com.example.ui.theme.RoyalBlue600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsBankScreen(
    questions: List<QuestionEntity>,
    selectedSubject: Subject?,
    selectedTypeFilter: String,
    selectedDifficultyFilter: String,
    searchQuery: String,
    onSelectSubject: (Subject?) -> Unit,
    onSelectTypeFilter: (String) -> Unit,
    onSelectDifficultyFilter: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleStar: (Long, Boolean) -> Unit,
    onAnswerQuestion: (QuestionEntity, String) -> Unit,
    onOpenFeedback: (QuestionEntity, String?) -> Unit,
    feedbacks: List<QuestionFeedbackEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Filter questions based on student selections
    val filteredQuestions = questions.filter { q ->
        val matchesSubject = if (selectedSubject == null) true else q.subject == selectedSubject.name
        val matchesType = when (selectedTypeFilter) {
            "MCQ" -> q.questionType == "MCQ"
            "ESSAY" -> q.questionType == "ESSAY"
            "STARRED" -> q.isStarred
            else -> true
        }
        val matchesDifficulty = if (selectedDifficultyFilter == "ALL") true else q.difficulty == selectedDifficultyFilter
        val matchesSearch = if (searchQuery.isBlank()) true else {
            q.questionText.contains(searchQuery, ignoreCase = true) ||
                    q.unitOrTopic.contains(searchQuery, ignoreCase = true) ||
                    q.explanation.contains(searchQuery, ignoreCase = true)
        }
        matchesSubject && matchesType && matchesDifficulty && matchesSearch
    }

    val answeredCount = questions.count { it.userSelectedAnswer != null }
    val correctCount = questions.count { 
        it.userSelectedAnswer != null && it.userSelectedAnswer.equals(it.correctAnswer, ignoreCase = true) 
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title & Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بنك أسئلة واختبارات البكالوريا",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                        Text(
                            text = "اختبر نفسك مع نماذج شاملة، حلول مفصلة وشروحات نموذجية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Progress Score Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$correctCount / $answeredCount صحيح 🎯",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Subject Filter Bar (Horizontal Carousel)
            item {
                Column {
                    Text(
                        text = "اختر المادة الدراسية:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All" Filter
                        FilterChip(
                            selected = selectedSubject == null,
                            onClick = { onSelectSubject(null) },
                            label = { Text("جميع المواد (${questions.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("subject_filter_all")
                        )

                        // Each Subject
                        Subject.entries.filter { it != Subject.GENERAL }.forEach { subj ->
                            val count = questions.count { it.subject == subj.name }
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { onSelectSubject(if (selectedSubject == subj) null else subj) },
                                label = { Text("${subj.arabicName} ($count)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("subject_filter_${subj.name}")
                            )
                        }
                    }
                }
            }

            // Search Bar & Filters Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Search Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("ابحث في نص السؤال، الوحدة، أو القوانين...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("questions_search_input")
                        )

                        // Question Types & Bookmarks Filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "ALL" to "الكل",
                                "MCQ" to "خيارات متعددة (MCQ)",
                                "ESSAY" to "مقالي وتطبيقي",
                                "STARRED" to "المحفوظة ⭐"
                            ).forEach { (filterKey, label) ->
                                val isSelected = selectedTypeFilter == filterKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectTypeFilter(filterKey) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("filter_type_$filterKey")
                                )
                            }
                        }

                        // Difficulty Filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مستوى الصعوبة:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            listOf(
                                "ALL" to "الكل",
                                "سهل" to "🟢 سهل",
                                "متوسط" to "🟡 متوسط",
                                "متقدم" to "🔴 متقدم"
                            ).forEach { (diffKey, label) ->
                                val isSelected = selectedDifficultyFilter == diffKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectDifficultyFilter(diffKey) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag("filter_diff_$diffKey")
                                )
                            }
                        }
                    }
                }
            }

            // Results Counter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النتائج (${filteredQuestions.size} سؤال)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Empty State
            if (filteredQuestions.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد أسئلة مطابقة للبحث أو المادة المحددة حالياً",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Questions List
                items(filteredQuestions, key = { it.id }) { question ->
                    val questionFeedbacks = feedbacks.filter { it.questionId == question.id }
                    val latestFeedback = questionFeedbacks.firstOrNull()
                    QuestionItemCard(
                        question = question,
                        onToggleStar = { onToggleStar(question.id, question.isStarred) },
                        onAnswerSelected = { option -> onAnswerQuestion(question, option) },
                        onOpenFeedback = { onOpenFeedback(question, question.userSelectedAnswer) },
                        hasFeedback = questionFeedbacks.isNotEmpty(),
                        feedbackStatus = latestFeedback?.status
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionItemCard(
    question: QuestionEntity,
    onToggleStar: () -> Unit,
    onAnswerSelected: (String) -> Unit,
    onOpenFeedback: () -> Unit,
    hasFeedback: Boolean = false,
    feedbackStatus: String? = null,
    modifier: Modifier = Modifier
) {
    val subjectEnum = try {
        Subject.valueOf(question.subject)
    } catch (e: Exception) {
        Subject.MATH
    }

    var showExplanation by remember(question.id, question.userSelectedAnswer) {
        mutableStateOf(question.userSelectedAnswer != null)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("question_card_${question.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Subject Badge + Topic + Difficulty + Note to Admin + Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Subject Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = subjectEnum.arabicName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Difficulty Badge
                    val diffColor = when (question.difficulty) {
                        "سهل" -> EmeraldSuccess
                        "متقدم" -> MaterialTheme.colorScheme.error
                        else -> AmberGold500
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = diffColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = question.difficulty,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = diffColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Year / Source Badge
                    if (question.yearOrSource.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = question.yearOrSource,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Actions: Note to Admin & Star
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Side Note / Error Report to Admin Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (hasFeedback) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(
                            1.dp,
                            if (hasFeedback) EmeraldSuccess else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenFeedback() }
                            .testTag("note_btn_${question.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "ملاحظة",
                                tint = if (hasFeedback) EmeraldSuccess else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (feedbackStatus) {
                                    "FIXED" -> "تم التصحيح ✓"
                                    "REVIEWED" -> "تم الفحص 🔵"
                                    "PENDING" -> "الملاحظة أرسلت 📩"
                                    else -> "ملاحظة 📝"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (hasFeedback) EmeraldSuccess else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Star / Bookmark Button
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(36.dp).testTag("star_question_${question.id}")
                    ) {
                        Icon(
                            imageVector = if (question.isStarred) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ السؤال",
                            tint = if (question.isStarred) AmberGold500 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Topic / Lesson Title
            if (question.unitOrTopic.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = question.unitOrTopic,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // MCQ Options or Essay Answer
            if (question.questionType == "MCQ") {
                // Header clarifying single choice selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خيارات الإجابة (اختر إجابة واحدة فقط):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (question.userSelectedAnswer != null) {
                        Text(
                            text = "إجابتك المختارة: الخيار ${question.userSelectedAnswer}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                val options = listOf(
                    "A" to question.optionA,
                    "B" to question.optionB,
                    "C" to question.optionC,
                    "D" to question.optionD
                ).filter { it.second.isNotBlank() }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { (optionKey, optionText) ->
                        val isSelected = question.userSelectedAnswer == optionKey
                        val isCorrectOption = question.correctAnswer.trim().equals(optionKey, ignoreCase = true)
                        val hasAnswered = question.userSelectedAnswer != null

                        val containerColor = when {
                            hasAnswered && isCorrectOption -> EmeraldSuccess.copy(alpha = 0.15f)
                            hasAnswered && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        }

                        val borderColor = when {
                            hasAnswered && isCorrectOption -> EmeraldSuccess
                            hasAnswered && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> Color.Transparent
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = containerColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onAnswerSelected(optionKey)
                                    showExplanation = true
                                }
                                .testTag("option_${optionKey}_${question.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                hasAnswered && isCorrectOption -> EmeraldSuccess
                                                hasAnswered && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionKey,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (hasAnswered && (isCorrectOption || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                if (hasAnswered && isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "صحيح",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (hasAnswered && isSelected && !isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "خطأ",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Essay Question: Toggle Model Answer
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "سؤال تحريري / مسألة تطبيقية",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { showExplanation = !showExplanation }) {
                                Icon(
                                    imageVector = if (showExplanation) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showExplanation) "إخفاء الحل النموذجي" else "عرض الحل النموذجي")
                            }
                        }

                        if (showExplanation && question.correctAnswer.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSuccess.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "الجواب النموذجي:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = question.correctAnswer,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Explanation Section
            AnimatedVisibility(visible = showExplanation && question.explanation.isNotBlank()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Surface(
                            shape = CircleShape,
                            color = RoyalBlue600.copy(alpha = 0.15f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💡", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "طريقة الحل والشرح المفصل:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue600
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
