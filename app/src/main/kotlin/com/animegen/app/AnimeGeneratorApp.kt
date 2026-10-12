package com.animegen.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnimeGeneratorApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
