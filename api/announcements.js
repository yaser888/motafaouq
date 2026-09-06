// Vercel Serverless Function: /api/announcements
// Broadcasts announcements from Web Admin to Android app

let inMemoryAnnouncements = [
  {
    id: "ann_1",
    title: "تحديث بنك الأسئلة الوزارية 🌟",
    message: "تمت إضافة نماذج وحلول وزارية جديدة لجميع الشعب العلمية والأدبية.",
    date: "اليوم",
    isImportant: true,
    timestamp: Date.now()
  }
];

export default async function handler(req, res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  try {
    if (req.method === "GET") {
      return res.status(200).json({
        success: true,
        data: inMemoryAnnouncements
      });
    }

    if (req.method === "POST") {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;
      const newAnn = {
        id: `ann_${Date.now()}`,
        title: body.title || "تنبيه جديد",
        message: body.message || "",
        date: "الآن",
        isImportant: body.isImportant || false,
        timestamp: Date.now()
      };
      inMemoryAnnouncements.unshift(newAnn);
      return res.status(201).json({
        success: true,
        message: "تم نشر الإعلان بنجاح إلى جميع الطلاب",
        data: newAnn
      });
    }

    return res.status(405).json({ success: false, message: "طريقة الطلب غير مدعومة" });
  } catch (error) {
    return res.status(500).json({ success: false, error: error.message });
  }
}
