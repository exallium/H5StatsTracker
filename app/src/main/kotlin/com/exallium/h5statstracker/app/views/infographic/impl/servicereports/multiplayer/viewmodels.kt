package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CampaignResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaServiceRecord
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaViewModel
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.successUi

internal val SERVICE_RECORD_MULTIPLAYER_PREFIX = 1000;
enum class MultiplayerServiceRecord {
    SPARTAN_RANK,
    PLAYTIME,
    GAMES_COMPLETED,
    KILL_DEATH_RATIO,
    WIN_PERCENTAGE,
    ASSASSINATIONS,
    HEADSHOTS,
    MATCH_HISTORY,
    ASSISTS;

    fun getViewType(): Int {
        return ordinal + SERVICE_RECORD_MULTIPLAYER_PREFIX
    }
}

class MultiplayerDataFactory(val mainController: MainController, val bundle: Bundle) : InfographicDataFactory<List<BaseServiceRecordResult>> {

    override fun getViewModels(fn: (List<InfographicViewModel<List<BaseServiceRecordResult>>>) -> Unit) {

        val statsService = mainController.statsService;
        val arenaPromise = statsService.onRequestArenaServiceRecord(bundle)
        val warzonePromise = statsService.onRequestWarzoneServiceRecord(bundle)
        val customPromise = statsService.onRequestCustomServiceRecord(bundle)

        combine(arenaPromise, warzonePromise, customPromise) successUi {
            val arena = arenaPromise.get()
            val warzone = warzonePromise.get()
            val custom = customPromise.get()

            val results = listOf(arena, warzone, custom).filterNotNull()
            val rankModel = MultiplayerViewModel(results, MultiplayerServiceRecord.SPARTAN_RANK)
            val kdrModel = MultiplayerViewModel(results, MultiplayerServiceRecord.KILL_DEATH_RATIO)
            val topCsrModel = ArenaViewModel(results, ArenaServiceRecord.TOP_CSR_CONDENSED)
            val playtimeModel = MultiplayerViewModel(results, MultiplayerServiceRecord.PLAYTIME)
            val gamesCompletedModel = MultiplayerViewModel(results, MultiplayerServiceRecord.GAMES_COMPLETED)
            val winPercentageModel = MultiplayerViewModel(results, MultiplayerServiceRecord.WIN_PERCENTAGE)
            val matchHistoryModel = MultiplayerViewModel(results, MultiplayerServiceRecord.MATCH_HISTORY)
            fn(listOf(rankModel, kdrModel, topCsrModel, playtimeModel, gamesCompletedModel, winPercentageModel, matchHistoryModel))
        }
    }
}

open class MultiplayerViewModel(val list: List<BaseServiceRecordResult>, val multiplayerServiceRecord: MultiplayerServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = multiplayerServiceRecord.getViewType()

    override fun getData(): List<BaseServiceRecordResult> {
        return list.filter { it !is CampaignResult }
    }

}