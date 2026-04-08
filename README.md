# TUF - Total User Finance Manager 🚀

A premium, offline-first personal finance management application built with modern Android technologies. **TUF** helps you track expenses, manage budgets, and gain deep insights into your financial health with a stunning Material 3 interface.

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=google&logoColor=white)

---

## 🔥 Features & How to Use Them

### 1. 📊 Smart Interactive Dashboard
Your core financial health at a glance.
- **How to use**: Simply open the app. The Home Dashboard aggregates your current month's spending limits, gives you a Financial Health score, and lists your most recent incomes and expenses.
- **Action**: Tap on any recent transaction directly from the dashboard to pop into the Edit/Delete Detail Screen!

### 2. 💱 Global Transactions & Swipe-Gestures
Full history of every rupee that comes in or goes out.
- **How to use**: Tap the `Transactions` tab on the Bottom Navigation Bar.
- **Action**: 
  - To **Add**: Tap the floating `+` button in the bottom right corner and choose `Add Income` or `Add Expense`.
  - To **Quick Delete**: Swipe any list item to the left and tap the glowing red trash can.
  - To **Quick Edit**: Swipe any list item to the right.
  - To **View**: Tap the center of the item to enter the detailed Breakdown Screen.

### 3. 👥 Group Bill Splitter (Local 'Splitwise' Engine)
An advanced ledger to track trips, dinners, and who owes whom.
- **How to use**: Tap the `Split` pie icon on your Bottom Navigation Bar (or find it in the left Navigation Drawer / Transactions `+` FAB).
- **Action**: 
  - **Create a Group**: Tap `+` and enter the names of participants (e.g., "You, Rahul, Sneha").
  - **Log an Expense**: Tap into a created group, hit the `+` icon, and add the bill amount!
  - **Split Logic**: You can instruct the app to split the dinner **Equally**, or jump to **Unequally** to assign specific costs to specific friends.
  - **Balances**: The top of the Group automatically tallies the active ledger (e.g., "Rahul owes ₹500").

### 4. 📉 Dynamic Budgeting
Prevent overspending before it happens by setting constraints.
- **How to use**: Tap the `Budget` tab on the bottom menu.
- **Action**: Click the `+` to set a limit (e.g., ₹5000 max for 'Food & Dining'). The ring progressively fills. If you click on an existing budget line, a drawer slides up granting you to edit limit amounts or entirely Delete it.

### 5. 🔁 Recurring Transactions
Put Netflix, Spotify, and Rent on Autopilot.
- **How to use**: Open the Left Hamburger Menu (top-left of Dashboard) and tap `Recurring`.
- **Action**: Input an amount, pick the category, and set a frequency (Weekly/Monthly). The App runs a highly-optimized background cron engine on launch that seamlessly auto-detects missed days and automatically processes pending transactions on your behalf!

### 6. 🌓 Smart Aesthetics & Dark Mode
Premium UX responsive to system settings.
- **How to use**: Slide open the left Navigation Drawer, tap the Sun/Moon icon nested at the top header, or let your system's light/dark mode take control smoothly. Navigation routing remains intact during toggles.

### 7. 🔒 Secure Analytics & Offline-First Core
Your data never leaves your phone!
- **How to use**: Tap the `Analytics` tab on the Bottom Bar to view beautiful, interactive pie charts slicing your categorical spend. Absolutely no data hits external clouds—everything is executed efficiently via Room SQLite on the hardware!

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
