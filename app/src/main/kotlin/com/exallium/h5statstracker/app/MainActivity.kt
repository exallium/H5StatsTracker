package com.exallium.h5statstracker.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.base.contentFrame
import kotlinx.android.synthetic.base.drawer
import kotlinx.android.synthetic.toolbar.toolbar_title
import timber.log.Timber

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
        setContentView(R.layout.base)

        val serviceIntent = Intent(this, MainService::class.java)
        bindService(serviceIntent, serviceConnection, BIND_ADJUST_WITH_ACTIVITY)
    }

    override fun onResume() {
        super.onResume()
        try {
            mainController.onResume()
        } catch (e: UninitializedPropertyAccessException) {
            Timber.d(e, "onResume Called before service bound")
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
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

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected from %s", name?.packageName)
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("Connected to %s", name?.packageName)

            mainController = (service as MainBinder).mainController

            val navView = findViewById(R.id.nav) as NavigationView
            navController = NavController(mainController, navView)

            Router.onCreate(this@MainActivity)
            mainController.onResume()
        }

    }

}