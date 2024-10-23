package com.example.dicodingevent.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val pref = SettingPreferences.getInstance(dataStore)

        val isDarkModeActive = runBlocking { pref.getThemeSetting().first() }

        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}