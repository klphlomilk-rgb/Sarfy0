package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory
import com.example.ui.util.AppStrings

@Composable
fun CategorySelectorRow(
    selectedCategory: ExpenseCategory,
    onCategorySelect: (ExpenseCategory) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    var showNoteField by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.selectCategory,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Optional note toggle button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showNoteField = !showNoteField }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = strings.noteOptional,
                    modifier = Modifier.size(16.dp),
                    tint = if (note.isNotEmpty() || showNoteField) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (note.isNotEmpty()) "${strings.notePrefix}$note" else strings.noteOptional,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = if (note.isNotEmpty() || showNoteField) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Category Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("category_selector_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpenseCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) category.badgeColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                    label = "cat_bg_${category.id}"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) category.badgeColor else Color.Transparent,
                    label = "cat_border_${category.id}"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { onCategorySelect(category) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = category.iconEmoji,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = category.getTitle(strings.isArabic),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Animated Note Input Field
        if (showNoteField) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text(strings.notePlaceholder, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_note_input")
            )
        }
    }
}
