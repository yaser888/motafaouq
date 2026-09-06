package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import com.example.data.StudyPlanData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OverallStats
import com.example.data.SubjectProgress
import com.example.data.models.ElevationStat
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue400
import com.example.ui.theme.RoyalBlue600

@Composable
fun ElevationChartScreen(
    stats: OverallStats,
    elevationList: List<ElevationStat>,
    subjectProgressList: List<SubjectProgress>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // Top Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elevation_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = "نسبة الارتفاع",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "جدول وإحصائيات نسبة الارتفاع",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "رصد وتيرة الصعود والتقدم التراكمي في خطة البكالوريا",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Visual Growth & Elevation Chart (منحنى الصعود)
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elevation_curve_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = RoyalBlue600
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "منحنى الارتفاع التراكمي (9 أشهر)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalBlue600.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "الهدف: 100%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue600
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas Elevation Chart
                    ElevationCurveCanvas(
                        elevationList = elevationList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (m in StudyPlanData.months) {
                            Text(
                                text = m.name.take(3),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Elevation Table (جدول نسبة الارتفاع) - Explicitly requested by user
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elevation_table_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = AmberGold500
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "جدول تمييز نسبة الارتفاع الشهري",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Container with Horizontal Scroll
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    ) {
                        // Table Header Row
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الشهر",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(90.dp),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = "المنجز %",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "المستهدف",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "الحصص",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(85.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "حالة الارتفاع",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(110.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Table Body Rows
                        elevationList.forEachIndexed { index, stat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${stat.monthNumber}. ${stat.monthName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.width(90.dp),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = "${String.format("%.1f", stat.currentRate)}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (stat.currentRate >= 100f) EmeraldSuccess500 else RoyalBlue600
                                    ),
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "100%",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(75.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${stat.completedTasks}/${stat.totalTasks}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(85.dp),
                                    textAlign = TextAlign.Center
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        stat.currentRate >= 100f -> EmeraldSuccess500.copy(alpha = 0.15f)
                                        stat.currentRate >= 50f -> RoyalBlue600.copy(alpha = 0.15f)
                                        stat.currentRate > 0f -> AmberGold500.copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    modifier = Modifier.width(110.dp)
                                ) {
                                    Text(
                                        text = stat.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                stat.currentRate >= 100f -> EmeraldSuccess500
                                                stat.currentRate >= 50f -> RoyalBlue600
                                                stat.currentRate > 0f -> AmberGold500
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        ),
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            if (index < elevationList.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Subject Progress Breakdown
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subject_progress_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            tint = RoyalBlue600
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نسبة إنجاز المواد الدراسية",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    subjectProgressList.forEach { subj ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(subj.subject.colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = subj.subject.arabicName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "${String.format("%.1f", subj.percentage)}% (${subj.completedSlots}/${subj.totalSlots})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(subj.subject.colorHex)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { subj.percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(subj.subject.colorHex),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElevationCurveCanvas(
    elevationList: List<ElevationStat>,
    modifier: Modifier = Modifier
) {
    val primaryColor = RoyalBlue600
    val gridColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 20f

        // Draw horizontal grid lines (0%, 25%, 50%, 75%, 100%)
        for (i in 0..4) {
            val y = height - padding - (i * (height - 2 * padding) / 4)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        if (elevationList.isEmpty()) return@Canvas

        val stepX = (width - 2 * padding) / (elevationList.size - 1).coerceAtLeast(1)
        val path = Path()
        val fillPath = Path()

        val points = elevationList.mapIndexed { index, stat ->
            val x = padding + index * stepX
            val normalizedRate = (stat.currentRate / 100f).coerceIn(0f, 1f)
            val y = (height - padding) - (normalizedRate * (height - 2 * padding))
            Offset(x, y)
        }

        // Draw Bars & Line
        points.forEachIndexed { i, pt ->
            // Background bar
            val barWidth = 14f
            val baseLineY = height - padding
            drawRect(
                color = gridColor.copy(alpha = 0.5f),
                topLeft = Offset(pt.x - barWidth / 2, padding),
                size = Size(barWidth, baseLineY - padding)
            )
            // Filled bar
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, RoyalBlue400),
                    startY = pt.y,
                    endY = baseLineY
                ),
                topLeft = Offset(pt.x - barWidth / 2, pt.y),
                size = Size(barWidth, baseLineY - pt.y)
            )
        }

        // Draw connecting curve
        points.forEachIndexed { index, pt ->
            if (index == 0) {
                path.moveTo(pt.x, pt.y)
                fillPath.moveTo(pt.x, height - padding)
                fillPath.lineTo(pt.x, pt.y)
            } else {
                val prev = points[index - 1]
                val controlX1 = (prev.x + pt.x) / 2
                path.cubicTo(controlX1, prev.y, controlX1, pt.y, pt.x, pt.y)
                fillPath.cubicTo(controlX1, prev.y, controlX1, pt.y, pt.x, pt.y)
            }
        }

        // Stroke line
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Draw point dots
        points.forEach { pt ->
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = pt
            )
            drawCircle(
                color = primaryColor,
                radius = 3.5f,
                center = pt
            )
        }
    }
}
