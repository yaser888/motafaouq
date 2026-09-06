package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.MistakeItem
import com.example.data.models.MistakeVaultData
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.CrimsonError500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600

@Composable
fun MistakeVaultScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mistakesList = remember { mutableStateListOf(*MistakeVaultData.initialMistakes.toTypedArray()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedTabFilter by remember { mutableStateOf("ALL") } // ALL, DUE_TODAY, IN_PROGRESS, MASTERED
    var isFlashReviewActive by remember { mutableStateOf(false) }
    var isAddMistakeDialogOpen by remember { mutableStateOf(false) }

    val filteredMistakes = mistakesList.filter { item ->
        val subjectMatch = selectedSubject == null || item.subject == selectedSubject
        val tabMatch = when (selectedTabFilter) {
            "DUE_TODAY" -> item.nextReviewDate == "اليوم" && !item.isMastered
            "IN_PROGRESS" -> !item.isMastered
            "MASTERED" -> item.isMastered
            else -> true
        }
        subjectMatch && tabMatch
    }

    val masteredCount = mistakesList.count { it.isMastered }
    val dueTodayCount = mistakesList.count { it.nextReviewDate == "اليوم" && !it.isMastered }
    val totalCount = mistakesList.size

    if (isFlashReviewActive) {
        MistakeFlashReviewSession(
            items = if (filteredMistakes.isNotEmpty()) filteredMistakes else mistakesList,
            onFinish = { isFlashReviewActive = false },
            onUpdateProgress = { id, isCorrect ->
                val index = mistakesList.indexOfFirst { it.id == id }
                if (index != -1) {
                    val current = mistakesList[index]
                    val newConsecutive = if (isCorrect) current.consecutiveCorrect + 1 else 0
                    val newLevel = if (isCorrect) (current.repetitionLevel + 1).coerceAtMost(4) else 1
                    val isNowMastered = newConsecutive >= 3 || newLevel >= 4
                    mistakesList[index] = current.copy(
                        consecutiveCorrect = newConsecutive,
                        repetitionLevel = newLevel,
                        isMastered = isNowMastered,
                        nextReviewDate = if (isNowMastered) "متقن ومثبت 🎉" else if (newLevel == 2) "بعد 3 أيام" else "بعد أسبوع"
                    )
                }
            }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("mistake_vault_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "🧠 دفتر الأخطاء الذكي",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "التكرار المتباعد لتثبيت المفاهيم الصعبة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { isAddMistakeDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "إضافة فخ",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Stats KPI Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(RoyalBlue600.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🛡️", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "خزنة حماية النقاط في البكالوريا",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "تحويل كل خطأ إلى 3 نقاط مضمونة يوم الامتحان",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = CrimsonError500.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$dueTodayCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = CrimsonError500)
                                Text(text = "مستحق اليوم ⚡", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold500.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${totalCount - masteredCount}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = AmberGold500)
                                Text(text = "قيد التثبيت ⏳", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess500.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$masteredCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = EmeraldSuccess500)
                                Text(text = "تم إتقانه 🎉", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { isFlashReviewActive = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_flash_review_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بدء جلسة مراجعة التكرار المتباعد السريعة",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "الكل ($totalCount)",
                    "DUE_TODAY" to "اليوم ($dueTodayCount)",
                    "IN_PROGRESS" to "قيد التكرار (${totalCount - masteredCount})",
                    "MASTERED" to "متقن ($masteredCount)"
                ).forEach { (key, label) ->
                    val isSelected = selectedTabFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTabFilter = key },
                        label = { Text(text = label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue600.copy(alpha = 0.15f),
                            selectedLabelColor = RoyalBlue600
                        )
                    )
                }
            }
        }

        // Subject Horizontal Selector
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSubject == null,
                        onClick = { selectedSubject = null },
                        label = { Text("جميع المواد") }
                    )
                }
                items(Subject.entries) { sub ->
                    FilterChip(
                        selected = selectedSubject == sub,
                        onClick = { selectedSubject = if (selectedSubject == sub) null else sub },
                        label = { Text(sub.displayName) }
                    )
                }
            }
        }

        // Mistake Cards List
        if (filteredMistakes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "✨", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد أخطاء في هذا التصنيف حالياً!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "كل ما تجاوب خطأ في بنك الأسئلة أو تضيف فخاً، سيظهر هنا لمراجعته.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredMistakes, key = { it.id }) { item ->
                MistakeVaultCard(
                    item = item,
                    onMarkMastered = {
                        val index = mistakesList.indexOfFirst { it.id == item.id }
                        if (index != -1) {
                            mistakesList[index] = item.copy(
                                isMastered = true,
                                consecutiveCorrect = 3,
                                repetitionLevel = 4,
                                nextReviewDate = "متقن ومثبت 🎉"
                            )
                        }
                    },
                    onDelete = {
                        mistakesList.removeAll { it.id == item.id }
                    }
                )
            }
        }
    }

    // Add Mistake Dialog
    if (isAddMistakeDialogOpen) {
        AddMistakeDialog(
            onDismiss = { isAddMistakeDialogOpen = false },
            onAdd = { newMistake ->
                mistakesList.add(0, newMistake)
                isAddMistakeDialogOpen = false
            }
        )
    }
}

@Composable
fun MistakeVaultCard(
    item: MistakeItem,
    onMarkMastered: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mistake_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isMastered) EmeraldSuccess500.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (item.isMastered) EmeraldSuccess500.copy(alpha = 0.4f) else CrimsonError500.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = item.subject.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.subject.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = item.subject.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.unitOrTopic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (item.isMastered) EmeraldSuccess500.copy(alpha = 0.15f) else AmberGold500.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (item.isMastered) "متقن 100% 🏆" else "تكرار ${item.repetitionLevel}/3 (${item.nextReviewDate})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (item.isMastered) EmeraldSuccess500 else AmberGold500,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.questionText,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "❌ إجابتك الخاطئة السابقة:", style = MaterialTheme.typography.labelSmall, color = CrimsonError500)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = item.studentWrongAnswer, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✅ الإجابة الصحيحة:", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess500)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = item.correctAnswer, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldSuccess500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trap & Root cause banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = CrimsonError500.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, CrimsonError500.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🎯", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "الفخ الشائع وسبب الخطأ:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonError500
                        )
                        Text(
                            text = item.trapReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (item.personalNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = AmberGold500.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, AmberGold500.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ملاحظتك لتفادي الخطأ:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberGold500
                            )
                            Text(
                                text = item.personalNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = RoyalBlue600.copy(alpha = 0.08f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📖 الشرح النموذجي والتبرير الرياضي:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue600
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        text = if (isExpanded) "إخفاء الشرح ▲" else "عرض الشرح النموذجي ▼",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row {
                    if (!item.isMastered) {
                        Button(
                            onClick = onMarkMastered,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "أتقنت السؤال ✅", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MistakeFlashReviewSession(
    items: List<MistakeItem>,
    onFinish: () -> Unit,
    onUpdateProgress: (String, Boolean) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    if (items.isEmpty() || currentIndex >= items.size) {
        // Completion screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉", fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "اكتملت جلسة المراجعة السريعة!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لقد راجعت نقاط الضعف والفخاخ بنجاح وثبتّ المفاهيم في الذاكرة طويلة المدى.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("العودة لدفتر الأخطاء", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val currentItem = items[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFinish) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
            }
            Text(
                text = "بطاقة ${currentIndex + 1} من ${items.size}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = currentItem.subject.color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = currentItem.subject.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = currentItem.subject.color,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / items.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = RoyalBlue600,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = currentItem.unitOrTopic,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.questionText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 28.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!isAnswerRevealed) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAnswerRevealed = true }
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = RoyalBlue600.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, RoyalBlue600.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "👁️", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "فكر في الإجابة ثم اضغط هنا لإظهار الحل والفخ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlue600,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess500.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, EmeraldSuccess500.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "✅ الإجابة الصحيحة:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldSuccess500)
                                Text(text = currentItem.correctAnswer, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = CrimsonError500.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, CrimsonError500.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "⚠️ الفخ الشائع الذي أخطأت فيه سابقاً:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CrimsonError500)
                                Text(text = currentItem.trapReason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue600.copy(alpha = 0.08f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "📖 الشرح والتبرير:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue600)
                                Text(text = currentItem.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        if (isAnswerRevealed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onUpdateProgress(currentItem.id, false)
                        isAnswerRevealed = false
                        currentIndex++
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonError500)
                ) {
                    Text(text = "أحتاج مراجعة لاحقة 🔄", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onUpdateProgress(currentItem.id, true)
                        isAnswerRevealed = false
                        currentIndex++
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500)
                ) {
                    Text(text = "أتقنت الفكرة تماماً ✅", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddMistakeDialog(
    onDismiss: () -> Unit,
    onAdd: (MistakeItem) -> Unit
) {
    var subject by remember { mutableStateOf(Subject.MATH) }
    var topic by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var trapReason by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "➕ إضافة فخ أو سؤال صعب للخزنة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("الوحدة أو الدرس (مثال: الدوال الأسية)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("نص السؤال أو الفكرة الصعبة") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = correctAnswer,
                    onValueChange = { correctAnswer = it },
                    label = { Text("الإجابة الصحيحة") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = trapReason,
                    onValueChange = { trapReason = it },
                    label = { Text("الفخ الشائع أو الخطأ المتكرر") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظتك الذهبية لتفادي الخطأ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("إلغاء") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (questionText.isNotBlank()) {
                                onAdd(
                                    MistakeItem(
                                        id = "mst_custom_${System.currentTimeMillis()}",
                                        subject = subject,
                                        unitOrTopic = if (topic.isNotBlank()) topic else "ملاحظة مضافة",
                                        questionText = questionText,
                                        correctAnswer = correctAnswer,
                                        studentWrongAnswer = "فخ تم تدوينه",
                                        explanation = note,
                                        trapReason = trapReason,
                                        personalNote = note,
                                        repetitionLevel = 1,
                                        consecutiveCorrect = 0,
                                        isMastered = false,
                                        addedDate = "اليوم",
                                        nextReviewDate = "اليوم"
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("حفظ في الخزنة")
                    }
                }
            }
        }
    }
}
