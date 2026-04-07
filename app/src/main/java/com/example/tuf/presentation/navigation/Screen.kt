package com.example.tuf.presentation.navigation

/**
 * Defines all navigation routes in the Finance Manager app.
 * Use [Screen.route] to navigate and [Screen.createRoute] for parameterized routes.
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Analytics : Screen("analytics")
    object Budget : Screen("budget")
    object Recurring : Screen("recurring")
    object Settings : Screen("settings")
    object Search : Screen("search")
    object Categories : Screen("categories")

    object AddTransaction : Screen("add_transaction?type={type}&transactionId={transactionId}") {
        fun createRoute(type: String = "EXPENSE", transactionId: Long = -1L): String =
            "add_transaction?type=$type&transactionId=$transactionId"
    }

    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Long): String = "transaction_detail/$transactionId"
    }
}
