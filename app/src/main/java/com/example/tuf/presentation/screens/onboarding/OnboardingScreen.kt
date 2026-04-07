package com.example.tuf.presentation.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuf.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

private val pages = listOf(
    OnboardingPage(
        emoji = "💰",
        title = "Track Every Rupee",
        subtitle = "Effortlessly record your income and expenses. Stay on top of your finances with beautiful insights.",
        gradientStart = Color(0xFF6C63FF),
        gradientEnd = Color(0xFF9B8FFF)
    ),
    OnboardingPage(
        emoji = "🗂️",
        title = "Smart Categories",
        subtitle = "Organize transactions with colorful categories. From food to freelance — everything categorized automatically.",
        gradientStart = Color(0xFFFF6584),
        gradientEnd = Color(0xFFFF8FA3)
    ),
    OnboardingPage(
        emoji = "🏆",
        title = "Set Budgets, Stay Safe",
        subtitle = "Create monthly budgets per category. Get alerts before you overspend and build healthy financial habits.",
        gradientStart = Color(0xFF00C897),
        gradientEnd = Color(0xFF00E5C0)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { onNavigateToDashboard() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex], pageIndex = pageIndex)
        }

        // Skip button
        if (currentPage < pages.size - 1) {
            TextButton(
                onClick = { viewModel.onSkip() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
            ) {
                Text(
                    "Skip",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                pages.indices.forEach { index ->
                    val isSelected = index == currentPage
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) pages[currentPage].gradientStart
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            if (currentPage < pages.size - 1) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pages[currentPage].gradientStart
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Next", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            } else {
                Button(
                    onClick = { viewModel.onGetStarted() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pages[currentPage].gradientStart
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Get Started 🚀", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, pageIndex: Int) {
    val enterTransition = remember { fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        page.gradientStart.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 180.dp)
        ) {
            // Animated emoji
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "emoji_scale"
            )
            Text(
                text = page.emoji,
                fontSize = (80 * scale).sp,
                modifier = Modifier.padding(bottom = Spacing.lg)
            )

            Text(
                text = page.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = page.gradientStart,
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 26.sp
            )
        }
    }
}
