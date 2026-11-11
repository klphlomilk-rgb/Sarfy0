package com.example.data.repository

import com.example.data.local.ExpenseDao
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun insert(amount: Double, category: String, note: String = ""): Long {
        val expense = ExpenseEntity(
            amount = amount,
            category = category,
            note = note.trim(),
            timestamp = System.currentTimeMillis()
        )
        return expenseDao.insertExpense(expense)
    }

    suspend fun delete(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun clearAll() {
        expenseDao.clearAll()
    }

    fun exportToCsv(expenses: List<ExpenseEntity>, currency: String, isArabic: Boolean): String {
        val locale = if (isArabic) Locale("ar") else Locale.US
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
        val sb = StringBuilder()
        if (isArabic) {
            sb.append("ID,المبلغ,العملة,الفئة,الملاحظات,التاريخ والوقت\n")
        } else {
            sb.append("ID,Amount,Currency,Category,Notes,Date & Time\n")
        }
        for (item in expenses) {
            val category = ExpenseCategory.fromId(item.category)
            val categoryTitle = category.getTitle(isArabic)
            val formattedDate = dateFormat.format(Date(item.timestamp))
            val cleanNote = item.note.replace(",", " - ").replace("\n", " ")
            sb.append("${item.id},${item.amount},$currency,$categoryTitle,\"$cleanNote\",$formattedDate\n")
        }
        return sb.toString()
    }
}
