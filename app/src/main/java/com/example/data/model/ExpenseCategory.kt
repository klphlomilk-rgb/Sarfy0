package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class ExpenseCategory(
    val id: String,
    val titleArabic: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val badgeColor: Color
) {
    FOOD("FOOD", "طعام", "Food", "🍔", Color(0xFFFF7043)),
    TRANSPORT("TRANSPORT", "مواصلات", "Transport", "🚗", Color(0xFF42A5F5)),
    BILLS("BILLS", "فواتير", "Bills", "💡", Color(0xFFFFCA28)),
    ENTERTAINMENT("ENTERTAINMENT", "ترفيه", "Entertainment", "🎮", Color(0xFFAB47BC)),
    OTHER("OTHER", "أخرى", "Other", "📦", Color(0xFF78909C));

    fun getTitle(isArabic: Boolean): String {
        return if (isArabic) titleArabic else titleEnglish
    }

    companion object {
        fun fromId(id: String): ExpenseCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
