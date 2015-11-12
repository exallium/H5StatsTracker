package com.exallium.h5statstracker.app

import android.content.Context
import android.os.Bundle
import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.stats.servicerecords.*
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

public class MainController(val context: Context) {

    val apiFactory = ApiFactory(context.resources.getString(R.string.api_key))

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

    fun onRequestArenaServiceRecord(bundle: Bundle?): Promise<ArenaResult, Exception> {
        return onRequestServiceRecord(bundle, { apiFactory.stats.getArenaServiceRecords(it) })
    }

    fun onRequestCampaignServiceRecord(bundle: Bundle?): Promise<CampaignResult, Exception> {
        return onRequestServiceRecord(bundle, { apiFactory.stats.getCampaignServiceRecords(it) })
    }

    fun onRequestCustomServiceRecord(bundle: Bundle?): Promise<CustomResult, Exception> {
        return onRequestServiceRecord(bundle, { apiFactory.stats.getCustomServiceRecords(it) })
    }

    fun onRequestWarzoneServiceRecord(bundle: Bundle?): Promise<WarzoneResult, Exception> {
        return onRequestServiceRecord(bundle, { apiFactory.stats.getWarzoneServiceRecords(it) })
    }

    private fun <T> onRequestServiceRecord(bundle: Bundle?,
                                   serviceRecordFn: (List<String?>) -> Call<ServiceRecordCollection<T>>): Promise<T, Exception> {
        val gamertag = if (bundle?.containsKey(Constants.GAMERTAG)?:false) {
            bundle?.getString(Constants.GAMERTAG)
        } else {
            getGamertag()
        }

        return async {
            val response = serviceRecordFn(listOf(gamertag)).execute()
            if (response.code() == 200 && response.body().results.size != 0) {
                val result = response.body().results.first()

                if (result.resultCode == 0) {
                    result.result
                }
            }

            throw IllegalAccessException("No Result Available")
        }
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
