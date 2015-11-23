package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.warzone

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.WarzoneResult
import com.exallium.h5statstracker.app.services.StatsService
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerServiceRecord
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerViewModel
import nl.komponents.kovenant.ui.successUi

internal val SERVICE_RECORD_WARZONE_PREFIX = 3000
enum class WarzoneServiceRecord {
    BASES_CAPTURED, CORE_DESTRUCTION_VICTORIES;

    fun getViewType(): Int {
        return ordinal + SERVICE_RECORD_WARZONE_PREFIX
    }
}

class WarzoneDataFactory(val statsService: StatsService, val bundle: Bundle) : InfographicDataFactory<List<BaseServiceRecordResult>> {
    override fun getViewModels(fn: (List<InfographicViewModel<List<BaseServiceRecordResult>>>) -> Unit) {

        statsService.onRequestWarzoneServiceRecord(bundle) successUi {

            val dataList = listOf(it as BaseServiceRecordResult)

            val kd = MultiplayerViewModel(dataList, MultiplayerServiceRecord.KILL_DEATH_RATIO)
            val assists = MultiplayerViewModel(dataList, MultiplayerServiceRecord.ASSISTS)
            val basesCaptured = WarzoneViewModel(dataList, WarzoneServiceRecord.BASES_CAPTURED)
            val winPercent = MultiplayerViewModel(dataList, MultiplayerServiceRecord.WIN_PERCENTAGE)
            val matchHistory = MultiplayerViewModel(dataList, MultiplayerServiceRecord.MATCH_HISTORY)
            val playtime = MultiplayerViewModel(dataList, MultiplayerServiceRecord.PLAYTIME)
            val gamesCompleted = MultiplayerViewModel(dataList, MultiplayerServiceRecord.GAMES_COMPLETED)
            val coreDestruction = WarzoneViewModel(dataList, WarzoneServiceRecord.CORE_DESTRUCTION_VICTORIES)

            fn(listOf(kd, assists, basesCaptured, winPercent, matchHistory, playtime, gamesCompleted, coreDestruction))
        }

    }
}

class WarzoneViewModel(val list: List<BaseServiceRecordResult>, val record: WarzoneServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = record.getViewType()

    override fun getData() = list.filterIsInstance(WarzoneResult::class.java)

}