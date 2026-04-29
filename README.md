# Rent-a-Car MK

> A native Android application for browsing, booking, and managing car rentals — built with Kotlin, Firebase, and Material 3.

Repository: https://github.com/StefanNajdoskii/rentacar-pmp

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack at a Glance](#tech-stack-at-a-glance)
- [Repository Structure](#repository-structure)
- [Quick Start](#quick-start)
- [Firebase Setup](#firebase-setup)
- [Screenshots](#screenshots)
- [Maintainers](#maintainers)

---

## Overview

Rent-a-Car MK is a fully functional Android rental platform. Users can browse an inventory of vehicles pulled from Cloud Firestore, pick rental dates, confirm bookings, and review their booking history — all from a single bottom-navigation host. The app supports multiple sign-in methods, a dark mode toggle, and full Macedonian localisation.

---

## Features

| Category | Details |
|---|---|
| **Authentication** | Email/password, Google Sign-In, Facebook Login, anonymous guest mode |
| **Car Catalogue** | Real-time Firestore sync, search/filter, spec breakdown (year, seats, fuel, transmission) |
| **Booking** | Date-range picker, availability check, price summary, one-tap confirm |
| **Booking History** | Per-user booking list with status and cancellation support |
| **Profile** | Display-name editing, avatar, dark mode switch, language selector (EN / МК) |
| **Offline Cache** | Room database mirrors car and booking data for offline reads |
| **Push Notifications** | Firebase Cloud Messaging for booking confirmations |
| **Crash Reporting** | Firebase Crashlytics |

---

## Tech Stack at a Glance

| Layer | Library / Tool | Version |
|---|---|---|
| Language | Kotlin | 2.0.21 |
| UI | Material 3, ViewBinding | 1.11.0 |
| Navigation | Navigation Component (Safe Args) | 2.7.7 |
| Architecture | MVVM · LiveData · ViewModel | 2.7.0 |
| Local DB | Room + KSP | 2.6.1 |
| Remote DB | Cloud Firestore | Firebase BoM 32.7.4 |
| Auth | Firebase Auth, Google Play Services Auth, Facebook SDK | — |
| Notifications | Firebase Cloud Messaging | — |
| Image Loading | Glide | 4.16.0 |
| Async | Kotlin Coroutines | 1.7.3 |
| Build | AGP 8.7.2, minSdk 24, targetSdk 34 | — |

---

## Repository Structure

```
RentaCar/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rentacar/
│   │   │   │   ├── auth/           # Login & Register activities
│   │   │   │   ├── cars/           # Car listing & detail fragments + ViewModels
│   │   │   │   ├── booking/        # Booking flow, history fragment + ViewModels
│   │   │   │   ├── profile/        # User profile & settings fragment + ViewModel
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/      # Room database, DAOs, entities
│   │   │   │   │   ├── remote/     # Firestore repository
│   │   │   │   │   └── repository/ # Car & Booking repositories
│   │   │   │   ├── model/          # Data models (Car, Booking)
│   │   │   │   ├── adapters/       # RecyclerView adapters
│   │   │   │   ├── messaging/      # FCM service
│   │   │   │   ├── utils/          # Mappers and helpers
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── RentACarApp.kt  # Application class (locale + night mode init)
│   │   │   └── res/
│   │   │       ├── layout/         # XML layouts (portrait + land variants)
│   │   │       ├── navigation/     # nav_graph.xml
│   │   │       ├── values/         # English strings, themes, colors
│   │   │       ├── values-mk/      # Macedonian strings
│   │   │       └── values-night/   # Dark mode color overrides
│   └── build.gradle
├── google-services.json            # Firebase config — not committed to repo
├── .gitignore
└── README.md
```

---

## Quick Start

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11
- An Android emulator (API 24+) or physical device
- Firebase project with `google-services.json`

### 1 — Clone the repo

```bash
git clone https://github.com/StefanNajdoskii/rentacar-pmp.git
cd rentacar-pmp
```

### 2 — Add Firebase config

Place your `google-services.json` inside `app/`. See [Firebase Setup](#firebase-setup) for how to obtain it.

### 3 — Set 3rd-party credentials

Open `app/src/main/res/values/strings.xml` and replace the placeholder values:

```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID</string>
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
<string name="fb_login_protocol_scheme">fbYOUR_FACEBOOK_APP_ID</string>
```

### 4 — Build & run on emulator

```bash
# Sync Gradle, then run:
./gradlew installDebug
```

Or press **Run ▶** in Android Studio with an AVD selected (Pixel 6 · API 34 recommended).

> **Tip — Google Sign-In on emulator:** Create the AVD with the **Google Play** system image so `play-services-auth` can resolve the account picker. In the emulator settings, sign in to a Google account under **Settings → Accounts**.

> **Tip — Facebook Login on emulator:** Facebook Login requires a device with the Facebook app installed or a real device. On emulator it will open a web-based OAuth flow instead.

---

## Firebase Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Add an Android app with package name `com.rentacar`.
3. Download `google-services.json` and place it in `app/`.
4. Enable the following services in the console:
   - **Authentication** — Email/Password, Google, Facebook, Anonymous
   - **Cloud Firestore** — Firestore Database (Europe region), create a `cars` collection
   - **Cloud Messaging** — no extra config needed
   - **Analytics** — enabled by default
   - **Crashlytics** — enable in the Crashlytics tab

### Firestore `cars` document schema

```
cars/{carId}
  ├── brand:         String
  ├── model:         String
  ├── year:          Number
  ├── pricePerDay:   Number
  ├── seats:         Number
  ├── transmission:  String   // "Manual" | "Automatic"
  ├── fuelType:      String
  ├── location:      String
  ├── imageUrl:      String
  ├── available:     Boolean
  └── description:   String
```

---

## Screenshots

> Screenshots will be added once the app is running on a device or emulator.

| Cars | Car Detail | Booking | History | Profile |
|------|-----------|---------|---------|---------|
| _(coming soon)_ | _(coming soon)_ | _(coming soon)_ | _(coming soon)_ | _(coming soon)_ |

---

## Maintainers

| Name | Role | Period |
|---|---|---|
| Stefan Najdoski | Developer | FIKT Summer Semester 2026 |

---

## License

FIKT Summer Semester 2026 Project
