package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.models.DuelBattleData
import com.example.data.models.DuelQuestion
import com.example.data.models.LeaderboardUser
import com.example.data.models.PeerRival
import com.example.data.models.Subject
import com.example.ui.theme.AmberGold500
import com.example.ui.theme.CrimsonError500
import com.example.ui.theme.EmeraldSuccess500
import com.example.ui.theme.RoyalBlue600
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class DuelState {
    LOBBY,
    MATCHING,
    BATTLE_ROUND,
    ROUND_RESULT,
    MATCH_OVER
}

@Composable
fun DuelBattleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Battle Arena, 1: Leaderboard
    var duelState by remember { mutableStateOf(DuelState.LOBBY) }

    var rival by remember { mutableStateOf(DuelBattleData.rivals.first()) }
    var currentRound by remember { mutableIntStateOf(1) } // 1 to 5
    var userScore by remember { mutableIntStateOf(0) }
    var rivalScore by remember { mutableIntStateOf(0) }
    var comboStreak by remember { mutableIntStateOf(0) }

    var currentQuestion by remember { mutableStateOf(DuelBattleData.duelQuestions.first()) }
    var userSelectedOption by remember { mutableStateOf<String?>(null) }
    var rivalSelectedOption by remember { mutableStateOf<String?>(null) }
    var roundSecondsRemaining by remember { mutableIntStateOf(15) }

    // Matchmaking logic
    LaunchedEffect(duelState) {
        if (duelState == DuelState.MATCHING) {
            delay(1800)
            rival = DuelBattleData.rivals.random()
            currentRound = 1
            userScore = 0
            rivalScore = 0
            comboStreak = 0
            currentQuestion = DuelBattleData.duelQuestions.random()
            userSelectedOption = null
            rivalSelectedOption = null
            roundSecondsRemaining = 15
            duelState = DuelState.BATTLE_ROUND
        }
    }

    // Round timer & rival answer simulation
    LaunchedEffect(duelState, roundSecondsRemaining) {
        if (duelState == DuelState.BATTLE_ROUND && roundSecondsRemaining > 0) {
            delay(1000)
            roundSecondsRemaining -= 1

            // Simulate rival answer at realistic time
            val elapsed = 15 - roundSecondsRemaining
            if (rivalSelectedOption == null && elapsed >= rival.speedLevelSeconds) {
                val isRivalCorrect = Random.nextFloat() <= rival.scoreRatio
                rivalSelectedOption = if (isRivalCorrect) currentQuestion.correctAnswer else listOf("A", "B", "C", "D").filter { it != currentQuestion.correctAnswer }.random()
                if (isRivalCorrect) rivalScore += 100 + (roundSecondsRemaining * 5)
            }

            if (roundSecondsRemaining <= 0 || (userSelectedOption != null && rivalSelectedOption != null)) {
                // Round ends
                delay(800)
                if (currentRound >= 5) {
                    duelState = DuelState.MATCH_OVER
                } else {
                    currentRound++
                    currentQuestion = DuelBattleData.duelQuestions.random()
                    userSelectedOption = null
                    rivalSelectedOption = null
                    roundSecondsRemaining = 15
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⚔️ تحديات الأقران المباشرة",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = RoyalBlue600
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("حلبة التحدي ⚡", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("لوحة الشرف الوطنية 🏆", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 1) {
            LeaderboardView(leaderboard = DuelBattleData.leaderboard)
            return@Column
        }

        when (duelState) {
            DuelState.LOBBY -> {
                DuelLobbyView(
                    onStartDuel = { duelState = DuelState.MATCHING }
                )
            }

            DuelState.MATCHING -> {
                DuelMatchingView()
            }

            DuelState.BATTLE_ROUND -> {
                DuelRoundView(
                    round = currentRound,
                    question = currentQuestion,
                    userScore = userScore,
                    rivalScore = rivalScore,
                    rival = rival,
                    secondsRemaining = roundSecondsRemaining,
                    userSelectedOption = userSelectedOption,
                    onSelectOption = { opt ->
                        if (userSelectedOption == null) {
                            userSelectedOption = opt
                            val isCorrect = opt == currentQuestion.correctAnswer
                            if (isCorrect) {
                                comboStreak++
                                val speedBonus = roundSecondsRemaining * 6
                                val comboBonus = comboStreak * 20
                                userScore += 100 + speedBonus + comboBonus
                            } else {
                                comboStreak = 0
                            }
                        }
                    }
                )
            }

            DuelState.MATCH_OVER, DuelState.ROUND_RESULT -> {
                DuelMatchOverView(
                    userScore = userScore,
                    rivalScore = rivalScore,
                    rival = rival,
                    onPlayAgain = { duelState = DuelState.MATCHING },
                    onGoLobby = { duelState = DuelState.LOBBY }
                )
            }
        }
    }
}

@Composable
fun DuelLobbyView(onStartDuel: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(AmberGold500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚔️", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "مبارزات الـ 1 ضد 1 المباشرة",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "نافس زملاءك ونخبة طلاب البكالوريا في 5 جولات سريعة وحصد نقاط الخبرة (XP) لتصدر الترتيب الوطني!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue600.copy(alpha = 0.1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "5 جولات", fontWeight = FontWeight.Bold, color = RoyalBlue600)
                                Text(text = "سريعة وحاسمة", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold500.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "15 ثانية", fontWeight = FontWeight.Bold, color = AmberGold500)
                                Text(text = "لكل سؤال", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSuccess500.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "+250 XP", fontWeight = FontWeight.Bold, color = EmeraldSuccess500)
                                Text(text = "مكافأة الفوز", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onStartDuel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("find_duel_match_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "البحث عن منافس وبدء التحدي 🚀", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        item {
            Text(
                text = "منافسون نشطون الآن في حلبة البكالوريا:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(DuelBattleData.rivals) { peer ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = peer.avatarEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = peer.name, fontWeight = FontWeight.Bold)
                            Text(text = peer.wilaya, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGold500.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = peer.rankBadge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberGold500,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DuelMatchingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 54.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "جارٍ البحث عن منافس متكافئ...",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "الاتصال بطلاب ولايات الجزائر (وهران، قسنطينة، سطيف، العاصمة...)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = RoyalBlue600
            )
        }
    }
}

@Composable
fun DuelRoundView(
    round: Int,
    question: DuelQuestion,
    userScore: Int,
    rivalScore: Int,
    rival: PeerRival,
    secondsRemaining: Int,
    userSelectedOption: String?,
    onSelectOption: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Battle Header: You vs Rival Scoreboard
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "أنت 🌟", fontWeight = FontWeight.Bold)
                    Text(
                        text = "$userScore",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = RoyalBlue600
                    )
                }

                // VS & Timer Center
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = if (secondsRemaining <= 5) CrimsonError500 else AmberGold500
                    ) {
                        Text(
                            text = "$secondsRemaining",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "الجولة $round / 5", style = MaterialTheme.typography.labelSmall)
                }

                // Rival
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${rival.avatarEmoji} ${rival.name.split(" ").first()}", fontWeight = FontWeight.Bold)
                    Text(
                        text = "$rivalScore",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = CrimsonError500
                    )
                }
            }
        }

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = question.subject.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = question.subject.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = question.subject.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "A" to question.optionA,
                        "B" to question.optionB,
                        "C" to question.optionC,
                        "D" to question.optionD
                    ).forEach { (optKey, optText) ->
                        val isSelected = userSelectedOption == optKey
                        val isCorrect = optKey == question.correctAnswer
                        val showResult = userSelectedOption != null

                        val cardBg = if (showResult) {
                            if (isCorrect) EmeraldSuccess500.copy(alpha = 0.2f)
                            else if (isSelected) CrimsonError500.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        } else {
                            if (isSelected) RoyalBlue600.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = userSelectedOption == null) {
                                    onSelectOption(optKey)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = BorderStroke(
                                1.dp,
                                if (showResult && isCorrect) EmeraldSuccess500 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (showResult && isCorrect) EmeraldSuccess500 else RoyalBlue600.copy(alpha = 0.15f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = optKey,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (showResult && isCorrect) Color.White else RoyalBlue600
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun DuelMatchOverView(
    userScore: Int,
    rivalScore: Int,
    rival: PeerRival,
    onPlayAgain: () -> Unit,
    onGoLobby: () -> Unit
) {
    val isWin = userScore >= rivalScore

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, if (isWin) EmeraldSuccess500.copy(alpha = 0.4f) else AmberGold500.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (isWin) "🏆" else "🤝", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isWin) "انتصار ساحق ومستحق! 🎉" else "مباراة قوية ورائعة!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isWin) "تغلبت على ${rival.name} وحققت نقاط خبرة إضافية!" else "تعلمت نقاطاً هامة من مواجهة ${rival.name}، التحدي القادم سيكون أفضل!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("نقاطك", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "$userScore",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = RoyalBlue600
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("نقاط ${rival.name.split(" ").first()}", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "$rivalScore",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = CrimsonError500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                ) {
                    Text("خوض تحدٍ جديد ⚔️", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onGoLobby, modifier = Modifier.fillMaxWidth()) {
                    Text("العودة لساحة التحديات")
                }
            }
        }
    }
}

@Composable
fun LeaderboardView(leaderboard: List<LeaderboardUser>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AmberGold500.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, AmberGold500.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👑", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "لوحة الشرف الوطنية - دوري المتفوقين",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "أفضل فرسان البكالوريا في الجزائر لشهر جوان",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(leaderboard) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (user.isCurrentUser) RoyalBlue600.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (user.isCurrentUser) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when (user.rank) {
                                1 -> AmberGold500
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${user.rank}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = user.name, fontWeight = FontWeight.Bold)
                            Text(text = "${user.wilaya} • ${user.rankTitle}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${user.xp} XP",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = RoyalBlue600
                        )
                        Text(text = "🔥 ${user.winStreak} انتصارات متتالية", style = MaterialTheme.typography.labelSmall, color = AmberGold500)
                    }
                }
            }
        }
    }
}
