<div align="center">

# SugarCare 🩺
### Glucose, Meals & Balance

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase)](https://firebase.google.com)
[![Material3](https://img.shields.io/badge/Design-Material%203-757575?style=flat&logo=materialdesign)](https://m3.material.io)

**A mobile companion app for diabetes patients — helping them track glucose, manage meals, medications, stay connected with emergency contacts, and take on the 90-Day No Sugar Challenge.**

</div>

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **Authentication** | Email/Password · Google · Facebook via Firebase Auth |
| 📊 **Glucose Tracker** | Log readings with notes, time & meal context |
| 🍽️ **Meal Plan** | Personalized meal suggestions for diabetic diet |
| 💊 **Medications** | Manage medications, mark taken / missed |
| 📈 **Weekly Analytics** | Animated bar chart of 7-day glucose averages |
| 🤖 **AI ChatBot** | Gemini-powered sugar health assistant |
| 🚨 **Emergency Contacts** | Add & call emergency contacts with one tap |
| 👤 **Profile** | Edit personal details, avatar initials |
| 🌙 **Dark Mode** | Full dark/light theme toggle from Profile screen |
| 💫 **Splash Screen** | Animated SugarCare logo on launch |
| 🏆 **90-Day No Sugar Challenge** | Daily commitment tracker with calendar & progress ring |

---

## 🏆 90-Day No Sugar Challenge

A dedicated feature to help users build a sugar-free habit over 90 days.

- 📅 **Pick your start date** — choose when your challenge begins
- ✅ **Daily check-in** — mark each day as Sugar-Free or Had Sugar
- 🔵 **Progress ring** — circular icon showing completion % (e.g. 45/90)
- 📆 **Calendar view** — 90-day grid with color-coded days
  - 🟢 Green = sugar-free day
  - 🔴 Red = had sugar
  - ⚪ Gray = not yet logged
- 🔥 **Streak counter** — consecutive sugar-free days
- ☁️ **Firebase sync** — progress saved in real-time to Firestore

---

## 🏗️ Architecture

```
MVVM + Repository Pattern
─────────────────────────
UI Layer          →  Jetpack Compose Screens + Navigation Host
ViewModel Layer   →  AuthViewModel · ProfileViewModel · ChatViewModel · ChallengeViewModel
Repository Layer  →  ProfileRepo · EmergencyContactRepository · ChallengeRepository
Data Layer        →  Firebase Firestore · Firebase Auth
```

**Package root:** `com.example.sugercare`

```
com.example.sugercare
├── navigation/           # SugerCareHost — NavHost + Screen sealed class
├── ui/
│   ├── theme/            # Color.kt · Theme.kt · Typography.kt
│   │   └── screens/      # All screen Composables incl. ChallengeScreen
│   ├── components/       # PrimaryButton · BottomNavBar · ProfilePicture…
│   └── screens/          # SplashScreen · PlaceholderScreens
├── viewModels/           # AuthViewModel · ProfileViewModel · ChatViewModel · ChallengeViewModel
├── profileRepo/          # ProfileRepo interface + implementation
├── Authentication/       # AuthManager — Google, Facebook, Email logic
├── crud/                 # Tracker.kt + SugarViewModel (Glucose CRUD)
└── utils/                # VibrationUtils
```

---

## 🗄️ Firestore Data Structure

```
users/{uid}/
├── glucoseReadings/{id}       # glucoseLevel, mealContext, notes, timestamp
├── emergencyContacts/{id}     # name, phone, relation, isPrimary
├── medicationLogs/{id}        # medName, dosage, taken, scheduledAt
└── challenge/current          # startDate, days: Map<String, Boolean>
                               # key = "YYYY-MM-DD", value = sugarFree (true/false)
```

> **Note:** Firebase Storage is **not used** in this project. Profile pictures display as initials avatars.

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Navigation:** Navigation Compose `2.8.5`
- **Backend:** Firebase Auth + Firestore *(Storage removed)*
- **Architecture:** MVVM + Repository
- **Async:** Kotlin Coroutines + Flow
- **Charts:** YCharts
- **Image Loading:** Coil
- **Min SDK:** 34 (Android 14)
- **Target SDK:** 35 (Android 15)

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or later
- JDK **17+**
- Android SDK **API 34+**
- Firebase account with access to `sugarcare-d30af` project

### 1. Clone the repo

```bash
git clone https://github.com/Mohamed-AB5/SugarCare.git
cd SugarCare
git checkout Authentication-branch
```

### 2. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com) → project `sugarcare-d30af`
2. Download `google-services.json` → **Project Settings → Your Apps**
3. Place at `app/google-services.json`
4. Enable **Authentication**: Email/Password, Google, Facebook
5. Enable **Cloud Firestore** (test mode)
6. Add **SHA-1** fingerprint → Project Settings → Your App

**Firestore Security Rules:**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }
  }
}
```

### 3. Build & Run

```
File → Open → SugarCare folder
Wait for Gradle sync
Click ▶ Run  or  Shift + F10
```

---

## ⚠️ Troubleshooting

| Problem | Solution |
|---|---|
| `google-services.json` missing | Place at `app/google-services.json` |
| Firebase crash on launch | Build → **Clean Project** → **Rebuild** |
| Gradle sync fails | **File → Invalidate Caches → Restart** |
| Google Sign-In fails | Add SHA-1 to Firebase Console |
| Challenge not saving | Check Firestore Security Rules |
| ChatScreen API 35 warning | Run on Android 15 or add `@Suppress("NewApi")` |

---

<div align="center">
Made with ❤️ for diabetes patients
</div>
