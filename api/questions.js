// Vercel Serverless Function: /api/questions
// Manages cloud questions bank for both Web Admin and Android App

let inMemoryQuestions = [
  {
    id: 1,
    subject: "MATH",
    unitOrTopic: "الدوال الأسية واللوغاريتمية",
    questionText: "لتكن الدالة f(x) = (2x - 1)e^x. إن مشتقة الدالة f'(x) تساوي:",
    questionType: "MCQ",
    optionA: "(2x + 1)e^x",
    optionB: "(2x - 1)e^x",
    optionC: "2e^x",
    optionD: "(x + 2)e^x",
    correctAnswer: "A",
    explanation: "باستخدام قاعدة مشتق الجداء (u.v)' = u'v + uv': f'(x) = 2e^x + (2x - 1)e^x = (2x + 1)e^x.",
    difficulty: "متوسط",
    yearOrSource: "بكالوريا 2023 الدورة العادية"
  },
  {
    id: 2,
    subject: "PHYSICS",
    unitOrTopic: "الكهرباء والمكثفات",
    questionText: "في دارة RC، ثابت الزمن τ يساوي بالجداء:",
    questionType: "MCQ",
    optionA: "R × C",
    optionB: "R / C",
    optionC: "C / R",
    optionD: "1 / (RC)",
    correctAnswer: "A",
    explanation: "ثابت الزمن في دارة الشحن والتفريغ هو τ = RC وله بعد زمني يقدر بالثانية (s).",
    difficulty: "سهل",
    yearOrSource: "بكالوريا 2022 تجريبي"
  },
  {
    id: 3,
    subject: "SCIENCE",
    unitOrTopic: "تركيب البروتين والترجمة",
    questionText: "تتم عملية الترجمة عند حقيقيات النوى في:",
    questionType: "MCQ",
    optionA: "الهيولى (السيتوبلازم) على مستوى الريبوزومات",
    optionB: "داخل النواة فقط",
    optionC: "داخل الميتوكوندريا فقط",
    optionD: "في الغشاء الهيولي الخارجي",
    correctAnswer: "A",
    explanation: "تتم عملية الاستنساخ داخل النواة بينما تتم عملية الترجمة في الهيولى بتدخل الريبوزومات و ARNt.",
    difficulty: "متوسط",
    yearOrSource: "وزاري 2021"
  }
];

export default async function handler(req, res) {
  // Enable CORS
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  try {
    if (req.method === "GET") {
      const { subject } = req.query || {};
      let filtered = inMemoryQuestions;
      if (subject && subject !== "ALL") {
        filtered = inMemoryQuestions.filter(q => q.subject === subject);
      }
      return res.status(200).json({
        success: true,
        count: filtered.length,
        data: filtered
      });
    }

    if (req.method === "POST") {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;

      if (Array.isArray(body)) {
        // Batch upload
        for (const q of body) {
          q.id = q.id || Date.now() + Math.floor(Math.random() * 1000);
          inMemoryQuestions.unshift(q);
        }
        return res.status(201).json({
          success: true,
          message: `تم رفع ${body.length} أسئلة بنجاح إلى السحابة`,
          count: inMemoryQuestions.length
        });
      }

      const newQ = {
        id: body.id || Date.now(),
        subject: body.subject || "MATH",
        unitOrTopic: body.unitOrTopic || "الوحدة العامة",
        questionText: body.questionText || "",
        questionType: body.questionType || "MCQ",
        optionA: body.optionA || "",
        optionB: body.optionB || "",
        optionC: body.optionC || "",
        optionD: body.optionD || "",
        correctAnswer: body.correctAnswer || "A",
        explanation: body.explanation || "",
        difficulty: body.difficulty || "متوسط",
        yearOrSource: body.yearOrSource || "سحابي أونلاين"
      };

      inMemoryQuestions.unshift(newQ);
      return res.status(201).json({
        success: true,
        message: "تم حفظ السؤال بنجاح في بنك الأسئلة السحابي",
        data: newQ
      });
    }

    if (req.method === "PUT") {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;
      const index = inMemoryQuestions.findIndex(q => String(q.id) === String(body.id));
      if (index === -1) {
        return res.status(404).json({ success: false, message: "السؤال غير موجود" });
      }

      inMemoryQuestions[index] = { ...inMemoryQuestions[index], ...body };
      return res.status(200).json({
        success: true,
        message: "تم تعديل السؤال في السحابة بنجاح",
        data: inMemoryQuestions[index]
      });
    }

    if (req.method === "DELETE") {
      const { id } = req.query || {};
      if (!id) return res.status(400).json({ success: false, message: "معرف السؤال مطلوب" });
      inMemoryQuestions = inMemoryQuestions.filter(q => String(q.id) !== String(id));
      return res.status(200).json({ success: true, message: "تم حذف السؤال" });
    }

    return res.status(405).json({ success: false, message: "طريقة الطلب غير مدعومة" });
  } catch (error) {
    console.error("Questions API Error:", error);
    return res.status(500).json({
      success: false,
      message: "حدث خطأ في الخادم السحابي",
      error: error.message
    });
  }
}
