package com.example

import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.preferences.PreferenceManager
import com.example.ui.util.AppStrings
import com.example.ui.viewmodel.ExpenseViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testExpenseCategoryMapping() {
    val food = ExpenseCategory.fromId("FOOD")
    assertEquals("طعام", food.titleArabic)
    assertEquals("Food", food.titleEnglish)
    assertEquals("🍔", food.iconEmoji)
    assertEquals("طعام", food.getTitle(true))
    assertEquals("Food", food.getTitle(false))

    val transport = ExpenseCategory.fromId("TRANSPORT")
    assertEquals("مواصلات", transport.titleArabic)
    assertEquals("Transport", transport.titleEnglish)
    assertEquals("🚗", transport.iconEmoji)

    val other = ExpenseCategory.fromId("UNKNOWN")
    assertEquals(ExpenseCategory.OTHER, other)
  }

  @Test
  fun testCryptoWalletAddresses() {
    assertEquals(
      "UQBCIoBsuw_odqfrhxSebxZV4v5x2udn7LXEFV9Jn9kw44je",
      ExpenseViewModel.TON_WALLET_ADDRESS
    )
    assertEquals(
      "TPaAWQ1S7Ms4ubD13ZdmEhZGHhsqQsbdWF",
      ExpenseViewModel.TRC20_WALLET_ADDRESS
    )
  }

  @Test
  fun testLanguageDictionary() {
    val arabicStrings = AppStrings.get("AR")
    assertTrue(arabicStrings.isArabic)
    assertEquals("صرفي", arabicStrings.appName)
    assertEquals("الميزانية الشهرية المستهدفة", arabicStrings.monthlyBudgetTitle)

    val englishStrings = AppStrings.get("EN")
    assertTrue(!englishStrings.isArabic)
    assertEquals("Sarfy", englishStrings.appName)
    assertEquals("Monthly Budget Limit", englishStrings.monthlyBudgetTitle)

    val enCurrencies = PreferenceManager.getCurrencies(false)
    assertTrue(enCurrencies.any { it.second.contains("Saudi Riyal") })
    assertTrue(enCurrencies.none { it.second.contains("الريال السعودي") })

    val arCurrencies = PreferenceManager.getCurrencies(true)
    assertTrue(arCurrencies.any { it.second.contains("الريال السعودي") })
  }

  @Test
  fun testMonthlyBudgetCalculation() {
    val budget = 1000.0
    val spendingUnder = 750.0
    val spendingOver = 1200.0

    val progressUnder = (spendingUnder / budget).toFloat()
    val progressOver = (spendingOver / budget).toFloat()

    assertTrue(progressUnder < 1.0f)
    assertTrue(progressOver > 1.0f)
    assertEquals(200.0, spendingOver - budget, 0.001)
  }

  @Test
  fun testExpenseEntityCreation() {
    val entity = ExpenseEntity(
      amount = 45.5,
      category = "FOOD",
      note = "Lunch"
    )
    assertEquals(45.5, entity.amount, 0.001)
    assertEquals("FOOD", entity.category)
    assertEquals("Lunch", entity.note)
    assertTrue(entity.timestamp > 0)
  }
}
