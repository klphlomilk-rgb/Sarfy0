package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BudgetDialog
import com.example.ui.components.CategoryBreakdownBar
import com.example.ui.components.CategorySelectorRow
import com.example.ui.components.DashboardCards
import com.example.ui.components.ExpenseHistoryItem
import com.example.ui.components.NumericKeypad
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SupportUsdtDialog
import com.example.ui.theme.SarfyTheme
import com.example.ui.util.AppStrings
import com.example.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()
            val appStrings by viewModel.appStrings.collectAsStateWithLifecycle()
            val layoutDirection = if (appStrings.isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            SarfyTheme(paletteName = themePalette) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        viewModel.toastEvent.collectLatest { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    SarfyMainScreen(viewModel = viewModel, strings = appStrings)
                }
            }
        }
    }
}

@Composable
fun SarfyMainScreen(
    viewModel: ExpenseViewModel,
    strings: AppStrings
) {
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val dashboardTotals by viewModel.dashboardTotals.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val amountInput by viewModel.amountInput.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val noteInput by viewModel.noteInput.collectAsStateWithLifecycle()
    val isProActivated by viewModel.isProActivated.collectAsStateWithLifecycle()
    val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()

    val showSupportDialog by viewModel.showSupportDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showBudgetDialog by viewModel.showBudgetDialog.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SarfyTopBar(
                isProActivated = isProActivated,
                strings = strings,
                onSupportClick = { viewModel.setSupportDialogVisible(true) },
                onSettingsClick = { viewModel.setSettingsDialogVisible(true) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Dashboard Cards (Today, Week, Month Spending + Monthly Budget Progress Bar)
            item(key = "dashboard_section") {
                DashboardCards(
                    totals = dashboardTotals,
                    currencySymbol = currencySymbol,
                    monthlyBudget = monthlyBudget,
                    strings = strings,
                    onBudgetClick = { viewModel.setBudgetDialogVisible(true) }
                )
            }

            // 2. Category Consumption Breakdown Bar
            item(key = "breakdown_section") {
                CategoryBreakdownBar(
                    percentages = dashboardTotals.categoryPercentages,
                    totals = dashboardTotals.categoryTotals,
                    currencySymbol = currencySymbol,
                    strings = strings
                )
            }

            // 3. Lightning Fast Input Section: Amount Display + Numeric Keypad
            item(key = "keypad_section") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Category Selector Row
                        CategorySelectorRow(
                            selectedCategory = selectedCategory,
                            onCategorySelect = viewModel::onCategorySelect,
                            note = noteInput,
                            onNoteChange = viewModel::onNoteChange,
                            strings = strings
                        )

                        // Keypad with instant Save action
                        NumericKeypad(
                            amount = amountInput,
                            currencySymbol = currencySymbol,
                            strings = strings,
                            onKeyPress = viewModel::onKeyPress,
                            onSaveClick = viewModel::onSaveExpense
                        )
                    }
                }
            }

            // 4. Recent Expenses History Header
            item(key = "history_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.recentExpenses,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (allExpenses.isNotEmpty()) {
                        Text(
                            text = strings.swipeToDelete(allExpenses.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // 5. Empty State or Expense Items
            if (allExpenses.isEmpty()) {
                item(key = "history_empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "💳",
                                fontSize = 34.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.noExpensesYet,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.noExpensesSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = allExpenses,
                    key = { it.id }
                ) { expense ->
                    ExpenseHistoryItem(
                        expense = expense,
                        currencySymbol = currencySymbol,
                        strings = strings,
                        onDelete = viewModel::onDeleteExpense
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Monthly Budget Dialog
    if (showBudgetDialog) {
        BudgetDialog(
            currentBudget = monthlyBudget,
            currencySymbol = currencySymbol,
            strings = strings,
            onSaveBudget = viewModel::updateMonthlyBudget,
            onDismiss = { viewModel.setBudgetDialogVisible(false) }
        )
    }

    // Support Modal
    if (showSupportDialog) {
        SupportUsdtDialog(
            isProActivated = isProActivated,
            strings = strings,
            onDismiss = { viewModel.setSupportDialogVisible(false) },
            onActivateHonor = viewModel::activateProHonor
        )
    }

    // Settings Modal
    if (showSettingsDialog) {
        SettingsDialog(
            isProActivated = isProActivated,
            currentCurrency = currencySymbol,
            currentTheme = themePalette,
            currentLanguage = appLanguage,
            monthlyBudget = monthlyBudget,
            strings = strings,
            onCurrencySelect = viewModel::updateCurrency,
            onThemeSelect = viewModel::updateTheme,
            onLanguageSelect = viewModel::updateLanguage,
            onOpenBudgetDialog = { viewModel.setBudgetDialogVisible(true) },
            onOpenSupportDialog = {
                viewModel.setSettingsDialogVisible(false)
                viewModel.setSupportDialogVisible(true)
            },
            onGetCsvContent = viewModel::getCsvContent,
            onDismiss = { viewModel.setSettingsDialogVisible(false) }
        )
    }
}

@Composable
fun SarfyTopBar(
    isProActivated: Boolean,
    strings: AppStrings,
    onSupportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Tagline
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.appName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "⚡",
                    fontSize = 18.sp
                )
            }
            Text(
                text = strings.appTagline,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Action Buttons: Support ($0.5) & Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Support / Pro Badge Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isProActivated) MaterialTheme.colorScheme.primaryContainer else Color(0xFFE91E63).copy(alpha = 0.12f))
                    .clickable { onSupportClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("topbar_support_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isProActivated) Icons.Default.Verified else Icons.Default.Favorite,
                        contentDescription = strings.supportBadge,
                        tint = if (isProActivated) MaterialTheme.colorScheme.primary else Color(0xFFE91E63),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isProActivated) strings.proBadge else strings.supportBadge,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = if (isProActivated) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFFE91E63)
                    )
                }
            }

            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .testTag("topbar_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = strings.settingsTitle,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
