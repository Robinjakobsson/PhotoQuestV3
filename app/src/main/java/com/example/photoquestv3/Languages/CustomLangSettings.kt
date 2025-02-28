package com.example.photoquestv3.Languages

import android.app.Application
import android.content.Context
import java.util.Locale

class CustomLangSettings: Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("selected_language", null)
        if (languageCode != null) {
            setLocale(languageCode)
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}

