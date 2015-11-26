package com.exallium.h5statstracker.app

import android.support.design.widget.NavigationView
import android.view.MenuItem
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import nl.komponents.kovenant.ui.successUi
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

public class NavController(val mainController: MainController, val navView: NavigationView) {

    lateinit var gamertagTextView: TextView
    lateinit var spartanImageView: CircleImageView

    private val navListener = object : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(p0: MenuItem?): Boolean {
            return when (p0?.itemId) {
                R.id.log_out -> logOut()
                R.id.service_record -> goToServiceRecordSummary()
                R.id.arena_stats -> goToArenaSummary()
                R.id.warzone_stats -> goToWarzoneSummary()
                R.id.custom_stats -> goToCustomSummary()
                //R.id.campaign_stats -> goToCampaignSummary()
                R.id.medals -> goToMedals()
                //R.id.commendations -> goToCommendations()
                R.id.weapons -> goToWeapons()
                else -> false
            }
        }
    }

    inner class GamertagSubscriber: Subscriber<String>() {
        override fun onNext(t: String?) {
            Timber.d("Received Gamertag Update: %s".format(t))
            spartanImageView.setImageDrawable(null)
            gamertagTextView.text = t

            if (t == null || t.isNullOrEmpty()) {
                spartanImageView.setImageDrawable(null)
            } else {
                mainController.profileService.getSpartanImageUrl(t) successUi {
                    Picasso.with(navView.context).load(it).fit().into(spartanImageView)
                }
            }
        }

        override fun onCompleted() {
            Timber.d("Subscriber Completion")
        }

        override fun onError(e: Throwable?) {
            Timber.e(e, "Something Bad Happened")
        }
    }

    init {
        gamertagTextView = navView.findViewById(R.id.gamertag) as TextView
        spartanImageView = navView.findViewById(R.id.spartan_image) as CircleImageView
        mainController.gamertagSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(GamertagSubscriber())
        navView.setNavigationItemSelectedListener(navListener)
    }

    public fun onRouteRequest(request: Router.Request) {
        navView.setCheckedItem(request.route.navId)
    }

    private fun logOut(): Boolean {
        mainController.logOut()
        return true
    }

    private fun goToServiceRecordSummary(): Boolean {
        Router.onRequest(Router.Request(Router.Route.SERVICE_RECORD_SUMMARY))
        return true
    }

    private fun goToArenaSummary(): Boolean {
        Router.onRequest(Router.Request(Router.Route.ARENA_SERVICE_RECORD))
        return true
    }

    private fun goToWarzoneSummary(): Boolean {
        Router.onRequest(Router.Request(Router.Route.WARZONE_SERVICE_RECORD))
        return true
    }

    private fun goToCustomSummary(): Boolean {
        Router.onRequest(Router.Request(Router.Route.CUSTOM_SERVICE_RECORD))
        return true
    }

    private fun goToCampaignSummary(): Boolean {
        Router.onRequest(Router.Request(Router.Route.CAMPAIGN_SERVICE_RECORD))
        return true
    }

    private fun goToMedals(): Boolean {
        Router.onRequest(Router.Request(Router.Route.MEDALS))
        return true
    }

    private fun goToCommendations(): Boolean {
        //Router.onRequest(Router.Request(Router.Route.COMMENDATIONS))
        return true
    }

    private fun goToWeapons(): Boolean {
        Router.onRequest(Router.Request(Router.Route.WEAPONS))
        return true
    }

}
