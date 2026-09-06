package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.OverallStats
import com.example.data.StudyPlanData
import com.example.data.db.StudyProgressEntity
import com.example.data.models.DayTask
import com.example.data.models.ElevationStat
import com.example.data.models.MonthInfo
import com.example.ui.AppTab
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600

@Composable
fun DashboardScreen(
    stats: OverallStats,
    elevationList: List<ElevationStat>,
    progressList: List<StudyProgressEntity>,
    onNavigateToTab: (AppTab) -> Unit,
    onSelectMonth: (Int) -> Unit,
    onStartFocus: () -> Unit,
    onTogglePeriod1: ((String, Boolean) -> Unit)? = null,
    onTogglePeriod2: ((String, Boolean) -> Unit)? = null,
    onTogglePeriod3: ((String, Boolean) -> Unit)? = null,
    onToggleCards: ((String, Boolean) -> Unit)? = null,
    onToggleRecitation: ((String, Boolean) -> Unit)? = null,
    onToggleAllDay: ((String, Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progressMap = remember(progressList) { progressList.associateBy { it.dayId } }

    val animatedOverall by animateFloatAsState(
        targetValue = stats.overallPercentage / 100f,
        animationSpec = tween(1000),
        label = "overall_progress"
    )

    // First unfinished task or first task of current week
    val activeDayTask = remember(progressList) {
        StudyPlanData.allTasks.firstOrNull { task ->
            if (task.isRestDay) false
            else {
                val p = progressMap[task.id]
                val done = p?.period1Done == true && p.period2Done == true &&
                        p.period3Done == true && p.cardsDone == true && p.recitationDone == true
                !done
            }
        } ?: StudyPlanData.allTasks.first()
    }

    val activeProgress = progressMap[activeDayTask.id] ?: StudyProgressEntity(dayId = activeDayTask.id)
    val isActiveAllDone = activeProgress.period1Done && activeProgress.period2Done &&
            activeProgress.period3Done && activeProgress.cardsDone && activeProgress.recitationDone

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Hero Study Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_study_banner_1788679732294),
                        contentDescription = "خطة البكالوريا المتفوق",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC0F172A),
                                        Color(0xF00F172A)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold500.copy(alpha = 0.95f)
                        ) {
                            Text(
                                text = "خطة الـ 7 إلى 9 أشهر • بكالوريا علمي",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "طريقك نحو المجموع التام والتفوق الدراسي",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Main Progress Card (نسبة كم ماشي بالخطة)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overall_progress_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "نسبة الإنجاز الكلية في الخطة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مجموع الحصص والمهام المكتملة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue600.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${String.format("%.1f", stats.overallPercentage)}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalBlue600
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular & Linear Combined Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedOverall },
                                modifier = Modifier.fillMaxSize(),
                                color = RoyalBlue600,
                                strokeWidth = 8.dp,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الحصص المنجزة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${stats.completedTasks} من ${stats.totalTasks}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { animatedOverall },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = RoyalBlue600,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldSuccess500.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "36 أسبوعاً",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess500,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberGold500.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "9 أشهر كاملة",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberGold500,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTab(AppTab.PLAN) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_plan"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح جدول الخطة")
                        }

                        OutlinedButton(
                            onClick = { onNavigateToTab(AppTab.ELEVATION) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_elevation"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جدول الارتفاع")
                        }
                    }
                }
            }
        }

        // Today's Interactive Study Task Card (قائمة مهام اليوم التفاعلية)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        tint = RoyalBlue600,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الدروس المستهدفة لليوم (${activeDayTask.dayOfWeek})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                TextButton(onClick = {
                    onSelectMonth(activeDayTask.monthNumber)
                    onNavigateToTab(AppTab.PLAN)
                }) {
                    Text("عرض الخطة كاملة")
                }
            }
        }

        item {
            DayCard(
                task = activeDayTask,
                progress = activeProgress,
                isCustomized = false,
                onTogglePeriod1 = { onTogglePeriod1?.invoke(activeDayTask.id, activeProgress.period1Done) },
                onTogglePeriod2 = { onTogglePeriod2?.invoke(activeDayTask.id, activeProgress.period2Done) },
                onTogglePeriod3 = { onTogglePeriod3?.invoke(activeDayTask.id, activeProgress.period3Done) },
                onToggleCards = { onToggleCards?.invoke(activeDayTask.id, activeProgress.cardsDone) },
                onToggleRecitation = { onToggleRecitation?.invoke(activeDayTask.id, activeProgress.recitationDone) },
                onToggleAllDay = { onToggleAllDay?.invoke(activeDayTask.id, !isActiveAllDone) },
                onOpenNoteDialog = {
                    onSelectMonth(activeDayTask.monthNumber)
                    onNavigateToTab(AppTab.PLAN)
                },
                onEditTask = {
                    onSelectMonth(activeDayTask.monthNumber)
                    onNavigateToTab(AppTab.PLAN)
                }
            )
        }

        // Focus Mode Quick Callout
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartFocus() }
                    .testTag("focus_callout_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = "دخول التركيز",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "وضع دخول التركيز وقفل المشتتات",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "شاشة أبيض وأسود، قفل التطبيقات، وقفل الوايفاي للدراسة الصارمة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "بدء",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Months Progress Overview (أشهر الخطة التسعة)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أشهر الخطة الدراسية (9 أشهر)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "اختر الشهر للتفاصيل",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // List of 9 Months Cards
        items(StudyPlanData.months) { monthInfo ->
            val stat = elevationList.firstOrNull { it.monthNumber == monthInfo.monthNumber }
            val currentRate = stat?.currentRate ?: 0f

            InteractiveMonthCard(
                monthInfo = monthInfo,
                currentRate = currentRate,
                onClick = {
                    onSelectMonth(monthInfo.monthNumber)
                    onNavigateToTab(AppTab.PLAN)
                }
            )
        }
    }
}

@Composable
fun InteractiveMonthCard(
    monthInfo: MonthInfo,
    currentRate: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "month_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("month_card_${monthInfo.monthNumber}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${monthInfo.monthNumber}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = monthInfo.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الأسابيع ${monthInfo.startWeek} - ${monthInfo.endWeek}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Percentage Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (currentRate >= 100f) EmeraldSuccess500.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${String.format("%.1f", currentRate)}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (currentRate >= 100f) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = monthInfo.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { currentRate / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (currentRate >= 100f) EmeraldSuccess500 else RoyalBlue600,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
