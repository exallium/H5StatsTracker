package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary

import android.os.Bundle
import android.util.Log
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.successUi
import java.util.*

public enum class Section {
    ARENA, WARZONE, CAMPAIGN, CUSTOM
}

internal val HEADER_PREFIX = 1000

public class SummaryDataFactory(val mainController: MainController, val bundle: Bundle) : InfographicDataFactory<BaseServiceRecordResult> {
    override fun getViewModels(fn: (List<InfographicViewModel<BaseServiceRecordResult>>) -> Unit) {

        // 4 calls
        val statsService = mainController.statsService;
        val arenaPromise = statsService.onRequestArenaServiceRecord(bundle)
        val warzonePromise = statsService.onRequestWarzoneServiceRecord(bundle)
        val campaignPromise = statsService.onRequestCampaignServiceRecord(bundle)
        val customPromise = statsService.onRequestCustomServiceRecord(bundle)

        combine(arenaPromise, warzonePromise, campaignPromise, customPromise) successUi {
            val arena = arenaPromise.get()
            val warzone = warzonePromise.get()
            val campaign = campaignPromise.get()
            val custom = customPromise.get()

            val list = LinkedList<InfographicViewModel<BaseServiceRecordResult>>()

            arena?.let {
                list.add(HeaderViewModel(arena, Section.ARENA))
                list.add(ArenaStatsViewModel(arena))
            }

            warzone?.let {
                list.add(HeaderViewModel(warzone, Section.WARZONE))
                list.add(WarzoneStatsViewModel(warzone))
            }

            campaign?.let {
                list.add(HeaderViewModel(campaign, Section.CAMPAIGN))
                list.add(CampaignStatsViewModel(campaign))
            }

            custom?.let {
                list.add(HeaderViewModel(custom, Section.CUSTOM))
                list.add(CustomStatsViewModel(custom))
            }

            fn(list)
        } fail {
            Log.d("SummaryDataFactory", "Something bad happened", it)
        }

    }
}

public class HeaderViewModel(val t: BaseServiceRecordResult, val section: Section) : InfographicViewModel<BaseServiceRecordResult> {
    override fun getData() = t
    override fun getViewType() = HEADER_PREFIX + section.ordinal
}

public class ArenaStatsViewModel(val t: ArenaResult) : InfographicViewModel<ArenaResult> {
    override fun getViewType() = Section.ARENA.ordinal
    override fun getData() = t
}

public class WarzoneStatsViewModel(val t: WarzoneResult) : InfographicViewModel<WarzoneResult> {
    override fun getViewType() = Section.WARZONE.ordinal
    override fun getData() = t
}

public class CampaignStatsViewModel(val t: CampaignResult) : InfographicViewModel<CampaignResult> {
    override fun getViewType() = Section.CAMPAIGN.ordinal
    override fun getData() = t
}

public class CustomStatsViewModel(val t: CustomResult) : InfographicViewModel<CustomResult> {
    override fun getViewType() = Section.CUSTOM.ordinal
    override fun getData() = t

}