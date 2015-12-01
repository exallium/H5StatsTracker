package com.exallium.h5statstracker.app

import android.app.Service
import android.content.Intent
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant
import timber.log.Timber

class MainService : Service() {

    lateinit var mainController: MainController

    override fun onBind(intent: Intent?) = MainBinder(mainController)

    override fun onCreate() {
        super.onCreate()
        Timber.d("MainService is created")
        startKovenant()
        mainController = MainController(this.applicationContext)
    }

    override fun onDestroy() {
        stopKovenant()
        super.onDestroy()
        Timber.d("MainService is destroyed")
    }

}
