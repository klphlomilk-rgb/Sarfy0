package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sarfy_preferences", Context.MODE_PRIVATE)

    private val _isProActivated = MutableStateFlow(prefs.getBoolean(KEY_PRO_ACTIVATED, false))
    val isProActivated: StateFlow<Boolean> = _isProActivated.asStateFlow()

    private val _currencySymbol = MutableStateFlow(prefs.getString(KEY_CURRENCY, "$") ?: "$")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _themePalette = MutableStateFlow(prefs.getString(KEY_THEME, "EMERALD") ?: "EMERALD")
    val themePalette: StateFlow<String> = _themePalette.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "SYSTEM") ?: "SYSTEM")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _monthlyBudget = MutableStateFlow(prefs.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble())
    val monthlyBudget: StateFlow<Double> = _monthlyBudget.asStateFlow()

    fun setProActivated(activated: Boolean) {
        prefs.edit().putBoolean(KEY_PRO_ACTIVATED, activated).apply()
        _isProActivated.value = activated
    }

    fun setCurrency(symbol: String) {
        prefs.edit().putString(KEY_CURRENCY, symbol).apply()
        _currencySymbol.value = symbol
    }

    fun setThemePalette(palette: String) {
        prefs.edit().putString(KEY_THEME, palette).apply()
        _themePalette.value = palette
    }

    fun setAppLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        _appLanguage.value = language
    }

    fun setMonthlyBudget(limit: Double) {
        prefs.edit().putFloat(KEY_MONTHLY_BUDGET, limit.toFloat()).apply()
        _monthlyBudget.value = limit
    }

    companion object {
        private const val KEY_PRO_ACTIVATED = "key_is_pro_activated"
        private const val KEY_CURRENCY = "key_currency_symbol"
        private const val KEY_THEME = "key_theme_palette"
        private const val KEY_LANGUAGE = "key_app_language"
        private const val KEY_MONTHLY_BUDGET = "key_monthly_budget_limit"

        fun getLanguages(isArabic: Boolean): List<Pair<String, String>> {
            return if (isArabic) {
                listOf(
                    "SYSTEM" to "تلقائي (لغة الجهاز)",
                    "AR" to "العربية",
                    "EN" to "الإنجليزية"
                )
            } else {
                listOf(
                    "SYSTEM" to "System Default",
                    "AR" to "العربية",
                    "EN" to "English"
                )
            }
        }

        fun getCurrencies(isArabic: Boolean): List<Pair<String, String>> {
            return if (isArabic) {
                listOf(
                    "$" to "الدولار الأمريكي ($)",
                    "ر.س" to "الريال السعودي (ر.س)",
                    "ج.م" to "الجنيه المصري (ج.م)",
                    "د.إ" to "الدرهم الإماراتي (د.إ)",
                    "€" to "اليورو (€)",
                    "£" to "الجنيه الإسترليني (£)",
                    "د.ك" to "الدينار الكويتي (د.ك)",
                    "ر.ع" to "الريال العماني (ر.ع)",
                    "ر.ق" to "الريال القطري (ر.ق)",
                    "د.ب" to "الدينار البحريني (د.ب)"
                )
            } else {
                listOf(
                    "$" to "US Dollar ($)",
                    "ر.س" to "Saudi Riyal (SAR)",
                    "ج.م" to "Egyptian Pound (EGP)",
                    "د.إ" to "UAE Dirham (AED)",
                    "€" to "Euro (€)",
                    "£" to "British Pound (£)",
                    "د.ك" to "Kuwaiti Dinar (KWD)",
                    "ر.ع" to "Omani Rial (OMR)",
                    "ر.ق" to "Qatari Riyal (QAR)",
                    "د.ب" to "Bahraini Dinar (BHD)"
                )
            }
        }

        fun getThemes(isArabic: Boolean): List<Pair<String, String>> {
            return if (isArabic) {
                listOf(
                    "EMERALD" to "الأخضر الزمردي (الافتراضي)",
                    "OCEAN" to "الأزرق المحيطي",
                    "AMBER" to "الكهرمان الدافئ",
                    "PURPLE" to "البنفسجي الملكي"
                )
            } else {
                listOf(
                    "EMERALD" to "Emerald Green (Default)",
                    "OCEAN" to "Ocean Blue",
                    "AMBER" to "Warm Amber",
                    "PURPLE" to "Royal Purple"
                )
            }
        }
    }
}
