package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.AppStrings
import com.example.ui.viewmodel.DashboardTotals
import java.util.Locale

@Composable
fun DashboardCards(
    totals: DashboardTotals,
    currencySymbol: String,
    monthlyBudget: Double,
    strings: AppStrings,
    onBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main Highlight: Today's Spending
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.todaySpending,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatAmount(totals.today, currencySymbol),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Decorative Today badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = strings.todayBadge,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-metrics: Week & Month in clean side-by-side cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSubCard(
                    title = strings.weekSpending,
                    amount = formatAmount(totals.thisWeek, currencySymbol),
                    modifier = Modifier.weight(1f)
                )

                MetricSubCard(
                    title = strings.monthSpending,
                    amount = formatAmount(totals.thisMonth, currencySymbol),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 🎯 Monthly Budget Section with Dynamic Progress Bar
            MonthlyBudgetBarSection(
                monthSpending = totals.thisMonth,
                monthlyBudget = monthlyBudget,
                currencySymbol = currencySymbol,
                strings = strings,
                onBudgetClick = onBudgetClick
            )
        }
    }
}

@Composable
private fun MonthlyBudgetBarSection(
    monthSpending: Double,
    monthlyBudget: Double,
    currencySymbol: String,
    strings: AppStrings,
    onBudgetClick: () -> Unit
) {
    if (monthlyBudget <= 0.0) {
        // No budget set yet - clickable prompt card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable { onBudgetClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("set_budget_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.budgetNoLimitHint,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = strings.setBudget,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    } else {
        // Budget is set - show progress bar with color change if exceeded
        val isExceeded = monthSpending > monthlyBudget
        val progressFraction = (monthSpending / monthlyBudget).toFloat()
        val percentage = (progressFraction * 100).toInt()

        val warningRed = Color(0xFFE53935)
        val warningAmber = Color(0xFFFFA000)
        val normalGreen = MaterialTheme.colorScheme.primary

        val barColor by animateColorAsState(
            targetValue = when {
                isExceeded -> warningRed
                progressFraction >= 0.85f -> warningAmber
                else -> normalGreen
            },
            label = "budget_bar_color"
        )

        val animatedProgress by animateFloatAsState(
            targetValue = progressFraction.coerceIn(0.01f, 1f),
            animationSpec = tween(500),
            label = "budget_progress"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (isExceeded) warningRed.copy(alpha = 0.5f) else Color.Transparent,
                    RoundedCornerShape(14.dp)
                )
                .clickable { onBudgetClick() }
                .padding(12.dp)
                .testTag("budget_progress_card")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: Title + Target Limit + Edit Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isExceeded) Icons.Default.Warning else Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = barColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.monthlyBudgetTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${formatAmount(monthlyBudget, currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = strings.editBudget,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The Progress Bar (Turns RED if exceeded)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(barColor)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom details: Spent vs Remaining or Exceeded Warning
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Spending ratio
                    Text(
                        text = strings.budgetProgressLabel(
                            percentage,
                            formatAmount(monthSpending, currencySymbol),
                            formatAmount(monthlyBudget, currencySymbol)
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Remaining or Exceeded Badge
                    if (isExceeded) {
                        val exceededDiff = monthSpending - monthlyBudget
                        Text(
                            text = strings.budgetExceededBy(formatAmount(exceededDiff, currencySymbol)),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = warningRed
                        )
                    } else {
                        val remaining = monthlyBudget - monthSpending
                        Text(
                            text = strings.budgetRemaining(formatAmount(remaining, currencySymbol)),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = if (progressFraction >= 0.85f) warningAmber else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricSubCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatAmount(amount: Double, currency: String): String {
    val formatted = if (amount % 1.0 == 0.0) {
        String.format(Locale.US, "%.0f", amount)
    } else {
        String.format(Locale.US, "%.2f", amount)
    }
    return "$formatted $currency"
}
