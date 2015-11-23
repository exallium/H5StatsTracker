package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.campaign

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CampaignResult
import com.exallium.h5statstracker.app.services.StatsService
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import nl.komponents.kovenant.ui.successUi

internal val SERVICE_REPORT_CAMPAIGN_PREFIX = 5000
enum class CampaignServiceRecord {
    COLLECTIBLES,
    PLAYTHROUGH_SOLO,
    PLAYTHROUGH_COOP;

    fun getViewType() = ordinal + SERVICE_REPORT_CAMPAIGN_PREFIX
}

class CampaignDataFactory(val statsService: StatsService, val bundle: Bundle) : InfographicDataFactory<List<BaseServiceRecordResult>> {
    override fun getViewModels(fn: (List<InfographicViewModel<List<BaseServiceRecordResult>>>) -> Unit) {
        statsService.onRequestCampaignServiceRecord(bundle) successUi {
            val wrapper = listOf(it as BaseServiceRecordResult)
            fn(listOf(
                    CampaignViewModel(wrapper, CampaignServiceRecord.PLAYTHROUGH_SOLO),
                    CampaignViewModel(wrapper, CampaignServiceRecord.PLAYTHROUGH_COOP),
                    CampaignViewModel(wrapper, CampaignServiceRecord.COLLECTIBLES)))
        }
    }
}

class CampaignViewModel(val list: List<BaseServiceRecordResult>, val campaignServiceRecord: CampaignServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = campaignServiceRecord.getViewType()
    override fun getData() = list.filterIsInstance(CampaignResult::class.java)

}