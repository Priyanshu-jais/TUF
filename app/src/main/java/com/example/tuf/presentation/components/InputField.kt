package com.example.tuf.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.ui.theme.Spacing

/**
 * A reusable OutlinedTextField with label, error message support, and animation.
 *
 * @param value Current text value.
 * @param onValueChange Called when text changes.
 * @param label Field label.
 * @param placeholder Optional placeholder text.
 * @param errorMessage Error message to show below — null means no error.
 * @param trailingIcon Optional trailing composable.
 * @param modifier Layout modifier.
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = 1,
    enabled: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
            isError = errorMessage != null,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            maxLines = maxLines,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.md, top = 2.dp)
            )
        }
    }
}
