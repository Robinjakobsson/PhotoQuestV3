package com.example.photoquestv3.Models

import androidx.appcompat.app.AppCompatActivity
import com.example.photoquestv3.Fragments.SettingsFragment
import com.example.photoquestv3.R
import java.util.Locale

class LanguageManager {

    companion object {
        fun setLanguage(activity: AppCompatActivity, language: String) {
            val setLang = Locale(language)
            Locale.setDefault(setLang)

            val config = android.content.res.Configuration(activity.resources.configuration)
            config.setLocale(setLang)
            activity.baseContext.resources.updateConfiguration(config, activity.baseContext.resources.displayMetrics)

            activity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, SettingsFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

}