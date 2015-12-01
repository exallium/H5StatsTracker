package com.exallium.h5statstracker.app

import android.app.Application
import android.content.Intent
import net.danlew.android.joda.JodaTimeAndroid

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        val serviceIntent = Intent(this, MainService::class.java)
        startService(serviceIntent)
    }
}