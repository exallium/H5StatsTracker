package com.exallium.h5statstracker.app

import android.content.Context
import android.os.Bundle
import com.exallium.h5.api.ApiFactory
import com.exallium.h5statstracker.app.model.DiskCache
import com.exallium.h5statstracker.app.model.MemoryCache
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.services.ProfileService
import com.exallium.h5statstracker.app.services.StatsService
import rx.lang.kotlin.BehaviourSubject
import timber.log.Timber

public class MainController(val context: Context) {

    val memoryCache = MemoryCache()
    val diskCache = DiskCache(context.cacheDir)
    val apiFactory = ApiFactory(context.resources.getString(R.string.api_key))
    val statsService = StatsService(this)
    val metadataService = MetadataService(apiFactory, memoryCache, diskCache)
    val profileService = ProfileService(apiFactory.profile)
    val gamertagSubject = BehaviourSubject<String>()

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    public fun getGamertag() = context
            .getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
            .getString(Constants.GAMERTAG, null)

    public fun onResume() {
        val gamerTag = getGamertag()

        if (gamerTag != null) {
            gamertagSubject.onNext(gamerTag)
            val bundle = Bundle()
            bundle.putString(Constants.GAMERTAG, gamerTag)
            Router.replaceTo(Router.Request(Router.Route.SERVICE_RECORD_SUMMARY))
        }
    }

    fun onSubmitGamertag(gamertag: String) = if (gamertag != "") {
        context.getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
                .edit().putString(Constants.GAMERTAG, gamertag).apply()
        onResume()
        true
    } else {
        false
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

    fun logOut() {
        context.getSharedPreferences(Constants.PREFERENCES, Constants.PREFERENCE_MODE)
            .edit().remove(Constants.GAMERTAG).apply()
        gamertagSubject.onNext("")
        Router.replaceTo(Router.Request(Router.Route.GAMERTAG))
    }

}
