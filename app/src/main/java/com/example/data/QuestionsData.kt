package com.example.data

import com.example.data.db.QuestionEntity
import com.example.data.models.Subject

object QuestionsData {
    val initialQuestions = listOf(
        // --- الرياضيات (MATH) ---
        QuestionEntity(
            subject = Subject.MATH.name,
            unitOrTopic = "الوحدة الأولى: التحليل والتوابع ونهايات",
            questionText = "ما هي نهاية التابع f(x) = (sin(3x)) / (2x) عندما x تسعى إلى الصفر؟",
            questionType = "MCQ",
            optionA = "3/2",
            optionB = "2/3",
            optionC = "0",
            optionD = "غير معينة",
            correctAnswer = "A",
            explanation = "باستخدام المبرهنة الشهيرة: lim (sin(kx)/x) عندما x -> 0 تساوي k. بالتالي lim (sin(3x)/(2x)) = (1/2) * lim (sin(3x)/x) = (1/2) * 3 = 3/2.",
            difficulty = "متوسط",
            yearOrSource = "دورة 2023 الأولى"
        ),
        QuestionEntity(
            subject = Subject.MATH.name,
            unitOrTopic = "الوحدة الثانية: الاشتقاق وتطبيقاته",
            questionText = "ما هو مشتق التابع f(x) = e^(2x) * cos(x) عند أي نقطة x؟",
            questionType = "MCQ",
            optionA = "e^(2x) * (2 cos(x) - sin(x))",
            optionB = "2 e^(2x) * sin(x)",
            optionC = "-e^(2x) * sin(x)",
            optionD = "e^(2x) * (cos(x) + sin(x))",
            correctAnswer = "A",
            explanation = "مشتق جداء تابعين: f'(x) = u'v + uv'. مشتق e^(2x) هو 2e^(2x)، ومشتق cos(x) هو -sin(x). بالتعويض: f'(x) = 2e^(2x)cos(x) - e^(2x)sin(x) = e^(2x)(2cos(x) - sin(x)).",
            difficulty = "متوسط",
            yearOrSource = "نموذجي"
        ),
        QuestionEntity(
            subject = Subject.MATH.name,
            unitOrTopic = "الوحدة الثالثة: الأشعة في الفراغ والجداء السلمي",
            questionText = "متى يكون الشعاعان u⃗ و v⃗ متعامدين في الفضاء الإقليدي؟",
            questionType = "MCQ",
            optionA = "إذا كان جدائهما السلمي u⃗ · v⃗ = 0",
            optionB = "إذا كان جدائهما السلمي u⃗ · v⃗ = 1",
            optionC = "إذا كانا متوازيين",
            optionD = "إذا كانت زاوية بينهما 0 درجة",
            correctAnswer = "A",
            explanation = "الشرط اللازم والكافي لتعامد شعاعين غير صفريين هو انعدام جدائهما السلمي u⃗ · v⃗ = xx' + yy' + zz' = 0.",
            difficulty = "سهل",
            yearOrSource = "قواعد أساسية"
        ),
        QuestionEntity(
            subject = Subject.MATH.name,
            unitOrTopic = "الوحدة الرابعة: التكامل والتوابع الأصلية",
            questionText = "احسب التكامل المحدد I = ∫ [من 0 إلى 1] (2x + 3) dx.",
            questionType = "ESSAY",
            optionA = "",
            optionB = "",
            optionC = "",
            optionD = "",
            correctAnswer = "I = 4",
            explanation = "التابع الأصلي لـ (2x + 3) هو F(x) = x² + 3x. بالتالي I = [x² + 3x] من 0 إلى 1 = (1² + 3(1)) - (0 + 0) = 1 + 3 = 4.",
            difficulty = "متوسط",
            yearOrSource = "مسائل الامتحان النهائي"
        ),

        // --- الفيزياء (PHYSICS) ---
        QuestionEntity(
            subject = Subject.PHYSICS.name,
            unitOrTopic = "الحركة والتحريك: النواسات (النواس المرن والثقلي)",
            questionText = "ما هو الدور الخاص T₀ لنواس مرن كتلته m وصلابة نوابضه k؟",
            questionType = "MCQ",
            optionA = "T₀ = 2π √(m/k)",
            optionB = "T₀ = 2π √(k/m)",
            optionC = "T₀ = 2π √(l/g)",
            optionD = "T₀ = 1 / (2π √(m/k))",
            correctAnswer = "A",
            explanation = "العلاقة النظرية للدور الخاص للنواس المرن الخطي في حالة الاهتزازات غير المتخامدة هي T₀ = 2π/ω₀ حيث نبض الحركة ω₀ = √(k/m)، وبالتالي T₀ = 2π √(m/k).",
            difficulty = "سهل",
            yearOrSource = "دورة 2022 الثانية"
        ),
        QuestionEntity(
            subject = Subject.PHYSICS.name,
            unitOrTopic = "الكهرباء والمغناطيسية: التحريض الكهرطيسي",
            questionText = "ينص قانون لنز للتحريض الكهرطيسي على أن التيار المتحرض ينشأ بجهة بحيث:",
            questionType = "MCQ",
            optionA = "يعاكس بفعله المغناطيسي السبب الذي أدى إلى حدوثه",
            optionB = "يزيد من التدفق المغناطيسي دائماً",
            optionC = "ينعدم عندما يكون التدفق أعظمياً",
            optionD = "يتجه دائماً نحو القطب الشمالي",
            correctAnswer = "A",
            explanation = "قانون لنز يحدد جهة القوة المحركة والجهد المتحرض، حيث يتولد تيار تحريضي يولد حقلاً مغناطيسياً يعاكس تغير التدفق المسبب له (قاعدة حفظ الطاقة).",
            difficulty = "متوسط",
            yearOrSource = "أسئلة نظرية هامة"
        ),
        QuestionEntity(
            subject = Subject.PHYSICS.name,
            unitOrTopic = "الدارات المتناوبة: دارة R-L-C على التسلسل",
            questionText = "ما هي حالة التجاوب الكهربائي (الطنين) في دارة RLC متسلسلة؟",
            questionType = "ESSAY",
            optionA = "",
            optionB = "",
            optionC = "",
            optionD = "",
            correctAnswer = "تتحقق عندما يكون النبض ω = 1/√(LC) وتكون الممانعة Z صغرية Z = R وتكون شدة التيار عظمى I = U/R وتكون الدارة بأكملها ذات سلوك أومي صرف.",
            explanation = "في حالة التجاوب، تتعادل ردية الوشيعة Lω مع اتساعية المكثفة 1/(Cω)، فيكون فرق الصفحة φ = 0 وعامل الاستطاعة cos(φ) = 1، وتصرف الدارة استطاعة متوسطة أعظمية.",
            difficulty = "متقدم",
            yearOrSource = "دورة 2024 الرسمية"
        ),

        // --- الكيمياء (CHEMISTRY) ---
        QuestionEntity(
            subject = Subject.CHEMISTRY.name,
            unitOrTopic = "الكيمياء الحركية والتوازن الكيميائي",
            questionText = "حسب مبدأ لوشاتولييه، إذا كان التفاعل ماصاً للحرارة (ΔH > 0)، فإن رفع درجة الحرارة يؤدي إلى:",
            questionType = "MCQ",
            optionA = "إزاحة موضع التوازن بالاتجاه المباشر (نحو النواتج)",
            optionB = "إزاحة موضع التوازن بالاتجاه العكسي (نحو المتفاعلات)",
            optionC = "لا يؤثر على موضع التوازن إطلاقاً",
            optionD = "نقصان قيمة ثابت التوازن Kc",
            correctAnswer = "A",
            explanation = "عند رفع درجة الحرارة، يزيح التوازن نفسه في الاتجاه الذي يمتص الحرارة لتقليل أثر الزيادة، وهو الاتجاه المباشر الماص للحرارة في هذا التفاعل، مما يزيد من قيمة ثابت التوازن Kc.",
            difficulty = "متوسط",
            yearOrSource = "دورة 2023"
        ),
        QuestionEntity(
            subject = Subject.CHEMISTRY.name,
            unitOrTopic = "الحموض والأسس والمحاليل المائية",
            questionText = "محلول مائي لحمض كلور الماء HCl تركيزه 0.01 mol/L، احسب قيمة الـ pH للمحلول.",
            questionType = "MCQ",
            optionA = "pH = 2",
            optionB = "pH = 1",
            optionC = "pH = 12",
            optionD = "pH = 7",
            correctAnswer = "A",
            explanation = "HCl حمض قوي يتأين كلياً: [H3O+] = C_a = 0.01 = 10^(-2) mol/L. بالتالي pH = -log[H3O+] = -log(10^(-2)) = 2.",
            difficulty = "سهل",
            yearOrSource = "مسائل السلم"
        ),
        QuestionEntity(
            subject = Subject.CHEMISTRY.name,
            unitOrTopic = "الكيمياء العضوية: الحموض الكربوكسيلية والإسترات",
            questionText = "ما هو ناتج تفاعل حمض كربوكسيلي مع كحول بوجود حمض الكبريت المركز كوسيط؟",
            questionType = "ESSAY",
            optionA = "",
            optionB = "",
            optionC = "",
            optionD = "",
            correctAnswer = "إستر + ماء (تفاعل الأسترة العكوس: R-COOH + R'-OH ⇌ R-COO-R' + H2O)",
            explanation = "تفاعل الأسترة تفاعل عكوس ولا ينتهي، ويستخدم حمض الكبريت المركز كحفاز ومجفف لسحب الماء وإزاحة التوازن نحو تشكل الإستر العطري.",
            difficulty = "متوسط",
            yearOrSource = "تسميع شامل"
        ),

        // --- العلوم الطبيعية (SCIENCE) ---
        QuestionEntity(
            subject = Subject.SCIENCE.name,
            unitOrTopic = "الجهاز العصبي والسيالة العصبية",
            questionText = "ما هي القنوات الشاردية المسؤولة عن زوال الاستقطاب (Depolarization) في كمون العمل العصبي؟",
            questionType = "MCQ",
            optionA = "قنوات الصوديوم Na+ المبوبة بالفولطاج",
            optionB = "قنوات البوتاسيوم K+ المبوبة بالفولطاج",
            optionC = "مضخة الصوديوم والبوتاسيوم فقط",
            optionD = "قنوات الكلور Cl-",
            correctAnswer = "A",
            explanation = "عند وصول التنبيه إلى عتبة التنبيه، تنفتح قنوات الصوديوم الحساسة للفولطاج وتتدفق شوارد Na+ بسرعة إلى داخل الخلية مسببة زوال الاستقطاب وانعكاس شحنة الغشاء إلى +30mV.",
            difficulty = "متوسط",
            yearOrSource = "دورة 2024"
        ),
        QuestionEntity(
            subject = Subject.SCIENCE.name,
            unitOrTopic = "المستقبلات الحسية والعين",
            questionText = "أين تتركز الخلايا المخروطية المسؤولة عن الرؤية النهارية واللونية في شبكية العين؟",
            questionType = "MCQ",
            optionA = "في اللطخة الصفراء (الحفيرة المركزية)",
            optionB = "في النقطة العمياء",
            optionC = "في أطراف الشبكية المحيطية",
            optionD = "في المشيمية",
            correctAnswer = "A",
            explanation = "اللطخة الصفراء والحفيرة المركزية تحتويان على أعلى كثافة من المخاريط وتعد منطقة حدة الإبصار العظمى وتمييز الألوان بدقة.",
            difficulty = "سهل",
            yearOrSource = "رسم وتسميع"
        ),

        // --- اللغة العربية (ARABIC) ---
        QuestionEntity(
            subject = Subject.ARABIC.name,
            unitOrTopic = "قواعد النحو: المنصوبات وإعراب الجمل",
            questionText = "ما هو المحل الإعرابي لجملة (وهو يبتسم) في قولنا: «جاء الطالب وهو يبتسم»؟",
            questionType = "MCQ",
            optionA = "جملة اسمية في محل نصب حال",
            optionB = "جملة فعلية في محل رفع خبر",
            optionC = "جملة في محل نصب مفعول به",
            optionD = "لا محل لها من الإعراب صلة الموصول",
            correctAnswer = "A",
            explanation = "الواو هنا واو الحالية، والضمير المنفصل (هو) مبتدأ و(يبتسم) جملة خبرية، والجملة الاسمية بأكملها (وهو يبتسم) في محل نصب حال لصاحب الحال المعرفة (الطالب).",
            difficulty = "متوسط",
            yearOrSource = "إعراب قصائد المنهاج"
        ),

        // --- اللغة الإنكليزية (ENGLISH) ---
        QuestionEntity(
            subject = Subject.ENGLISH.name,
            unitOrTopic = "Grammar & Conditionals (If Clauses)",
            questionText = "Complete the sentence: If I ______ harder during the term, I would have passed the exam with distinction.",
            questionType = "MCQ",
            optionA = "had studied",
            optionB = "studied",
            optionC = "have studied",
            optionD = "study",
            correctAnswer = "A",
            explanation = "This is the Third Conditional (unreal past): If + Past Perfect (had studied) -> would have + Past Participle (would have passed).",
            difficulty = "متوسط",
            yearOrSource = "Official Baccalaureate Exam"
        ),

        // --- التربية الإسلامية (ISLAMIC) ---
        QuestionEntity(
            subject = Subject.ISLAMIC.name,
            unitOrTopic = "أصول الفقه ومقاصد الشريعة الإسلامية",
            questionText = "ما هي الكليات الخمس (الضروريات) التي اتفقت الشرائع السماوية على حفظها ورعايتها؟",
            questionType = "ESSAY",
            optionA = "",
            optionB = "",
            optionC = "",
            optionD = "",
            correctAnswer = "حفظ الدين، وحفظ النفس، وحفظ العقل، وحفظ النسل (أو العِرض)، وحفظ المال.",
            explanation = "الكليات الخمس هي أعلى مراتب مقاصد الشريعة الإسلامية والتي بدونها تختل مصالح البشر وتعم الفوضى والفساد في المعاش والمعاد.",
            difficulty = "سهل",
            yearOrSource = "سؤال اختباري شامل"
        )
    )
}
