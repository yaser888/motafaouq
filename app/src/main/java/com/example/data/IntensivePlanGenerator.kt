package com.example.data

import com.example.data.models.DayTask
import com.example.data.models.EducationalStream
import com.example.data.models.MonthInfo
import com.example.data.models.PlanConfig
import com.example.data.models.Subject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Intelligent Intensive Plan Generator:
 * Dynamically distributes full educational curricula (Grade 9, Bac Scientific, Bac Literary)
 * across any customized time period between start date and end date selected by the student.
 */
object IntensivePlanGenerator {

    private val ARABIC_DAYS = arrayOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
    private val ARABIC_MONTHS = arrayOf(
        "كانون الثاني", "شباط", "آذار", "نيسان", "أيار", "حزيران",
        "تموز", "آب", "أيلول", "تشرين الأول", "تشرين الثاني", "كانون الأول"
    )

    fun generatePlan(config: PlanConfig): Pair<List<MonthInfo>, List<DayTask>> {
        val startCal = Calendar.getInstance().apply {
            timeInMillis = config.startDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = Calendar.getInstance().apply {
            timeInMillis = config.endDateMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        // Ensure end date is after start date
        if (endCal.timeInMillis < startCal.timeInMillis) {
            endCal.timeInMillis = startCal.timeInMillis + (30L * 24 * 60 * 60 * 1000L)
        }

        val diffMillis = endCal.timeInMillis - startCal.timeInMillis
        val rawDays = (diffMillis / (24L * 60 * 60 * 1000L)).toInt() + 1
        val totalDays = maxOf(3, rawDays)

        // Determine number of phase periods
        val numPeriods = when {
            totalDays <= 21 -> 2
            totalDays <= 45 -> 3
            totalDays <= 75 -> 4
            totalDays <= 120 -> 5
            totalDays <= 180 -> 6
            else -> minOf(9, maxOf(3, (totalDays + 27) / 28))
        }

        val totalWeeks = ((totalDays - 1) / 7) + 1
        val weeksPerPeriod = maxOf(1, totalWeeks / numPeriods)

        // Build Period Months Info
        val monthsInfo = mutableListOf<MonthInfo>()
        for (p in 1..numPeriods) {
            val startW = ((p - 1) * weeksPerPeriod) + 1
            val endW = if (p == numPeriods) totalWeeks else p * weeksPerPeriod

            val (pName, pTitle, pDesc) = getPeriodMeta(config.stream, p, numPeriods, totalDays)
            monthsInfo.add(
                MonthInfo(
                    monthNumber = p,
                    name = pName,
                    title = pTitle,
                    description = pDesc,
                    startWeek = startW,
                    endWeek = maxOf(startW, endW)
                )
            )
        }

        // Retrieve curriculum units for selected stream
        val p1Curriculum = getPeriod1Curriculum(config.stream)
        val p2Curriculum = getPeriod2Curriculum(config.stream)

        val tasks = mutableListOf<DayTask>()
        val currCal = startCal.clone() as Calendar

        var p1Index = 0
        var p2Index = 0

        val finalRevisionDaysCount = maxOf(2, minOf(8, (totalDays * 0.08f).toInt()))
        val finalStartDay = totalDays - finalRevisionDaysCount + 1

        for (dayIndex in 1..totalDays) {
            val dayOfWeekInt = currCal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 7=Sat
            val dayOfWeekName = ARABIC_DAYS[dayOfWeekInt - 1]
            val dayOfMonth = currCal.get(Calendar.DAY_OF_MONTH)
            val monthIdx = currCal.get(Calendar.MONTH) // 0-11
            val arabicMonthName = ARABIC_MONTHS[monthIdx]
            val dateLabel = "$dayOfMonth $arabicMonthName"

            val weekNum = ((dayIndex - 1) / 7) + 1
            // Compute period index (1..numPeriods)
            val periodNum = minOf(numPeriods, ((dayIndex - 1) * numPeriods / totalDays) + 1)

            val isFriday = dayOfWeekInt == Calendar.FRIDAY
            val isFinalPhase = dayIndex >= finalStartDay

            val task = when {
                isFinalPhase -> {
                    // Final days: Comprehensive Exam Simulation & past paper drills
                    buildFinalExamDayTask(
                        config = config,
                        dayIndex = dayIndex,
                        periodNum = periodNum,
                        weekNum = weekNum,
                        dayOfWeek = "$dayOfWeekName ($dateLabel)",
                        countdownRemaining = totalDays - dayIndex + 1
                    )
                }
                isFriday -> {
                    // Friday: weekly consolidation, review & rest day
                    buildFridayReviewTask(
                        config = config,
                        dayIndex = dayIndex,
                        periodNum = periodNum,
                        weekNum = weekNum,
                        dayOfWeek = "$dayOfWeekName ($dateLabel)"
                    )
                }
                else -> {
                    // Regular intensive study day
                    val p1Unit = p1Curriculum[p1Index % p1Curriculum.size]
                    val p2Unit = p2Curriculum[p2Index % p2Curriculum.size]
                    p1Index++
                    p2Index++

                    DayTask(
                        id = "p${periodNum}_d${dayIndex}",
                        monthNumber = periodNum,
                        weekNumber = weekNum,
                        dayOfWeek = "$dayOfWeekName ($dateLabel)",
                        period1Subject = p1Unit.subject,
                        period1Content = p1Unit.content,
                        period2Subject = p2Unit.subject,
                        period2Content = p2Unit.content,
                        period3Content = "تثبيت المساء: مراجعة كبسولات الحفظ، حل تمارين وتمارين سابقة للمادتين (${p1Unit.subject.arabicName} و${p2Unit.subject.arabicName}).",
                        hasCardsTask = true,
                        hasRecitationTask = true,
                        isRestDay = false
                    )
                }
            }

            tasks.add(task)
            currCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return Pair(monthsInfo, tasks)
    }

    private fun buildFridayReviewTask(
        config: PlanConfig,
        dayIndex: Int,
        periodNum: Int,
        weekNum: Int,
        dayOfWeek: String
    ): DayTask {
        val streamLabel = config.stream.shortName
        return DayTask(
            id = "p${periodNum}_d${dayIndex}",
            monthNumber = periodNum,
            weekNumber = weekNum,
            dayOfWeek = dayOfWeek,
            period1Subject = Subject.GENERAL,
            period1Content = "جلسة المراجعة الشاملة للأسبوع: تثبيت القوانين وحل التمارين الصعبة واستدراك أي نقص.",
            period2Subject = Subject.GENERAL,
            period2Content = "اختبار أسبوعي مؤتمت ومحاكاة نموذج جزئي في مواد $streamLabel لتقييم الحفظ والسرعة.",
            period3Content = "استراحة مسائية واسترجاع طاقة الأسبوع، وتسميع كبسولات الأسبوع وترتيب جدول الأسبوع القادم.",
            hasCardsTask = true,
            hasRecitationTask = true,
            isRestDay = true
        )
    }

    private fun buildFinalExamDayTask(
        config: PlanConfig,
        dayIndex: Int,
        periodNum: Int,
        weekNum: Int,
        dayOfWeek: String,
        countdownRemaining: Int
    ): DayTask {
        val streamName = config.stream.shortName
        val p1Subj = when (config.stream) {
            EducationalStream.GRADE_9 -> Subject.MATH
            EducationalStream.BAC_SCIENTIFIC -> Subject.MATH
            EducationalStream.BAC_LITERARY -> Subject.ARABIC
        }
        val p2Subj = when (config.stream) {
            EducationalStream.GRADE_9 -> Subject.SCIENCE
            EducationalStream.BAC_SCIENTIFIC -> Subject.PHYSICS
            EducationalStream.BAC_LITERARY -> Subject.PHILOSOPHY
        }

        return DayTask(
            id = "p${periodNum}_d${dayIndex}",
            monthNumber = periodNum,
            weekNumber = weekNum,
            dayOfWeek = dayOfWeek,
            period1Subject = p1Subj,
            period1Content = "محاكاة الامتحان الشامل (باقي $countdownRemaining أيام على الختام): حل نموذج رسمي مؤقت لـ ${p1Subj.arabicName}.",
            period2Subject = p2Subj,
            period2Content = "مراجعة النماذج الاسترشادية والتوقعات الامتحانية وحل الأسئلة الرسمية الشاملة لـ ${p2Subj.arabicName}.",
            period3Content = "ترميم الثغرات الأخيرة، حل أسئلة الدورات الامتحانية السابقة، وجلسة تسميع كبسولات الحفظ المركزة.",
            hasCardsTask = true,
            hasRecitationTask = true,
            isRestDay = false
        )
    }

    private fun getPeriodMeta(
        stream: EducationalStream,
        period: Int,
        totalPeriods: Int,
        totalDays: Int
    ): Triple<String, String, String> {
        val periodName = "المرحلة $period"
        val title = when (period) {
            1 -> "المرحلة الأولى: التأسيس وإتقان المبادئ الأساسية"
            totalPeriods -> "المرحلة الأخيرة: المراجعة الشاملة وحل الدورات والامتحانات"
            totalPeriods - 1 -> "المرحلة قبل الختام: إتقان الوحدات المتقدمة والتثبيت"
            else -> "المرحلة $period: التقدم المتسارع والتطبيق العملي"
        }

        val desc = when (stream) {
            EducationalStream.GRADE_9 -> when (period) {
                1 -> "إتقان الأعداد والجذور ومبرهنة تالس والجهاز العصبي وقواعد اللغة العربية الأولى."
                totalPeriods -> "حل نماذج امتحانية شاملة لشهادة التعليم الأساسي (التاسع) وترميم الثغرات لضمان التفوق."
                else -> "تغطية مكثفة للجبر والهندسة والعلوم العامة واللغات والدراسات الاجتماعية والإسلامية."
            }
            EducationalStream.BAC_SCIENTIFIC -> when (period) {
                1 -> "إتقان المتتاليات والنهايات والنواسات والكيمياء النووية والغازات والتنسيق العصبي."
                totalPeriods -> "حل دورات البكالوريا العلمية الرسمية السابقة والنماذج الاسترشادية الشاملة تحت ضغط المؤقت."
                else -> "تغطية مكثفة للتحليل الرياضي والفيزياء والكيمياء والوراثة والتكاثر واللغات."
            }
            EducationalStream.BAC_LITERARY -> when (period) {
                1 -> "الأدب العربي والقضايا الوطنية، نظرية المعرفة في الفلسفة، وتاريخ سوريا المعاصر."
                totalPeriods -> "حل دورات البكالوريا الأدبية الرسمية وكتابة الموضوع الأدبي الإجباري ونماذج الفلسفة والجغرافيا."
                else -> "تغطية مكثفة للنصوص الأدبية، القواعد، الفلسفة وعلم النفس، التاريخ، الجغرافيا، واللغات."
            }
        }

        return Triple(periodName, title, desc)
    }

    data class SyllabusUnit(val subject: Subject, val content: String)

    // Curriculum for Period 1 (Morning Heavy Subjects)
    private fun getPeriod1Curriculum(stream: EducationalStream): List<SyllabusUnit> {
        return when (stream) {
            EducationalStream.GRADE_9 -> listOf(
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): الأعداد العادية، PGCD وخوارزمية إقليدس وحل التمارين ص 9-18."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): مبرهنة تالس في المثلث وحساب أطوال الأضلاع ص 9-22."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: الجهاز العصبي المركزي (الدماغ والنخاع) والأفعال المنعكسة ص 10-25."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): قوى الأعداد العادية وقواعد القوى والكتابة العلمية ص 20-30."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): عكس مبرهنة تالس وإثبات توازي المستقيمات ص 24-34."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: المستقبلات الحسية (العين والأذن وبنية الحواس) ص 37-64."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): الجذور التربيعية والعمليات عليها والتبسيط ص 32-44."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): تشابه المثلثات وحالات التشابه الثلاث ص 47-60."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: الغدد الصم والتنظيم الهرموني ووظائف الهرمونات ص 66-78."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): النشر والتحليل والمتطابقات التربيعية الشهيرة ص 46-58."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): النسب المثلثية لزاوية حادة والعلاقات الأساسية ص 62-75."),
                SyllabusUnit(Subject.SCIENCE, "الفيزياء: الحركة المستقيمة المنتظمة ومتوسط السرعة وقوانين الحركة ص 10-24."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): حل المعادلات من الدرجة الأولى والمعادلات الصفرية ص 60-70."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): الدائرة: الأوتار والمماسات والوضع النسبي لمستقيم ودائرة ص 77-90."),
                SyllabusUnit(Subject.SCIENCE, "الفيزياء: القوة وعناصرها وتوازن الأجسام ومحصلة قوتين ص 26-40."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): المتراجحات من الدرجة الأولى وتمثيل الحلول ص 72-82."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): الزوايا في الدائرة (المركزية والمحيطية والمماسية) ص 92-105."),
                SyllabusUnit(Subject.SCIENCE, "الفيزياء: العمل الميكانيكي، الاستطاعة، والطاقة وحفظ الطاقة ص 42-58."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): جملة معادلتين خطيتين بمجهولين (التعويض والجمع) ص 84-98."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): الرباعي الدائري وإثبات كون الرباعي دائرياً ص 107-118."),
                SyllabusUnit(Subject.SCIENCE, "الكيمياء: المحاليل المائية والتراكيز والحساب الكيميائي ص 10-24."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): حل المسائل الحياتية عبر جمل المعادلات ص 100-110."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): الهندسة الفضائية: الموشور والأسطوانة الدورانية والحجوم ص 132-144."),
                SyllabusUnit(Subject.SCIENCE, "الكيمياء: التفاعلات الكيميائية وأنواعها وسرعة التفاعل ص 26-40."),
                SyllabusUnit(Subject.MATH, "الرياضيات (جبر): الإحصاء والاحتمالات وحساب احتمالات الأحداث ص 124-148."),
                SyllabusUnit(Subject.MATH, "الرياضيات (هندسة): الهرم والمخروط والكرة والمجسمات الفضائية ص 146-160."),
                SyllabusUnit(Subject.SCIENCE, "الكيمياء: الحموض والأسس والأملاح وتفاعلات الترسيب ص 42-72.")
            )

            EducationalStream.BAC_SCIENTIFIC -> listOf(
                SyllabusUnit(Subject.MATH, "الرياضيات: عموميات المتتاليات: طرق تعريف المتتالية الحسابية والهندسية، وإيجاد الحدود ص 7-26."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: النواس المرن: الدراسة التحريكية والتابع الزمني والطاقة الميكانيكية ص 7-25."),
                SyllabusUnit(Subject.MATH, "الرياضيات: البرهان بالتدريج وتطبيقاته على المتتاليات وحساب المجاميع ص 28-38."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: الكيمياء النووية: تفاعلات التفكك، الأسر، والانشطار النووي وطاقة الارتباط ص 7-25."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: النسيج العصبي وخواص الليف العصبي وكمون الراحة وكمون العمل ص 7-28."),
                SyllabusUnit(Subject.MATH, "الرياضيات: نهايات التوابع ومبرهنات الإحاطة والمقارنة ص 40-58."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: نواس الفتل: العزم، المعادلة التفاضلية ودور النواس وتطبيقاته ص 27-42."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: الغازات وقوانين الغاز المثالي وضغوط دالتون الجزئية ص 27-46."),
                SyllabusUnit(Subject.MATH, "الرياضيات: حالات عدم التعيين وإزالتها وتطبيقات المقاربات ص 60-78."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: المشابك والنواقل الكيميائية وانتقال السيالة ص 30-50."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: النواس الثقيل البسيط والمركب ومقارنة الأدوار ص 44-60."),
                SyllabusUnit(Subject.MATH, "الرياضيات: الاستمرار والاشتقاق ومبرهنة القيمة الوسطى ص 80-98."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: سرعة التفاعل الكيميائي ورتبة التفاعل ونظرية التصادم ص 48-68."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: ميكانيك الموائع ونظرية برنولي ومعادلة الاستمرارية ومقياس فنتوري ص 62-80."),
                SyllabusUnit(Subject.MATH, "الرياضيات: تطبيقات الاشتقاق والتقريب الخطي وقرين الاطراد والقيم الحدية ص 100-120."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: المستقبلات الحسية (العين والأذن والمستقبلات الآلية) ص 52-78."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: المغناطيسية وقوة لورنتز وقوة لابلاس وتجربة السكتين ص 82-120."),
                SyllabusUnit(Subject.MATH, "الرياضيات: التابع اللوغاريتمي النيبيري (الخواص، النهايات، الاشتقاق ورسم الخط) ص 122-145."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: التوازن الكيميائي، ثابت التوازن ول format وقاعدة لوشاتولييه ص 70-92."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: التحريض الكهرومغناطيسي، قانون فاراداي ولينز والتحريض الذاتي ص 122-144."),
                SyllabusUnit(Subject.MATH, "الرياضيات: التابع الأسي النيبيري (الخواص، النهايات، الاشتقاق ورسم الخط البياني) ص 147-170."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: الغدد الصم والتنظيم الهرموني ووظائف الهرمونات ص 80-104."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: التيارات المتناوبة الجيبية ودارة RLC وظاهرة الطنين ص 146-170."),
                SyllabusUnit(Subject.MATH, "الرياضيات: التكامل والتوابع الأصلية وقواعد حساب التوابع الأصلية والتجزئة ص 172-218."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: الحموض والأسس القوية والضعيفة وحسابات pH والمشعر ص 94-118."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: الدارات المهتزة والأمواج المستقرة وتجربة ملد والمزامير ص 194-235."),
                SyllabusUnit(Subject.MATH, "الرياضيات: الأعداد العقدية: الشكل الجبري والمثلثي والأسي ودستور دو موافر ص 7-55."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: التكاثر لدى النباتات عاريات ومغلفات البذور والتكاثر البشري ص 106-165."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: محاليل الأملاح والحلهمة وجداء الانحلال والمعايرة الحجمية ص 120-168."),
                SyllabusUnit(Subject.MATH, "الرياضيات: تطبيقات العقدية في الهندسة والأشعة بالفراغ والمستويات والكرات ص 57-132."),
                SyllabusUnit(Subject.PHYSICS, "الفيزياء: الفيزياء الفلكية ونظرية النسبية الخاصة ونماذج الذرة ص 237-260."),
                SyllabusUnit(Subject.CHEMISTRY, "الكيمياء: الكيمياء العضوية: الأغوال، الإيثرات، الألدهيدات، الإسترات والأميدات ص 170-230."),
                SyllabusUnit(Subject.SCIENCE, "علم الأحياء: الوراثة الكلاسيكية والبشرية وقوانين مندل والطفرات ص 194-250."),
                SyllabusUnit(Subject.MATH, "الرياضيات: التحليل التوافقي والاحتمالات والمتغيرات العشوائية والتوزيعات ص 134-185.")
            )

            EducationalStream.BAC_LITERARY -> listOf(
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: مدخل إلى الأدب العربي وعصور الأدب والشعر الإحيائي ص 7-25."),
                SyllabusUnit(Subject.PHILOSOPHY, "الفلسفة: نظرية المعرفة: مصادر المعرفة الإنسانية (العقل، الحس، الحدس) ص 7-30."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: الوطن العربي تحت الحكم العثماني والسياسات الاستبدادية ص 7-32."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: الموقع الفلكي والجغرافي وأهميته الجيوسياسية ص 7-30."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: دراسة قصيدة (عرس المجد) للشاعر عمر أبو ريشة تحليلاً وإعراباً ص 27-45."),
                SyllabusUnit(Subject.PHILOSOPHY, "الفلسفة: الإدراك الحسي والوعي والشعور واللاشعور ص 32-55."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: حركات اليقظة العربية والجمعيات الإصلاحية والثورة الكبرى ص 34-60."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: البنية الجيولوجية والتضاريس الكبرى للوطن العربي ص 32-58."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: النحو: المنصوبات (المفاعيل الخمسة، الحال، التمييز، المستثنى) ص 47-70."),
                SyllabusUnit(Subject.PHILOSOPHY, "الفلسفة: المنطق الصوري ومبادئ الفكر وقضايا الاستدلال ص 57-82."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: الاستعمار الأوروبي والاتفاقيات السرية وميسلون ص 62-90."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: المناخ والأقاليم المناخية وتأثير الاحتباس الحراري ص 60-85."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: دراسة قصيدة (الوطن) وبنية القصيدة الوجدانية والقومية ص 72-90."),
                SyllabusUnit(Subject.PHILOSOPHY, "الفلسفة: فلسفة الأخلاق: الواجب عند كانط ومذهب المنفعة ص 84-110."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: الثورات الوطنية ضد الانتداب الفرنسي والبريطاني والاستقلال ص 92-120."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: الموارد المائية ومشكلات التصحر والجفاف ص 87-115."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: الصرف: مصادر الأفعال الثلاثية وفوق الثلاثية والاسم المشتق ص 92-115."),
                SyllabusUnit(Subject.PHILOSOPHY, "الفلسفة: فلسفة العلوم والمنهج الاستقرائي والفرضي العلمي ص 112-138."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: القضية الفلسطينية والحروب العربية والصراع المعاصر ص 122-155."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: الجغرافيا السكانية: الهجرة والديموغرافيا والنمو الحضري ص 117-145."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: دراسة قصيدة (المهاجر) والشعر المهجري والرمزية ص 117-138."),
                SyllabusUnit(Subject.PHILOSOPHY, "علم النفس: الشخصية ونظرياتها والعوامل المحددة لبناء الشخصية ص 140-165."),
                SyllabusUnit(Subject.HISTORY, "التاريخ: حركات التحرر في المغرب العربي والتضامن العربي ص 157-190."),
                SyllabusUnit(Subject.GEOGRAPHY, "الجغرافيا: الموارد الاقتصادية والنفط والزراعة والتجارة ص 147-178."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: البلاغة والنقد الأدبي: التشبيه، الاستعارة، ومناهج النقد ص 140-160."),
                SyllabusUnit(Subject.PHILOSOPHY, "علم النفس: الانفعالات والعمليات العقلية المعرفية والتعلم ص 167-192."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: فن الرواية والمسرحية ودراسة رواية (دمشق يا بسمة الحزن) ص 212-240."),
                SyllabusUnit(Subject.PHILOSOPHY, "علم الاجتماع: التغير الاجتماعي والمؤسسات وقضايا الفلسفة المعاصرة ص 194-250."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: التعبير الأدبي الإجباري: كتابة الموضوع الأدبي الموازي ص 267-295.")
            )
        }
    }

    // Curriculum for Period 2 (Medium / Complementary Subjects)
    private fun getPeriod2Curriculum(stream: EducationalStream): List<SyllabusUnit> {
        return when (stream) {
            EducationalStream.GRADE_9 -> listOf(
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: نص (قيم إنسانية) وحفظ الأبيات وإعراب المفردات ص 10-18."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Tenses (Present & Past) + مفردات وقواعد الوحدة 1."),
                SyllabusUnit(Subject.HISTORY_GEO, "التاريخ: الحضارات القديمة في بلاد الشام ومظاهرها الحضارية ص 10-25."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: القرآن الكريم وأحكام التجويد والحديث النبوي ص 10-25."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: زمن الماضي المركب والمفردات الأساسية للوحدة 1-2."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: المشتقات (اسم الفاعل واسم المفعول والصفة المشبهة) ص 20-30."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Passive Voice & Modals + نصوص القراءة الوحدة 2-3."),
                SyllabusUnit(Subject.HISTORY_GEO, "الجغرافيا: تضاريس الوطن العربي والموقع الجغرافي لسوريا ص 10-28."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: العقيدة الإسلامية وسيرة النبي ﷺ والأخلاق ص 27-48."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: زمن المستقبل البسيط والضمائر الشخصية ص 25-45."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: نص (دمشق عاصمة المجد) ودراسة الأساليب البلاغية ص 32-42."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Conditionals (If 1 & 2) + كتابة الموضوع الأول."),
                SyllabusUnit(Subject.HISTORY_GEO, "التربية الوطنية: الدولة، الدستور، سيادة القانون والمواطنة ص 10-35."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: اسما الزمان والمكان واسم الآلة والممنوع من الصرف ص 44-54."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: نصوص الفهم والتعبير الكتابي والتمارين الامتحانية."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: التعبير الكتابي: كتابة سيرة ومقالة اجتماعية ص 122-135.")
            )

            EducationalStream.BAC_SCIENTIFIC -> listOf(
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: بداية نص (عرس المجد) للشاعر عمر أبو ريشة: قراءة النص وإعراب المقطع 1 ص 7-15."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 1: Reading comprehension, grammar (Relative Clauses) & Vocabulary."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 1: Texte, temps du passé et vocabulaire thématique."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: الوحدة الأولى: القرآن الكريم (سورة النور)، العقيدة، والأخلاق ص 10-35."),
                SyllabusUnit(Subject.NATIONAL, "التربية الوطنية: القضايا الوطنية والمواطنة والدستور وسيادة القانون ص 10-30."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: نص (عرس المجد) المقطع الثاني وإعراب الجمَل والتطبيقات النحوية ص 17-25."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 2: Passive Voice and Causative Verbs + الموضوع الأول."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 2: La condition et l'hypothèse + Exercices d'expression écrite."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: نص (الوطن) ودراسة المعاني النفسية والبلاغة والجماليات ص 27-40."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: الحديث الشريف، الفقه الإسلامي، والمعاملات المالية ص 37-65."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: نص (المهاجر) للشعر المهجري ودراسة الموسيقى الداخلية ص 42-58."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 3: Conditionals (Wish & If clauses) + كتابة المقال."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 3: Le discours rapporté et synthèse grammaticale."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: كتابة الموضوع الأدبي الإجباري الشامل وموضوع المقالة ص 150-180."),
                SyllabusUnit(Subject.NATIONAL, "التربية الوطنية: الفكر السياسي والتنمية الاقتصادية والمجتمع السوري ص 32-60.")
            )

            EducationalStream.BAC_LITERARY -> listOf(
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 1: Literature texts, advanced reading & grammar tenses."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 1: Compréhension écrite, littérature et grammaire avancée."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: الفكر الإسلامي، مقاصد الشريعة، والفلسفة الإسلامية ص 10-40."),
                SyllabusUnit(Subject.NATIONAL, "التربية الوطنية: الدولة، المجتمع، الدستور السوري، والسيادة الوطنية ص 10-35."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 2: Passive forms, connectors and essay writing skills."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 2: L'argumentation, le subjonctif et production d'écrits."),
                SyllabusUnit(Subject.ARABIC, "اللغة العربية: البلاغة والعروض: أوزان البحور الشعرية والتقطيع العروضي ص 240-265."),
                SyllabusUnit(Subject.ISLAMIC, "التربية الإسلامية: القضايا الأخلاقية والاجتماعية في الفقه والحديث ص 42-75."),
                SyllabusUnit(Subject.ENGLISH, "اللغة الإنكليزية: Unit 3: Advanced Conditionals, Reported Speech & Writing."),
                SyllabusUnit(Subject.FRENCH, "اللغة الفرنسية: Unité 3: Les expressions métaphoriques et analyse textuelle."),
                SyllabusUnit(Subject.NATIONAL, "التربية الوطنية: العلاقات الدولية، القانون الدولي الإنساني، وقضايا العصر ص 37-68.")
            )
        }
    }
}
