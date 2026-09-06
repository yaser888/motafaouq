package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyPlanData
import com.example.data.db.CustomDayTaskEntity
import com.example.data.db.StudyProgressEntity
import com.example.data.models.DayTask
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600

@Composable
fun StudyPlanScreen(
    allTasks: List<DayTask>,
    customDayTasks: List<CustomDayTaskEntity>,
    selectedMonth: Int,
    selectedWeek: Int,
    selectedSubjectFilter: Subject?,
    searchQuery: String,
    progressList: List<StudyProgressEntity>,
    onSelectMonth: (Int) -> Unit,
    onSelectWeek: (Int) -> Unit,
    onSetSubjectFilter: (Subject?) -> Unit,
    onSetSearchQuery: (String) -> Unit,
    onTogglePeriod1: (String, Boolean) -> Unit,
    onTogglePeriod2: (String, Boolean) -> Unit,
    onTogglePeriod3: (String, Boolean) -> Unit,
    onToggleCards: (String, Boolean) -> Unit,
    onToggleRecitation: (String, Boolean) -> Unit,
    onToggleAllDay: (String, Boolean) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onOpenEditTask: (DayTask) -> Unit,
    onResetAllCustomTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressMap = remember(progressList) { progressList.associateBy { it.dayId } }
    val customMap = remember(customDayTasks) { customDayTasks.associateBy { it.dayId } }
    val currentMonthInfo = StudyPlanData.months.firstOrNull { it.monthNumber == selectedMonth }
        ?: StudyPlanData.months.first()

    var activeNoteDayId by remember { mutableStateOf<String?>(null) }
    var activeNoteText by remember { mutableStateOf("") }
    var showResetCustomConfirm by remember { mutableStateOf(false) }

    // Filter tasks based on month, week, subject, and search query
    val displayedTasks = remember(allTasks, selectedMonth, selectedWeek, selectedSubjectFilter, searchQuery) {
        var tasks = allTasks.filter { it.monthNumber == selectedMonth }

        if (searchQuery.isNotBlank()) {
            tasks = allTasks.filter { task ->
                task.period1Content.contains(searchQuery, ignoreCase = true) ||
                        task.period2Content.contains(searchQuery, ignoreCase = true) ||
                        task.period3Content.contains(searchQuery, ignoreCase = true) ||
                        task.dayOfWeek.contains(searchQuery, ignoreCase = true)
            }
        } else {
            // filter by week
            tasks = tasks.filter { it.weekNumber == selectedWeek }
            // filter by subject if specified
            if (selectedSubjectFilter != null) {
                tasks = tasks.filter {
                    it.period1Subject == selectedSubjectFilter || it.period2Subject == selectedSubjectFilter
                }
            }
        }
        tasks
    }

    // Calculate completion for selected week
    val weekTotalSlots = displayedTasks.filter { !it.isRestDay }.size * 5
    var weekDoneSlots = 0
    for (t in displayedTasks) {
        if (t.isRestDay) continue
        val p = progressMap[t.id]
        if (p?.period1Done == true) weekDoneSlots++
        if (p?.period2Done == true) weekDoneSlots++
        if (p?.period3Done == true) weekDoneSlots++
        if (p?.cardsDone == true) weekDoneSlots++
        if (p?.recitationDone == true) weekDoneSlots++
    }
    val weekPercentage = if (weekTotalSlots > 0) (weekDoneSlots.toFloat() / weekTotalSlots.toFloat()) * 100f else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSetSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plan_search_input"),
                placeholder = { Text("ابحث في الدروس أو المواد أو الأيام...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "بحث")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSetSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Student Customization Control Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("custom_plan_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "تحكم الطالب بالخطة الدراسية",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (customDayTasks.isNotEmpty()) "لديك ${customDayTasks.size} حصة مخصصة وفق جدولك الخاص" else "يمكنك تعديل أي حصة أو تبديل المواد بسهولة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (customDayTasks.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { showResetCustomConfirm = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("reset_all_custom_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استعادة الأصلية", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Month Selector Carousel
        item {
            Column {
                Text(
                    text = "اختر الشهر الدراسي (الخطة المتصاعدة):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("months_selector_row")
                ) {
                    items(StudyPlanData.months) { month ->
                        val isSelected = month.monthNumber == selectedMonth
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) RoyalBlue600 else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectMonth(month.monthNumber) }
                                .testTag("month_chip_${month.monthNumber}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = month.name,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "الشهر ${month.monthNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Current Month Description Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${currentMonthInfo.name}: ${currentMonthInfo.title}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentMonthInfo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Week Selector Row
        item {
            Column {
                Text(
                    text = "اختر الأسبوع:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val startWeek = currentMonthInfo.startWeek
                    val endWeek = currentMonthInfo.endWeek
                    for (weekNum in startWeek..endWeek) {
                        val isSelected = weekNum == selectedWeek
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectWeek(weekNum) }
                                .testTag("week_btn_$weekNum")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "الأسبوع",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$weekNum",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subject Filter Chips
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تصفية حسب المادة:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (selectedSubjectFilter != null) {
                        TextButton(onClick = { onSetSubjectFilter(null) }) {
                            Text("إلغاء التصفية", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedSubjectFilter == null,
                            onClick = { onSetSubjectFilter(null) },
                            label = { Text("الكل") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    items(Subject.entries.filter { it != Subject.GENERAL }) { subject ->
                        FilterChip(
                            selected = selectedSubjectFilter == subject,
                            onClick = {
                                onSetSubjectFilter(if (selectedSubjectFilter == subject) null else subject)
                            },
                            label = { Text(subject.arabicName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(subject.colorHex).copy(alpha = 0.2f),
                                selectedLabelColor = Color(subject.colorHex)
                            )
                        )
                    }
                }
            }
        }

        // Week Progress Summary Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "إنجاز الأسبوع $selectedWeek",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تم إنجاز $weekDoneSlots من أصل $weekTotalSlots حصة ومهمة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (weekPercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (weekPercentage >= 100f) EmeraldSuccess500 else RoyalBlue600,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${weekPercentage.toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (weekPercentage >= 100f) EmeraldSuccess500 else RoyalBlue600
                        )
                    )
                }
            }
        }

        // Days Tasks List
        items(displayedTasks, key = { it.id }) { task ->
            val progress = progressMap[task.id] ?: StudyProgressEntity(dayId = task.id)
            val isCustomized = customMap.containsKey(task.id)

            DayCard(
                task = task,
                progress = progress,
                isCustomized = isCustomized,
                onTogglePeriod1 = { onTogglePeriod1(task.id, progress.period1Done) },
                onTogglePeriod2 = { onTogglePeriod2(task.id, progress.period2Done) },
                onTogglePeriod3 = { onTogglePeriod3(task.id, progress.period3Done) },
                onToggleCards = { onToggleCards(task.id, progress.cardsDone) },
                onToggleRecitation = { onToggleRecitation(task.id, progress.recitationDone) },
                onToggleAllDay = {
                    val allDone = progress.period1Done && progress.period2Done && progress.period3Done && progress.cardsDone && progress.recitationDone
                    onToggleAllDay(task.id, !allDone)
                },
                onOpenNoteDialog = {
                    activeNoteDayId = task.id
                    activeNoteText = progress.note
                },
                onEditTask = { onOpenEditTask(task) }
            )
        }
    }

    // Note Dialog
    if (activeNoteDayId != null) {
        AlertDialog(
            onDismissRequest = { activeNoteDayId = null },
            title = { Text("ملاحظات وتقييم اليوم") },
            text = {
                OutlinedTextField(
                    value = activeNoteText,
                    onValueChange = { activeNoteText = it },
                    placeholder = { Text("اكتب ما أنجزته، الصعوبات التي واجهتك، أو خطتك للتعويض...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )
            },
            confirmButton = {
                Button(onClick = {
                    activeNoteDayId?.let { id -> onSaveNote(id, activeNoteText) }
                    activeNoteDayId = null
                }) {
                    Text("حفظ الملاحظة")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeNoteDayId = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Reset Custom Tasks Confirmation Dialog
    if (showResetCustomConfirm) {
        AlertDialog(
            onDismissRequest = { showResetCustomConfirm = false },
            title = { Text("استعادة الخطة الأصلية") },
            text = { Text("هل تريد إلغاء جميع التخصيصات والعودة إلى جدول الخطة المتصاعدة النموذجية لجميع الأيام؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllCustomTasks()
                        showResetCustomConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("استعادة الخطة الأصلية")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetCustomConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun DayCard(
    task: DayTask,
    progress: StudyProgressEntity,
    isCustomized: Boolean,
    onTogglePeriod1: () -> Unit,
    onTogglePeriod2: () -> Unit,
    onTogglePeriod3: () -> Unit,
    onToggleCards: () -> Unit,
    onToggleRecitation: () -> Unit,
    onToggleAllDay: () -> Unit,
    onOpenNoteDialog: () -> Unit,
    onEditTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAllDone = !task.isRestDay &&
            progress.period1Done &&
            progress.period2Done &&
            progress.period3Done &&
            progress.cardsDone &&
            progress.recitationDone

    val completedCount = listOf(
        progress.period1Done,
        progress.period2Done,
        progress.period3Done,
        progress.cardsDone,
        progress.recitationDone
    ).count { it }

    val totalCount = 5
    val progressFraction = completedCount.toFloat() / totalCount.toFloat()

    val animatedProgressFraction by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "day_progress_fraction"
    )

    val cardBgColor by animateColorAsState(
        targetValue = when {
            task.isRestDay -> AmberGold500.copy(alpha = 0.06f)
            isAllDone -> EmeraldSuccess500.copy(alpha = 0.08f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(350),
        label = "card_bg"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            task.isRestDay -> AmberGold500.copy(alpha = 0.3f)
            isAllDone -> EmeraldSuccess500.copy(alpha = 0.4f)
            isCustomized -> RoyalBlue600.copy(alpha = 0.4f)
            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        },
        animationSpec = tween(350),
        label = "card_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("day_card_${task.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isAllDone) 1.dp else 2.dp),
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAllDone) EmeraldSuccess500 else if (task.isRestDay) AmberGold500 else RoyalBlue600,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isAllDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "مكتمل",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = task.dayOfWeek.take(2),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = task.dayOfWeek,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isCustomized) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = RoyalBlue600.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "مخصصة ✨",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoyalBlue600,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (isAllDone) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess500.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "مكتمل ✓",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess500,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "الأسبوع ${task.weekNumber} • الشهر ${task.monthNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Quick Actions: Edit (Customization) + Note Icon + Mark All Interactive Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEditTask,
                        modifier = Modifier.testTag("btn_edit_task_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = "تخصيص الخطة",
                            tint = if (isCustomized) RoyalBlue600 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onOpenNoteDialog,
                        modifier = Modifier.testTag("btn_note_${task.id}")
                    ) {
                        Icon(
                            imageVector = if (progress.note.isNotBlank()) Icons.Default.Notes else Icons.Default.Edit,
                            contentDescription = "ملاحظات",
                            tint = if (progress.note.isNotBlank()) RoyalBlue600 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!task.isRestDay) {
                        InteractiveActionButton(
                            text = if (isAllDone) "إلغاء الكل" else "تحديد الكل",
                            isHighlight = isAllDone,
                            icon = if (isAllDone) Icons.Default.DoneAll else Icons.Default.Check,
                            onClick = onToggleAllDay,
                            testTag = "toggle_all_day_${task.id}"
                        )
                    }
                }
            }

            // Day Progress Bar (for non-rest days)
            if (!task.isRestDay) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isAllDone) EmeraldSuccess500 else RoyalBlue600,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$completedCount/$totalCount",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isAllDone) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (task.isRestDay) {
                // Rest Day Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AmberGold500.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AmberGold500.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "راحة ومراجعة حرة",
                            tint = AmberGold500,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "يوم راحة ذهنية وتثبيت حر 🌿",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberGold500
                            )
                            Text(
                                text = "استعد طاقتك، مارس الرياضة، وراجع ما تحتاجه دون ضغط المواعيد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                // Interactive 3 Study Periods + Cards & Recitation
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Period 1 (Heavy)
                    InteractiveTaskItem(
                        periodName = "الفترة الأولى (الصباحية - المادة الثقيلة)",
                        timeRange = "2.5 إلى 3.5 ساعات",
                        subject = task.period1Subject,
                        content = task.period1Content,
                        icon = Icons.Default.LightMode,
                        isDone = progress.period1Done,
                        onToggle = onTogglePeriod1,
                        testTag = "task_p1_${task.id}"
                    )

                    // Period 2 (Medium)
                    InteractiveTaskItem(
                        periodName = "الفترة الثانية (الظهر/العصر - المادة المتوسطة)",
                        timeRange = "1.5 إلى 2.5 ساعة",
                        subject = task.period2Subject,
                        content = task.period2Content,
                        icon = Icons.Default.WbSunny,
                        isDone = progress.period2Done,
                        onToggle = onTogglePeriod2,
                        testTag = "task_p2_${task.id}"
                    )

                    // Period 3 (Evening consolidation)
                    InteractiveTaskItem(
                        periodName = "الفترة الثالثة (المساء - التثبيت والاختبار)",
                        timeRange = "1.5 إلى 2 ساعة",
                        subject = Subject.GENERAL,
                        content = task.period3Content,
                        icon = Icons.Default.NightsStay,
                        isDone = progress.period3Done,
                        onToggle = onTogglePeriod3,
                        testTag = "task_p3_${task.id}"
                    )

                    // Extra Habits (Cards & Recitation)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Flashcards
                        InteractiveMiniHabit(
                            title = "البطاقات التعليمية",
                            subtitle = "مراجعة متباعدة",
                            icon = Icons.Default.Style,
                            isDone = progress.cardsDone,
                            onToggle = onToggleCards,
                            modifier = Modifier.weight(1f),
                            testTag = "habit_cards_${task.id}"
                        )

                        // Recitation / Explanation
                        InteractiveMiniHabit(
                            title = "التسميع الذاتي",
                            subtitle = "تقنية فاينمان",
                            icon = Icons.Default.RecordVoiceOver,
                            isDone = progress.recitationDone,
                            onToggle = onToggleRecitation,
                            modifier = Modifier.weight(1f),
                            testTag = "habit_recite_${task.id}"
                        )
                    }
                }
            }

            // Display Note if present
            if (progress.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            tint = RoyalBlue600,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = progress.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTaskItem(
    periodName: String,
    timeRange: String,
    subject: Subject,
    content: String,
    icon: ImageVector,
    isDone: Boolean,
    onToggle: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "interactive_task_scale"
    )

    val itemBgColor by animateColorAsState(
        targetValue = if (isDone) EmeraldSuccess500.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        animationSpec = tween(250),
        label = "task_bg"
    )

    val itemBorderColor by animateColorAsState(
        targetValue = if (isDone) EmeraldSuccess500.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(250),
        label = "task_border"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = itemBgColor,
        border = BorderStroke(1.dp, itemBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Circle (Custom animated)
            Surface(
                shape = CircleShape,
                color = if (isDone) EmeraldSuccess500 else Color.Transparent,
                border = BorderStroke(
                    2.dp,
                    if (isDone) EmeraldSuccess500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                ),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "تم الإنجاز",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Period Info & Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isDone) EmeraldSuccess500 else Color(subject.colorHex),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = periodName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDone) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Time Duration Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDone) EmeraldSuccess500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = timeRange,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isDone) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subject Pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(subject.colorHex).copy(alpha = if (isDone) 0.12f else 0.18f),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = subject.arabicName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(subject.colorHex)
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Lesson Content
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}

@Composable
fun InteractiveMiniHabit(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDone: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "interactive_habit_scale"
    )

    val habitBgColor by animateColorAsState(
        targetValue = if (isDone) EmeraldSuccess500.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(250),
        label = "habit_bg"
    )

    val habitBorderColor by animateColorAsState(
        targetValue = if (isDone) EmeraldSuccess500.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(250),
        label = "habit_border"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = habitBgColor,
        border = BorderStroke(1.dp, habitBorderColor),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            )
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isDone) EmeraldSuccess500 else Color.Transparent,
                border = BorderStroke(
                    1.5.dp,
                    if (isDone) EmeraldSuccess500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                ),
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "تم",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDone) EmeraldSuccess500 else RoyalBlue600,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDone) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun InteractiveActionButton(
    text: String,
    isHighlight: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "action_btn_scale"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isHighlight) EmeraldSuccess500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (isHighlight) EmeraldSuccess500.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isHighlight) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
