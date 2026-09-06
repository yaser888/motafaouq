package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.models.RescueDailyTask
import com.example.data.models.RescueSubjectPriority
import com.example.data.models.SmartRescuePlannerData
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.CrimsonError500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600

@Composable
fun SmartRescuePlannerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var daysRemaining by remember { mutableIntStateOf(75) }
    var dailyHours by remember { mutableIntStateOf(6) }

    var mathReadiness by remember { mutableIntStateOf(50) }
    var physicsReadiness by remember { mutableIntStateOf(45) }
    var scienceReadiness by remember { mutableIntStateOf(65) }
    var philosophyReadiness by remember { mutableIntStateOf(35) }
    var arabicReadiness by remember { mutableIntStateOf(60) }
    var historyGeoReadiness by remember { mutableIntStateOf(40) }

    var isPlanApplied by remember { mutableStateOf(false) }

    val (overallReadiness, priorities) = remember(
        daysRemaining, dailyHours, mathReadiness, physicsReadiness, scienceReadiness, philosophyReadiness, arabicReadiness, historyGeoReadiness
    ) {
        SmartRescuePlannerData.generateRescuePlan(
            daysRemaining, dailyHours, mathReadiness, physicsReadiness, scienceReadiness, philosophyReadiness, arabicReadiness, historyGeoReadiness
        )
    }

    val animatedGauge by animateFloatAsState(
        targetValue = overallReadiness / 100f,
        label = "rescue_gauge"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
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
                        text = "🗺️ مخطط الإنقاذ والمراجعة المتبقية",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "خطة تعويضية ذكية مبنية على معاملات البكالوريا",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Readiness Gauge Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مؤشر الجاهزية الكلي للبكالوريا",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "محسوب بالأوزان والمعاملات الرسمية (7، 6، 5...)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (overallReadiness >= 70) EmeraldSuccess500.copy(alpha = 0.15f) else AmberGold500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${String.format("%.1f", overallReadiness)}%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (overallReadiness >= 70) EmeraldSuccess500 else AmberGold500,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { animatedGauge },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (overallReadiness >= 70) EmeraldSuccess500 else if (overallReadiness >= 50) AmberGold500 else CrimsonError500,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (overallReadiness >= 70) "🟢 وضعك ممتاز ومستقر! الخطة تركز على تثبيت الدرجات وحل المواضيع الكاملة."
                        else if (overallReadiness >= 50) "🟡 وضعك قابل للتدارك السريع! ركز 70% من وقتك على المواد الأساسية (معامل 7 و 6)."
                        else "🔴 خطة الطوارئ والإنقاذ المكثف! تم إعادة توزيع ساعاتك للتركيز على الأبواب الأكثر ضماناً للنقاط.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Configuration Sliders Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚙️ ضبط معطيات خطتك الشخصية:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days remaining
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "الأيام المتبقية حتى البكالوريا:", style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalBlue600.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "$daysRemaining يوماً",
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Slider(
                        value = daysRemaining.toFloat(),
                        onValueChange = { daysRemaining = it.toInt() },
                        valueRange = 15f..180f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = RoyalBlue600, activeTrackColor = RoyalBlue600)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Daily study hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ساعات المراجعة اليومية المتاحة:", style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold500.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "$dailyHours ساعات/يوم",
                                fontWeight = FontWeight.Bold,
                                color = AmberGold500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Slider(
                        value = dailyHours.toFloat(),
                        onValueChange = { dailyHours = it.toInt() },
                        valueRange = 3f..12f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = AmberGold500, activeTrackColor = AmberGold500)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "تقييم جاهزيتك الحالية في كل مادة (%):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Subject sliders
                    SubjectSliderRow(name = "الرياضيات (معامل 7)", value = mathReadiness, onValueChange = { mathReadiness = it }, color = Subject.MATH.color)
                    SubjectSliderRow(name = "العلوم الطبيعية (معامل 6)", value = scienceReadiness, onValueChange = { scienceReadiness = it }, color = Subject.SCIENCE.color)
                    SubjectSliderRow(name = "العلوم الفيزيائية (معامل 5)", value = physicsReadiness, onValueChange = { physicsReadiness = it }, color = Subject.PHYSICS.color)
                    SubjectSliderRow(name = "الفلسفة (معامل 2)", value = philosophyReadiness, onValueChange = { philosophyReadiness = it }, color = Subject.PHILOSOPHY.color)
                    SubjectSliderRow(name = "الأدب العربي (معامل 3)", value = arabicReadiness, onValueChange = { arabicReadiness = it }, color = Subject.ARABIC.color)
                    SubjectSliderRow(name = "التاريخ والجغرافيا (معامل 2)", value = historyGeoReadiness, onValueChange = { historyGeoReadiness = it }, color = Subject.HISTORY_GEO.color)
                }
            }
        }

        // Prioritization Recommendations
        item {
            Text(
                text = "📌 مصفوفة الأولويات وخطة الإنقاذ الموجهة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(priorities) { prio ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = prio.subject.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${prio.subject.displayName} (معامل ${prio.coefficient})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = prio.subject.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalBlue600.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "مخصص: ${String.format("%.1f", prio.targetDailyHours)} ساعة/يوم",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = prio.recommendation,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "الوحدات العاجلة ذات الثقل النقطي:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    prio.urgentUnits.forEach { unit ->
                        Text(
                            text = "• $unit",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Daily Rescue Roadmap Sample
        item {
            Text(
                text = "📅 خطة الأيام الأولى من الإنقاذ والتعويض:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(SmartRescuePlannerData.sampleRescueDailyTasks) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اليوم ${task.dayNumber} من خطة الإنقاذ",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue600
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGold500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${task.estimatedHours} ساعات",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberGold500,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1️⃣ ${task.primarySubject.displayName}: ${task.primaryGoal}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2️⃣ ${task.secondarySubject.displayName}: ${task.secondaryGoal}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🌙 الحفظ المسائي: ${task.nightRecitationGoal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Apply plan button
        item {
            Button(
                onClick = { isPlanApplied = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_rescue_plan_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlanApplied) EmeraldSuccess500 else RoyalBlue600
                )
            ) {
                Icon(
                    imageVector = if (isPlanApplied) Icons.Default.Check else Icons.Default.AutoGraph,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPlanApplied) "تم اعتماد وتطبيق خطة الإنقاذ بنجاح! ✅" else "اعتماد وتطبيق خطة الإنقاذ في جدول دراستي 🚀",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SubjectSliderRow(
    name: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(text = "$value%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}
