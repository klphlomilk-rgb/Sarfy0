package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory
import com.example.ui.util.AppStrings
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryBreakdownBar(
    percentages: Map<ExpenseCategory, Float>,
    totals: Map<ExpenseCategory, Double>,
    currencySymbol: String,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val activeCategories = ExpenseCategory.entries.filter { (totals[it] ?: 0.0) > 0 }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_breakdown_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.monthDistribution,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (activeCategories.isNotEmpty()) {
                Text(
                    text = strings.activeCategoriesCount(activeCategories.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (activeCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = strings.noExpensesThisMonth,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Multi-segment progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                activeCategories.forEach { category ->
                    val fraction = percentages[category] ?: 0f
                    val animatedWeight by animateFloatAsState(
                        targetValue = fraction.coerceAtLeast(0.01f),
                        animationSpec = tween(500),
                        label = "segment_${category.id}"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(animatedWeight)
                            .background(category.badgeColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Legend Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeCategories.forEach { category ->
                    val pct = ((percentages[category] ?: 0f) * 100).toInt()
                    val amount = totals[category] ?: 0.0
                    val formattedAmount = if (amount % 1.0 == 0.0) {
                        String.format(Locale.US, "%.0f", amount)
                    } else {
                        String.format(Locale.US, "%.1f", amount)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(category.badgeColor)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "${category.iconEmoji} ${category.getTitle(strings.isArabic)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "$pct% ($formattedAmount $currencySymbol)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
