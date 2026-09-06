# 🚀 دليل رفع موقع الهبوط واستضافة التطبيق على Supabase

تم تصميم موقع الهبوط الخاص بـ **منصة متفوق التعليمية** بأحدث التقنيات السريعة (HTML5, Tailwind CSS, FontAwesome) ليكون جاهزاً للنشر الفوري والاستضافة على **Supabase Storage** أو **GitHub Pages**.

---

## 📁 محتويات مجلد `landing_website/`
1. `index.html`: صفحة الهبوط الرئيسية باللغة العربية مع روابط التحميل المباشرة واستعراض مميزات المنصة.
2. `logo.jpg`: شعار المنصة الرسمي المشتق من صور التطبيق.
3. `app-release.apk`: ملف أندرويد النهائي الجاهز للتحميل للطلاب (يتم وضعه في مجلد `apps` داخل Supabase Storage).

---

## ⚡ خطوات الرفع والتشغيل على Supabase

### الخطوة 1: إنشاء Bucket في Supabase Storage
1. افتح مشروعك على موقع [Supabase.com](https://supabase.com).
2. انتقل إلى قسم **Storage** وأنطق حزمة جديدة باسم `apps` وجعلها Public.
3. قم برفع ملف `app-release.apk` إلى هذا المجلد لكي يحصل الطلاب على رابط تحميل مباشر.

### الخطوة 2: ربط قاعدة البيانات
1. قم بتشغيل الأوامر الموجودة في ملف `supabase_schema.sql` في محرر الـ SQL الخاص بـ Supabase.

---

## 🔗 النتيجة
- ستحصل فوراً على رابط تحفيزي ومحمي بشهادة SSL مثل:  
  `https://your-project.supabase.co`
- سيمتلك الطلاب رابط تحميل مباشر للتطبيق عبر:  
  `https://your-project.supabase.co/storage/v1/object/public/apps/app-release.apk`

