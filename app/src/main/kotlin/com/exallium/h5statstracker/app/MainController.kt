package com.exallium.h5statstracker.app

import android.content.Context
import android.os.Bundle
import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5.api.models.stats.servicerecords.ServiceRecordCollection
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

    fun onRequestArenaServiceRecord(bundle: Bundle?, callback: ViewCallback<ArenaResult>) {

        val gamertag = if (bundle?.containsKey(Constants.GAMERTAG)?:false) {
            bundle?.getString(Constants.GAMERTAG)
        } else {
            getGamertag()
        }

        apiFactory.stats.getArenaServiceRecords(listOf(gamertag)).enqueue(object : Callback<ServiceRecordCollection<ArenaResult>> {

            override fun onFailure(t: Throwable?) {
                callback.onError()
            }

            override fun onResponse(response: Response<ServiceRecordCollection<ArenaResult>>, retrofit: Retrofit) {

                if (response.code() == 200 && response.body().results.size != 0) {
                    val result = response.body().results.first()

                    if (result.resultCode == 0) {
                        callback.onResult(result.result)
                        return
                    }
                }
                callback.onError()
            }

        })
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
