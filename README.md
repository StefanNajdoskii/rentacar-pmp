# RentaCar MK
A mobile car rental platform for Macedonia, built with Firebase backend. Android app in Kotlin + Jetpack Compose with Material 3 design.

Repository: https://github.com/StefanNajdoskii/rentacar-pmp

## Features
- Multi-language support (Macedonian / English)
- Dark/Light theme toggle
- Firebase Authentication (Email/Password, Google, Anonymous)
- Car browsing and booking
- Booking history
- User profile management
- Firebase Firestore for data storage
- Firebase Cloud Messaging for notifications
- Firebase Analytics

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Repository pattern
- **Backend**: Firebase (Auth, Firestore, Storage, Messaging, Analytics)
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Navigation**: Navigation Compose
- **Build**: Gradle 8.4, JDK 17

## Prerequisites
- Android Studio (latest stable)
- JDK 17
- Android SDK 34+
- Firebase project with google-services.json

## Setup
1. Clone the repository:
```bash
git clone https://github.com/StefanNajdoskii/rentacar-pmp.git
cd rentacar-pmp
```

2. Add your `google-services.json` to `app/` folder

3. Open in Android Studio and sync Gradle

4. Run on emulator or device

## Firebase Setup
Enable these services in Firebase Console:
- Authentication (Email/Password, Google, Anonymous)
- Firestore Database (Europe region)
- Cloud Messaging
- Analytics

## Project Structure
RentaCar/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rentacar/
│   │   │   │   ├── auth/         # Login & Register
│   │   │   │   ├── cars/         # Car listing & details
│   │   │   │   ├── booking/      # Booking flow
│   │   │   │   ├── profile/      # User profile & settings
│   │   │   │   ├── data/         # Repositories & DAOs
│   │   │   │   ├── model/        # Data models
│   │   │   │   └── adapters/     # RecyclerView adapters
│   │   │   └── res/
│   │   │       ├── values/       # English strings
│   │   │       ├── values-mk/    # Macedonian strings
│   │   │       └── layout/       # XML layouts
│   └── build.gradle
└── build.gradle
## License

FIKT Summer Semester 2026 Project

## Author
Stefan Najdoski
