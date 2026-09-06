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
    PHILOSOPHY("الفلسفة", 0xFF8B5CF6),
    HISTORY_GEO("التاريخ والجغرافيا", 0xFFD97706),
    HISTORY("التاريخ", 0xFFEA580C),
    GEOGRAPHY("الجغرافيا", 0xFF0284C7),
    NATIONAL("التربية الوطنية", 0xFF4F46E5),
    GENERAL("مراجعة وتثبيت", 0xFF4B5563);

    val displayName: String get() = arabicName
    val color: androidx.compose.ui.graphics.Color get() = androidx.compose.ui.graphics.Color(colorHex)
}

enum class EducationalStream(
    val id: String,
    val title: String,
    val subtitle: String,
    val shortName: String,
    val badge: String
) {
    GRADE_9(
        id = "GRADE_9",
        title = "صف تاسع (شهادة التعليم الأساسي)",
        subtitle = "جبر، هندسة، علوم عامة (أحياء وفيزياء وكيمياء)، لغة عربية، لغات، دراسات اجتماعية، إسلامية",
        shortName = "صف تاسع",
        badge = "🎓 إعدادي"
    ),
    BAC_SCIENTIFIC(
        id = "BAC_SCIENTIFIC",
        title = "بكالوريا علمي (الشهادة الثانوية العامة)",
        subtitle = "رياضيات، فيزياء، كيمياء، علم الأحياء، لغة عربية، لغات، تربية إسلامية، وطنية",
        shortName = "بكالوريا علمي",
        badge = "🔬 علمي"
    ),
    BAC_LITERARY(
        id = "BAC_LITERARY",
        title = "بكالوريا أدبي (الشهادة الثانوية العامة)",
        subtitle = "لغة عربية، فلسفة ومنطق، تاريخ، جغرافيا، لغات، تربية إسلامية، وطنية",
        shortName = "بكالوريا أدبي",
        badge = "📚 أدبي"
    );

    val subjects: List<Subject>
        get() = when (this) {
            GRADE_9 -> listOf(Subject.MATH, Subject.SCIENCE, Subject.ARABIC, Subject.ENGLISH, Subject.FRENCH, Subject.HISTORY_GEO, Subject.ISLAMIC, Subject.NATIONAL)
            BAC_SCIENTIFIC -> listOf(Subject.MATH, Subject.PHYSICS, Subject.CHEMISTRY, Subject.SCIENCE, Subject.ARABIC, Subject.ENGLISH, Subject.FRENCH, Subject.ISLAMIC, Subject.NATIONAL)
            BAC_LITERARY -> listOf(Subject.ARABIC, Subject.PHILOSOPHY, Subject.HISTORY, Subject.GEOGRAPHY, Subject.ENGLISH, Subject.FRENCH, Subject.ISLAMIC, Subject.NATIONAL)
        }

    companion object {
        fun fromId(id: String?): EducationalStream {
            return entries.firstOrNull { it.name.equals(id, ignoreCase = true) || it.id.equals(id, ignoreCase = true) }
                ?: BAC_SCIENTIFIC
        }
    }
}

data class PlanConfig(
    val stream: EducationalStream = EducationalStream.BAC_SCIENTIFIC,
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000L),
    val isConfigured: Boolean = false
) {
    val totalDays: Int
        get() {
            val diff = endDateMillis - startDateMillis
            val days = (diff / (24L * 60 * 60 * 1000L)).toInt() + 1
            return if (days < 1) 1 else days
        }
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
