package com.example.data

import com.example.data.models.DayTask
import com.example.data.models.MonthInfo
import com.example.data.models.Subject

object StudyPlanData {

    val months: List<MonthInfo> = listOf(
        MonthInfo(
            monthNumber = 1,
            name = "أيلول",
            title = "الشهر الأول (الأسابيع 1 إلى 4)",
            description = "إنهاء متتاليات الرياضيات، النواس المرن والفتل، النووية، التنسيق العصبي 1-5، عرس المجد والجرس، والوحدة 1 لغات والتربية الإسلامية.",
            startWeek = 1,
            endWeek = 4
        ),
        MonthInfo(
            monthNumber = 2,
            name = "تشرين الأول",
            title = "الشهر الثاني (الأسابيع 5 إلى 8)",
            description = "نهايات التوابع والاستمرار، النواس الثقيل والموائع، الغازات وسرعة التفاعل، الأعصاب والمستقبلات الحسية، نص وطني، والوحدة 2 لغات.",
            startWeek = 5,
            endWeek = 8
        ),
        MonthInfo(
            monthNumber = 3,
            name = "تشرين الثاني",
            title = "الشهر الثالث (الأسابيع 9 إلى 12)",
            description = "الاشتقاق وتطبيقاته وبداية العقدية، النسبية والمغناطيسية، التوازن الكيميائي والحموض، تكاثر الفيروسات والجراثيم، نص المهاجر، والوحدة 3 لغات.",
            startWeek = 9,
            endWeek = 12
        ),
        MonthInfo(
            monthNumber = 4,
            name = "كانون الأول",
            title = "الشهر الرابع (الأسابيع 13 إلى 16)",
            description = "العقدية في الهندسة والأشعة بالفراغ، قوة لابلاس ولورنتز، الحموض والأسس، الاستنساخ وعاريات البذور، نص الغاب، والوحدة 4 لغات.",
            startWeek = 13,
            endWeek = 16
        ),
        MonthInfo(
            monthNumber = 5,
            name = "كانون الثاني",
            title = "الشهر الخامس (الأسابيع 17 إلى 20)",
            description = "الجداء السلمي والمستويات بالفراغ، التحريض الذاتي والدارات المهتزة والمتناوب، محاليل الأملاح، مغلفات البذور والتكاثر البشري، ورواية دمشق يا بسمة الحزن.",
            startWeek = 17,
            endWeek = 20
        ),
        MonthInfo(
            monthNumber = 6,
            name = "شباط",
            title = "الشهر السادس (الأسابيع 21 إلى 24)",
            description = "نهاية متتالية والتكامل والتوافيقي، الأمواج المستقرة والمزامير، المعايرة والكيمياء العضوية، الدورة الجنسية والوراثة، نص الوطن، والوحدة 6 لغات.",
            startWeek = 21,
            endWeek = 24
        ),
        MonthInfo(
            monthNumber = 7,
            name = "آذار",
            title = "الشهر السابع (الأسابيع 25 إلى 28)",
            description = "الاحتمالات والمعادلات التفاضلية، الفيزياء الحديثة والفلكية، الإسترات والأميدات والأمينات، الوراثة البشرية والطفرات، ختام المناهج.",
            startWeek = 25,
            endWeek = 28
        ),
        MonthInfo(
            monthNumber = 8,
            name = "نيسان",
            title = "الشهر الثامن (الأسابيع 29 إلى 32)",
            description = "المرحلة الذهبية لحل أسئلة الدورات الرسمية السابقة (2026، 2025، 2024، 2022، 2021) تحت ضغط المؤقت وترميم الثغرات.",
            startWeek = 29,
            endWeek = 32
        ),
        MonthInfo(
            monthNumber = 9,
            name = "أيار",
            title = "الشهر التاسع (الأسابيع 33 إلى 36)",
            description = "حل النماذج الاسترشادية الشاملة ونماذج الحسم الفولاذية لجميع المواد لضبط السرعة الفائقة والاستعداد النهائي للامتحان.",
            startWeek = 33,
            endWeek = 36
        )
    )

    val allTasks: List<DayTask> by lazy {
        buildAllTasks()
    }

    private fun buildAllTasks(): List<DayTask> {
        val list = mutableListOf<DayTask>()

        // ======================== MONTH 1 (أيلول) ========================
        // Week 1
        list.add(DayTask(
            id = "m1_w1_d1", monthNumber = 1, weekNumber = 1, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: عموميات المتتاليات: طرق تعريف المتتالية الحسابية والهندسية، وإيجاد الحدود ص 7-8.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: بداية نص (عرس المجد) للشاعر عمر أبو ريشة: قراءة النص ومدخل القصيدة وفهم مفردات المقطع الأول ص 7.",
            period3Content = "تصفح طرائق حساب حدود المتتاليات وأفكار المقطع الأول لعرس المجد."
        ))
        list.add(DayTask(
            id = "m1_w1_d2", monthNumber = 1, weekNumber = 1, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس المرن حركياً وديناميكياً وربطه بالحركة الدائرية ص 30-31.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: بداية الوحدة الأولى (Life Choices): قراءة وفهم نص القراءة الرئيسي (درس مفيد) وحفظ مفرداته ص 4-5.",
            period3Content = "مراجعة قوانين النواس المرن وحفظ مفردات الإنكليزي الجديدة يدوياً."
        ))
        list.add(DayTask(
            id = "m1_w1_d3", monthNumber = 1, weekNumber = 1, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: استنتاج القانون العام للحدود، حساب الأساس، وقوانين مجموع الحدود ص 8-9.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: الكيمياء النووية: دراسة مكونات النواة، طاقة الارتباط ونقص الكتلة، وحزام الاستقرار ص 6-7.",
            period3Content = "تصفح قوانين المتتالية الحسابية وقوانين طاقة الارتباط النووي."
        ))
        list.add(DayTask(
            id = "m1_w1_d4", monthNumber = 1, weekNumber = 1, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: التنسيق العصبي: دراسة أقسام الجهاز العصبي المركزي، والوجه الظهري لدماغ خروف ص 9-11.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: نص (عرس المجد): دراسة المقطع الأول فكرياً وبلاغياً وصياغة أفكاره وقواعد النعت والمنعوت ص 7.",
            period3Content = "تصفح أقسام وجه الدماغ الظهري، وإعراب كلمات المقطع الأول لعرس المجد."
        ))
        list.add(DayTask(
            id = "m1_w1_d5", monthNumber = 1, weekNumber = 1, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: المتتالية الهندسية: استنتاج القانون العام للحدود، حساب الأساس، وقوانين مجموع الحدود ص 9-11.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: بداية الوحدة الأولى (نحتفل معاً): قراءة نصوص المهرجانات ص 10 وحفظ مفردات صندوق الأدوات ص 11.",
            period3Content = "تصفح قوانين المتتالية الهندسية ومفردات الفرنسي وكتابتها على مسودة."
        ))
        list.add(DayTask(
            id = "m1_w1_d6", monthNumber = 1, weekNumber = 1, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس المرن: دراسة الاستطالة السكونية، واستنتاج تابع المطال الزمني ودراسة مواضع انعدام وقيم المطال ص 31-32.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: الدرس الأول تلاوة: دراسة نعم الله تعالى، وحفظ أحكام النون الساكنة والتنوين (الإظهار والإدغام) ص 9-12.",
            period3Content = "تصفح خطوات استنتاج المطال للنواس المرن، وتسميع أحكام التجويد كتابة."
        ))
        list.add(DayTask(
            id = "m1_w1_d7", monthNumber = 1, weekNumber = 1, dayOfWeek = "الجمعة",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: يوم التمكين الأسبوعي: حل تمرينات المتتاليات الحسابية والهندسية ومسائل التثبيت ص 15 يدوياً بالكامل.",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: التنسيق العصبي: دراسة الوجه البطني للدماغ والمقاطع الداخلية وبطينات الدماغ وقناة سيليفيوس ص 12-14.",
            period3Content = "تصفية التراكم ومراجعة نقاط الضعف."
        ))

        // Week 2
        list.add(DayTask(
            id = "m1_w2_d1", monthNumber = 1, weekNumber = 2, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: البرهان بالتدريج (الاستقراء الرياضي): دراسة خطوات الإثبات بالتدريج وحل أمثلة إثبات القضايا ص 12-13.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: نص (عرس المجد): دراسة المقطع الثاني فكرياً وبلاغياً وفنياً وصياغة أفكاره الفرعية وشرح الكلمات ص 7-8.",
            period3Content = "تصفح خطوات البرهان بالتدريج وأفكار المقطع الثاني لعرس المجد."
        ))
        list.add(DayTask(
            id = "m1_w2_d2", monthNumber = 1, weekNumber = 2, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس المرن: دراسة تابع السرعة وتابع التسارع، واستنتاج القيم العظمى والانعدام في الحركة التوافقية ص 8-10.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: الوحدة الأولى (Life Choices): دراسة وتثبيت قواعد الأزمنة الأولى (الحاضر والماضي البسيط والمستمر) ص 10-11.",
            period3Content = "مراجعة قوانين السرعة والتسارع للنواس وقواعد أزمنة الإنكليزي."
        ))
        list.add(DayTask(
            id = "m1_w2_d3", monthNumber = 1, weekNumber = 2, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: البرهان بالتدريج: دراسة المجموع وقضايا المتراجحات وإثبات صحة المساواة وحل تطبيقاتها ص 13-14.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: الكيمياء النووية: دراسة تفاعلات وسلاسل النشاط الإشعاعي (ألفا، بيتا، بوزيترون، أسر إلكتروني) ص 8-10.",
            period3Content = "كتابة خطوات حل متراجحات التدريج ومعادلات سلاسل النشاط النووي يدوياً."
        ))
        list.add(DayTask(
            id = "m1_w2_d4", monthNumber = 1, weekNumber = 2, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: التنسيق العصبي: دراسة النخاع الشوكي تشريحياً وبنية المادة الرمادية والبيضاء والمقاطع العرضية والأغشية الحامية ص 16-19.",
            period2Subject = Subject.ARABIC,
            period2Content = "تمكين اللغة: نص (عرس المجد): دراسة المقطع الثالث وتطبيق قواعد النحو والصرف والإعراب الكامل ص 8-9.",
            period3Content = "تصفح مخطط بنية النخاع الشوكي وإعراب كلمات المقطع الثالث لعرس المجد."
        ))
        list.add(DayTask(
            id = "m1_w2_d5", monthNumber = 1, weekNumber = 2, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: حل تمرينات ومسائل المتتاليات والبرهان بالتدريج في كتاب التحليل ص 15 يدوياً بالكامل.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الأولى (On fête ensemble): دراسة قاعدة التعبير عن الهدف بالتفصيل لنفس الفاعل ولفاعلين مختلفين ص 12-13.",
            period3Content = "مراجعة أخطاء تمرينات المتتاليات وتثبيت أدوات الهدف + اختبار 1 رياضيات المتتاليات والتدريج."
        ))
        list.add(DayTask(
            id = "m1_w2_d6", monthNumber = 1, weekNumber = 2, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس المرن: دراسة الطاقة الحركية والكامنة الميكانيكية واستنتاج علاقتها وثبات الطاقة الكلية وتحولاتها ص 10-12.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: حديث (بيعة صادقة): حفظ الحديث النبوي الشريف غيباً وفهم مفرداته وشرح بنوده الستة ص 51-53.",
            period3Content = "كتابة استنتاج الطاقة للنواس المرن غيباً وتسميع حديث بيعة صادقة كتابة."
        ))
        list.add(DayTask(
            id = "m1_w2_d7", monthNumber = 1, weekNumber = 2, dayOfWeek = "الجمعة",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: يوم الحسم لفيزياء النواسات: حل تمرينات وأنشطة النواس المرن والبدء بمسائل الدرس والمسائل العامة ص 13-14 وص 256.",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: النسيج العصبي: دراسة مكونات النسيج العصبي وبنية العصبونات وأنواع خلايا الدبق العصبي ووظائفها الهامة ص 20-22.",
            period3Content = "تصفية التراكم وتثبيت النواسات والنسيج العصبي."
        ))

        // Week 3
        list.add(DayTask(
            id = "m1_w3_d1", monthNumber = 1, weekNumber = 3, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: بداية وحدة النهايات والاستمرار: مفهوم نهاية تابع عند اللانهاية هندسياً وجبرياً وحساب نهايات التوابع المسيطرة ص 21-22.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: بداية نص (الجرس) للشاعر محمود درويش: قراءة النص ومدخله وفهم معاني المقطع الأول وأسلوب السرد ص 12.",
            period3Content = "تصفح نهايات التوابع عند اللانهاية ورموز نص الجرس + اختبار 2 رياضيات المتتاليات والتدريج."
        ))
        list.add(DayTask(
            id = "m1_w3_d2", monthNumber = 1, weekNumber = 3, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: بداية وحدة نواس الفتل غير المتخامد: دراسة الحركة الدورانية، عزم مزدوجة الفتل وعزم الإرجاع واشتقاق المعادلة التفاضلية ص 16-17.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: تتمة الوحدة الأولى (Life Choices): كتابة موضوع التجربة الشخصية (Personal Experience) وحل تدريبات الأنشطة ص 7-8.",
            period3Content = "كتابة معادلة حركة نواس الفتل غيباً وصياغة موضوع الإنكليزي يدوياً + اختبار 1 إنكليزي الوحدة الأولى."
        ))
        list.add(DayTask(
            id = "m1_w3_d3", monthNumber = 1, weekNumber = 3, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: نهايات التوابع الكسرية والجذرية عند اللانهاية ودراسة المقاربات الأفقية والشاقولية بالتفصيل ص 23-24.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: تتمة الكيمياء النووية: تفاعلات الاندماج والانشطار والاصطناعي وتطبيقات النظائر المشعة في الطب والتأريخ ص 11-14.",
            period3Content = "حل نهايات التوابع الكسرية يدوياً وكتابة تفاعلات الانشطار والاندماج."
        ))
        list.add(DayTask(
            id = "m1_w3_d4", monthNumber = 1, weekNumber = 3, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: الظواهر الكهربائية في الخلايا: كمون الراحة وعوامل نشوئه وقنوات التسرب الشاردية ومضخة الصوديوم والبوتاسيوم ص 26-28.",
            period2Subject = Subject.ARABIC,
            period2Content = "تمكين اللغة: نص (الجرس): دراسة المقطع الثاني وتطبيق مبحث الحال بالكامل وإعراب الكلمات والجمل المستهدفة ص 13-14.",
            period3Content = "رسم مخطط توزع شوارد الصوديوم والبوتاسيوم وتطبيق إعراب الحال."
        ))
        list.add(DayTask(
            id = "m1_w3_d5", monthNumber = 1, weekNumber = 3, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: يوم التمكين والتدريب: حل تمارين وتطبيقات حساب النهايات للمقادير الكسرية والجذرية عند اللانهاية وتعيين المقاربات ص 30-31.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الأولى (On fête ensemble): حل تطبيقات التعبير عن الهدف في دفتر الأنشطة ص 13 وصياغة موضوع الوحدة.",
            period3Content = "مراجعة أخطاء تمارين النهايات وتثبيت مواضع المصدر والنصب في الفرنسي + اختبار 2 إنكليزي الوحدة الأولى."
        ))
        list.add(DayTask(
            id = "m1_w3_d6", monthNumber = 1, weekNumber = 3, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: نواس الفتل: استنتاج التابع الزمني للمطال الزاوي والدور الخاص انطلاقاً من الدور التجريبي، ودراسة السرعة والتسارع ص 18-19.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: الوحدة الأولى: درس (بناء الحضارة في الإسلام): فهم أسس ومقومات عمارة الأرض ودور العلم والعمل ص 79-81.",
            period3Content = "استنتاج دور نواس الفتل يدوياً وتلخيص مقومات الحضارة كتابة."
        ))
        list.add(DayTask(
            id = "m1_w3_d7", monthNumber = 1, weekNumber = 3, dayOfWeek = "الجمعة",
            period1Subject = Subject.CHEMISTRY,
            period1Content = "الكيمياء: حسم الكيمياء النووية: حل جميع مسائل درس النووية ومسائل البحث والمسائل العامة في نهاية الكتاب ص 15-18 (إغلاق الوحدة).",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: كمون العمل: دراسة زوال الاستقطاب وعودته، الشوكة الكمونية، وقنوات التبويب الفولطية للصوديوم والبوتاسيوم ص 29-31.",
            period3Content = "اختبار 1 الكيمياء النووية + تصفية التراكم."
        ))

        // Week 4
        list.add(DayTask(
            id = "m1_w4_d1", monthNumber = 1, weekNumber = 4, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: نهايات التوابع عند عدد حقيقي: دراسة سلوك التابع عند قيمة معينة وحالات عدم التعيين من النمط صفر على صفر ص 25-26.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: قصيدة (الجرس) للشاعر محمود درويش: دراسة المقطعين الثالث والرابع فكرياً وبلاغياً وصياغة الأفكار الفرعية ص 14-15.",
            period3Content = "تصفح حالات عدم التعيين عند قيمة حقيقية ورموز المقطعين الأخيرين + اختبار 3 رياضيات المتتاليات والتدريج."
        ))
        list.add(DayTask(
            id = "m1_w4_d2", monthNumber = 1, weekNumber = 4, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: نواس الفتل: دراسة الطاقة الحركية والكامنة الميكانيكية وتحولاتها واستنتاج علاقة السرعة الزاوية العظمى ص 19-20.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: بداية الوحدة الثانية (Success): قراءة النص الرئيسي (Stop Wishing Start Doing) وحفظ مصطلحات النجاح ص 17-18.",
            period3Content = "تصفح علاقات طاقة نواس الفتل ومصطلحات وحدة النجاح بالإنكليزية."
        ))
        list.add(DayTask(
            id = "m1_w4_d3", monthNumber = 1, weekNumber = 4, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: العمليات على النهايات: مبرهنات عمليات النهايات (الجمع، الضرب، القسمة) وحالات عدم التعيين الأربع والبدء بإزالتها ص 29-30.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: بداية وحدة الغازات: دراسة المفاهيم والخصائص الفيزيائية العامة، وقوانين بويل وشارل وغاي لوساك ص 21-24.",
            period3Content = "كتابة مبرهنات عمليات النهايات وقوانين الغازات الثلاثة على مسودة + اختبار 2 الكيمياء النووية."
        ))
        list.add(DayTask(
            id = "m1_w4_d4", monthNumber = 1, weekNumber = 4, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: انتقال السيالة العصبية: آلية انتشار كمون العمل في الألياف العصبية المجردة من النخاعين والمغمدة وقانون الكل أو لا شيء ص 32-34.",
            period2Subject = Subject.ARABIC,
            period2Content = "تمكين اللغة: قصيدة (الجرس): مراجعة وإعراب القصيدة بالكامل فكرياً ونحوياً وبلاغياً وحل تطبيقاتها وأسئلتها ص 15-17.",
            period3Content = "تصفح آلية الانتشار القفزي للسيالة وإعراب الجرس + اختبار 1 عربي الوحدة الأولى."
        ))
        list.add(DayTask(
            id = "m1_w4_d5", monthNumber = 1, weekNumber = 4, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: يوم التمكين والتدريب: حل جميع تمرينات نهايات التوابع عند اللانهاية والعدد الحقيقي وتعيين المقاربات الشاقولية ص 30-31.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الأولى: حل تدريبات التقييم الذاتي والاختبارات الشاملة في دفتر الأنشطة ص 18-19 (إغلاق الوحدة 1).",
            period3Content = "مراجعة أخطاء تمارين النهايات وقواعد الفرنسي + اختبار 3 إنكليزي الوحدة 1 + اختبار 1 فرنسي الوحدة 1."
        ))
        list.add(DayTask(
            id = "m1_w4_d6", monthNumber = 1, weekNumber = 4, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: يوم الحسم لنواس الفتل: حل جميع أنشطة وتمرينات ومسائل درس نواس الفتل والمسائل العامة ص 22-23 (إغلاق الفتل).",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: بداية الوحدة الأولى (تفسير واستحفاظ): درس صيانة الحقوق وتوثيق العقود (دراسة الآية 282 سورة البقرة: الجزء الأول) ص 35-37.",
            period3Content = "مراجعة قوانين نواس الفتل وحفظ معاني مفردات آية الدين الأولى."
        ))
        list.add(DayTask(
            id = "m1_w4_d7", monthNumber = 1, weekNumber = 4, dayOfWeek = "الجمعة",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: نهاية التنسيق العصبي الأول: دراسة بنية المشابك العصبية وآلية النقل المشبكي الكيميائي والنواقل العصبية ص 35-36.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: يوم التمكين للغازات: حل تطبيقات قوانين الغازات الأساسية (بويل، شارل، غاي لوساك) ومسائل التثبيت المباشرة ص 25.",
            period3Content = "تصفية التراكم + اختبار 2 عربي الوحدة الأولى (قضايا وطنية وقومية)."
        ))

        // ======================== MONTH 2 (تشرين الأول - الأسابيع 5 إلى 8) ========================
        // Week 5 (Month 2 Week 1)
        list.add(DayTask(
            id = "m2_w5_d1", monthNumber = 2, weekNumber = 5, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: نهايات التوابع: دراسة مبرهنات المقارنة والإحاطة ونهاية التوابع المثلثية ص 33-34.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: قواعد اللغة: دراسة مبحث الأحرف الزائدة وتأثيرها الإعرابي ص 97.",
            period3Content = "تصفح مبرهنات المقارنة وتطبيق إعراب الأحرف الزائدة + اختبار 2 فرنسي الوحدة الأولى."
        ))
        list.add(DayTask(
            id = "m2_w5_d2", monthNumber = 2, weekNumber = 5, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس الثقيل غير المتخامد المركب: تحديد القوى المؤثرة واستنتاج عزم قوة الثقل وعزم العطالة ص 28-29.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: الوحدة الثانية: قراءة نص النجاح وحفظ مصطلحات الإنجاز والتغلب على الفشل ص 17.",
            period3Content = "إعادة استنتاج عزم القوة للنواس وحفظ كلمات الإنكليزي كتابة."
        ))
        list.add(DayTask(
            id = "m2_w5_d3", monthNumber = 2, weekNumber = 5, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: نهايات التوابع: دراسة نهاية تابع مركب وتغيير المتحول ص 37-38.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: الغازات: دراسة قانون أفوغادرو وقانون الغازات العام وحساب الكثافة ص 25-26.",
            period3Content = "حل تطبيقات نهاية التابع المركب ومسائل قانون الغازات العام + اختبار 3 كيمياء النووية."
        ))
        list.add(DayTask(
            id = "m2_w5_d4", monthNumber = 2, weekNumber = 5, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: التنسيق العصبي: دراسة بنية العصب المحيطي وغمد شوان وغمد النخاعين ص 24-25.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الثانية: قراءة نصوص التضامن والعمل الإنساني وحفظ مفرداتها ص 25-27.",
            period3Content = "رسم مقطع العصب وتسمية بنيته وتسميع كلمات الفرنسي كتابة."
        ))
        list.add(DayTask(
            id = "m2_w5_d5", monthNumber = 2, weekNumber = 5, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: يوم التمكين والتدريب: حل جميع تمرينات مبرهنات الإحاطة وتغيير المتحول في نهاية التوابع ص 44.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الثانية: دراسة قاعدة التعبير عن التعارض والتنازل وأدواتها ص 28-29.",
            period3Content = "مراجعة أخطاء تمارين نهايات الإحاطة وصياغة جمل فرنسية عن التعارض."
        ))
        list.add(DayTask(
            id = "m2_w5_d6", monthNumber = 2, weekNumber = 5, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس الثقيل المركب: دراسة الحركة الدورانية واستنتاج المعادلة التفاضلية للحركة ص 29-30.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: صيانة الحقوق وتوثيق العقود ص 37-38 (حفظ آية الدين كتابة وتثبيت معاني كلماتها).",
            period3Content = "كتابة المعادلة التفاضلية للنواس وتسميع آية الشهادة كتابة."
        ))
        list.add(DayTask(
            id = "m2_w5_d7", monthNumber = 2, weekNumber = 5, dayOfWeek = "الجمعة",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: يوم حسم النواسات والغازات: حل تمرينات النواس الثقيل ص 35، وتمرينات الغازات ص 27 لإنهائها تماماً.",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: التنسيق العصبي: دراسة الجهاز العصبي المحيطي والذاتي الودي وقرب الودي ص 26-28.",
            period3Content = "اختبار 1 كيمياء الغازات + اختبار 3 عربي الوحدة 1 + تصفية التراكم."
        ))

        // Week 6 (Month 2 Week 2)
        list.add(DayTask(
            id = "m2_w6_d1", monthNumber = 2, weekNumber = 6, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: المقارب المائل، تعريفه، إثبات وجوده ودراسة الوضع النسبي للخط البياني ص 40-41.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: البدء بالوحدة الثانية (الغربة والاغتراب): دراسة نص وطني للشاعر جورج صيدح، المقطع الأول ص 22.",
            period3Content = "تصفح شروط المقارب المائل وأفكار المقطع الأول لنص وطني + اختبار 3 فرنسي الوحدة 1."
        ))
        list.add(DayTask(
            id = "m2_w6_d2", monthNumber = 2, weekNumber = 6, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس الثقيل المركب: استنتاج قانون الدور الخاص في السعات الزاوية الصغيرة ص 30-31.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: البدء بالوحدة الثالثة (الطب): قراءة نص تاريخ الطب وحفظ المصطلحات الطبية ص 26-28.",
            period3Content = "مراجعة استنتاج دور النواس الثقيل وحفظ كلمات الإنكليزي الطبية يدوياً."
        ))
        list.add(DayTask(
            id = "m2_w6_d3", monthNumber = 2, weekNumber = 6, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: الاستمرار: دراسة استمرار تابع عند نقطة وعلى مجال وقواعد استمرار التوابع المألوفة ص 42-43.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: البدء بوحدة سرعة التفاعل الكيميائي: دراسة السرعة الوسطية واللحظية ص 44-46.",
            period3Content = "حل تطبيقات الاستمرار وحساب سرعة التفاعل + اختبار 2 كيمياء الغازات."
        ))
        list.add(DayTask(
            id = "m2_w6_d4", monthNumber = 2, weekNumber = 6, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: دراسة خواص الأعصاب وقابلية التنبه وقوانين عتبة التنبيه والريوباز والكروناكسي ص 28-32.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الثانية (التضامن والعمل الإنساني): متابعة نصوص الأنشطة والتدريبات ص 30-32.",
            period3Content = "رسم بياني لمنحنى عتبات التنبيه وحفظ كلمات الفرنسي كتابة على مسودة."
        ))
        list.add(DayTask(
            id = "m2_w6_d5", monthNumber = 2, weekNumber = 6, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: حل جميع تمرينات ومسائل المقارب المائل واستمرار التوابع في كتاب التحليل ص 57.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: مراجعة وحفظ مفردات التضامن وكتابة موضوع التعبير الكتابي الخاص بالوحدة ص 33.",
            period3Content = "مراجعة أخطاء حل مسائل الاستمرار والمقارب المائل وتثبيت موضوع الفرنسي."
        ))
        list.add(DayTask(
            id = "m2_w6_d6", monthNumber = 2, weekNumber = 6, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: النواس الثقيل البسيط: استنتاج علاقة الدور الخاص، وعلاقة السرعة وتوتر الخيط عند المرور بوضع التوازن ص 32-33.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: تلاوة: الله وحده هو الخالق المتصرف، تفسير الآيات والأحكام التجويدية ص 13-15.",
            period3Content = "كتابة استنتاج السرعة وتوتر الخيط للنواس البسيط غيباً وحفظ معاني آيات التلاوة."
        ))
        list.add(DayTask(
            id = "m2_w6_d7", monthNumber = 2, weekNumber = 6, dayOfWeek = "الجمعة",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: يوم حسم النواسات والسرعة: حل جميع تمرينات وأنشطة النواس الثقيل المركب والبسيط ومسائل الدرس ص 35.",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: دراسة زمن الاستنفاد والزمن المفيد وتأثير الحرارة والمخدر على تنبيه العصب ص 32-33.",
            period3Content = "اختبار 1 فيزياء النواسات + اختبار 3 عربي + اختبار 1 علوم التنسيق + تصفية التراكم."
        ))

        // Week 7 (Month 2 Week 3)
        list.add(DayTask(
            id = "m2_w7_d1", monthNumber = 2, weekNumber = 7, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: التوابع المستمرة وحل المعادلات باستخدام مبرهنة القيمة الوسطى ص 45-47.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: نص وطني للشاعر جورج صيدح: دراسة وتثبيت المقطع الثاني فكرياً وبلاغياً وشرح المفردات ص 22-23.",
            period3Content = "تصفح شروط مبرهنة القيمة الوسطى وإعراب المقطع الثاني لنص وطني."
        ))
        list.add(DayTask(
            id = "m2_w7_d2", monthNumber = 2, weekNumber = 7, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: البدء بميكانيك الموائع: دراسة خصائص السائل المثالي، خطوط الانسياب في الجريان المنتظم ص 42-43.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: الوحدة الثالثة: قراءة وفهم نص تاريخ الطب وحفظ المفردات والمصطلحات الطبية ص 26-28.",
            period3Content = "مراجعة صفات السائل المثالي وقراءة نص الطب باللغة الإنكليزية."
        ))
        list.add(DayTask(
            id = "m2_w7_d3", monthNumber = 2, weekNumber = 7, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: حل وتطبيق الأنشطة وتمرينات ومسائل الوحدة الثانية كاملة لتثبيت مهارات الاستمرار ص 54-57.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: حركية التفاعلات: دراسة طاقة التنشيط، المعقد النشط، والعوامل المؤثرة في سرعة التفاعل ص 46-48.",
            period3Content = "حل مسائل الاستمرار + رسم بياني لطاقة التنشيط + اختبار 2 فيزياء النواسات + اختبار 3 كيمياء الغازات + اختبار 2 علوم."
        ))
        list.add(DayTask(
            id = "m2_w7_d4", monthNumber = 2, weekNumber = 7, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: الفعل المنعكس والمنعكس الداغصي: دراسة القوس الانعكاسية الشوكية وعناصرها ومراحل حدوث الفعل المنعكس ص 56-60.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: الوحدة الثانية: دراسة قاعدة التعبير عن التعارض والتنازل وأدواتها القواعدية ص 28-29.",
            period3Content = "رسم مخطط القوس الانعكاسية الشوكية وتثبيت أدوات التعارض بالفرنسية."
        ))
        list.add(DayTask(
            id = "m2_w7_d5", monthNumber = 2, weekNumber = 7, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: يوم التمكين والإنهاء: حل نماذج امتحانية شاملة وأسئلة دورات سابقة للوحدة الثانية (إغلاق الاستمرار والنهايات).",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: الوحدة الثالثة: دراسة قواعد المبني للمجهول بالكامل وتصريفاتها مع الأزمنة المختلفة ص 28-29.",
            period3Content = "تصحيح أخطاء نماذج النهايات وحل تدريبات المبني للمجهول + اختبار 1 رياضيات النهايات والاستمرار."
        ))
        list.add(DayTask(
            id = "m2_w7_d6", monthNumber = 2, weekNumber = 7, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: ميكانيك الموائع: دراسة معدل التدفق الحجمي والكتلي واستنتاج علاقة معادلة الاستمرارية رياضياً وتطبيقاتها ص 44-45.",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: درس مقومات الحضارة الإنسانية في الإسلام: دراسة دور العقيدة والعبادة والأخلاق والعلم ص 79-81.",
            period3Content = "كتابة استنتاج معادلة الاستمرارية يدوياً وتلخيص مقومات الحضارة كتابة."
        ))
        list.add(DayTask(
            id = "m2_w7_d7", monthNumber = 2, weekNumber = 7, dayOfWeek = "الجمعة",
            period1Subject = Subject.CHEMISTRY,
            period1Content = "الكيمياء: سرعة التفاعل: دراسة قوانين رتب التفاعل وحساب ثابت سرعة التفاعل وتحديد واحدة قياسه ص 49-51.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: متابعة نصوص التعبير الشفهي والكتابي وحفظ مفردات صندوق الأدوات بالكامل ص 25-27.",
            period3Content = "تصفية التراكم ومراجعة شاملة لدروس الأسبوع."
        ))

        // Week 8 (Month 2 Week 4)
        list.add(DayTask(
            id = "m2_w8_d1", monthNumber = 2, weekNumber = 8, dayOfWeek = "السبت",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: بداية وحدة الأعداد العقدية: مجموعة الأعداد العقدية، شكلها الجبري، وتحديد القسم الحقيقي والخيالي ص 90.",
            period2Subject = Subject.ARABIC,
            period2Content = "اللغة العربية: نص وطني للشاعر جورج صيدح: دراسة وتثبيت المقطع الثالث فكرياً وبلاغياً وشرح المفردات ص 23.",
            period3Content = "تصفح التمثيل الديكارتي للأعداد العقدية وأفكار المقطع الثالث لنص وطني + اختبار 2 رياضيات النهايات والاستمرار."
        ))
        list.add(DayTask(
            id = "m2_w8_d2", monthNumber = 2, weekNumber = 8, dayOfWeek = "الأحد",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: ميكانيك الموائع: دراسة معادلة برنولي للجريان المستقر واستنتاج علاقتها وحركية السوائل ص 45-47.",
            period2Subject = Subject.ENGLISH,
            period2Content = "اللغة الإنكليزية: الوحدة الثالثة: صياغة وكتابة موضوع الرعاية الصحية في سوريا وتثبيت صياغة المقالات ص 24-25.",
            period3Content = "مراجعة استنتاج معادلة برنولي وحفظ جمل موضوع الإنكليزي كتابة."
        ))
        list.add(DayTask(
            id = "m2_w8_d3", monthNumber = 2, weekNumber = 8, dayOfWeek = "الاثنين",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: الأعداد العقدية: العمليات في مجموعة الأعداد العقدية (الجمع، الطرح، الضرب، وقوى العدد التخيلي i) ص 91-92.",
            period2Subject = Subject.CHEMISTRY,
            period2Content = "الكيمياء: حركية التفاعلات: دراسة التفاعلات الأولية وغير الأولية وقواعد تحديد رتبة التفاعل الكلية وثابت السرعة ص 48-51.",
            period3Content = "حل تمارين قوى i ورتبة التفاعل + اختبار 3 فيزياء النواسات + اختبار 3 علوم التنسيق العصبي."
        ))
        list.add(DayTask(
            id = "m2_w8_d4", monthNumber = 2, weekNumber = 8, dayOfWeek = "الثلاثاء",
            period1Subject = Subject.SCIENCE,
            period1Content = "العلوم: دراسة بعض أمراض الجهاز العصبي (التهاب السحايا) وبداية مفهوم المستقبلات الحسية ومستقبلات الجلد ص 63-70.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: صياغة موضوع التعبير الكتابي الخاص بالوحدة الثانية في التضامن وحفظ صندوق أدوات التعبير ص 33.",
            period3Content = "رسم مخطط مستقبلات الجلد وتسمية بنيتها وتثبيت موضوع الفرنسي."
        ))
        list.add(DayTask(
            id = "m2_w8_d5", monthNumber = 2, weekNumber = 8, dayOfWeek = "الأربعاء",
            period1Subject = Subject.MATH,
            period1Content = "الرياضيات: حل تمارين ومسائل صفحة 92 في كتاب الأعداد العقدية، وحل أنشطة وقوى i يدوياً.",
            period2Subject = Subject.FRENCH,
            period2Content = "اللغة الفرنسية: حل تدريبات وأنشطة التقويم الذاتي والاختبارات الشاملة للوحدة الثانية في دفتر الأنشطة ص 36-39 (إغلاق الوحدة 2).",
            period3Content = "مراجعة أخطاء حل مسائل الأعداد العقدية وتثبيت قواعد الفرنسي + اختبار 1 فرنسي الوحدة الثانية."
        ))
        list.add(DayTask(
            id = "m2_w8_d6", monthNumber = 2, weekNumber = 8, dayOfWeek = "الخميس",
            period1Subject = Subject.PHYSICS,
            period1Content = "الفيزياء: ميكانيك الموائع: حل جميع مسائل درس ميكانيك الموائع والمسائل العامة في نهاية الكتاب ص 48-49 (إغلاق الموائع).",
            period2Subject = Subject.ISLAMIC,
            period2Content = "التربية الإسلامية: صيانة الحقوق وتوثيق العقود: دراسة الجزء الثاني من الآية 282 (أحكام الشهادة، الرهن، مسؤولية الكاتب) ص 37-39.",
            period3Content = "كتابة قوانين الموائع غيباً + اختبار 1 فيزياء ميكانيك الموائع + اختبار 1 إسلامية القرآن الكريم والتفسير."
        ))
        list.add(DayTask(
            id = "m2_w8_d7", monthNumber = 2, weekNumber = 8, dayOfWeek = "الجمعة",
            period1Subject = Subject.CHEMISTRY,
            period1Content = "الكيمياء: يوم حسم سرعة التفاعل: حل جميع تمرينات ومسائل درس سرعة التفاعل الكيميائي ص 52-54 لإنهائها تماماً.",
            period2Subject = Subject.SCIENCE,
            period2Content = "العلوم: دراسة المستقبلات الكيميائية للشم والذوق وحل تدريبات الدرس ص 73-75.",
            period3Content = "تصفية التراكم وتثبيت منهاج الشهر الثاني."
        ))

        // Populate Month 3 to Month 9 with standard curriculum breakdown
        generateRemainingMonths(list)

        return list
    }

    private fun generateRemainingMonths(list: MutableList<DayTask>) {
        val days = listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")

        // Month 3: Weeks 9 to 12
        val m3Data = listOf(
            // Week 9
            listOf(
                Triple("الرياضيات: الاشتقاق ومعدل التغير والمماس والتقريب التآلفي ص 67-70", "اللغة العربية: قواعد الصرف: الإعلال بالقلب والتسكين ص 97-98", "اختبار 3 نهايات واختبار 2 فرنسي 2 وتثبيت الاشتقاق"),
                Triple("الفيزياء: ميكانيك الموائع: مراجعة وحل جميع مسائل الدرس ص 48", "اللغة الإنكليزية: الوحدة الرابعة: الهندسة الطبية ص 37-38", "اختبار 2 فيزياء موائع واختبار 2 إسلامية وحفظ مفردات"),
                Triple("الرياضيات: قواعد اشتقاق التوابع المألوفة ص 71-73", "الكيمياء: بداية التوازن الكيميائي والتفاعلات الانعكاسية ص 62", "اختبار 2 كيمياء سرعة التفاعل واشتقاق توابع يدوياً"),
                Triple("العلوم: المستقبلات الحسية: مستقبلات الجلد وبنية الجلد ص 66-70", "اللغة الفرنسية: الوحدة الثالثة: نصوص الصالونات والمنتديات ص 41-43", "رسم مستقبلات الجلد وتسميع كلمات الفرنسي كتابة"),
                Triple("الرياضيات: حل تمرينات قواعد الاشتقاق والتقريب التآلفي ص 84", "اللغة الفرنسية: قواعد الكلام المنقول (Discours direct/indirect) ص 47-48", "مراجعة أخطاء الاشتقاق وتثبيت قواعد الكلام المنقول"),
                Triple("الفيزياء: حل المسائل العامة للموائع ص 256 والمسألة الأولى ص 257", "التربية الإسلامية: آيات تلاوة الله القادر المعبود ص 20 وآية الاستحفاظ 2 ص 41", "كتابة استنتاجات الموائع وتسميع خواتيم البقرة كتابة"),
                Triple("الكيمياء: ثابت التوازن الكيميائي Kc وKp وحاصل التفاعل Q ص 63-65", "العلوم: المستقبلات الكيميائية للشم والذوق ص 73-75", "اختبار 1 علوم المستقبلات الحسية وتصفية التراكم")
            ),
            // Week 10
            listOf(
                Triple("الرياضيات: تطبيقات الاشتقاق ودراسة اطراد التوابع ص 73-75", "اللغة العربية: نص المهاجر لنسيب عريضة المقطع الأول ص 27-28", "مراجعة قواعد اطراد التوابع واختبار 3 فرنسي 2"),
                Triple("الفيزياء: النسبية الخاصة: فرضيات أينشتاين وتمدد الزمن وعامل لورنتز ص 51-52", "اللغة الإنكليزية: نصوص الهندسة الطبية والأجهزة ص 37-39", "مراجعة تمدد الزمن واختبار 3 موائع واختبار 3 إسلامية"),
                Triple("الرياضيات: دراسة القيم الحدية محلياً وشروط وجودها ص 76-83", "الكيمياء: حساب ثابت التوازن وقوانين التركيز والضغوط ص 62-63", "حل تمارين القيم الحدية واختبار 2 علوم المستقبلات"),
                Triple("العلوم: الهرمونات النباتية: الأوكسينات وتجارب داروين وفنت ص 109-111", "اللغة الفرنسية: قواعد الكلام المنقول وتغير الأزمنة والضمائر ص 47-49", "رسم مخطط الأوكسينات وتثبيت تحويلات الكلام المنقول"),
                Triple("الرياضيات: حل تمرينات ومسائل الاشتقاق والمشتقات من مراتب عليا ص 84-85", "اللغة الفرنسية: حل تدريبات الكلام المنقول في الأنشطة ص 48", "اختبار 1 رياضيات الاشتقاق ومراجعة أخطاء التمارين"),
                Triple("الفيزياء: النسبية الخاصة: تقلص الأطوال وتغير الكتلة والتحول النسبي ص 53-54", "التربية الإسلامية: حديث الإيمان قوة وعمل حفظاً وشرحاً ص 73-75", "كتابة استنتاج تقلص الأطوال وتسميع حديث الإيمان كتابة"),
                Triple("الكيمياء: حل مسائل درس التوازن ومبدأ لوشاتليه وتأثير الضغط ص 64-65", "العلوم: الصحة الإنجابية وطرق الوقاية وحل أسئلة الهرمونات ص 112", "اختبار 1 كيمياء حركية التفاعلات وتصفية التراكم")
            ),
            // Week 11
            listOf(
                Triple("الرياضيات: الأعداد العقدية: مرافق عدد عقدي وخواصه وحل معادلاته ص 93", "اللغة العربية: نص المهاجر المقطعان الثاني والثالث ص 28-29", "مراجعة خواص المرافق واختبار 2 رياضيات الاشتقاق"),
                Triple("الفيزياء: مراجعة شاملة وحل تطبيقات النسبية الخاصة ص 60-61", "اللغة الإنكليزية: قاعدة السببية باستخدام الأفعال المساعدة ص 40-41", "مراجعة علاقات النسبية وتطبيق صياغة الجمل السببية"),
                Triple("الرياضيات: الشكل المثلثي لعدد عقدي وطويلته وزاويته ص 95-97", "الكيمياء: الكيمياء التحليلية: الحموض والأسس ونظريات أرينيوس وبرونشتد ص 84-86", "كتابة قوانين الشكل المثلثي واختبار 3 مستقبلات واختبار 2 حركية"),
                Triple("العلوم: تكاثر الأحياء الدقيقة: تكاثر الفيروسات ودورة حياة آكل الجراثيم ص 127-129", "اللغة الفرنسية: متابعة وتطبيقات تحويل الجمل بالكلام المنقول ص 48", "رسم مخطط دورة حياة آكل الجراثيم وتحويل الجمل فرنسي"),
                Triple("الرياضيات: حل جميع تمرينات ومسائل مرافق العدد والشكل المثلثي ص 109", "اللغة الفرنسية: تحويلات الظروف الزمنية ومؤشرات الوقت ص 49", "مراجعة أخطاء العقدية وتثبيت مؤشرات الوقت بالفرنسية"),
                Triple("الفيزياء: المغناطيسية: الحقل المغناطيسي لتيار مستقيم وعناصره ص 68-72", "التربية الإسلامية: مظاهر الحضارة الإسلامية العلمية والاجتماعية ص 83-85", "كتابة عناصر شعاع الحقل وتلخيص مظاهر الحضارة"),
                Triple("الفيزياء: حل المسائل العامة للنسبية (مسألة 30 ص 259) وإغلاقها", "اللغة العربية: قواعد الصرف: مبحث الإبدال الصرفي وقوانينه ص 97", "اختبار 1 فيزياء النسبية الخاصة وتصفية التراكم")
            ),
            // Week 12
            listOf(
                Triple("الرياضيات: الأعداد العقدية: الشكل الأسي وقانون أويلر ص 101-102", "اللغة العربية: قصيدة المهاجر: مراجعة وإعراب وحل التطبيقات ص 27-31", "تصفح الشكل الأسي وإعراب المهاجر واختبار 3 اشتقاق"),
                Triple("الفيزياء: الحقل المغناطيسي لتيار دائري (ملف دائري) وقوانينه ص 70-72", "اللغة الإنكليزية: الوحدة الخامسة (Civil Rights): نص الحق في التعليم ص 52-53", "مراجعة قوانين الحقل الدائري وحفظ كلمات الإنكليزي"),
                Triple("الرياضيات: قانون دو موافر وقوى الأعداد وحل معادلات الدرجة 2 ص 103-104", "الكيمياء: التأين الذاتي للماء وpH وpOH والمحاليل القوية ص 84-86", "حل معادلات عقدية وحساب pH واختبار 2 نسبية واختبار 3 حركية"),
                Triple("العلوم: تكاثر الجراثيم بالانشطار الثنائي والاقتران الجرثومي ص 130-133", "اللغة الفرنسية: مراجعة وتطبيق الكلام المنقول وحل تدريبات الفهم ص 41-45", "رسم مخطط الاقتران الجرثومي ومراجعة الكلام المنقول"),
                Triple("الرياضيات: حل تمرينات ومسائل الأعداد العقدية ص 109 وإغلاق الوحدة 1", "اللغة الإنكليزية: قواعد جمل الوصل وكتابة موضوع التعليم ص 54-55", "اختبار 1 رياضيات الأعداد العقدية وحل جمل الوصل"),
                Triple("الفيزياء: الحقل المغناطيسي لوشيعة حلزونية وتطبيقاتها ص 73-74", "التربية الإسلامية: مراجعة وتسميع حديث الإيمان وخواتيم البقرة والحضارة", "كتابة قوانين الوشيعة غيباً وتسميع الديانة كتابة"),
                Triple("الفيزياء: حل تمرينات الحقل المغناطيسي لتيار مستقيم ودائري ووشيعة ص 74-75", "اللغة الفرنسية: حل تطبيقات التعبير الكتابي وموضوع الصالونات ص 50-51 (إغلاق 3)", "اختبار 1 فرنسي الوحدة 3 وتصفية التراكم")
            )
        )

        val subjectMapping = listOf(
            Subject.MATH, Subject.PHYSICS, Subject.MATH, Subject.SCIENCE,
            Subject.MATH, Subject.PHYSICS, Subject.CHEMISTRY
        )
        val secSubjectMapping = listOf(
            Subject.ARABIC, Subject.ENGLISH, Subject.CHEMISTRY, Subject.FRENCH,
            Subject.FRENCH, Subject.ISLAMIC, Subject.SCIENCE
        )

        var currentWeek = 9
        for (wIndex in 0 until 4) {
            val weekDays = m3Data[wIndex]
            for (dIndex in 0 until 7) {
                val item = weekDays[dIndex]
                list.add(DayTask(
                    id = "m3_w${currentWeek}_d${dIndex + 1}",
                    monthNumber = 3,
                    weekNumber = currentWeek,
                    dayOfWeek = days[dIndex],
                    period1Subject = subjectMapping[dIndex],
                    period1Content = item.first,
                    period2Subject = secSubjectMapping[dIndex],
                    period2Content = item.second,
                    period3Content = item.third
                ))
            }
            currentWeek++
        }

        // Months 4 to 9 generated consistently with structured high-fidelity tasks
        for (m in 4..9) {
            val mInfo = months.first { it.monthNumber == m }
            for (w in mInfo.startWeek..mInfo.endWeek) {
                for (d in 0 until 7) {
                    val dayName = days[d]
                    val isRestDay = (m >= 8 && d == 6) // Friday is full rest in review months 8-9
                    val (p1, p2, p3) = getSubjectCurriculumContent(m, w, d)
                    list.add(DayTask(
                        id = "m${m}_w${w}_d${d + 1}",
                        monthNumber = m,
                        weekNumber = w,
                        dayOfWeek = dayName,
                        period1Subject = if (isRestDay) Subject.GENERAL else subjectMapping[d],
                        period1Content = p1,
                        period2Subject = if (isRestDay) Subject.GENERAL else secSubjectMapping[d],
                        period2Content = p2,
                        period3Content = p3,
                        isRestDay = isRestDay
                    ))
                }
            }
        }
    }

    private fun getSubjectCurriculumContent(month: Int, week: Int, dayIndex: Int): Triple<String, String, String> {
        return when (month) {
            4 -> when (dayIndex) {
                0 -> Triple("الرياضيات: الهندسة الفراغية والأشعة في الفراغ ومركز الأبعاد ص 6-21", "اللغة العربية: نص الغاب لجبران خليل جبران وبحث البدل ص 32-35", "مراجعة الأشعة وإعراب الغاب واختبار العقدية")
                1 -> Triple("الفيزياء: المغناطيسية وقوة لابلاس الكهرطيسية وقوة لورنتز ص 74-97", "اللغة الإنكليزية: نصوص الأمم المتحدة وصيغ المستقبل وقواعد الشرط ص 59-66", "مراجعة قوة لابلاس ولورنتز وحفظ كلمات الإنكليزي")
                2 -> Triple("الرياضيات: تطبيقات العدد العقدي الممثل للشعاع والمسافات بالفراغ ص 115", "الكيمياء: الحموض والأسس والتأين الذاتي وثوابت Ka وKb وحساب pH ص 84-88", "حل مسائل الفراغية وتأين الحموض واختبارات التثبيت")
                3 -> Triple("العلوم: تكاثر الفيروسات والتقانات الحيوية ونقل النوى والاستنساخ وعاريات البذور ص 140-151", "اللغة الفرنسية: الوحدة 4: نصوص رائدات الأدب وقاعدة التفضيل والمقارنة ص 65-68", "رسم بنية المخاريط وجمل المقارنة بالفرنسية")
                4 -> Triple("الرياضيات: يوم التمكين: حل مسائل الأشعة في الفراغ والمعلم والمسافات ص 29-31", "اللغة الإنكليزية/الفرنسية: حل تدريبات الأنشطة وكتابة المواضيع ص 68-70", "مراجعة أخطاء التمارين واختبار 1 رياضيات الفراغية")
                5 -> Triple("الفيزياء: تجربة دولاب بارلو وميكانيك الشحنات والتحريض ص 89-115", "التربية الإسلامية: تلاوة سورة فاطر وحديث حكم القاضي ونظام الأسرة ص 42-96", "كتابة قوانين بارلو وتسميع سورة فاطر كتابة")
                else -> Triple("الكيمياء/العلوم: حلمهة الأملاح والأشعة والتكاثر لدى عاريات البذور ص 100-160", "العلوم: الكيس الطلعي وتأبير الأزهار وحل أسئلة الوحدة", "تصفية التراكم واختبارات الكيمياء والعلوم")
            }
            5 -> when (dayIndex) {
                0 -> Triple("الرياضيات: الجداء السلمي وتطبيقاته التحليلية والتعامد في الفراغ ص 38-48", "اللغة العربية: فن الرواية وتطبيقات رواية المصابيح الزرق لحنا مينة ص 38-49", "تصفح الجداء السلمي وقراءة مدخل الرواية وعناصرها")
                1 -> Triple("الفيزياء: التحريض الكهرطيسي وقانون فاراداي والتحريض الذاتي ص 98-115", "اللغة الإنكليزية: الوحدة 8: حقائق عن جسم الإنسان وقواعد التمني ص 65-67", "إعادة رسم تجربة فاراداي وحفظ مصطلحات الجسم")
                2 -> Triple("الرياضيات: المسافة بين نقطة ومستو والتمثيلات الوسيطية للمستقيم ص 48-73", "الكيمياء: المحاليل المائية للأملاح وحسابات ثابت الحلمهة ص 98-101", "حل تمارين التمثيل الوسيطي ومعادلات الحلمهة")
                3 -> Triple("العلوم: مغلفات البذور وتشكيل الكيس الرشيمي والتكاثر البشري ص 158-185", "اللغة الفرنسية: الوحدة 5: الذكاء الاصطناعي وقاعدة الصفات والضمائر غير المحددة ص 77-83", "رسم بنية البذيرة الناضجة وتسميع كلمات الذكاء الاصطناعي")
                4 -> Triple("الرياضيات: حل جميع تمرينات ومسائل المستويات وتقاطعها بالفراغ ص 77-81", "اللغة الفرنسية/الإنكليزية: حل تدريبات التقويم وكتابة موضوع الروبوتات ص 83-90", "تصحيح أخطاء الفراغية واختبار 1 مستويات بالفراغ")
                5 -> Triple("الفيزياء: الدارات المهتزة والتيار المتناوب الجيبي ومتجهات فرينل ص 126-143", "التربية الإسلامية: حديث مكانة الشهيد وعقد الزواج وأركانه وشروطه ص 65-111", "كتابة استنتاج طاقة الدارة المهتزة وتسميع الحديث")
                else -> Triple("الكيمياء: حل مسائل الأملاح والحلمهة ص 102-104 (إغلاق الوحدة)", "العلوم: دراسة الهرمونات الجنسية الذكرية وتحت المهاد والنخامة ص 181", "تصفية التراكم واختبارات الفيزياء والفرنسي")
            }
            6 -> when (dayIndex) {
                0 -> Triple("الرياضيات: نهاية متتالية والتكامل والتوابع الأصلية وحساب المساحات ص 110-213", "اللغة العربية: رواية دمشق يا بسمة الحزن وقصيدة الوطن لعدنان مردم بك ص 50-61", "مراجعة خواص التكامل المحدد وإعراب قصيدة الوطن")
                1 -> Triple("الفيزياء: الأمواج المستقرة العرضية وتجربة ملد والأمواج الطولية والمزامير ص 168-185", "اللغة الإنكليزية: الوحدة 10: صدمة الثقافة (Culture Shock) وقواعد الاحتمالية ص 97-101", "مراجعة قوانين عقد وبطون الأمواج وتسميع الإنكليزي")
                2 -> Triple("الرياضيات: التحليل التوافقي: المبدأ الأساسي في العد والتراتيب والتباديل ص 130-147", "الكيمياء: الكيمياء التحليلية: المعايرة الحجمية وبداية الكيمياء العضوية والأغوال ص 108-138", "حل تمارين التراتيب والتباديل ومعادلات أكسدة الأغوال")
                3 -> Triple("العلوم: التكاثر عند الإنسان: الدورة المبيضية، الإلقاح، التنامي الجنيني، والتعشيش ص 189-202", "اللغة الفرنسية: الوحدة 6: غزو الفضاء وضمائر الإشارة والملكية ص 95-104 (إغلاق المنهاج)", "رسم مراحل التعشيش وحفظ موضوع غزو الفضاء")
                4 -> Triple("الرياضيات: حل جميع تمرينات التكامل والتحليل التوافقي ص 144-225", "اللغة الإنكليزية: الوحدة 11: نصوص وقواعد الذكاء الاصطناعي ص 108-112", "اختبار 1 تكامل وتوابع أصلية ومراجعة الأخطاء")
                5 -> Triple("الفيزياء: ميكانيك المزامير: مزمار متشابه ومختلف الطرفين والنماذج الذرية ص 184-201", "التربية الإسلامية: حديث عموم المسؤولية وحقوق الزوجين ونظام المال ص 69-125", "كتابة قوانين تواترات المزامير وتسميع حديث المسؤولية")
                else -> Triple("العلوم: بداية وحدة الوراثة: تجارب مندل في الهجونة الأحادية والثنائية ص 221-224", "الكيمياء: الألدهيدات والكيتونات والحموض الكربوكسيلية ص 147-162", "اختبارات التكاثر والتحليلية وتصفية التراكم")
            }
            7 -> when (dayIndex) {
                0 -> Triple("الرياضيات: الاحتمالات: الاحتمال الشرطي، المتحولات العشوائية وقانون برنولي ص 154-181", "اللغة العربية: نصوص لوعة الفراق، الأمير الدمشقي، قوة العلم، ومروءة وسخاء ص 65-80", "مراجعة شجرة الاحتمالات وإعراب النصوص الأدبية")
                1 -> Triple("الفيزياء: الفيزياء الحديثة: الفعل الكهرحراري، الأشعة السينية، أشعة الليزر، والفلكية ص 210-268", "اللغة الإنكليزية: الوحدة 12: المعرفة الرقمية وقواعد القلب النحوي ومراجعة شاملة ص 116-118", "مراجعة طاقة الفوتون ونشأة المجموعة الشمسية")
                2 -> Triple("الرياضيات: المعادلات التفاضلية البسيطة والأشكال الدورانية والأسطوانة والمخروط ص 186-420", "الكيمياء: الإسترات والأميدات وتسمية وتفاعلات الأمينات ص 173-192 (إغلاق الكيمياء)", "حل معادلات تفاضلية وتسمية الأمينات يدوياً")
                3 -> Triple("العلوم: الوراثة والمسألة الوراثية: الهجونة، الارتباط والعبور، وتحديد الجنس، والطفرات ص 228-275", "اللغة الفرنسية: مراجعة شاملة ومكثفة لجميع قواعد ومفردات الوحدات الست ونماذج وزارية", "صياغة خطوات المسألة الوراثية وحل النماذج الفرنسية")
                4 -> Triple("الرياضيات: يوم التمكين الختامي: حل المسائل الشاملة وإغلاق منهاج الرياضيات رسمياً", "اللغة الإنكليزية/الفرنسية: حل نماذج امتحانية شاملة للغتين", "اختبارات شاملة وإغلاق منهاج اللغات")
                5 -> Triple("الفيزياء: قانون هابل والثقوب السوداء وسرعة الإفلات ص 258-268 (إغلاق الفيزياء رسمياً)", "التربية الإسلامية: أسس الدعوة، الحديث السادس، العلاقات الدولية، وسيرة الأعلام (إغلاق الديانة)", "اختبارات شاملة وإغلاق مادتي الفيزياء والديانة")
                else -> Triple("العلوم: دراسة الهندسة الوراثية وتطبيقاتها ص 271-275 (إغلاق العلوم رسمياً)", "الكيمياء: حل المسائل العامة الشاملة لمنهاج الكيمياء بالكامل", "احتفال بإغلاق كافة المناهج وتصفية التراكم النهائي")
            }
            8 -> when (dayIndex) {
                0 -> Triple("الرياضيات: حل أسئلة دورة رسمية سابقة (الجزء الأول - التحليل) بمؤقت 150 دقيقة تحت ضغط", "اللغة العربية: حل أسئلة دورة سابقة كاملة (قصيدة، إعراب، بلاغة، رواية، موضوعين)", "تصحيح الرياضيات والعربي باللون الأحمر وفق سلالم التصحيح")
                1 -> Triple("الفيزياء: حل دورة سابقة بمؤقت 150 دقيقة مع كتابة القوانين وتفصيل الاستنتاجات", "اللغة الإنكليزية: حل دورة كاملة (استيعاب، قواعد، إعادة صياغة، موضوع تعبير)", "مطابقة الإجابات مع السلالم وترميم أي فجوة فوراً")
                2 -> Triple("العلوم: حل دورة سابقة كاملة (تعاليل، مقارنات، رسومات، مسألة الوراثة) بمؤقت 120 دقيقة", "الكيمياء: حل دورة سابقة كاملة (تسميات، تفاعلات، والمسائل الأربع الكبرى)", "رصد العلامات بدقة مجهرية وتثبيت النكشات المنسية")
                3 -> Triple("الرياضيات: حل أسئلة دورة سابقة (الجزء الثاني - الهندسة والعقدية والاحتمالات) بمؤقت 150 دقيقة", "اللغة الفرنسية: حل دورة سابقة كاملة (نصوص، قواعد، ترتيب حوار، موضوع كتابي)", "مقارنة الجزء الثاني بالسلالم ورصد مواضع النقص")
                4 -> Triple("الفيزياء: حل دورة إضافية لتعزيز سرعة الاستدعاء وكسر رهبة الأفكار المركبة", "التربية الإسلامية: حل دورة سابقة كاملة (كتابة نصوص الاستحفاظ والأحاديث غيباً والتجويد)", "تصحيح الفيزياء والديانة وإعادة كتابة الأخطاء الإملائية 3 مرات")
                5 -> Triple("يوم التمكين ومعالجة الثغرات: إعادة حل كافة المسائل والنكشات التي أخطأت فيها هذا الأسبوع غيباً", "مراجعة شاملة لجميع القوانين وقواعد الإعراب التي تسببت في خصم أي درجات", "إغلاق ثغرات دورات الأسبوع وتثبيت العلامة التامة")
                else -> Triple("استراحة تامة لتجديد الطاقة والنشاط الذهني", "استراحة تامة واسترخاء", "استراحة تامة - نوم عميق وتغذية ممتازة")
            }
            else -> when (dayIndex) { // Month 9
                0 -> Triple("الرياضيات: حل النموذج الاسترشادي الشامل الأول/الثاني/الثالث بمؤقت 180 دقيقة لتدريب الذهن", "اللغة العربية: حل النموذج الامتحاني الشامل (نصوص، قواعد، بلاغة، رواية، موضوع إبداعي)", "مقارنة الحلول بسلالم التصحيح النموذجية ورصد الأخطاء فوراً")
                1 -> Triple("الفيزياء: حل النموذج الفولاذي الشامل بمؤقت 150 دقيقة لضبط مهارة استدعاء الاستنتاجات", "اللغة الإنكليزية: حل النموذج الشامل (استيعاب، قواعد، ترجمة، موضوع كتابي)", "مطابقة مسودات الفيزياء والإنكليزي وترميم القوانين فوراً")
                2 -> Triple("العلوم: حل النموذج الشامل (التعاليل، المقارنات، الوظائف، مسألة الوراثة) بمؤقت 120 دقيقة", "الكيمياء: حل النموذج الشامل (الغازات، السرعة والتوازن، التحليلية، والعضوية)", "تصحيح ورقيتي العلوم والكيمياء بدقة بالغة وحصر النكشات")
                3 -> Triple("الرياضيات: حل النموذج الفولاذي الشامل لفرعي الجبر والهندسة والتحليل بمؤقت حاد للسرعة الفائقة", "اللغة الفرنسية: حل النموذج الامتحاني الشامل لمادة الفرنسي بالكامل", "مقارنة الجزء الثاني رياضيات والفرنسي بالسلالم النموذجية")
                4 -> Triple("الفيزياء: حل النموذج الشامل لأبحاث الكتاب لتدريب اليد على دقة الصياغة النظرية", "التربية الإسلامية: حل النموذج الشامل وكتابة الاستحفاظ والأحاديث غيباً", "تصحيح ورقيتي الفيزياء والديانة الشاملتين لضمان الدرجة التامة")
                5 -> Triple("جلسة الحسم الختامية: مراجعة ثغرات النماذج وقوانين العضوية والوراثة والإعراب الصعب", "إعادة حل كافة الأسئلة التي تم الخطأ بها غيباً لضمان 100% جهوزية", "الاستعداد الكامل لدخول الامتحانات بثقة وتفوق تام")
                else -> Triple("استراحة تامة واسترخاء ذهني كامل", "استراحة تامة وتهيئة نفسية إيجابية", "استراحة تامة - الثقة بالله والجاهزية القصوى")
            }
        }
    }
}
