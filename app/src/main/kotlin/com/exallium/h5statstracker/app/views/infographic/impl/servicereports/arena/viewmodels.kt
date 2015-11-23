package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5statstracker.app.services.StatsService
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerServiceRecord
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerViewModel
import nl.komponents.kovenant.ui.successUi

internal val SERVICE_RECORD_ARENA_PREFIX = 2000
enum class ArenaServiceRecord {
    TOP_CSR, TOP_CSR_CONDENSED;

    fun getViewType(): Int {
        return ordinal + SERVICE_RECORD_ARENA_PREFIX
    }
}

class ArenaDataFactory(val statsService: StatsService, val bundle: Bundle) : InfographicDataFactory<List<BaseServiceRecordResult>> {
    override fun getViewModels(fn: (List<InfographicViewModel<List<BaseServiceRecordResult>>>) -> Unit) {

        statsService.onRequestArenaServiceRecord(bundle) successUi {
            val dataList = listOf(it as BaseServiceRecordResult)

            val rank = MultiplayerViewModel(dataList, MultiplayerServiceRecord.SPARTAN_RANK)
            val kd = MultiplayerViewModel(dataList, MultiplayerServiceRecord.KILL_DEATH_RATIO)
            val headshots = MultiplayerViewModel(dataList, MultiplayerServiceRecord.HEADSHOTS)
            val assassinations = MultiplayerViewModel(dataList, MultiplayerServiceRecord.ASSASSINATIONS)
            val winPercent = MultiplayerViewModel(dataList, MultiplayerServiceRecord.WIN_PERCENTAGE)
            val matchRecord = MultiplayerViewModel(dataList, MultiplayerServiceRecord.MATCH_HISTORY)
            val gamesCompleted = MultiplayerViewModel(dataList, MultiplayerServiceRecord.GAMES_COMPLETED)
            val playtime = MultiplayerViewModel(dataList, MultiplayerServiceRecord.PLAYTIME)
            val topCsr = ArenaViewModel(dataList, ArenaServiceRecord.TOP_CSR)

            fn(listOf(rank, kd, headshots, assassinations, winPercent, matchRecord, gamesCompleted, playtime, topCsr))

        }

    }
}

class ArenaViewModel(val list: List<BaseServiceRecordResult>, val record: ArenaServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = record.getViewType()

    override fun getData() = list.filterIsInstance(ArenaResult::class.java)

}