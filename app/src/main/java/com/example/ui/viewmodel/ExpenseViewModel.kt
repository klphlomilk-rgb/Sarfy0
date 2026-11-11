package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.preferences.PreferenceManager
import com.example.data.repository.ExpenseRepository
import com.example.ui.util.AppStrings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardTotals(
    val today: Double = 0.0,
    val thisWeek: Double = 0.0,
    val thisMonth: Double = 0.0,
    val categoryPercentages: Map<ExpenseCategory, Float> = emptyMap(),
    val categoryTotals: Map<ExpenseCategory, Double> = emptyMap()
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = ExpenseRepository(database.expenseDao())
    private val preferenceManager = PreferenceManager(application)

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isProActivated: StateFlow<Boolean> = preferenceManager.isProActivated
    val currencySymbol: StateFlow<String> = preferenceManager.currencySymbol
    val themePalette: StateFlow<String> = preferenceManager.themePalette
    val appLanguage: StateFlow<String> = preferenceManager.appLanguage
    val monthlyBudget: StateFlow<Double> = preferenceManager.monthlyBudget

    val appStrings: StateFlow<AppStrings> = appLanguage.map { lang ->
        AppStrings.get(lang)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppStrings.get("SYSTEM"))

    // Input States
    private val _amountInput = MutableStateFlow("0")
    val amountInput: StateFlow<String> = _amountInput.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ExpenseCategory.FOOD)
    val selectedCategory: StateFlow<ExpenseCategory> = _selectedCategory.asStateFlow()

    private val _noteInput = MutableStateFlow("")
    val noteInput: StateFlow<String> = _noteInput.asStateFlow()

    // Dialogs
    private val _showSupportDialog = MutableStateFlow(false)
    val showSupportDialog: StateFlow<Boolean> = _showSupportDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showBudgetDialog = MutableStateFlow(false)
    val showBudgetDialog: StateFlow<Boolean> = _showBudgetDialog.asStateFlow()

    private val _cryptoNetwork = MutableStateFlow("TON")
    val cryptoNetwork: StateFlow<String> = _cryptoNetwork.asStateFlow()

    // Event Messages
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Dashboard calculations
    val dashboardTotals: StateFlow<DashboardTotals> = allExpenses.combine(amountInput) { expenses, _ ->
        computeTotals(expenses)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardTotals())

    private fun computeTotals(expenses: List<ExpenseEntity>): DashboardTotals {
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = todayCalendar.timeInMillis

        val weekCalendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfWeek = weekCalendar.timeInMillis

        val monthCalendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = monthCalendar.timeInMillis

        var todaySum = 0.0
        var weekSum = 0.0
        var monthSum = 0.0
        val categoryTotals = mutableMapOf<ExpenseCategory, Double>()

        for (expense in expenses) {
            val ts = expense.timestamp
            if (ts >= startOfToday) {
                todaySum += expense.amount
            }
            if (ts >= startOfWeek) {
                weekSum += expense.amount
            }
            if (ts >= startOfMonth) {
                monthSum += expense.amount
                val cat = ExpenseCategory.fromId(expense.category)
                categoryTotals[cat] = (categoryTotals[cat] ?: 0.0) + expense.amount
            }
        }

        val percentages = mutableMapOf<ExpenseCategory, Float>()
        if (monthSum > 0) {
            categoryTotals.forEach { (cat, amount) ->
                percentages[cat] = (amount / monthSum).toFloat()
            }
        }

        return DashboardTotals(
            today = todaySum,
            thisWeek = weekSum,
            thisMonth = monthSum,
            categoryPercentages = percentages,
            categoryTotals = categoryTotals
        )
    }

    // Keypad Logic
    fun onKeyPress(key: String) {
        val current = _amountInput.value
        when (key) {
            "C" -> {
                _amountInput.value = "0"
            }
            "DEL" -> {
                if (current.length <= 1) {
                    _amountInput.value = "0"
                } else {
                    _amountInput.value = current.dropLast(1)
                }
            }
            "." -> {
                if (!current.contains(".")) {
                    _amountInput.value = "$current."
                }
            }
            else -> {
                if (current == "0") {
                    _amountInput.value = key
                } else {
                    val dotIndex = current.indexOf('.')
                    if (dotIndex != -1 && current.length - dotIndex > 2) {
                        return
                    }
                    if (current.length < 9) {
                        _amountInput.value = current + key
                    }
                }
            }
        }
    }

    fun onCategorySelect(category: ExpenseCategory) {
        _selectedCategory.value = category
    }

    fun onNoteChange(note: String) {
        _noteInput.value = note
    }

    fun onSaveExpense() {
        val amount = _amountInput.value.toDoubleOrNull() ?: 0.0
        val strings = appStrings.value
        if (amount <= 0.0) {
            viewModelScope.launch {
                _toastEvent.emit(strings.enterValidAmount)
            }
            return
        }

        val category = _selectedCategory.value
        val note = _noteInput.value

        viewModelScope.launch {
            repository.insert(amount, category.id, note)
            _amountInput.value = "0"
            _noteInput.value = ""
            _toastEvent.emit(strings.expenseSaved(category.getTitle(strings.isArabic)))
        }
    }

    fun onDeleteExpense(expense: ExpenseEntity) {
        val strings = appStrings.value
        viewModelScope.launch {
            repository.delete(expense)
            _toastEvent.emit(strings.expenseDeleted)
        }
    }

    fun setSupportDialogVisible(visible: Boolean) {
        _showSupportDialog.value = visible
    }

    fun setSettingsDialogVisible(visible: Boolean) {
        _showSettingsDialog.value = visible
    }

    fun setBudgetDialogVisible(visible: Boolean) {
        _showBudgetDialog.value = visible
    }

    fun setCryptoNetwork(network: String) {
        _cryptoNetwork.value = network
    }

    fun activateProHonor() {
        preferenceManager.setProActivated(true)
        _showSupportDialog.value = false
        val strings = appStrings.value
        viewModelScope.launch {
            _toastEvent.emit(strings.proCelebration)
        }
    }

    fun updateCurrency(symbol: String) {
        preferenceManager.setCurrency(symbol)
        val strings = appStrings.value
        viewModelScope.launch {
            _toastEvent.emit(strings.currencyChanged(symbol))
        }
    }

    fun updateTheme(palette: String) {
        preferenceManager.setThemePalette(palette)
    }

    fun updateLanguage(language: String) {
        preferenceManager.setAppLanguage(language)
    }

    fun updateMonthlyBudget(budget: Double) {
        preferenceManager.setMonthlyBudget(budget)
        val strings = appStrings.value
        viewModelScope.launch {
            if (budget > 0) {
                _toastEvent.emit(strings.budgetUpdatedToast)
            } else {
                _toastEvent.emit(strings.budgetRemovedToast)
            }
        }
    }

    fun getCsvContent(): String {
        return repository.exportToCsv(allExpenses.value, currencySymbol.value, appStrings.value.isArabic)
    }

    companion object {
        const val TRC20_WALLET_ADDRESS = "TPaAWQ1S7Ms4ubD13ZdmEhZGHhsqQsbdWF"
        const val TON_WALLET_ADDRESS = "UQBCIoBsuw_odqfrhxSebxZV4v5x2udn7LXEFV9Jn9kw44je"
    }
}
