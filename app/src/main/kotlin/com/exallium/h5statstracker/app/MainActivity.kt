package com.exallium.h5statstracker.app

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager

import kotlinx.android.synthetic.base.contentFrame
import kotlinx.android.synthetic.toolbar.toolbar_title
import kotlinx.android.synthetic.base.drawer
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant

class MainActivity : AppCompatActivity(), Router.Listener {

    override fun goToScreen(request: Router.Request) {
        hideSoftKeyboard()
        handleDrawerState(request.route)
        navController.onRouteRequest(request)

        val view = getRouterView(request, this, mainController)
        contentFrame.removeAllViews()
        contentFrame.addView(view)

        val bundle = mainController.prepareBundle(request.bundle)
        toolbar_title.text = getString(request.route.titleId).interpolate(bundle)
    }

    lateinit var mainController: MainController
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKovenant()
        setContentView(R.layout.base)
        mainController = MainController(applicationContext)

        val navView = findViewById(R.id.nav) as NavigationView
        navController = NavController(mainController, navView)

        Router.onCreate(this)
    }

    override fun onResume() {
        super.onResume()
        mainController.onResume()
    }

    override fun onDestroy() {
        stopKovenant()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!Router.goBack()) {
            super.onBackPressed()
        }
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var focus = currentFocus
        if (focus == null) {
           focus = View(this)
        }
        inputMethodManager.hideSoftInputFromWindow(focus.windowToken, 0)
    }

    private fun handleDrawerState(route: Router.Route) {
        drawer.closeDrawer(Gravity.LEFT)
        drawer.setDrawerLockMode(if (route == Router.Route.GAMERTAG) {
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        } else {
            DrawerLayout.LOCK_MODE_UNLOCKED
        })
    }

}