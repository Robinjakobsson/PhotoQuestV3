package com.example.photoquestv3.Models

import android.media.audiofx.DynamicsProcessing.Config
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Configuration
import java.util.Locale

class LanguageManager {

    companion object {

        fun setLanguage(activity: AppCompatActivity, language: String) {
            val setLang = Locale(language)
            Locale.setDefault(setLang)

            val config = android.content.res.Configuration(activity.resources.configuration)
            config.setLocale(setLang)
            activity.baseContext.resources.updateConfiguration(config, activity.baseContext.resources.displayMetrics)
            activity.recreate()
        }

    }

}