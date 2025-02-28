package com.example.photoquestv3

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.photoquestv3.Languages.dataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale


open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val languageKey = stringPreferencesKey("selected_language")
        val prefs = runBlocking { newBase.dataStore.data.first() }
        val languageCode = prefs[languageKey] ?: Locale.getDefault().language
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
}