package ru.ironcore.app.data

import android.content.Context
import ru.ironcore.app.ui.theme.ThemeMode

class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences("ironcore", Context.MODE_PRIVATE)

    var theme: ThemeMode
        get() = ThemeMode.entries.find { it.name == prefs.getString("theme", null) } ?: ThemeMode.SYSTEM
        set(value) = prefs.edit().putString("theme", value.name).apply()

    var rememberMe: Boolean
        get() = prefs.getBoolean("remember", true)
        set(value) = prefs.edit().putBoolean("remember", value).apply()

    var addresses: List<String>
        get() = prefs.getString("addresses", "").orEmpty().lines().filter { it.isNotBlank() }
        set(value) = prefs.edit().putString("addresses", value.joinToString("\n")).apply()

    var defaultAddress: String?
        get() = prefs.getString("default_address", null)
        set(value) = prefs.edit().putString("default_address", value).apply()

    var payment: String
        get() = prefs.getString("payment", null) ?: Payment.CARD
        set(value) = prefs.edit().putString("payment", value).apply()
}
