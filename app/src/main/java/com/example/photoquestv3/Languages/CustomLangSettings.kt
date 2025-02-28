package com.example.photoquestv3.Languages

import android.app.Application
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class CustomLangSettings : Application() {

    override fun onCreate() {
        super.onCreate()
        val languageKey = stringPreferencesKey("selected_language")
        val prefsFlow = applicationContext.dataStore.data

        val prefs = runBlocking { prefsFlow.first() }
        val languageCode = prefs[languageKey]
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