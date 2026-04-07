package com.example.tuf.core.extensions

/** Parses #hashtags from a note string and returns them as a list (without the # prefix). */
fun String.parseHashTags(): List<String> {
    val regex = Regex("#(\\w+)")
    return regex.findAll(this).map { it.groupValues[1] }.toList()
}

/** Returns true if the string contains a specific #tag (case-insensitive). */
fun String.containsTag(tag: String): Boolean {
    return parseHashTags().any { it.equals(tag, ignoreCase = true) }
}

/** Capitalizes the first letter of each word in the string. */
fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }

/** Truncates the string to [maxLength] characters and appends "..." if truncated. */
fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) "${take(maxLength)}..." else this
}

/** Returns true if the string is a valid non-empty numeric amount string. */
fun String.isValidAmount(): Boolean {
    return isNotBlank() && toDoubleOrNull()?.let { it > 0 } == true
}

/** Strips all non-numeric characters except decimal point for amount input. */
fun String.toCleanAmountString(): String {
    return filter { it.isDigit() || it == '.' }
}
