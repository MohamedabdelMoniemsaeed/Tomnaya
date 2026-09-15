# مشروع تمناية - نسخة الفلاتر (Flutter Multiplatform for Android & iOS)

هذا المجلد يحتوي على كود تطبيق **تَمْنَايَة (Tomnaya)** المكتوب بلغة **Dart & Flutter** ليعمل على كل من:
- 📱 **هواتف الأندرويد (Android)**
- 🍏 **هواتف الآيفون (iOS)**

---

## 🚀 كيفية تشغيل مشروع Flutter على جهازك:

### 1. المتطلبات:
- تثبيت [Flutter SDK](https://docs.flutter.dev/get-started/install) على جهاز الكمبيوتر.
- محاكي أندرويد أو جهاز حقيقي، أو جهاز Mac مع Xcode لتشغيل الآيفون.

### 2. خطوات التشغيل:
افتح مجلد `flutter_version` في Terminal أو VS Code ونفذ الأوامر التالية:

```bash
# 1. تحميل الحزم والمكتبات
flutter pub get

# 2. تشغيل التطبيق على الهاتف المتصل (سواء آيفون أو أندرويد)
flutter run
```

---

## 📦 كيفية استخراج ملف الـ APK عبر Flutter:

لاستخراج ملف الـ APK الخاص بالأندرويد عبر فلاتر:
```bash
flutter build apk --release
```
ستجد الملف الناتج في المسار:
`build/app/outputs/flutter-apk/app-release.apk`

---

## 🍏 كيفية تشغيل وبناء نسخة الآيفون (iOS):
إذا كان لديك جهاز Mac وتريد تجربة التطبيق على الآيفون:
```bash
# بناء نسخة الـ iOS
flutter build ios
```
ثم فتح مجلد `ios/Runner.xcworkspace` في برنامج **Xcode** وتثبيته على جهاز الآيفون أو رفعه إلى TestFlight / App Store.

---

## 📂 هيكل ملفات الفلاتر في هذا المجلد:
- `pubspec.yaml`: ملف الحزم والمكتبات المعتمدة (Cairo Google Fonts, Localizations, etc).
- `lib/main.dart`: نقطة الدخول الرئيسية، واجهات الحجز، وتفاوض الأجرة، وسجل المشاوير، وتحديد المواقف.
- `lib/models/trip_model.dart`: نماذج بيانات الرحلات والكباتن وحجوزات الكراسي.
- `lib/widgets/seat_selector_widget.dart`: المخطط التفاعلي لصالون السوزوكي الفان (الـ 7 مقاعد).
- `lib/widgets/driver_dashboard.dart`: لوحة تحكم الكابتن لتقفيل العداد وحساب إيرادات اليوم.
