<div align="center">

<img src="app/src/main/res/drawable/ic_logo.xml" width="120" alt="SugarCare Logo"/>

# SugarCare 🩺
### Glucose, Meals & Balance

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat&logo=firebase)](https://firebase.google.com)
[![Material3](https://img.shields.io/badge/Design-Material%203-757575?style=flat&logo=materialdesign)](https://m3.material.io)

**A mobile companion app for diabetes patients — helping them track glucose, manage meals, medications, and stay connected with emergency contacts.**

</div>

---

## 📱 Screenshots

> *Welcome · Home · Glucose Tracker · Medications · Weekly Analytics · Profile (Dark Mode)*

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
| 👤 **Profile** | Edit personal details, profile photo |
| 🌙 **Dark Mode** | Full dark/light theme toggle from Profile screen |
| 💫 **Splash Screen** | Animated SugarCare logo on launch |

---

## 🏗️ Architecture

```
MVVM + Repository Pattern
─────────────────────────
UI Layer          →  Jetpack Compose Screens + Navigation
ViewModel Layer   →  AuthViewModel · ProfileViewModel · ChatViewModel  
Repository Layer  →  ProfileRepo · EmergencyContactRepository
Data Layer        →  Firebase Firestore · Firebase Auth · Firebase Storage
```

**Package root:** `com.example.sugercare`

```
com.example.sugercare
├── navigation/          # SugerCareHost — NavHost + Screen sealed class
├── ui/
│   ├── theme/           # Color.kt · Theme.kt · Typography.kt
│   │   └── screens/     # All screen Composables
│   ├── components/      # Shared UI: PrimaryButton, BottomNavBar, ProfilePicture…
│   └── screens/         # SplashScreen · PlaceholderScreens
├── viewModels/          # AuthViewModel · ProfileViewModel · ChatViewModel
├── profileRepo/         # ProfileRepo interface + implementation
├── Authentication/      # AuthManager — Google, Facebook, Email logic
├── crud/                # Tracker.kt + SugarViewModel (Glucose CRUD)
└── utils/               # VibrationUtils
```

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Navigation:** Navigation Compose `2.8.5`
- **Backend:** Firebase (Auth · Firestore · Storage)
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
2. Download `google-services.json` from **Project Settings → Your Apps**
3. Place it at `app/google-services.json`
4. Enable **Authentication** providers: Email/Password, Google, Facebook
5. Enable **Cloud Firestore** (test mode for development)
6. Add your **SHA-1** fingerprint in Project Settings → Your App (required for Google Sign-In)

### 3. Open in Android Studio

```
File → Open → select the SugarCare folder
Wait for Gradle sync to complete (~2-5 min)
```

### 4. Run

```
Click ▶ Run  or  press Shift + F10
```

> **Recommended:** Run on a physical Android 14+ device for best performance.

---

## 🗄️ Firestore Data Structure

```
users/{uid}/
├── glucoseReadings/{id}     # glucoseLevel, mealContext, notes, timestamp
├── emergencyContacts/{id}   # name, phone, relation, isPrimary
├── medicationLogs/{id}      # medName, dosage, taken, scheduledAt
└── medicalHistory/profile   # bloodType, diabetesType, hba1c, conditions…
```

---

## ⚠️ Troubleshooting

| Problem | Solution |
|---|---|
| `google-services.json` missing | Place at `app/google-services.json` — not the project root |
| Firebase crash on launch | Build → **Clean Project** → **Rebuild Project** |
| Gradle sync fails | **File → Invalidate Caches → Restart** |
| Google Sign-In fails | Add SHA-1 fingerprint to Firebase Console |
| ChatScreen API 35 warning | Run on Android 15 device or add `@Suppress("NewApi")` |

---

## 📄 License

This project is for educational purposes as part of a university graduation project.

---

<div align="center">
Made with ❤️ for diabetes patients
</div>
