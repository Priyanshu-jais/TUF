# TUF - Total User Finance Manager 🚀

A premium, offline-first personal finance management application built with modern Android technologies. **TUF** helps you track expenses, manage budgets, and gain deep insights into your financial health with a stunning Material 3 interface.

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=google&logoColor=white)

---

## ✨ Key Features

- **📊 Advanced Analytics**: Visualize your spending with weekly bar charts, 6-month trends, and category breakdown pie charts.
- **💰 Budget Management**: Set monthly limits for specific categories and get real-time progress tracking with "Over Budget" alerts.
- **🔄 Recurring Transactions**: Automate regular income and expenses (Daily, Weekly, Monthly, Yearly).
- **🛡️ Financial Health Score**: A unique scoring algorithm based on your savings rate, budget adherence, and regularity.
- **🌓 Adaptive UI**: Full support for High-Contrast Dark Mode and Vibrant Light Mode.
- **📥 CSV Export**: Export all your transaction data to CSV for external analysis.
- **🛡️ Privacy First**: Completely offline-first! Your financial data never leaves your device.

---

## 🛠️ Tech Stack

- **UI**: 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) for a reactive, modern interface.
- **Dependency Injection**: [Koin](https://insert-koin.io/) - Lightweight and powerful DI.
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) - Robust SQLite abstraction.
- **State Management**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/).
- **Navigation**: Compose Navigation with Type-Safe arguments.
- **Networking**: Mocked domain layer (ready for API integration).
- **Design System**: Material 3 with customized Typography (Nunito & Sora) and Spacing.

---

## 📂 Folder Structure (Clean Architecture)

```bash
com.example.tuf/
├── 🏰 core/                # Base classes, extensions, and common utilities
├── 🧱 data/                # Data access (Room, DataStore, Repository Impls)
│   ├── local/              # Room DB, TypeConverters, DAOs
│   └── repository/         # Implementation of domain repositories
├── 🏛️ domain/              # Business Logic (Pure Kotlin)
│   ├── model/              # Domain entities (Transaction, Category, Budget)
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Specific business actions (AddTransaction, GetAnalytics)
├── 📱 presentation/        # UI Layer (Jetpack Compose)
│   ├── components/         # Reusable UI elements (BalanceCard, TransactionItem)
│   └── screens/            # Feature-specific screens (Dashboard, Analytics, etc.)
└── 🎨 ui/theme/            # Custom Material3 Design System (Colors, Typography)
```

---

## ⚙️ Project Setup

### Prerequisites
- Android Studio Ladybug (2024.2.1) or later.
- JDK 17.
- Minimum SDK: 24 (Android 7.0).

### Build Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/TUF.git
   ```
2. Open in Android Studio.
3. Sync project with Gradle files.
4. Run the app on an emulator or physical device.

---

## 💎 Premium Design Aesthetics

The app features:
- **Glassmorphic Elements**: Subtle transparencies in cards and backgrounds.
- **Animated Progress Rings**: Interactive health score visualizations.
- **Bouncy Interactions**: Spring animations for category selections.
- **Custom Fonts**: Nunito for readability and Sora for bold display elements.

---

## 📜 License
*Designed and Developed by Priyanshu Jaiswal.*
