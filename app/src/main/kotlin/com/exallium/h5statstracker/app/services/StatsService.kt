package com.exallium.h5statstracker.app.services

import android.os.Bundle
import com.exallium.h5.api.models.stats.matches.Match
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.Constants
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.Units
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import retrofit.Call
import timber.log.Timber

class StatsService(val mainController: MainController) {

    companion object {
        val WARZONE_RESULT_KEY = "warzoneResult"
        val ARENA_RESULT_KEY = "arenaResult"
        val CAMPAIGN_RESULT_KEY = "campaignResult"
        val CUSTOM_RESULT_KEY = "customResult"
        val RESULT_TTL = Units.MINUTE_MILLIS * 10

        val MATCHES_KEY = "matches"
        val MATCHES_TTL = Units.MINUTE_MILLIS * 10
        val MATCHES_DEFAULT_START_OFFSET = 0
        val MATCHES_DEFAULT_COUNT = 20
        val MATCHES_DEFAULT_GAMEMODES = listOf("arena", "warzone", "custom")
    }

    fun onRequestMatchHistory(bundle: Bundle?): Promise<List<Match>, Exception> {
        val gamertag = getGamertag(bundle)
        val startOffset = bundle?.getInt(Constants.START_OFFSET) ?: MATCHES_DEFAULT_START_OFFSET
        val count = bundle?.getInt(Constants.COUNT) ?: MATCHES_DEFAULT_COUNT
        val gameModes = bundle?.getStringArrayList(Constants.GAME_MODES) ?: MATCHES_DEFAULT_GAMEMODES
        val cacheKey = "%s-%s-%d-%d-%s".format(MATCHES_KEY, gamertag, startOffset, count, gameModes.joinToString("-"))

        return mainController.cacheService.readListFromCache(cacheKey, Match::class.java, MATCHES_TTL, {
            val response = mainController.apiFactory.stats.getRecentMatchInfo(gamertag, gameModes.joinToString(","), startOffset, count).execute()
            Timber.d("%s: Origin Response Code: %d", cacheKey, response.code())
            if (response.isSuccess && response.body().resultCount != 0) {
                response.body().results
            } else {
                listOf()
            }
        })
    }

    fun onRequestArenaServiceRecord(bundle: Bundle?): Promise<ArenaResult?, Exception> {
        return onRequestServiceRecord(bundle, ARENA_RESULT_KEY, RESULT_TTL,
                ArenaResult::class.java, { mainController.apiFactory.stats.getArenaServiceRecords(it) })
    }

    fun onRequestCampaignServiceRecord(bundle: Bundle?): Promise<CampaignResult?, Exception> {
        return onRequestServiceRecord(bundle, CAMPAIGN_RESULT_KEY, RESULT_TTL,
                CampaignResult::class.java, { mainController.apiFactory.stats.getCampaignServiceRecords(it) })
    }

    fun onRequestCustomServiceRecord(bundle: Bundle?): Promise<CustomResult?, Exception> {
        return onRequestServiceRecord(bundle, CUSTOM_RESULT_KEY, RESULT_TTL,
                CustomResult::class.java, { mainController.apiFactory.stats.getCustomServiceRecords(it) })
    }

    fun onRequestWarzoneServiceRecord(bundle: Bundle?): Promise<WarzoneResult?, Exception> {
        return onRequestServiceRecord(bundle, WARZONE_RESULT_KEY, RESULT_TTL,
                WarzoneResult::class.java, { mainController.apiFactory.stats.getWarzoneServiceRecords(it) })
    }

    private fun getGamertag(bundle: Bundle?) = if (bundle?.containsKey(Constants.GAMERTAG) ?: false) {
        bundle?.getString(Constants.GAMERTAG)
    } else {
        mainController.getGamertag()
    }

    private fun <T> onRequestServiceRecord(bundle: Bundle?,
                                           resultKey: String,
                                           resultTtl: Long,
                                           resultClass: Class<T>,
                                           serviceRecordFn: (String?) -> Call<ServiceRecordCollection<T>>): Promise<T?, Exception> {
        val gamertag = getGamertag(bundle)
        val key = "%s-%s".format(resultKey, gamertag)
        return mainController.cacheService.readItemFromCache(key, resultClass, resultTtl, {
            val response = serviceRecordFn(gamertag).execute()
            if (response.code() == 200 && response.body().results.size != 0) {
                val result = response.body().results.first()
                if (result.resultCode == 0) {
                    result.result
                } else {
                    null
                }
            } else {
                null
            }
        })
    }

}
