package com.example.tuf.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tuf.ui.theme.*

private val screensWithBottomNav = setOf(
    Screen.Dashboard.route,
    Screen.Transactions.route,
    Screen.Analytics.route,
    Screen.Budget.route
)

private val screensWithoutScaffold = setOf(
    Screen.Onboarding.route,
    Screen.AddTransaction.route.substringBefore("?"),
    Screen.TransactionDetail.route.substringBefore("/")
)

/**
 * Root scaffold composable wrapping the navigation graph with bottom bar and drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    startDestination: String,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: ""

    val showBottomBar = bottomNavItems.any { currentRoute.startsWith(it.route) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                navController = navController,
                drawerState = drawerState,
                currentRoute = currentRoute
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    AppBottomNavBar(navController = navController, currentRoute = currentRoute)
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    onThemeToggle = onThemeToggle
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavBar(navController: NavController, currentRoute: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    currentRoute: String
) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(GradientStartLight, GradientEndLight)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "💰 Finance Manager",
                    style = MaterialTheme.typography.headlineMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track every rupee",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val drawerItems = listOf(
            Triple(Screen.Dashboard.route, Icons.Default.Home, "Dashboard"),
            Triple(Screen.Transactions.route, Icons.Default.SwapHoriz, "Transactions"),
            Triple(Screen.Analytics.route, Icons.Default.BarChart, "Analytics"),
            Triple(Screen.Budget.route, Icons.Default.PieChart, "Budget"),
            Triple(Screen.Recurring.route, Icons.Default.Autorenew, "Recurring"),
            Triple(Screen.Search.route, Icons.Default.Search, "Search"),
            Triple(Screen.Categories.route, Icons.Default.Category, "Categories"),
            Triple(Screen.Settings.route, Icons.Default.Settings, "Settings")
        )

        drawerItems.forEach { (route, icon, label) ->
            val isSelected = currentRoute.startsWith(route)
            NavigationDrawerItem(
                label = { Text(label) },
                selected = isSelected,
                icon = { Icon(icon, contentDescription = label) },
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
