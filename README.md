# Manha Decor Sender — Android App

Event decoration catalog ko WhatsApp par ek tap me client ke number par bhejne wala app.

## Yeh app kya karta hai
1. Client ka mobile number likhein/paste karein
2. Client ka naam (optional)
3. Category select karein (e.g. Birthday, Wedding, Anniversary)
4. "Send Photos" dabayein → app us category ki saari photos Google Drive se download karega aur WhatsApp us number ki chat photos ke saath ready khol dega
5. **Aapko sirf WhatsApp ke andar "Send" ek baar tap karna hoga** — yeh Android ka apna security rule hai, koi app dusre app ka button khud nahi daba sakta. Isse zyada automatic karna sirf WhatsApp Business API (paid, Meta approval) se possible hai, ya risky/ToS-violating "auto-tap" tricks se jo aapka number ban bhi kara sakte hain — isliye is app me wo nahi rakha gaya.

## Category & Photos kaise manage karein
- "Manage Categories & Photos" button se naya category banayein (jaise "Birthday", "Wedding")
- Category kholkar Google Drive photo links add karein — jitne chahe utne
- **Zaroori:** Har Drive photo ko "Anyone with the link → Viewer" access set karna hoga, warna app download nahi kar payega
- Sent History me har send ka record milega (kis number par, kab, kitni photos)

## Google Drive link kaise share karein (sirf ek baar setup)
1. Google Drive me photo par right-click → Share
2. "General access" ko "Anyone with the link" karein, role "Viewer"
3. "Copy link" karke us link ko app me category ke andar paste karein

## APK online/cloud me banana (Android Studio install kiye bina) — Easiest tarika

Coding ya Android Studio ki zaroorat nahi. GitHub ka free "Actions" service is project ko cloud me build karke aapko seedha APK file de dega.

1. **github.com** par jaake free account banayein (agar pehle se nahi hai)
2. Login karke top-right "+" icon → **New repository** → naam de dein jaise `manha-decor-sender` → **Public** ya **Private** koi bhi → **Create repository**
3. Jo `ManhaDecorSender.zip` maine diya hai, usko apne computer/phone me **extract/unzip** kar lein
4. GitHub repo ke page par **"uploading an existing file"** link par click karein → extract ki hui `ManhaDecorSender` folder ke andar ki saari files aur sub-folders ko select karke wahan **drag-and-drop** kar dein (poora folder structure ek saath upload ho jayega) → niche **Commit changes** button dabayein
5. Upload complete hone ke baad repo ke top par **"Actions"** tab par click karein
6. Ek build workflow apne aap start ho jayegi ("Build Debug APK"). Agar start na ho to left side "Build Debug APK" par click karke **"Run workflow"** button dabayein
7. **3-5 minute** wait karein jab tak build ka green tick ✅ na aa jaye
8. Us completed run par click karein → sabse niche **"Artifacts"** section me **"ManhaDecorSender-debug-apk"** milega → usko download kar lein
9. Downloaded zip ko extract karein — andar `app-debug.apk` milegi
10. Yeh APK apne Android phone me bhejein (WhatsApp/Google Drive/email se) aur install kar lein (agar "unknown apps" ki warning aaye to allow kar dein — yeh normal hai kyunki APK Play Store se nahi hai)

Bas itna hi — na coding, na Android Studio, na computer setup. Poora build GitHub ke server par hota hai.

## Alternative: Android Studio se (agar computer par karna ho)

Yeh ek complete Android Studio (Kotlin) project hai. Aapko pehle bhi Android Studio use kiya hai, isliye steps simple hain:

1. Android Studio kholein → **Open** → is folder (`ManhaDecorSender`) ko select karein
2. Gradle sync hone dein (pehli baar internet chahiye hoga dependencies download karne ke liye)
3. Top menu me **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Build complete hone ke baad "locate" link se APK milega: `app/build/outputs/apk/debug/app-debug.apk`
5. Us APK ko apne Android phone me transfer karke install kar lein (Settings → allow install from unknown sources agar poochhe)

Agar Android Studio "Gradle JDK" ke baare me poochhe, JDK 17 select karein.

## Technical notes (future reference ke liye)
- Local database: Room (SQLite) — categories aur photo links offline save hote hain
- Google Drive files download hote hain OkHttp se, phir WhatsApp ko `FileProvider` ke through secure URI diya jata hai
- WhatsApp ka specific number par seedha khulna `jid` intent extra se hota hai — yeh number contacts me saved na ho tab bhi kaam karta hai
- Agar future me photos bahut zyada (jaise 15+ per category) hongi ya bade size ki hongi, download me thoda time lag sakta hai — progress dikhaya jata hai

## Agla step jo aap chahenge (optional, future)
- WhatsApp Business API integrate karke **true one-click, zero-tap** sending — iske liye Meta business verification aur per-message cost lagta hai
- Multiple categories ek saath ek client ko bhejna
- Drive folder link se saari photos automatically fetch karna (abhi per-photo link add karna padta hai)

Bataiye agar in me se koi feature chahiye — add kar denge.
