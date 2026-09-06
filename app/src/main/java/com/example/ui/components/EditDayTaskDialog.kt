package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DayTask
import com.example.data.models.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayTaskDialog(
    task: DayTask,
    onDismiss: () -> Unit,
    onSave: (
        dayId: String,
        monthNumber: Int,
        weekNumber: Int,
        dayOfWeek: String,
        p1Subject: Subject,
        p1Content: String,
        p2Subject: Subject,
        p2Content: String,
        p3Content: String,
        isRestDay: Boolean
    ) -> Unit,
    onResetDayToDefault: (String) -> Unit
) {
    var isRestDay by remember { mutableStateOf(task.isRestDay) }
    var p1Subject by remember { mutableStateOf(task.period1Subject) }
    var p1Content by remember { mutableStateOf(task.period1Content) }
    var p2Subject by remember { mutableStateOf(task.period2Subject) }
    var p2Content by remember { mutableStateOf(task.period2Content) }
    var p3Content by remember { mutableStateOf(task.period3Content) }

    var p1Expanded by remember { mutableStateOf(false) }
    var p2Expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EditCalendar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "تخصيص خطة ${task.dayOfWeek}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "الشهر ${task.monthNumber} • الأسبوع ${task.weekNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rest Day Toggle
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تعيين كيوم استراحة أو مراجعة حرة",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "إلغاء الحصص المجدولة وإتاحة الراحة الذهنية",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isRestDay,
                                onCheckedChange = { isRestDay = it },
                                modifier = Modifier.testTag("rest_day_switch")
                            )
                        }
                    }
                }

                if (!isRestDay) {
                    // Period 1 (Heavy morning subject)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "الفترة الأولى (الصباحية - المادة الثقيلة)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenuBox(
                                expanded = p1Expanded,
                                onExpandedChange = { p1Expanded = !p1Expanded }
                            ) {
                                OutlinedTextField(
                                    value = p1Subject.arabicName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("المادة") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = p1Expanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = p1Expanded,
                                    onDismissRequest = { p1Expanded = false }
                                ) {
                                    Subject.entries.filter { it != Subject.GENERAL }.forEach { subj ->
                                        DropdownMenuItem(
                                            text = { Text(subj.arabicName) },
                                            onClick = {
                                                p1Subject = subj
                                                p1Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = p1Content,
                                onValueChange = { p1Content = it },
                                label = { Text("محتوى الدرس أو المهام المطلوبة") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }

                    // Period 2 (Medium afternoon subject)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "الفترة الثانية (الظهر - المادة المتوسطة)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenuBox(
                                expanded = p2Expanded,
                                onExpandedChange = { p2Expanded = !p2Expanded }
                            ) {
                                OutlinedTextField(
                                    value = p2Subject.arabicName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("المادة") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = p2Expanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = p2Expanded,
                                    onDismissRequest = { p2Expanded = false }
                                ) {
                                    Subject.entries.filter { it != Subject.GENERAL }.forEach { subj ->
                                        DropdownMenuItem(
                                            text = { Text(subj.arabicName) },
                                            onClick = {
                                                p2Subject = subj
                                                p2Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = p2Content,
                                onValueChange = { p2Content = it },
                                label = { Text("محتوى الدرس أو المهام المطلوبة") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }

                    // Period 3 (Evening consolidation)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "الفترة الثالثة (المساء - التثبيت والاختبار)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            OutlinedTextField(
                                value = p3Content,
                                onValueChange = { p3Content = it },
                                label = { Text("موضوع التثبيت / حل التمارين السريعة") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        task.id,
                        task.monthNumber,
                        task.weekNumber,
                        task.dayOfWeek,
                        p1Subject,
                        p1Content,
                        p2Subject,
                        p2Content,
                        p3Content,
                        isRestDay
                    )
                },
                modifier = Modifier.testTag("save_custom_plan_btn")
            ) {
                Text("حفظ التعديل")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { onResetDayToDefault(task.id) }
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استعادة الافتراضي")
                }
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )
}
