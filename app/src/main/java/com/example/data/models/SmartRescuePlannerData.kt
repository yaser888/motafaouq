package com.example.data.models

data class RescueSubjectPriority(
    val subject: Subject,
    val coefficient: Int, // e.g. Math: 7, Science: 6, Physics: 5
    val currentReadinessPct: Int, // 0 to 100
    val urgentUnits: List<String>,
    val targetDailyHours: Float,
    val recommendation: String
)

data class RescueDailyTask(
    val dayNumber: Int,
    val primarySubject: Subject,
    val primaryGoal: String,
    val secondarySubject: Subject,
    val secondaryGoal: String,
    val nightRecitationGoal: String,
    val estimatedHours: Float,
    val isCompleted: Boolean = false
)

object SmartRescuePlannerData {

    fun generateRescuePlan(
        daysRemaining: Int,
        dailyHours: Int,
        mathReadiness: Int,
        physicsReadiness: Int,
        scienceReadiness: Int,
        philosophyReadiness: Int,
        arabicReadiness: Int,
        historyGeoReadiness: Int
    ): Pair<Float, List<RescueSubjectPriority>> {
        // Weighted Readiness Calculation using official BAC coefficients for Scientific streams
        // Math: 7, Science: 6, Physics: 5, Arabic: 3, History/Geo: 2, Philosophy: 2, Islamic: 2, Lang: 2
        val weightedSum = (mathReadiness * 7) + (scienceReadiness * 6) + (physicsReadiness * 5) +
                (arabicReadiness * 3) + (philosophyReadiness * 2) + (historyGeoReadiness * 2)
        val totalWeights = 7 + 6 + 5 + 3 + 2 + 2 // 25
        val overallReadiness = weightedSum.toFloat() / totalWeights.toFloat()

        val priorities = mutableListOf<RescueSubjectPriority>()

        priorities.add(
            RescueSubjectPriority(
                subject = Subject.MATH,
                coefficient = 7,
                currentReadinessPct = mathReadiness,
                urgentUnits = listOf("الدوال العددية والأسية", "المتتاليات والبرهان بالتراجع", "التكاملات والمساحات"),
                targetDailyHours = (dailyHours * 0.35f),
                recommendation = if (mathReadiness < 60) "🔴 أولوية قصوى عاجلة! حل 3 تمارين بكالوريا يومياً في الدوال والمتتاليات لضمان 15+ نقطة." else "🟢 ممتاز، واصل حل مواضيع بكالوريا تجريبية كاملة تحت الضغط الزمني."
            )
        )

        priorities.add(
            RescueSubjectPriority(
                subject = Subject.SCIENCE,
                coefficient = 6,
                currentReadinessPct = scienceReadiness,
                urgentUnits = listOf("تركيب البروتين والترجمة", "النشاط الإنزيمي والبنية الفراغية", "المناعة والذات واللاذات"),
                targetDailyHours = (dailyHours * 0.25f),
                recommendation = if (scienceReadiness < 60) "🔴 ركز على منهجية الاستدلال العلمي والكلمات المفتاحية في سلم التنقيط." else "🟢 درب نفسك على التمرين الثالث (8 نقاط) ومهمة التركيب الشامل."
            )
        )

        priorities.add(
            RescueSubjectPriority(
                subject = Subject.PHYSICS,
                coefficient = 5,
                currentReadinessPct = physicsReadiness,
                urgentUnits = listOf("المتابعة الزمنية وسرعة التفاعل", "الظواهر الكهربائية (RC و RL)", "حركة الكواكب والأقمار (الميكانيك)"),
                targetDailyHours = (dailyHours * 0.20f),
                recommendation = if (physicsReadiness < 60) "🟡 اهتم بالتحليل البعدي وتطبيقات قوانين نيوتن واستغلال المنحنيات البيانية." else "🟢 التركيز على التمارين التجريبية ووحدة الأحماض والأسس."
            )
        )

        priorities.add(
            RescueSubjectPriority(
                subject = Subject.PHILOSOPHY,
                coefficient = 2,
                currentReadinessPct = philosophyReadiness,
                urgentUnits = listOf("انطباق الفكر مع الواقع والرياضيات", "فلسفة العلوم والبيولوجيا", "الشعور بالأنا والشعور بالغير"),
                targetDailyHours = (dailyHours * 0.10f),
                recommendation = "حفظ مخططات المقالات الجدلية والاستقصاء بالوضع مع 4 مقولات فلسفية أساسية لكل مقال."
            )
        )

        priorities.add(
            RescueSubjectPriority(
                subject = Subject.ARABIC,
                coefficient = 3,
                currentReadinessPct = arabicReadiness,
                urgentUnits = listOf("إعراب الجمل ومفردات إذ وإذا", "الصور البيانية والمحسنات البديعية", "الشعر السياسي والتحرري"),
                targetDailyHours = (dailyHours * 0.10f),
                recommendation = "حل موضوع بكالوريا أسبوعياً لضبط أسئلة البناء الفكري واللغوي."
            )
        )

        return Pair(overallReadiness, priorities)
    }

    val sampleRescueDailyTasks = listOf(
        RescueDailyTask(
            dayNumber = 1,
            primarySubject = Subject.MATH,
            primaryGoal = "حل تمرينين بكالوريا رسمية في دراسة الدالة الأسية وحساب المساحات",
            secondarySubject = Subject.PHYSICS,
            secondaryGoal = "مراجعة قوانين دارة RC ومعادلات التفاضلية للتوتر والشحنة",
            nightRecitationGoal = "حفظ 4 شخصيات تاريخية في الحرب الباردة (ترومان، ستالين، مارشال، جدانوف)",
            estimatedHours = 5.5f
        ),
        RescueDailyTask(
            dayNumber = 2,
            primarySubject = Subject.SCIENCE,
            primaryGoal = "تطبيق منهجية الاستدلال العلمي على تمرين المناعة (الاستجابة الخلطية والخلوية)",
            secondarySubject = Subject.MATH,
            secondaryGoal = "حل تمرين متتاليات شامل يحتوي على متتالية مساعدة وحساب المجموع Sn",
            nightRecitationGoal = "حفظ مصطلحات الجغرافيا (عالم الشمال، عالم الجنوب، الثالوث الاقتصادي)",
            estimatedHours = 6.0f
        ),
        RescueDailyTask(
            dayNumber = 3,
            primarySubject = Subject.PHYSICS,
            primaryGoal = "دراسة حركة الأقمار والكواكب وقوانين كبلر الثلاثة وتطبيق القانون الثاني لنيوتن",
            secondarySubject = Subject.PHILOSOPHY,
            secondaryGoal = "كتابة تصميم مقالة جدلية حول 'أصل المفاهيم الرياضية: عقلية أم تجريبية'",
            nightRecitationGoal = "مراجعة قواعد اللغة العربية: إعراب الجمل التي لها محل والتي لا محل لها",
            estimatedHours = 5.0f
        ),
        RescueDailyTask(
            dayNumber = 4,
            primarySubject = Subject.MATH,
            primaryGoal = "الأعداد المركبة: الشكل المثلثي والأسي وحل المعادلات في مجموعة الأعداد المركبة C",
            secondarySubject = Subject.SCIENCE,
            secondaryGoal = "مخطط تحصيلي لآليات تركيب البروتين (الاستنساخ والترجمة وتنشيط الأحماض)",
            nightRecitationGoal = "حفظ درس مقاصد الشريعة الإسلامية وأنواعها (الضروريات، الحاجيات، التحسينيات)",
            estimatedHours = 6.5f
        )
    )
}
