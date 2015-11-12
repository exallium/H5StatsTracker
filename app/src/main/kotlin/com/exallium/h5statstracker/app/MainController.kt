package com.exallium.h5statstracker.app

import android.content.Context
import android.os.Bundle
import com.exallium.h5.api.ApiFactory
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.services.StatsService

public class MainController(val context: Context) {

    val apiFactory = ApiFactory(context.resources.getString(R.string.api_key))
    val statsService = StatsService(this)
    val metadataService = MetadataService(apiFactory)

    public fun getGamertag() = context
            .getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
            .getString(Constants.GAMERTAG, null)

    public fun onResume() {
        val gamerTag = getGamertag()

        if (gamerTag == null) {
            Router.onRequest(Router.Request(Router.Route.GAMERTAG))
        }
    }

    fun onSubmitGamertag(gamertag: String) {
        context.getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
            .edit().putString(Constants.GAMERTAG, gamertag).apply()
        Router.goBack()
    }

    fun prepareBundle(bundle: Bundle): Bundle {
        val prepBundle = if (bundle.containsKey(Constants.ACTIVITY_BUNDLE)) {
            bundle.getBundle(Constants.ACTIVITY_BUNDLE)
        } else {
            Bundle()
        }

        if (!prepBundle.containsKey(Constants.GAMERTAG)) {
            prepBundle.putString(Constants.GAMERTAG, getGamertag())
        }

        return prepBundle;
    }

}
