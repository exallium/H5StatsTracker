package com.exallium.h5statstracker.app;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity(), MainController.Callbacks {

    override fun getGamertag(): String? {
        return getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
            .getString(Constants.GAMERTAG, null)
    }

    override fun showContentGamertag() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, GamertagFragment.create())
                .commit()
    }

    override fun showContentServiceRecord() {
        throw UnsupportedOperationException()
    }

    val mainController = MainController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)
    }

    override fun onResume() {
        super.onResume()
        mainController.onResume(this)
    }

}