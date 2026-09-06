package com.example.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class MistakeItem(
    val id: String,
    val subject: Subject,
    val unitOrTopic: String,
    val questionText: String,
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String,
    val studentWrongAnswer: String,
    val explanation: String,
    val trapReason: String, // فخ الإجابة الخاطئة
    val personalNote: String = "", // ملاحظة الطالب لتفادي الخطأ
    val repetitionLevel: Int = 1, // 1: اليوم, 2: بعد 3 أيام, 3: بعد أسبوع, 4: متقن
    val consecutiveCorrect: Int = 0,
    val isMastered: Boolean = false,
    val addedDate: String = "اليوم",
    val nextReviewDate: String = "اليوم"
)

object MistakeVaultData {
    val initialMistakes = listOf(
        MistakeItem(
            id = "mst_1",
            subject = Subject.MATH,
            unitOrTopic = "الدوال الأسية واللوغاريتمية",
            questionText = "نهاية الدالة f(x) = (e^x - 1) / x لما يؤول x إلى 0 تساوي:",
            optionA = "1",
            optionB = "0",
            optionC = "+∞",
            optionD = "غير معرفة",
            correctAnswer = "A",
            studentWrongAnswer = "B",
            explanation = "هذه نهاية شهيرة تعبر عن العدد المشتق للدالة الأسية عند 0: lim (e^x - e^0)/(x - 0) = e^0 = 1.",
            trapReason = "التعويض المباشر يعطي 0/0 (حالة عدم تعيين) لكن الطالب تعجل واختار 0 بدلاً من تطبيق النهاية الشهيرة أو العدد المشتق.",
            personalNote = "تذكر دائماً أن (e^x - 1)/x عند 0 هي نهاية شهيرة تساوي 1 وليست 0!",
            repetitionLevel = 1,
            consecutiveCorrect = 1,
            isMastered = false,
            addedDate = "أمس",
            nextReviewDate = "اليوم"
        ),
        MistakeItem(
            id = "mst_2",
            subject = Subject.MATH,
            unitOrTopic = "التكامل وحساب المساحات",
            questionText = "قيمة التكامل I = ∫[0 إلى 1] (2x / (x² + 1)) dx تساوي بدقة:",
            optionA = "ln(2)",
            optionB = "ln(3)",
            optionC = "2 ln(2)",
            optionD = "1/2 ln(2)",
            correctAnswer = "A",
            studentWrongAnswer = "B",
            explanation = "الدالة من الشكل u'(x)/u(x) حيث u(x) = x² + 1. دالتها الأصلية ln|x² + 1|. بالتعويض [0..1]: ln(2) - ln(1) = ln(2).",
            trapReason = "نسيان أن ln(1) = 0 والخطأ في حساب تعويض الحد السفلي 0.",
            personalNote = "انتبه: ln(1) = 0 دوماً! لا تنسَ كتابة التعويض خطوة بخطوة.",
            repetitionLevel = 2,
            consecutiveCorrect = 2,
            isMastered = false,
            addedDate = "منذ يومين",
            nextReviewDate = "بعد يومين"
        ),
        MistakeItem(
            id = "mst_3",
            subject = Subject.PHYSICS,
            unitOrTopic = "الظواهر الكهربائية (دارة RC)",
            questionText = "في دارة RC أثناء الشحن، تكون الطاقة المخزنة في المكثفة في اللحظة t = ∞ مساوية لـ:",
            optionA = "1/2 C E²",
            optionB = "1/2 L I²",
            optionC = "C E",
            optionD = "C E²",
            correctAnswer = "A",
            studentWrongAnswer = "D",
            explanation = "الطاقة الأعظمية للمكثفة مشحونة كلياً هي Ee_max = 1/2 C.Uc² = 1/2 C E² (بالجول J).",
            trapReason = "نسيان المعامل 1/2 في قانون طاقة المكثفة والخلط بينها وبين شحنة المكثفة Q = C.E.",
            personalNote = "قانون الطاقة فيه دائماً 1/2 (نصف السعة في مربع التوتر الأعظمي).",
            repetitionLevel = 1,
            consecutiveCorrect = 0,
            isMastered = false,
            addedDate = "اليوم",
            nextReviewDate = "اليوم"
        ),
        MistakeItem(
            id = "mst_4",
            subject = Subject.SCIENCE,
            unitOrTopic = "النشاط الإنزيمي وعلاقته بالبنية",
            questionText = "تأثير درجة الحرارة المرتفعة جداً (أكثر من 60°C) على النشاط الإنزيمي يكون:",
            optionA = "تخريب غير عكوس للبنية الفراغية للموقع الفعال",
            optionB = "تثبيط مؤقت يزول بعودة الحرارة للمثلى",
            optionC = "زيادة سرعة التفاعل الإنزيمي",
            optionD = "تغيير في تتابع الأحماض الأمينية الأولية",
            correctAnswer = "A",
            studentWrongAnswer = "B",
            explanation = "الحرارة المرتفعة تكسر الروابط الهيدروجينية والشاردية وتخرب البنية الفراغية نهائياً (تخريب غير عكوس)، بينما الحرارة المنخفضة تثبط النشاط مؤقتاً.",
            trapReason = "الخلط بين تأثير الحرارة المنخفضة (عكوس) وتأثير الحرارة المرتفعة (غير عكوس نهائي).",
            personalNote = "الحرارة المنخفضة = تجميد عكوس. الحرارة العالية = مسخ وتخريب نهائي للموقع الفعال.",
            repetitionLevel = 3,
            consecutiveCorrect = 3,
            isMastered = true,
            addedDate = "منذ 6 أيام",
            nextReviewDate = "متقن ومثبت 🎉"
        ),
        MistakeItem(
            id = "mst_5",
            subject = Subject.ARABIC,
            unitOrTopic = "إعراب الجمل والقواعد",
            questionText = "في جملة: 'وقف المعلم يشرح الدرس'، الجملة الفعلية (يشرح الدرس) في محل:",
            optionA = "نصب حال",
            optionB = "رفع خبر",
            optionC = "نعت",
            optionD = "لا محل لها من الإعراب",
            correctAnswer = "A",
            studentWrongAnswer = "C",
            explanation = "صاحب الحال 'المعلم' معرف بال، والقاعدة: الجمل بعد المعارف أحوال وبعد النكرات نعوت.",
            trapReason = "التسرع وعدم الانتباه لتعريف صاحب الحال بـ (ال).",
            personalNote = "دائماً افحص الكلمة التي قبل الجملة: هل هي معرفة (إذن حال) أم نكرة (إذن نعت).",
            repetitionLevel = 2,
            consecutiveCorrect = 2,
            isMastered = false,
            addedDate = "منذ 3 أيام",
            nextReviewDate = "غداً"
        )
    )
}
