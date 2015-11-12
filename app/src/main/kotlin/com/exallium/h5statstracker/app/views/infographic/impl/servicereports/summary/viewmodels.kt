package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary

import android.os.Bundle
import android.util.Log
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.ViewCallback
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import nl.komponents.kovenant.all
import nl.komponents.kovenant.combine.combine

public enum class Section {
    ARENA, WARZONE, CAMPAIGN, CUSTOM
}

internal val HEADER_PREFIX = 1000

public class SummaryDataFactory(val mainController: MainController, val bundle: Bundle) : InfographicDataFactory<BaseServiceRecordResult> {
    override fun getViewModels(fn: (List<InfographicViewModel<BaseServiceRecordResult>>) -> Unit) {

        // 4 calls
        val arenaPromise = mainController.onRequestArenaServiceRecord(bundle)
        val warzonePromise = mainController.onRequestWarzoneServiceRecord(bundle)
        val campaignPromise = mainController.onRequestCampaignServiceRecord(bundle)
        val customPromise = mainController.onRequestCustomServiceRecord(bundle)

        combine(arenaPromise, warzonePromise, campaignPromise, customPromise) success {
            val arena = arenaPromise.get()
            val warzone = warzonePromise.get()
            val campaign = campaignPromise.get()
            val custom = customPromise.get()

            fn(listOf(
                    HeaderViewModel(arena, Section.ARENA),
                    ArenaStatsViewModel(arena),
                    HeaderViewModel(warzone, Section.WARZONE),
                    WarzoneStatsViewModel(warzone),
                    HeaderViewModel(campaign, Section.CAMPAIGN),
                    CampaignStatsViewModel(campaign),
                    HeaderViewModel(custom, Section.CUSTOM),
                    CustomStatsViewModel(custom)
            ))
        } fail {
            Log.d("SummaryDataFactory", "Something bad happened")
        }

    }
}

public class HeaderViewModel(t: BaseServiceRecordResult, val section: Section) : InfographicViewModel<BaseServiceRecordResult> {
    override fun getData(): BaseServiceRecordResult {
        throw UnsupportedOperationException()
    }

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