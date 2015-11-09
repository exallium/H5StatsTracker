package com.exallium.h5statstracker.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.base.contentFrame
import kotlinx.android.synthetic.toolbar.toolbar_title

class MainActivity : AppCompatActivity(), Router.Listener {

    override fun goToScreen(request: Router.Request) {

        if (!request.bundle.containsKey(Constants.GAMERTAG)) {
            request.bundle.putString(Constants.GAMERTAG, mainController.getGamertag())
        }

        val view = getRouterView(request, this, mainController)
        contentFrame.removeAllViews()
        contentFrame.addView(view)
        toolbar_title.text = getString(request.route.titleId).interpolate(request.bundle)
    }

    lateinit var mainController: MainController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base)
        mainController = MainController(applicationContext)
        Router.onCreate(this)
    }

    override fun onResume() {
        super.onResume()
        mainController.onResume()
    }

    override fun onBackPressed() {
        if (!Router.goBack()) {
            super.onBackPressed()
        }
    }

}