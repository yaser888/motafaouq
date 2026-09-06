package com.example.data.models

enum class Subject(val arabicName: String, val colorHex: Long) {
    MATH("الرياضيات", 0xFF2563EB),
    PHYSICS("الفيزياء", 0xFF7C3AED),
    CHEMISTRY("الكيمياء", 0xFF059669),
    SCIENCE("العلوم", 0xFF0D9488),
    ARABIC("اللغة العربية", 0xFFD97706),
    ENGLISH("اللغة الإنكليزية", 0xFFDC2626),
    FRENCH("اللغة الفرنسية", 0xFFDB2777),
    ISLAMIC("التربية الإسلامية", 0xFF16A34A),
    GENERAL("مراجعة وتثبيت", 0xFF4B5563)
}

enum class PeriodType(val arabicName: String, val timeRange: String, val iconName: String) {
    PERIOD_1("الفترة الأولى (الصباحية - المادة الثقيلة)", "2.5 إلى 3.5 ساعات فور الاستيقاظ", "light_mode"),
    PERIOD_2("الفترة الثانية (الظهر/العصر - المادة المتوسطة)", "1.5 إلى 2.5 ساعة", "wb_sunny"),
    PERIOD_3("الفترة الثالثة (المساء - التثبيت والاختبار)", "1.5 إلى 2 ساعة", "nights_stay")
}

data class DayTask(
    val id: String,
    val monthNumber: Int,
    val weekNumber: Int,
    val dayOfWeek: String, // السبت، الأحد، الاثنين، الثلاثاء، الأربعاء، الخميس، الجمعة
    val period1Subject: Subject,
    val period1Content: String,
    val period2Subject: Subject,
    val period2Content: String,
    val period3Content: String,
    val hasCardsTask: Boolean = true,
    val hasRecitationTask: Boolean = true,
    val isRestDay: Boolean = false
)

data class MonthInfo(
    val monthNumber: Int,
    val name: String,
    val title: String,
    val description: String,
    val startWeek: Int,
    val endWeek: Int
)

data class ElevationStat(
    val monthNumber: Int,
    val monthName: String,
    val plannedRate: Float,
    val currentRate: Float,
    val completedTasks: Int,
    val totalTasks: Int,
    val status: String
)
