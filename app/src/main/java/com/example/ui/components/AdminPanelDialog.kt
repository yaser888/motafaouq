package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import com.example.ui.MainViewModel
import com.example.ui.VerifiedQuestion
import com.example.data.db.QuestionEntity
import com.example.data.db.QuestionFeedbackEntity
import com.example.data.models.EducationalStream
import com.example.data.models.PlanConfig
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelDialog(
    feedbacks: List<QuestionFeedbackEntity>,
    questions: List<QuestionEntity>,
    planConfig: PlanConfig,
    onDismiss: () -> Unit,
    onUpdateFeedbackStatus: (id: Long, status: String, reply: String) -> Unit,
    onDeleteFeedback: (id: Long) -> Unit,
    onEditQuestion: (QuestionEntity) -> Unit,
    onOpenPlanSetup: () -> Unit
) {
    var currentTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ملاحظات الأسئلة (${feedbacks.size})", "التحكم بالخطة المكثفة", "لوحة الويب السحابية", "استخراج وتحقق PDF", "اختبار التحدي")

    var editingQuestionTarget by remember { mutableStateOf<QuestionEntity?>(null) }
    var filterFeedbackStatus by remember { mutableStateOf("ALL") }

    val filteredFeedbacks = remember(feedbacks, filterFeedbackStatus) {
        if (filterFeedbackStatus == "ALL") feedbacks
        else feedbacks.filter { it.status == filterFeedbackStatus }
    }

    val dateFormatter = remember {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("ar"))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("admin_panel_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_mutafawweq_1788679709620),
                                contentDescription = "شعار التطبيق",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "لوحة تحكم الأدمن والمشرف 🛡️",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "التحكم الكامل بملاحظات الطلاب وبنك الأسئلة والخطط المكثفة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_admin_panel")) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }
                }

                // Tabs Row
                ScrollableTabRow(
                    selectedTabIndex = currentTab,
                    edgePadding = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }
                }

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp)
                ) {
                    when (currentTab) {
                        0 -> {
                            // Feedback & Reports Management Tab
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Filter chips
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الحالة:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    listOf(
                                        "ALL" to "الكل (${feedbacks.size})",
                                        "PENDING" to "قيد المراجعة 🟡",
                                        "FIXED" to "تم التصحيح 🟢",
                                        "REVIEWED" to "تم الفحص 🔵"
                                    ).forEach { (key, label) ->
                                        FilterChip(
                                            selected = filterFeedbackStatus == key,
                                            onClick = { filterFeedbackStatus = key },
                                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (filteredFeedbacks.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Feedback,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "لا توجد ملاحظات أو بلاغات حالياً بهذه الحالة",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(filteredFeedbacks, key = { it.id }) { item ->
                                            val relatedQuestion = questions.firstOrNull { it.id == item.questionId }
                                            FeedbackAdminCard(
                                                feedback = item,
                                                question = relatedQuestion,
                                                dateFormatted = dateFormatter.format(Date(item.timestamp)),
                                                onStatusChange = { newStatus, reply ->
                                                    onUpdateFeedbackStatus(item.id, newStatus, reply)
                                                },
                                                onDelete = { onDeleteFeedback(item.id) },
                                                onEditQuestion = {
                                                    if (relatedQuestion != null) {
                                                        editingQuestionTarget = relatedQuestion
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Intensive Study Plan Controller Tab
                            IntensivePlanAdminView(
                                planConfig = planConfig,
                                onOpenPlanSetup = onOpenPlanSetup
                            )
                        }

                        2 -> {
                            // Web Admin Link & Integration Tab
                            WebAdminInfoView(
                                questionsCount = questions.size,
                                feedbacksCount = feedbacks.size
                            )
                        }
                        3 -> {
                            // Smart Verification Tab
                            PdfVerificationTab()
                        }
                    }
                }
            }

            // Edit Question In-Place Modal
            if (editingQuestionTarget != null) {
                AdminEditQuestionDialog(
                    question = editingQuestionTarget!!,
                    onDismiss = { editingQuestionTarget = null },
                    onSaveQuestion = { updatedQ ->
                        onEditQuestion(updatedQ)
                        editingQuestionTarget = null
                    }
                )
            }
        }
    }
}

@Composable
fun FeedbackAdminCard(
    feedback: QuestionFeedbackEntity,
    question: QuestionEntity?,
    dateFormatted: String,
    onStatusChange: (newStatus: String, reply: String) -> Unit,
    onDelete: () -> Unit,
    onEditQuestion: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf(feedback.adminReply) }
    var showReplyInput by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(
            1.dp,
            when (feedback.status) {
                "FIXED" -> EmeraldSuccess500
                "REVIEWED" -> RoyalBlue600
                else -> AmberGold500
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Question ID, Subject, Status Badge, Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "سؤال #${feedback.questionId} (${feedback.subject})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                val (statusLabel, statusColor) = when (feedback.status) {
                    "FIXED" -> "تم التصحيح 🟢" to EmeraldSuccess500
                    "REVIEWED" -> "تم الفحص 🔵" to RoyalBlue600
                    "REJECTED" -> "مرفوض 🔴" to MaterialTheme.colorScheme.error
                    else -> "قيد المراجعة 🟡" to AmberGold500
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason & Single Chosen Option
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "السبب: ${feedback.feedbackReason}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!feedback.selectedOption.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "إجابة الطالب المختارة: الخيار ${feedback.selectedOption}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Student Note
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ملاحظة الطالب:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = feedback.noteText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (question != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "نص السؤال الحالي: ${question.questionText}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    maxLines = if (isExpanded) 10 else 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Admin Reply Section if exists
            if (feedback.adminReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSuccess500.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "رد المشرف: ${feedback.adminReply}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = EmeraldSuccess500
                        )
                    }
                }
            }

            if (showReplyInput) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("اكتب رد المشرف للطالب...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (question != null) {
                    Button(
                        onClick = onEditQuestion,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعديل السؤال ✏️", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Button(
                    onClick = {
                        onStatusChange("FIXED", if (replyText.isNotBlank()) replyText else "تم تدقيق السؤال وتصحيحه بنجاح.")
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اعتماد وتصحيح ✅", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { showReplyInput = !showReplyInput },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(if (showReplyInput) "إخفاء الرد" else "رد 💬", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف البلاغ", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun IntensivePlanAdminView(
    planConfig: PlanConfig,
    onOpenPlanSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚙️ التحكم ببرامج الخطط المكثفة للطلاب",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "المرحلة الحالية: ${planConfig.stream.badge} ${planConfig.stream.title}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "إجمالي الأيام الموزعة: ${planConfig.totalDays} يوماً دراسياً مكثفاً",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenPlanSetup,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة ضبط الخطة والتواريخ والمرحلة 🔄")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "المراحل الدراسية الخاضعة للإدارة (${EducationalStream.entries.size} مراحل):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        EducationalStream.entries.forEach { stream ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${stream.badge} ${stream.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "المواد الموزعة (${stream.subjects.size} مواد): " +
                                stream.subjects.joinToString("، ") { it.arabicName },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WebAdminInfoView(
    questionsCount: Int,
    feedbacksCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = RoyalBlue600)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لوحة التحكم السحابية (Web Admin Panel)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "يتوفر نظام ويب متكامل في مجلد /web-admin/index.html يتيح استخراج الامتحانات من PDF وصور الهاتف عبر تقنية OCR وتحليل معدل أخطاء الطلاب وملاحظاتهم في السحابة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📊 إحصائيات النظام الحالية:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("• إجمالي بنك الأسئلة: $questionsCount سؤال")
                Text("• إجمالي ملاحظات وبلاغات الطلاب: $feedbacksCount ملاحظة")
                Text("• حالة الاتصال السحابي: متصل ومزامن 🟢")
            }
        }
    }
}

@Composable
fun AdminEditQuestionDialog(
    question: QuestionEntity,
    onDismiss: () -> Unit,
    onSaveQuestion: (QuestionEntity) -> Unit
) {
    var questionText by remember { mutableStateOf(question.questionText) }
    var optA by remember { mutableStateOf(question.optionA) }
    var optB by remember { mutableStateOf(question.optionB) }
    var optC by remember { mutableStateOf(question.optionC) }
    var optD by remember { mutableStateOf(question.optionD) }
    var correctAns by remember { mutableStateOf(question.correctAnswer) }
    var explanation by remember { mutableStateOf(question.explanation) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "تعديل السؤال #${question.id} وتصحيح الخطأ",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("نص السؤال") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (question.questionType == "MCQ") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = optA,
                            onValueChange = { optA = it },
                            label = { Text("خيار A") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = optB,
                            onValueChange = { optB = it },
                            label = { Text("خيار B") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = optC,
                            onValueChange = { optC = it },
                            label = { Text("خيار C") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = optD,
                            onValueChange = { optD = it },
                            label = { Text("خيار D") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = correctAns,
                        onValueChange = { correctAns = it },
                        label = { Text("الخيار الصحيح (A, B, C, D)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("الشرح والحل المفصل") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val updated = question.copy(
                                questionText = questionText,
                                optionA = optA,
                                optionB = optB,
                                optionC = optC,
                                optionD = optD,
                                correctAnswer = correctAns,
                                explanation = explanation
                            )
                            onSaveQuestion(updated)
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500)
                    ) {
                        Text("حفظ التعديل ✅")
                    }
                }
            }
        }
    }
}

@Composable
fun PdfVerificationTab(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var selectedProvider by remember { mutableStateOf("Gemini") }
    var apiKey by remember { mutableStateOf("") }
    var modelId by remember { mutableStateOf("google/gemini-2.5-flash") }
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.processAndVerifyPdf(context, uri, null, selectedProvider, apiKey, modelId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("التحقق الذكي ومنع التكرار", style = MaterialTheme.typography.titleLarge)
        Text("اختر مزود الذكاء الاصطناعي لاستخراج الأسئلة من الـ PDF:", style = MaterialTheme.typography.bodyMedium)
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedProvider == "Gemini",
                onClick = { selectedProvider = "Gemini" },
                label = { Text("جيمناي (مدمج)") }
            )
            FilterChip(
                selected = selectedProvider == "OpenRouter",
                onClick = { selectedProvider = "OpenRouter" },
                label = { Text("أوبن راوتر (OpenRouter)") }
            )
        }
        
        if (selectedProvider == "OpenRouter") {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("مفتاح API الخاص بـ OpenRouter") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = modelId,
                onValueChange = { modelId = it },
                label = { Text("معرف النموذج (مثال: google/gemini-2.5-flash)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        Button(onClick = { launcher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text("اختيار ملف PDF والبدء")
        }

        if (uiState.isExtractingPdf) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("جاري الاستخراج والتحقق...", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.verifiedPdfQuestions.isNotEmpty()) {
            Text("نتائج التحقق (${uiState.verifiedPdfQuestions.size} سؤال):", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.verifiedPdfQuestions) { vq ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (vq.isDuplicate) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(vq.question.subject, fontWeight = FontWeight.Bold)
                                if (vq.isDuplicate) {
                                    Text("مكرر ⚠️", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("جديد ✅", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(vq.question.questionText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.clearVerifiedQuestions() }, modifier = Modifier.weight(1f)) {
                    Text("إلغاء")
                }
                Button(
                    onClick = {
                        val newQuestions = uiState.verifiedPdfQuestions.filter { !it.isDuplicate }.map { it.question }
                        viewModel.publishVerifiedQuestions(newQuestions)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("نشر الأسئلة الجديدة")
                }
            }
        }
    }
}

@Composable
fun ChallengeControlTab(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("إرسال اختبار تحدي للطلاب 🚀", style = MaterialTheme.typography.titleLarge)
        Text("قم بإنشاء اختبار سريع مبني على أكثر 10 أسئلة سجلت فيها أخطاء عالية، وأرسله كإشعار فوري لجميع الطلاب لاختبار تركيزهم واستدراك الأخطاء الشائعة.", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.sendChallengeTest() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("إرسال إشعار التحدي الآن", style = MaterialTheme.typography.titleMedium)
        }
    }
}
