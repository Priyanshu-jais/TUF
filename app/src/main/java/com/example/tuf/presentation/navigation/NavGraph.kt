package com.example.tuf.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tuf.presentation.screens.analytics.AnalyticsScreen
import com.example.tuf.presentation.screens.budget.BudgetScreen
import com.example.tuf.presentation.screens.categories.CategoryScreen
import com.example.tuf.presentation.screens.dashboard.DashboardScreen
import com.example.tuf.presentation.screens.onboarding.OnboardingScreen
import com.example.tuf.presentation.screens.recurring.RecurringScreen
import com.example.tuf.presentation.screens.search.SearchScreen
import com.example.tuf.presentation.screens.settings.SettingsScreen
import com.example.tuf.presentation.screens.transactions.AddTransactionScreen
import com.example.tuf.presentation.screens.transactions.TransactionDetailScreen
import com.example.tuf.presentation.screens.transactions.TransactionListScreen
import com.example.tuf.presentation.screens.auth.LoginScreen
import com.example.tuf.presentation.screens.profile.ProfileScreen
import com.example.tuf.presentation.screens.split.GroupListScreen
import com.example.tuf.presentation.screens.split.GroupDetailScreen
import com.example.tuf.presentation.screens.split.AddGroupExpenseScreen
import com.example.tuf.data.local.DataStoreManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val TRANSITION_DURATION = 300

/**
 * Main navigation graph for the Finance Manager app.
 * Uses slide-in/out and fade transitions for premium feel.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onThemeToggle: () -> Unit,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(tween(TRANSITION_DURATION))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val scope = androidx.compose.runtime.rememberCoroutineScope()
            val dataStoreManager: DataStoreManager = koinInject()
            val isOnboardingCompleted by dataStoreManager.isOnboardingCompleted.collectAsState(initial = false)
            
            LoginScreen(
                onLoginSuccess = { userName ->
                    scope.launch {
                        dataStoreManager.setUserName(userName)
                        dataStoreManager.setLoggedIn(true)
                        if (isOnboardingCompleted) {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { type ->
                    navController.navigate(Screen.AddTransaction.createRoute(type))
                },
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                onNavigateToAllBudgets = { navController.navigate(Screen.Budget.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.TransactionDetail.createRoute(id)) },
                onThemeToggle = onThemeToggle,
                onMenuClick = onMenuClick
            )
        }

        composable(Screen.Transactions.route) {
            TransactionListScreen(
                onNavigateToAddTransaction = { type ->
                    navController.navigate(Screen.AddTransaction.createRoute(type))
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId = id))
                },
                onNavigateToGroupSplit = {
                    navController.navigate(Screen.SplitList.route)
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("type") { defaultValue = "EXPENSE"; type = NavType.StringType },
                navArgument("transactionId") { defaultValue = -1L; type = NavType.LongType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "EXPENSE"
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: -1L
            AddTransactionScreen(
                initialType = type,
                transactionId = transactionId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: -1L
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId = id))
                }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.Budget.route) {
            BudgetScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.Recurring.route) {
            RecurringScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddRecurring = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onThemeToggle = onThemeToggle
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.TransactionDetail.createRoute(id))
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoryScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.SplitList.route) {
            GroupListScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToGroup = { id -> navController.navigate(Screen.SplitDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.SplitDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: -1L
            GroupDetailScreen(
                groupId = groupId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddExpense = { id -> navController.navigate(Screen.AddSplit.createRoute(id)) }
            )
        }

        composable(
            route = Screen.AddSplit.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: -1L
            AddGroupExpenseScreen(
                groupId = groupId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
