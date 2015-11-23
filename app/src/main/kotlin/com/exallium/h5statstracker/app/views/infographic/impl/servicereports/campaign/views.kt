package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.campaign

import android.content.Context
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CampaignResult
import com.exallium.h5.api.models.stats.servicerecords.CampaignStat
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import nl.komponents.kovenant.ui.successUi
import timber.log.Timber

val getCampaignServiceRecordViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when(viewType) {
        CampaignServiceRecord.COLLECTIBLES.getViewType() -> CompletionView(context, mainController.metadataService)
        CampaignServiceRecord.PLAYTHROUGH_COOP.getViewType() -> PlaythroughView(context, R.string.coop)
        CampaignServiceRecord.PLAYTHROUGH_SOLO.getViewType() -> PlaythroughView(context, R.string.solo)
        else -> throw IllegalArgumentException("Unknown View Type: %d".format(viewType))
    }
}

class PlaythroughView(context: Context, val titleId: Int) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.campaign_playthrough) {
    override fun render(data: List<BaseServiceRecordResult>) {
        val type = findViewById(R.id.campaign_type) as TextView
        val completed = findViewById(R.id.campaign_missions_completed) as TextView

        type.setText(titleId)

        val campaignResult = data.filterIsInstance(CampaignResult::class.java).first()

        val mapFn = if (titleId == R.string.coop) {
            value: CampaignStat.CampaignMissionStats -> value.coopStats
        } else {
            value: CampaignStat.CampaignMissionStats -> value.soloStats
        }


        val statsMaps = campaignResult.campaignStat.campaignMissionStats.map(mapFn)
        val totalCompletedMissions = statsMaps.filter { it.isNotEmpty() }.count()
        completed.text = "%d/15".format(totalCompletedMissions)
    }
}

class CompletionView(context: Context, val metadataService: MetadataService) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.campaign_completion) {
    override fun render(data: List<BaseServiceRecordResult>) {
        val skullCount = findViewById(R.id.skulls) as TextView
        val intelCount = findViewById(R.id.mission_intel) as TextView

        val campaignResult = data.filterIsInstance(CampaignResult::class.java).first()

        campaignResult.campaignStat.impulses.forEach {
            metadataService.getImpulse(it.id) successUi {
                Timber.d(it?.internalName)
            }
        }

    }
}