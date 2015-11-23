package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.custom

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CustomResult
import com.exallium.h5statstracker.app.services.StatsService
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerServiceRecord
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerViewModel
import nl.komponents.kovenant.ui.successUi

internal val SERVICE_RECORD_CUSTOM_PREFIX = 4000
enum class CustomServiceRecord {
    TOP_GAME_BASE_VARIANT;

    fun getViewType() = ordinal + SERVICE_RECORD_CUSTOM_PREFIX
}

class CustomDataFactory(val statsService: StatsService, val bundle: Bundle) : InfographicDataFactory<List<BaseServiceRecordResult>> {
    override fun getViewModels(fn: (List<InfographicViewModel<List<BaseServiceRecordResult>>>) -> Unit) {
        statsService.onRequestCustomServiceRecord(bundle) successUi {
            val wrapper = listOf(it as BaseServiceRecordResult)

            val kdr = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.KILL_DEATH_RATIO)
            val topVariant = CustomViewModel(wrapper, CustomServiceRecord.TOP_GAME_BASE_VARIANT)
            val playtime = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.PLAYTIME)
            val headshots = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.HEADSHOTS)
            val assassinations = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.ASSASSINATIONS)
            val gamesCompleted = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.GAMES_COMPLETED)
            val matchHistory = MultiplayerViewModel(wrapper, MultiplayerServiceRecord.MATCH_HISTORY)

            fn(listOf(kdr, topVariant, playtime, headshots, assassinations, gamesCompleted, matchHistory))
        }
    }
}

class CustomViewModel(val list: List<BaseServiceRecordResult>, val customServiceRecord: CustomServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = customServiceRecord.getViewType()

    override fun getData() = list.filterIsInstance(CustomResult::class.java)

}