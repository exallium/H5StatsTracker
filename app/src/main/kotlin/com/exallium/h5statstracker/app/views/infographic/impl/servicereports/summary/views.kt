package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary

import android.content.Context
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.InfographicView

val getSummaryInfographicViewByType: (Int, Context) -> InfographicView<BaseServiceRecordResult> = { viewType: Int, context: Context ->
    when (viewType) {
        HEADER_PREFIX + Section.ARENA.ordinal -> HeaderView(context, Section.ARENA)
        HEADER_PREFIX + Section.WARZONE.ordinal -> HeaderView(context, Section.WARZONE)
        HEADER_PREFIX + Section.CAMPAIGN.ordinal -> HeaderView(context, Section.CAMPAIGN)
        HEADER_PREFIX + Section.CUSTOM.ordinal -> HeaderView(context, Section.CUSTOM)
        Section.ARENA.ordinal -> ArenaStatsSummaryView(context)
        Section.WARZONE.ordinal -> WarzoneStatsSummaryView(context)
        Section.CAMPAIGN.ordinal -> CampaignStatsSummaryView(context)
        Section.CUSTOM.ordinal -> CustomStatsSummaryView(context)
        else -> throw IllegalStateException("Unknown ViewType %d".format(viewType))
    }
}

public class HeaderView(context: Context, val section: Section) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_header) {
    override fun render(data: BaseServiceRecordResult) {
        (findViewById(R.id.header_title) as TextView).text = section.name
    }
}

public class ArenaStatsSummaryView(context: Context) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_arena_summary) {
    override fun render(data: BaseServiceRecordResult) {
        if (data !is ArenaResult) {
            return
        }

        (findViewById(R.id.service_rank) as TextView).text = "%s%d".format(context.getString(R.string.sr), data.spartanRank)

        val xp = data.xp

        (findViewById(R.id.xp_progress_text))
        (findViewById(R.id.top_variant_name) as TextView).text = data.arenaStat.highestCsrPlaylistId

    }
}

public class CampaignStatsSummaryView(context: Context) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_campaign_summary) {
    override fun render(data: BaseServiceRecordResult) {
        if (data !is CampaignResult) {
            return
        }
    }
}

public class WarzoneStatsSummaryView(context: Context) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_warzone_summary) {
    override fun render(data: BaseServiceRecordResult) {
        if (data !is WarzoneResult) {
            return
        }
    }
}

public class CustomStatsSummaryView(context: Context) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_custom_summary) {
    override fun render(data: BaseServiceRecordResult) {
        if (data !is CustomResult) {
            return
        }
    }
}