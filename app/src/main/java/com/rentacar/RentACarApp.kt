package com.rentacar

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class RentACarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val nightMode = prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val KEY_NIGHT_MODE = "night_mode"
        const val KEY_LANGUAGE = "language"
        const val KEY_DISPLAY_NAME_SET = "displayNameSet"

        fun applyLocale(base: Context): Context {
            val prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lang = prefs.getString(KEY_LANGUAGE, null) ?: return base
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(base.resources.configuration)
            config.setLocale(locale)
            return base.createConfigurationContext(config)
        }
    }
}
