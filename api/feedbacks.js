// Vercel Serverless Function: /api/feedbacks
// Manages student feedbacks and questions reports sent from the Android app

let inMemoryFeedbacks = [
  {
    id: "fb_1",
    questionId: 1,
    questionText: "لتكن الدالة f(x) = (2x - 1)e^x. إن مشتقة الدالة f'(x) تساوي:",
    subject: "MATH",
    selectedOption: "B",
    feedbackReason: "خطأ في الإجابة الصحيحة",
    noteText: "أعتقد أن الإشارة في الخيار A سالبة وليست موجبة، يرجى مراجعة إشارة المشتق.",
    status: "PENDING",
    adminReply: "",
    timestamp: Date.now() - 7200000
  },
  {
    id: "fb_2",
    questionId: 2,
    questionText: "في دارة RC، ثابت الزمن τ يساوي بالجداء:",
    subject: "PHYSICS",
    selectedOption: "A",
    feedbackReason: "شرح غير واضح",
    noteText: "الشرح لم يوضح وحدة قياس المقاومة والسعة معاً بالتفصيل.",
    status: "REVIEWED",
    adminReply: "تم فحص الملاحظة وتوضيح أن وحدة الفاراد ضرب الأوم تكافئ الثانية (s).",
    timestamp: Date.now() - 18000000
  }
];

export default async function handler(req, res) {
  // Enable CORS
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  try {
    if (req.method === "GET") {
      return res.status(200).json({
        success: true,
        count: inMemoryFeedbacks.length,
        data: inMemoryFeedbacks
      });
    }

    if (req.method === "POST") {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;
      
      const newFeedback = {
        id: body.id || `fb_${Date.now()}_${Math.floor(Math.random() * 1000)}`,
        questionId: body.questionId || 0,
        questionText: body.questionText || "بدون نص",
        subject: body.subject || "عام",
        selectedOption: body.selectedOption || null,
        feedbackReason: body.feedbackReason || "ملاحظة عامة",
        noteText: body.noteText || "",
        status: "PENDING",
        adminReply: "",
        timestamp: body.timestamp || Date.now()
      };

      inMemoryFeedbacks.unshift(newFeedback);

      return res.status(201).json({
        success: true,
        message: "تم استلام الملاحظة بنجاح في السحابة",
        data: newFeedback
      });
    }

    if (req.method === "PUT" || req.method === "PATCH") {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;
      const { id, status, adminReply } = body;

      const item = inMemoryFeedbacks.find(f => String(f.id) === String(id));
      if (!item) {
        return res.status(404).json({ success: false, message: "الملاحظة غير موجودة" });
      }

      if (status) item.status = status;
      if (adminReply !== undefined) item.adminReply = adminReply;

      return res.status(200).json({
        success: true,
        message: "تم تحديث حالة الملاحظة بنجاح",
        data: item
      });
    }

    if (req.method === "DELETE") {
      const { id } = req.query || {};
      if (!id) {
        return res.status(400).json({ success: false, message: "معرف الملاحظة مطلوب" });
      }

      inMemoryFeedbacks = inMemoryFeedbacks.filter(f => String(f.id) !== String(id));
      return res.status(200).json({
        success: true,
        message: "تم حذف الملاحظة بنجاح"
      });
    }

    return res.status(405).json({ success: false, message: "طريقة الطلب غير مدعومة" });
  } catch (error) {
    console.error("Feedbacks API Error:", error);
    return res.status(500).json({
      success: false,
      message: "حدث خطأ في الخادم السحابي",
      error: error.message
    });
  }
}
