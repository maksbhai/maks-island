package com.maks.island

import android.app.Application
import com.maks.island.data.preferences.SettingsRepository

class MaksIslandApp : Application() {
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
    }
}
