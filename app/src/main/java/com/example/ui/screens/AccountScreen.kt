package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountScreen(
    onLogout: () -> Unit,
    onOpenAdminPanel: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("student_auth_prefs", Context.MODE_PRIVATE) }
    val studentName = prefs.getString("student_name", "أحمد الطالب") ?: "أحمد الطالب"
    val studentEmail = prefs.getString("student_email", "student@exam.com") ?: "student@exam.com"

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = studentName.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = studentName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Text(
                    text = studentEmail,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Text(
                            text = "المستوى: طالب متفوق (الرتبة الذهبية 🌟)",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                modifier = Modifier.weight(1f),
                title = "الأسئلة المحلولة",
                value = "142",
                icon = Icons.Default.Quiz,
                color = Color(0xFF3B82F6)
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "ساعات التركيز",
                value = "24 س",
                icon = Icons.Default.LockClock,
                color = Color(0xFF10B981)
            )
            StatBox(
                modifier = Modifier.weight(1f),
                title = "سلسلة التميز",
                value = "7 أيام",
                icon = Icons.Default.LocalFireDepartment,
                color = Color(0xFFF59E0B)
            )
        }

        // Section 1: Personal Info
        AccountSectionCard(title = "معلومات الطالب الشخصية", icon = Icons.Default.Person) {
            InfoRow(label = "الاسم الكامل", value = studentName)
            InfoRow(label = "البريد الإلكتروني", value = studentEmail)
            InfoRow(label = "المسار الدراسي", value = "علوم تجريبية / رياضيات")
            InfoRow(label = "حالة الحساب", value = "نشط وموثق سحابياً 🟢")
        }

        // Section 2: Academic Level & Progress
        AccountSectionCard(title = "المستوى الأكاديمي والتقدم", icon = Icons.Default.TrendingUp) {
            InfoRow(label = "التقدير المتوقع", value = "ممتاز (معدل 17.5+)")
            InfoRow(label = "نقاط الخبرة (XP)", value = "2,450 نقطة")
            InfoRow(label = "نسبة إنجاز خطة البكالوريا", value = "68%")
            LinearProgressIndicator(
                progress = { 0.68f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        // Section 2.5: Subject Progress
        AccountSectionCard(title = "تقدم الطالب في المواد", icon = Icons.Default.MenuBook) {
            SubjectProgressRow(subjectName = "الرياضيات", progress = 0.85f, percentage = "85%")
            SubjectProgressRow(subjectName = "الفيزياء", progress = 0.70f, percentage = "70%")
            SubjectProgressRow(subjectName = "العلوم الطبيعية", progress = 0.60f, percentage = "60%")
            SubjectProgressRow(subjectName = "اللغة العربية", progress = 0.90f, percentage = "90%")
            SubjectProgressRow(subjectName = "اللغة الإنجليزية", progress = 0.75f, percentage = "75%")
        }

        // Section 3: Cloud Sync
        AccountSectionCard(title = "الاتصال والسحابة", icon = Icons.Default.CloudSync) {
            InfoRow(label = "حالة الاتصال", value = "متصل بالسحابة بنجاح 🟢")
            InfoRow(label = "المزامنة التلقائية", value = "مفعلة (تحديثات فورية)")
            InfoRow(label = "إصدار التطبيق", value = "v2.1.0 (أحدث نسخة)")
        }

        // Section 4: Admin Controls & Management
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "صلاحيات الأدمن والمشرف 🛡️",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "الوصول السريع للوحة تحكم المشرف لإدارة الأسئلة، مراجعة البلاغات، وبث الاختبارات.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onOpenAdminPanel,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فتح لوحة التحكم الكاملة للأدمن", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Logout Button
        Button(
            onClick = {
                prefs.edit().putBoolean("is_logged_in", false).apply()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "تسجيل الخروج من الحساب",
                color = MaterialTheme.colorScheme.onError,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatBox(modifier: Modifier = Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AccountSectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
fun SubjectProgressRow(subjectName: String, progress: Float, percentage: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = subjectName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = percentage, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
