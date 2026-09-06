package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.models.BacExercise
import com.example.data.models.BacExamTopic
import com.example.data.models.BacSimulatorData
import com.example.data.models.BacSubjectExam
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.CrimsonError500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600
import kotlinx.coroutines.delay

enum class BacSimPhase {
    SELECT_EXAM,
    READING_AND_CHOICE, // 30 min reading topic 1 & 2
    LIVE_EXAM,          // Timer countdown
    RESULT_SCORING      // Marking scheme review & self grading
}

@Composable
fun BacSimulatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(BacSimPhase.SELECT_EXAM) }
    var selectedExam by remember { mutableStateOf(BacSimulatorData.officialExams.first()) }
    var chosenTopicNumber by remember { mutableIntStateOf(1) }
    var remainingSeconds by remember { mutableIntStateOf(selectedExam.durationMinutes * 60) }
    var totalExamSeconds by remember { mutableIntStateOf(selectedExam.durationMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var userSelfScore by remember { mutableFloatStateOf(16.5f) }

    // Timer loop
    LaunchedEffect(isTimerRunning, remainingSeconds) {
        if (isTimerRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
            if (remainingSeconds <= 0) {
                isTimerRunning = false
                phase = BacSimPhase.RESULT_SCORING
            }
        }
    }

    when (phase) {
        BacSimPhase.SELECT_EXAM -> {
            BacSelectExamView(
                exams = BacSimulatorData.officialExams,
                onSelectExam = { exam ->
                    selectedExam = exam
                    totalExamSeconds = exam.durationMinutes * 60
                    remainingSeconds = totalExamSeconds
                    phase = BacSimPhase.READING_AND_CHOICE
                },
                onBack = onBack,
                modifier = modifier
            )
        }

        BacSimPhase.READING_AND_CHOICE -> {
            BacReadingChoiceView(
                exam = selectedExam,
                onTopicSelected = { topicNum ->
                    chosenTopicNumber = topicNum
                    isTimerRunning = true
                    phase = BacSimPhase.LIVE_EXAM
                },
                onCancel = { phase = BacSimPhase.SELECT_EXAM },
                modifier = modifier
            )
        }

        BacSimPhase.LIVE_EXAM -> {
            val currentTopic = if (chosenTopicNumber == 1) selectedExam.topic1 else selectedExam.topic2
            BacLiveExamView(
                exam = selectedExam,
                topic = currentTopic,
                remainingSeconds = remainingSeconds,
                totalSeconds = totalExamSeconds,
                isTimerRunning = isTimerRunning,
                onTogglePause = { isTimerRunning = !isTimerRunning },
                onSubmitExam = {
                    isTimerRunning = false
                    phase = BacSimPhase.RESULT_SCORING
                },
                modifier = modifier
            )
        }

        BacSimPhase.RESULT_SCORING -> {
            val currentTopic = if (chosenTopicNumber == 1) selectedExam.topic1 else selectedExam.topic2
            BacScoringView(
                exam = selectedExam,
                topic = currentTopic,
                userScore = userSelfScore,
                onScoreChange = { userSelfScore = it },
                onFinish = {
                    phase = BacSimPhase.SELECT_EXAM
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun BacSelectExamView(
    exams: List<BacSubjectExam>,
    onSelectExam: (BacSubjectExam) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "⏱️ محاكي البكالوريا الحقيقي",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "اختبارات رسمية مطابقة للتوقيت والظروف الرسمية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stress test explanation card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AmberGold500.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎯", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "كسر حاجز الخوف والضغط النفسي",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "تدرب على اختيار الموضوع وإدارة الساعات الـ 3.5 بصرامة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RoyalBlue600.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LockClock, contentDescription = null, tint = RoyalBlue600, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "المحاكي يتضمن: 30 دقيقة اختيار الموضوع + مؤقت رسمي + سلم التنقيط الرسمي بالكسور الجزئية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoyalBlue600
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "اختر مادة الامتحان التجريبي:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(exams) { exam ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_card_${exam.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = exam.subject.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = exam.subject.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = exam.subject.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "⏱️ ${exam.durationMinutes / 60} ساعات و ${exam.durationMinutes % 60} دقيقة",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberGold500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = exam.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المسار: ${exam.stream}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "الموضوع 1: ${exam.topic1.exercises.size} تمارين",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "الموضوع 2: ${exam.topic2.exercises.size} تمارين",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onSelectExam(exam) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Text(text = "دخول قاعة الامتحان والمحاكاة 🚀", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BacReadingChoiceView(
    exam: BacSubjectExam,
    onTopicSelected: (Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var readingSeconds by remember { mutableIntStateOf(30 * 60) }
    var selectedTopicNumber by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (readingSeconds > 0) {
            delay(1000)
            readingSeconds -= 1
        }
    }

    val minutes = readingSeconds / 60
    val seconds = readingSeconds % 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "إلغاء")
                }
                Text(
                    text = "مرحلة قراءة واختيار الموضوع",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CrimsonError500.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "⏳ ${String.format("%02d:%02d", minutes, seconds)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = CrimsonError500,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = AmberGold500.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, AmberGold500.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نصيحة هامة: خذ 15 دقيقة لقراءة الموضوعين جيداً، واختر الموضوع الذي تضمن فيه أعلى علامة في التمرين الأكبر.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Topic 1 Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTopicNumber = 1 },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTopicNumber == 1) RoyalBlue600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    2.dp,
                    if (selectedTopicNumber == 1) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الموضوع الأول (01)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTopicNumber == 1) RoyalBlue600 else MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedTopicNumber == 1) {
                            Text(text = "تم التحديد ✅", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue600)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exam.topic1.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exam.topic1.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Topic 2 Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTopicNumber = 2 },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTopicNumber == 2) RoyalBlue600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    2.dp,
                    if (selectedTopicNumber == 2) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الموضوع الثاني (02)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTopicNumber == 2) RoyalBlue600 else MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedTopicNumber == 2) {
                            Text(text = "تم التحديد ✅", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue600)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exam.topic2.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exam.topic2.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val chosenTitle = if (selectedTopicNumber == 1) "الأول" else "الثاني"
        Button(
            onClick = { onTopicSelected(selectedTopicNumber) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
        ) {
            Text(
                text = "تأكيد اختيار الموضوع $chosenTitle وبدء كتابة الامتحان ✍️",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun BacLiveExamView(
    exam: BacSubjectExam,
    topic: BacExamTopic,
    remainingSeconds: Int,
    totalSeconds: Int,
    isTimerRunning: Boolean,
    onTogglePause: () -> Unit,
    onSubmitExam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Exam Live Timer Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exam.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onTogglePause) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTimerRunning) "إيقاف مؤقت" else "استئناف",
                                tint = RoyalBlue600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (remainingSeconds < 900) CrimsonError500 else RoyalBlue600
                    )
                    Text(
                        text = "الوقت المتبقي في الامتحان الرسمي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (remainingSeconds < 900) CrimsonError500 else RoyalBlue600,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Chosen Topic Title
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = RoyalBlue600.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📄", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue600
                        )
                        Text(
                            text = topic.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Exercises
        items(topic.exercises) { exercise ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exercise.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${exercise.points} نقاط",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberGold500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = exercise.questionsText,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = onSubmitExam,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_bac_exam_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسليم ورقة الإجابة والاطلاع على سلم التنقيط الرسمي 📝",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun BacScoringView(
    exam: BacSubjectExam,
    topic: BacExamTopic,
    userScore: Float,
    onScoreChange: (Float) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, EmeraldSuccess500.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_mutafawweq_1788679709620),
                        contentDescription = "شعار التطبيق",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "سلم التنقيط الرسمي والحل النموذجي",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "قارن إجابتك خطوة بخطوة مع معايير التصحيح المعتمدة وسجل علامتك التقديرية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = EmeraldSuccess500.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "علامتك التقديرية:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${String.format("%.1f", userScore)} / 20",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = EmeraldSuccess500
                            )
                        }
                    }
                }
            }
        }

        items(topic.exercises) { exercise ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📌 ${exercise.title}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue600
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📖 الحل النموذجي المفصل:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = exercise.modelSolution,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "معايير سلم التنقيط الرسمي الجزئي:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    exercise.markingScheme.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.criteria}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess500.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "+${item.points}ن",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldSuccess500,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
            ) {
                Text(text = "إنهاء وحفظ النتيجة في سجل التقدم 🎯", fontWeight = FontWeight.Bold)
            }
        }
    }
}
