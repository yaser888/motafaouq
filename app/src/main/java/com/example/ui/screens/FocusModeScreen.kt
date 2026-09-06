package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FilterBAndW
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.FocusSessionEntity
import com.example.data.models.Subject
import com.example.focus.FocusSoundType
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FocusModeScreen(
    isGrayscaleEnabled: Boolean,
    isFocusLockActive: Boolean,
    focusRemainingSeconds: Int,
    focusTotalSeconds: Int,
    isTimerRunning: Boolean,
    selectedSound: FocusSoundType,
    focusSubject: String,
    focusSessions: List<FocusSessionEntity>,
    onToggleGrayscale: () -> Unit,
    onSetDuration: (Int) -> Unit,
    onSetSubject: (String) -> Unit,
    onSetSound: (FocusSoundType) -> Unit,
    onStartFocus: (Boolean, Boolean) -> Unit,
    onPauseFocus: () -> Unit,
    onStopFocus: () -> Unit,
    onDismissLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEmergencyUnlockDialog by remember { mutableStateOf(false) }
    var enableGrayscaleOnStart by remember { mutableStateOf(true) }
    var enableAppLockOnStart by remember { mutableStateOf(true) }

    val formattedMinutes = String.format(Locale.US, "%02d", focusRemainingSeconds / 60)
    val formattedSeconds = String.format(Locale.US, "%02d", focusRemainingSeconds % 60)
    val progress = if (focusTotalSeconds > 0) {
        focusRemainingSeconds.toFloat() / focusTotalSeconds.toFloat()
    } else 1f

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Fullscreen Locked Focus View if Active
    if (isFocusLockActive && isTimerRunning) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D16))
                .padding(24.dp)
                .testTag("fullscreen_focus_lock_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Focus Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AmberGold500.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AmberGold500,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "وضع التركيز الصارم مقفل",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AmberGold500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsing Big Timer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(240.dp)
                        .scale(if (isTimerRunning) pulseScale else 1f)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                        color = if (isGrayscaleEnabled) Color.White else RoyalBlue600,
                        trackColor = Color(0xFF1E293B),
                        strokeCap = StrokeCap.Round
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$formattedMinutes:$formattedSeconds",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "مادة: $focusSubject",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isGrayscaleEnabled) Color.LightGray else AmberGold500
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Motivational Quote for Syrian Baccalaureate
                Text(
                    text = "«التركيز العميق الآن هو الفارق بين الطالب العادي والمتفوق بالدرجة التامة»",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sound state
                if (selectedSound != FocusSoundType.OFF) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedSound.arabicName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Emergency Unlock / Pause Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = onPauseFocus,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = "إيقاف مؤقت")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إيقاف مؤقت")
                    }

                    OutlinedButton(
                        onClick = { showEmergencyUnlockDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "إنهاء الجلسة")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إنهاء / خروج")
                    }
                }
            }

            if (showEmergencyUnlockDialog) {
                AlertDialog(
                    onDismissRequest = { showEmergencyUnlockDialog = false },
                    title = { Text("إنهاء جلسة التركيز؟") },
                    text = {
                        Text("هل تريد حقاً مغادرة وضع التركيز وحفظ الدقائق المنجزة؟ الاستمرار سيضمن تثبيت المعلومات.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showEmergencyUnlockDialog = false
                                onStopFocus()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("تأكيد الإنهاء")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEmergencyUnlockDialog = false }) {
                            Text("متابعة الدراسة")
                        }
                    }
                )
            }
        }
        return
    }

    // Standard Focus Configuration Screen
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
                    .testTag("focus_config_header"),
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
                            imageVector = Icons.Default.LockClock,
                            contentDescription = "وضع التركيز",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "وضع دخول التركيز والعزل التام",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "حظر التشتت، تفعيل الأبيض والأسود، وقفل الاتصالات للدراسة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Timer Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_timer_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Circular Clock
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(190.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 10.dp,
                            color = RoyalBlue600,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$formattedMinutes:$formattedSeconds",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isTimerRunning) "جلسة جارية..." else "جاهز للانطلاق",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isTimerRunning) RoyalBlue600 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Preset Durations
                    Text(
                        text = "اختر مدة جلسة التركيز:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val durations = listOf(25 to "25 د", 45 to "45 د", 60 to "60 د", 90 to "90 د")
                        durations.forEach { (mins, label) ->
                            val isSelected = focusTotalSeconds == mins * 60
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetDuration(mins) },
                                label = { Text(label) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlue600,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Start / Pause / Stop Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (!isTimerRunning) {
                            Button(
                                onClick = { onStartFocus(enableGrayscaleOnStart, enableAppLockOnStart) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_start_focus"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("دخول التركيز وقفل الشاشة")
                            }
                        } else {
                            Button(
                                onClick = onPauseFocus,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_pause_focus"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold500)
                            ) {
                                Icon(imageVector = Icons.Default.Pause, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إيقاف مؤقت")
                            }

                            OutlinedButton(
                                onClick = onStopFocus,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_stop_focus"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إنهاء وحفظ")
                            }
                        }
                    }
                }
            }
        }

        // Subject Selector
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_subject_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المادة الدراسية الحالية:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Subject.entries.filter { it != Subject.GENERAL }) { subj ->
                            val isSelected = focusSubject == subj.arabicName
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetSubject(subj.arabicName) },
                                label = { Text(subj.arabicName) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(subj.colorHex),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Focus & Phone Lock Controls (الأبيض والأسود، قفل التطبيقات، قفل الوايفاي)
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_tools_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "أدوات وقفل المشتتات في الهاتف:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Grayscale Mode Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterBAndW,
                                contentDescription = null,
                                tint = RoyalBlue600
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "جعل شاشة الهاتف أبيض وأسود",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "إلغاء ألوان الشاشة لعزل المشتتات البصرية",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isGrayscaleEnabled,
                            onCheckedChange = { onToggleGrayscale() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalBlue600),
                            modifier = Modifier.testTag("grayscale_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 2. App Distraction Lock Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AmberGold500
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "قفل التطبيقات وتأمين الشاشة",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "منع الخروج العفوي ومقاومة إشعارات السوشيال ميديا",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = enableAppLockOnStart,
                            onCheckedChange = { enableAppLockOnStart = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AmberGold500),
                            modifier = Modifier.testTag("app_lock_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 3. Wi-Fi Disconnect / DND Helper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = EmeraldSuccess500
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "قفل الوايفاي والاتصالات",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "إيقاف الإنترنت ووضع الطيران لقطع المقاطعات",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess500),
                            modifier = Modifier.testTag("btn_wifi_settings")
                        ) {
                            Text("قفل الوايفاي")
                        }
                    }
                }
            }
        }

        // Ambient Sound Waves Synthesizer
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ambient_sound_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "أصوات هادئة لعزل الضوضاء الذهنية:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val sounds = listOf(
                        FocusSoundType.OFF,
                        FocusSoundType.RAIN,
                        FocusSoundType.ALPHA_WAVES,
                        FocusSoundType.WHITE_NOISE,
                        FocusSoundType.DEEP_STUDY
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sounds) { sound ->
                            val isSelected = selectedSound == sound
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetSound(sound) },
                                label = { Text(sound.arabicName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (sound) {
                                            FocusSoundType.RAIN -> Icons.Default.WaterDrop
                                            FocusSoundType.ALPHA_WAVES -> Icons.Default.Waves
                                            FocusSoundType.WHITE_NOISE -> Icons.Default.GraphicEq
                                            FocusSoundType.DEEP_STUDY -> Icons.Default.MenuBook
                                            FocusSoundType.OFF -> Icons.Default.VolumeOff
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlue600,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Focus Sessions History
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_history_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = RoyalBlue600
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سجل جلسات التركيز المكتملة (${focusSessions.size} جلسة)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (focusSessions.isEmpty()) {
                        Text(
                            text = "لم تُسجل أي جلسات تركيز بعد. ابدأ أول جلسة بومودورو الآن!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
                        focusSessions.take(5).forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${session.sessionType} • ${session.subjectName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dateFormat.format(Date(session.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldSuccess500.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${session.durationMinutes} دقيقة",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess500
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }
}
