package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun LandingPageScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var isLampOn by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Dynamic Theme Color Animations
    val primaryTextTextColor by animateColorAsState(
        targetValue = if (isLampOn) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
        animationSpec = tween(500),
        label = "textColor"
    )
    val subtitleTextColor by animateColorAsState(
        targetValue = if (isLampOn) Color(0xFFFEF3C7) else Color(0xFFCBD5E1),
        animationSpec = tween(500),
        label = "subtitleColor"
    )
    val cardBgColor by animateColorAsState(
        targetValue = if (isLampOn) Color(0xFF1C1917).copy(alpha = 0.88f) else Color(0xFF0F172A).copy(alpha = 0.88f),
        animationSpec = tween(500),
        label = "cardBg"
    )
    val accentColor by animateColorAsState(
        targetValue = if (isLampOn) Color(0xFFF59E0B) else Color(0xFF3B82F6),
        animationSpec = tween(500),
        label = "accentColor"
    )

    AnimatedLandingBackground(
        isLampOn = isLampOn,
        onToggleLamp = { isLampOn = !isLampOn }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Login / Sign Up buttons + App Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_mutafawweq_1788679709620),
                        contentDescription = "شعار التطبيق",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "منصة متفوق",
                        color = primaryTextTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onNavigateToLogin,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isLampOn) Color(0xFFFBBF24) else Color(0xFF60A5FA)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                if (isLampOn) listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)) else listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
                            )
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("landing_login_btn")
                    ) {
                        Text("تسجيل دخول", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToSignUp,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("landing_signup_btn")
                    ) {
                        Text("إنشاء حساب", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Interactive Lamp Switch Floating Bar
            Surface(
                onClick = { isLampOn = !isLampOn },
                shape = RoundedCornerShape(20.dp),
                color = if (isLampOn) Color(0xFFFEF08A) else Color(0xFF1E293B),
                contentColor = if (isLampOn) Color(0xFF78350F) else Color(0xFF94A3B8),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("lamp_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = if (isLampOn) Color(0xFFD97706) else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isLampOn) "💡 مصباح المكتبة مفعّل (انقر لإطفائه وإشعال النمط الليلي)" else "🌙 مصباح المكتبة مطفأ (انقر لإضاءة المكتبة والكتب)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Hero Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Surface(
                    color = cardBgColor,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "المنصة الأولى لطلاب البكالوريا والامتحانات",
                            color = if (isLampOn) Color(0xFFFDE047) else Color(0xFF93C5FD),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "طريقك الأضمن نحو التفوق والدرجات النهائية 🚀",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "استمتع ببنك أسئلة سحابي متكامل، ومساعد ذكاء اصطناعي (AI) يصحح أخطاءك، وخطط دراسية ذكية مصممة خصيصاً لتحقيق أحلامك.",
                    fontSize = 13.sp,
                    color = subtitleTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Feature Highlights
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FeatureItem("بنك أسئلة ذكي ومحدث دورياً مع تحديثات سحابية مستمرة", isLampOn, cardBgColor)
                    FeatureItem("مدقق ومستخرج ذكاء اصطناعي متطور للصور وملفات الـ PDF", isLampOn, cardBgColor)
                    FeatureItem("خطط دراسية إنقاذية ومحاكي امتحانات دقيق 100%", isLampOn, cardBgColor)
                }
            }

            // Bottom CTA Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("landing_start_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLampOn) Color(0xFFD97706) else Color(0xFF10B981)
                    )
                ) {
                    Text(
                        text = "ابدأ التعلم الآن ✨",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "منصة متفوق التعليمية © 2026 - جميع الحقوق محفوظة",
                    fontSize = 11.sp,
                    color = subtitleTextColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnimatedLandingBackground(
    isLampOn: Boolean,
    onToggleLamp: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Smooth transition for light intensity
    val lightAlpha by animateFloatAsState(
        targetValue = if (isLampOn) 1f else 0.12f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "lightAlpha"
    )

    val lampGlowColor by animateColorAsState(
        targetValue = if (isLampOn) Color(0xFFFEF08A) else Color(0xFF475569),
        animationSpec = tween(500),
        label = "lampGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // High Performance Background Canvas: Renders Desk Lamp + 4 Bookshelves + Warm Light Beam
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onToggleLamp() }
        ) {
            val w = size.width
            val h = size.height

            // --- 1. Draw 4 Wooden Bookshelves in Background ---
            val shelfCount = 4
            val startY = h * 0.22f
            val endY = h * 0.88f
            val shelfSpacing = (endY - startY) / (shelfCount - 1)

            val shelfWoodColor = if (isLampOn) Color(0xFF78350F) else Color(0xFF1E293B)
            val shelfHighlightColor = if (isLampOn) Color(0xFFB45309) else Color(0xFF334155)

            val bookColorsWarm = listOf(
                Color(0xFFEF4444), // Red
                Color(0xFF3B82F6), // Blue
                Color(0xFF10B981), // Emerald
                Color(0xFFF59E0B), // Amber
                Color(0xFF8B5CF6), // Purple
                Color(0xFFEC4899), // Pink
                Color(0xFF06B6D4)  // Cyan
            )

            val bookColorsDim = listOf(
                Color(0xFF334155),
                Color(0xFF1E293B),
                Color(0xFF475569),
                Color(0xFF0F172A)
            )

            for (shelfIdx in 0 until shelfCount) {
                val shelfY = startY + (shelfIdx * shelfSpacing)
                val shelfThickness = 12.dp.toPx()

                // Draw Wooden Shelf Planks
                drawRoundRect(
                    color = shelfWoodColor,
                    topLeft = Offset(w * 0.05f, shelfY),
                    size = Size(w * 0.9f, shelfThickness),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Shelf edge highlight line
                drawLine(
                    color = shelfHighlightColor,
                    start = Offset(w * 0.05f, shelfY),
                    end = Offset(w * 0.95f, shelfY),
                    strokeWidth = 2.dp.toPx()
                )

                // Draw Row of Books standing on each shelf
                val bookCountOnShelf = 12
                val bookAreaStart = w * 0.08f
                val bookAreaWidth = w * 0.84f
                val bookSlotWidth = bookAreaWidth / bookCountOnShelf

                for (b in 0 until bookCountOnShelf) {
                    val bookX = bookAreaStart + (b * bookSlotWidth) + 3.dp.toPx()
                    val bookW = (bookSlotWidth - 5.dp.toPx()).coerceAtLeast(8.dp.toPx())
                    // Varied book heights per slot
                    val heightRatio = 0.55f + (((b * 7 + shelfIdx * 13) % 40) / 100f)
                    val bookH = (shelfSpacing * 0.7f) * heightRatio
                    val bookY = shelfY - bookH

                    val colorList = if (isLampOn) bookColorsWarm else bookColorsDim
                    val bookColor = colorList[(b + shelfIdx * 3) % colorList.size]

                    // Draw book spine
                    drawRoundRect(
                        color = bookColor.copy(alpha = if (isLampOn) 0.85f else 0.4f),
                        topLeft = Offset(bookX, bookY),
                        size = Size(bookW, bookH),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Gold / Silver Spine Stripe detail on book
                    if (isLampOn && (b % 2 == 0)) {
                        drawLine(
                            color = Color(0xFFFEF08A).copy(alpha = 0.6f * lightAlpha),
                            start = Offset(bookX + 2.dp.toPx(), bookY + bookH * 0.2f),
                            end = Offset(bookX + bookW - 2.dp.toPx(), bookY + bookH * 0.2f),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }
            }

            // --- 2. Draw Warm Desk Lamp Light Cone (Yellow Library Illumination) ---
            val lampX = w * 0.5f
            val lampY = h * 0.08f

            if (lightAlpha > 0.05f) {
                val lightBeamPath = Path().apply {
                    moveTo(lampX - 16.dp.toPx(), lampY + 20.dp.toPx())
                    lineTo(w * 0.02f, h * 0.95f)
                    lineTo(w * 0.98f, h * 0.95f)
                    lineTo(lampX + 16.dp.toPx(), lampY + 20.dp.toPx())
                    close()
                }

                drawPath(
                    path = lightBeamPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF08A).copy(alpha = 0.45f * lightAlpha), // Bright Desk Lamp Yellow
                            Color(0xFFFDE047).copy(alpha = 0.25f * lightAlpha),
                            Color(0xFFF59E0B).copy(alpha = 0.12f * lightAlpha),
                            Color.Transparent
                        ),
                        startY = lampY,
                        endY = h
                    )
                )

                // Radial spotlight glow at top
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFEF08A).copy(alpha = 0.7f * lightAlpha),
                            Color(0xFFFACC15).copy(alpha = 0.35f * lightAlpha),
                            Color.Transparent
                        ),
                        center = Offset(lampX, lampY + 15.dp.toPx()),
                        radius = w * 0.45f
                    ),
                    center = Offset(lampX, lampY + 15.dp.toPx()),
                    radius = w * 0.45f
                )
            }

            // --- 3. Draw Floating Light Dust Particles when Lamp is ON ---
            if (lightAlpha > 0.1f) {
                val particleCount = 18
                for (p in 0 until particleCount) {
                    val pRatio = ((p * 17 + (System.currentTimeMillis() / 40)) % 1000) / 1000f
                    val px = (lampX - w * 0.35f * pRatio) + (p * 43) % (w * 0.7f)
                    val py = lampY + (h * 0.85f * pRatio)
                    val pAlpha = (0.2f + 0.6f * (1f - pRatio)) * lightAlpha

                    drawCircle(
                        color = Color(0xFFFEF08A).copy(alpha = pAlpha),
                        center = Offset(px, py),
                        radius = (1.5.dp + (p % 3).dp).toPx()
                    )
                }
            }

            // --- 4. Draw Desk Lamp Head & Stand & Pull Cord ---
            // Lamp Arm / Cable from ceiling / stand
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(lampX, 0f),
                end = Offset(lampX, lampY),
                strokeWidth = 3.dp.toPx()
            )

            // Lamp Pull Cord Switch
            val cordY = lampY + 35.dp.toPx()
            drawLine(
                color = if (isLampOn) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                start = Offset(lampX + 18.dp.toPx(), lampY + 12.dp.toPx()),
                end = Offset(lampX + 18.dp.toPx(), cordY),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = if (isLampOn) Color(0xFFFEF08A) else Color(0xFFCBD5E1),
                center = Offset(lampX + 18.dp.toPx(), cordY + 4.dp.toPx()),
                radius = 4.dp.toPx()
            )

            // Metallic Lamp Shade Body
            val shadePath = Path().apply {
                moveTo(lampX - 22.dp.toPx(), lampY + 18.dp.toPx())
                lineTo(lampX + 22.dp.toPx(), lampY + 18.dp.toPx())
                lineTo(lampX + 12.dp.toPx(), lampY)
                lineTo(lampX - 12.dp.toPx(), lampY)
                close()
            }

            drawPath(
                path = shadePath,
                color = if (isLampOn) Color(0xFFD97706) else Color(0xFF334155)
            )

            // Bulb inside lamp shade
            drawCircle(
                color = lampGlowColor,
                center = Offset(lampX, lampY + 18.dp.toPx()),
                radius = 8.dp.toPx()
            )
        }

        // Dark dimming overlay when lamp is OFF, or subtle vignette when ON
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isLampOn) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1C1917).copy(alpha = 0.25f),
                                Color.Transparent,
                                Color(0xFF1C1917).copy(alpha = 0.55f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A).copy(alpha = 0.75f),
                                Color(0xFF0F172A).copy(alpha = 0.88f)
                            )
                        )
                    }
                )
        )

        content()
    }
}

@Composable
fun FeatureItem(
    text: String,
    isLampOn: Boolean = false,
    bgColor: Color = Color(0xFF0F172A).copy(alpha = 0.8f)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isLampOn) Color(0xFFF59E0B) else Color(0xFF34D399),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = if (isLampOn) Color(0xFFFEF3C7) else Color(0xFFE2E8F0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
