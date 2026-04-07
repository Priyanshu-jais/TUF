package com.example.tuf.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Helper function for LazyList items that enter with a staggered animation.
 * Each item animates in with a delay proportional to its index.
 *
 * Usage:
 * ```
 * LazyColumn {
 *   staggeredItems(items, key = { it.id }) { item ->
 *       TransactionItem(item)
 *   }
 * }
 * ```
 */
fun <T> LazyListScope.staggeredItems(
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T, index: Int) -> Unit
) {
    items(
        count = items.size,
        key = if (key != null) { index -> key(items[index]) } else null
    ) { index ->
        StaggeredAnimatedItem(index = index) {
            itemContent(items[index], index)
        }
    }
}

@Composable
fun StaggeredAnimatedItem(
    index: Int,
    baseDelay: Int = 50,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index.toLong() * baseDelay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
    ) {
        content()
    }
}
