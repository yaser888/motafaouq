package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AudioFlashcard
import com.example.data.models.AudioFlashcardsData
import com.example.data.models.Subject
import com.example.focus.ArabicTtsPlayer
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600

@Composable
fun AudioFlashcardsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ttsPlayer = remember { ArabicTtsPlayer(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsPlayer.shutdown()
        }
    }

    val flashcards = remember { mutableStateListOf(*AudioFlashcardsData.sampleFlashcards.toTypedArray()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var speechSpeed by remember { mutableFloatStateOf(1.0f) }
    var isLooping by remember { mutableStateOf(false) }

    val filteredCards = flashcards.filter {
        selectedSubject == null || it.subject == selectedSubject
    }

    val activeCard = if (filteredCards.isNotEmpty()) {
        filteredCards[currentCardIndex.coerceIn(0, filteredCards.size - 1)]
    } else flashcards.first()

    fun playCurrentCard() {
        isPlaying = true
        ttsPlayer.speak(
            text = "${activeCard.title}. ${activeCard.audioScriptText}",
            speechRate = speechSpeed,
            onDone = {
                if (isLooping) {
                    playCurrentCard()
                } else {
                    isPlaying = false
                }
            }
        )
    }

    fun stopAudio() {
        isPlaying = false
        ttsPlayer.stop()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    stopAudio()
                    onBack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "🎙️ كبسولات الحفظ السمعية السريعة",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "حفظ شخصيات التاريخ، مصطلحات الجغرافيا ومقولات الفلسفة بالصوت",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        onClick = {
                            selectedSubject = null
                            currentCardIndex = 0
                            stopAudio()
                        },
                        label = { Text("جميع المواد") }
                    )
                }
                items(listOf(Subject.HISTORY_GEO, Subject.PHILOSOPHY, Subject.ARABIC, Subject.ISLAMIC)) { sub ->
                    FilterChip(
                        selected = selectedSubject == sub,
                        onClick = {
                            selectedSubject = if (selectedSubject == sub) null else sub
                            currentCardIndex = 0
                            stopAudio()
                        },
                        label = { Text(sub.displayName) }
                    )
                }
            }
        }

        // Active Player Card Hero
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_flashcard_player_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = activeCard.subject.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = activeCard.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = activeCard.subject.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (activeCard.isMemorized) EmeraldSuccess500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (activeCard.isMemorized) "محفوظة 100% ✅" else "قيد الحفظ ⏳",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (activeCard.isMemorized) EmeraldSuccess500 else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = activeCard.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Wave visualizer simulation
                    AudioWaveformVisualizer(isPlaying = isPlaying)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Spoken script quote
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "« ${activeCard.audioScriptText} »",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Controls Row (Rewind, Play/Pause, Forward, Loop, Speed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isLooping = !isLooping }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "تكرار",
                                tint = if (isLooping) AmberGold500 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                if (currentCardIndex > 0) {
                                    currentCardIndex--
                                    if (isPlaying) playCurrentCard()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.FastRewind, contentDescription = "السابق")
                        }

                        Surface(
                            shape = CircleShape,
                            color = RoyalBlue600,
                            modifier = Modifier
                                .size(56.dp)
                                .clickable {
                                    if (isPlaying) stopAudio() else playCurrentCard()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (currentCardIndex < filteredCards.size - 1) {
                                    currentCardIndex++
                                    if (isPlaying) playCurrentCard()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.FastForward, contentDescription = "التالي")
                        }

                        // Speed toggle button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                speechSpeed = when (speechSpeed) {
                                    0.75f -> 1.0f
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    else -> 0.75f
                                }
                                if (isPlaying) playCurrentCard()
                            }
                        ) {
                            Text(
                                text = "${speechSpeed}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val index = flashcards.indexOfFirst { it.id == activeCard.id }
                            if (index != -1) {
                                val current = flashcards[index]
                                flashcards[index] = current.copy(isMemorized = !current.isMemorized)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeCard.isMemorized) EmeraldSuccess500 else MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeCard.isMemorized) "محفوظة في الذاكرة ✅" else "تحديد كـ محفوظة تماماً 🎯",
                            fontWeight = FontWeight.Bold,
                            color = if (activeCard.isMemorized) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Key Bullet Points
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📌 العناصر والمصطلحات الأساسية للتنقيط:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    activeCard.bulletPoints.forEach { point ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = RoyalBlue600, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = point, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp))
                        }
                    }
                }
            }
        }

        // All Capsules in this category
        item {
            Text(
                text = "جميع كبسولات هذه الفئة (${filteredCards.size}):",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(filteredCards.indices.toList()) { idx ->
            val card = filteredCards[idx]
            val isCurrent = idx == currentCardIndex
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentCardIndex = idx
                        if (isPlaying) playCurrentCard()
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) RoyalBlue600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isCurrent) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (card.isMemorized) Icons.Default.CheckCircle else Icons.Default.Headphones,
                            contentDescription = null,
                            tint = if (card.isMemorized) EmeraldSuccess500 else if (isCurrent) RoyalBlue600 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = card.title, fontWeight = FontWeight.Bold)
                            Text(text = card.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (isCurrent && isPlaying) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalBlue600.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "يتم الاستماع 🔊",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue600,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioWaveformVisualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val heights = (0..9).map { index ->
        if (isPlaying) {
            val anim by infiniteTransition.animateFloat(
                initialValue = 12f,
                targetValue = (24 + (index * 4) % 24).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(400 + (index * 70)),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )
            anim
        } else {
            12f
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(40.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPlaying) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}
