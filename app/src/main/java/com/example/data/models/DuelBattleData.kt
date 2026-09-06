package com.example.data.models

data class DuelQuestion(
    val id: String,
    val subject: Subject,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val explanation: String
)

data class PeerRival(
    val name: String,
    val wilaya: String,
    val avatarEmoji: String,
    val scoreRatio: Float, // accuracy 0.6 to 0.95
    val rankBadge: String,
    val speedLevelSeconds: Float // 3.0s to 8.0s
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val wilaya: String,
    val xp: Int,
    val winStreak: Int,
    val rankTitle: String,
    val isCurrentUser: Boolean = false
)

object DuelBattleData {
    val rivals = listOf(
        PeerRival("أمين زروقي", "وهران 🦁", "👨‍🎓", 0.85f, "فارس الرياضيات", 4.2f),
        PeerRival("سارة بلقاسم", "قسنطينة 🌉", "👩‍🔬", 0.92f, "عبقرية العلوم", 3.8f),
        PeerRival("ياسين منصوري", "الجزائر العاصمة ⚓", "🧑‍💻", 0.80f, "متحدي البكالوريا", 5.1f),
        PeerRival("خديجة بن علي", "سطيف 🦅", "🧕", 0.88f, "نجمة الفيزياء", 4.5f),
        PeerRival("حمزة لعروسي", "باتنة 🌲", "👨‍💼", 0.75f, "فيلسوف الدفعة", 6.0f),
        PeerRival("نور الهدى", "تلمسان 🌺", "👩‍🎓", 0.95f, "أسطورة الامتياز", 3.5f)
    )

    val duelQuestions = listOf(
        DuelQuestion(
            id = "dq_1",
            subject = Subject.MATH,
            questionText = "مشتقة الدالة f(x) = ln(2x + 4) هي:",
            optionA = "1 / (x + 2)",
            optionB = "2 / (x + 2)",
            optionC = "1 / (2x + 4)",
            optionD = "2(2x + 4)",
            correctAnswer = "A",
            explanation = "f'(x) = 2 / (2x + 4) = 2 / (2(x + 2)) = 1 / (x + 2)."
        ),
        DuelQuestion(
            id = "dq_2",
            subject = Subject.PHYSICS,
            questionText = "الوحدة الدولية لثابت النشاط الإشعاعي λ هي:",
            optionA = "s^(-1)",
            optionB = "Bq (بيكرل)",
            optionC = "J (جول)",
            optionD = "متر/ثانية",
            correctAnswer = "A",
            explanation = "ثابت التفكك λ وحدته مقلوب الزمن ثانية^-1، بينما النشاط A يقدر بالبيكرل."
        ),
        DuelQuestion(
            id = "dq_3",
            subject = Subject.SCIENCE,
            questionText = "تتشكل الرابطة البيبتيدية بين حمضين أمينيين بخروج جزيئة:",
            optionA = "ماء (H2O)",
            optionB = "ثاني أكسيد الكربون (CO2)",
            optionC = "أمونياك (NH3)",
            optionD = "أكسجين (O2)",
            correctAnswer = "A",
            explanation = "تتشكل الرابطة البيبتيدية بين COOH للحمض الأول و NH2 للحمض الثاني مع تحرير جزيء ماء H2O."
        ),
        DuelQuestion(
            id = "dq_4",
            subject = Subject.PHILOSOPHY,
            questionText = "صاحب مقولة 'أنا أفكر إذن أنا موجود' هو الفيلسوف:",
            optionA = "رينيه ديكارت",
            optionB = "إيمانويل كانط",
            optionC = "فريدريك نيتشه",
            optionD = "أرسطو",
            correctAnswer = "A",
            explanation = "الكوجيتو الديكارتي الشهير (Je pense, donc je suis) من أسس الفلسفة العقلانية الحديثة."
        ),
        DuelQuestion(
            id = "dq_5",
            subject = Subject.HISTORY_GEO,
            questionText = "تأسست حركة عدم الانحياز رسمياً في مؤتمر بلغراد سنة:",
            optionA = "1961",
            optionB = "1955",
            optionC = "1973",
            optionD = "1945",
            correctAnswer = "A",
            explanation = "مؤتمر باندونغ كان تمهيداً عام 1955، لكن التأسيس الرسمي لحركة عدم الانحياز كان في بلغراد 1961."
        ),
        DuelQuestion(
            id = "dq_6",
            subject = Subject.ARABIC,
            questionText = "الشاعر الملقب بـ 'شاعر الثورة الجزائرية' ومؤلف النشيد الوطني هو:",
            optionA = "مفدي زكريا",
            optionB = "محمد البشير الإبراهيمي",
            optionC = "أبو القاسم الشابي",
            optionD = "محمود درويش",
            correctAnswer = "A",
            explanation = "مفدي زكريا هو مؤلف نشيد قسماً وشاعر اللهب المقدس وإلياذة الجزائر."
        ),
        DuelQuestion(
            id = "dq_7",
            subject = Subject.MATH,
            questionText = "إذا كان z = 1 + i فإن طويلته |z| وعمدته arg(z) هما:",
            optionA = "√2 و π/4",
            optionB = "2 و π/4",
            optionC = "√2 و π/3",
            optionD = "1 و π/2",
            correctAnswer = "A",
            explanation = "|z| = √(1² + 1²) = √2، cos θ = 1/√2, sin θ = 1/√2 => θ = π/4."
        )
    )

    val leaderboard = listOf(
        LeaderboardUser(1, "سارة بلقاسم", "قسنطينة", 4850, 14, "أسطورة الامتياز 👑"),
        LeaderboardUser(2, "نور الهدى", "تلمسان", 4620, 11, "فارس الرياضيات ⚡"),
        LeaderboardUser(3, "أمين زروقي", "وهران", 4310, 9, "عبقري التكاملات 🎯"),
        LeaderboardUser(4, "أنت (المتفوق)", "الجزائر", 3950, 6, "نجم البكالوريا 🌟", isCurrentUser = true),
        LeaderboardUser(5, "خديجة بن علي", "سطيف", 3740, 5, "نجمة الفيزياء 🔬"),
        LeaderboardUser(6, "ياسين منصوري", "العاصمة", 3420, 4, "متحدي البكالوريا 🚀"),
        LeaderboardUser(7, "حمزة لعروسي", "باتنة", 3100, 3, "فيلسوف الدفعة 📚")
    )
}
