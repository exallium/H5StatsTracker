package com.exallium.h5statstracker.app.services

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.Constants
import com.exallium.h5statstracker.app.MainController
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import retrofit.Call

class StatsService(val mainController: MainController) {

    fun onRequestArenaServiceRecord(bundle: Bundle?): Promise<ArenaResult?, Exception> {
        return onRequestServiceRecord(bundle, { mainController.apiFactory.stats.getArenaServiceRecords(it) })
    }

    fun onRequestCampaignServiceRecord(bundle: Bundle?): Promise<CampaignResult?, Exception> {
        return onRequestServiceRecord(bundle, { mainController.apiFactory.stats.getCampaignServiceRecords(it) })
    }

    fun onRequestCustomServiceRecord(bundle: Bundle?): Promise<CustomResult?, Exception> {
        return onRequestServiceRecord(bundle, { mainController.apiFactory.stats.getCustomServiceRecords(it) })
    }

    fun onRequestWarzoneServiceRecord(bundle: Bundle?): Promise<WarzoneResult?, Exception> {
        return onRequestServiceRecord(bundle, { mainController.apiFactory.stats.getWarzoneServiceRecords(it) })
    }

    private fun <T> onRequestServiceRecord(bundle: Bundle?,
                                           serviceRecordFn: (List<String?>) -> Call<ServiceRecordCollection<T>>): Promise<T?, Exception> {
        val gamertag = if (bundle?.containsKey(Constants.GAMERTAG)?:false) {
            bundle?.getString(Constants.GAMERTAG)
        } else {
            mainController.getGamertag()
        }

        return async {
            val response = serviceRecordFn(listOf(gamertag)).execute()
            if (response.code() == 200 && response.body().results.size != 0) {
                val result = response.body().results.first()

                if (result.resultCode == 0) {
                    result.result
                } else { null }
            } else { null }
        }
    }

}
