package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary

import android.animation.ObjectAnimator
import android.content.Context
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.*
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.model.Impulses
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.github.lzyzsd.circleprogress.DonutProgress
import com.squareup.picasso.Picasso
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.successUi
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

internal val DURATION_PER_PERCENT = 8L

internal val PLAYTIME_FORMATTER = PeriodFormatterBuilder()
        .appendHours()
        .appendSuffix(":")
        .appendMinutes()
        .appendSuffix(":")
        .appendSeconds()
        .toFormatter()

val getSummaryInfographicViewByType: (Int, Context, MetadataService) -> InfographicView<BaseServiceRecordResult> = {
    viewType: Int, context: Context, metadataService: MetadataService ->
    when (viewType) {
        HEADER_PREFIX + Section.ARENA.ordinal -> HeaderView(context, Section.ARENA)
        HEADER_PREFIX + Section.WARZONE.ordinal -> HeaderView(context, Section.WARZONE)
        HEADER_PREFIX + Section.CAMPAIGN.ordinal -> HeaderView(context, Section.CAMPAIGN)
        HEADER_PREFIX + Section.CUSTOM.ordinal -> HeaderView(context, Section.CUSTOM)
        Section.ARENA.ordinal -> ArenaStatsSummaryView(context, metadataService)
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

public class ArenaStatsSummaryView(context: Context, val metadataService: MetadataService) : InfographicView<BaseServiceRecordResult>(context, R.layout.servicereport_arena_summary) {
    override fun render(data: BaseServiceRecordResult) {
        if (data !is ArenaResult) {
            return
        }

        (findViewById(R.id.service_rank) as TextView).text = "%s%d".format(context.getString(R.string.sr), data.spartanRank)

        // get rank info
        val currentRankPromise = metadataService.getSpartanRank(data.spartanRank)
        val nextRankPromise = metadataService.getSpartanRank(data.spartanRank + 1)

        combine(currentRankPromise, nextRankPromise) successUi {

            val currentRank = currentRankPromise.get()
            val nextRank = nextRankPromise.get()

            val delta = nextRank.startXp - currentRank.startXp
            val progress = data.xp - currentRank.startXp

            val progressBar = (findViewById(R.id.xp_progress_bar) as ProgressBar)
            val progressBarEndValue = Math.round((progress.toFloat() / delta.toFloat()) * 100)
            val anim = ObjectAnimator.ofInt(progressBar, "progress", progressBarEndValue)
            anim.setDuration(DURATION_PER_PERCENT * progressBarEndValue)
            anim.start()

            (findViewById(R.id.xp_progress_text) as TextView).text = "%d / %d".format(progress, delta)
        }

        if (data.arenaStat.highestCsrAttained != null) {
            metadataService.getPlaylist(data.arenaStat.highestCsrPlaylistId) successUi {
                (findViewById(R.id.top_variant_name) as TextView).text = it?.name
            }

            metadataService.getCsrDesignation(data.arenaStat.highestCsrAttained.designationId.toLong()) successUi {
                val tier = it?.tiers?.find { it.id == data.arenaStat.highestCsrAttained.tier.toLong() }
                val name = it?.name?:""
                tier?.let {
                    Picasso.with(context).load(tier.iconImageUrl).into((findViewById(R.id.csr_image) as ImageView))
                    (findViewById(R.id.csr_title) as TextView).text = "%s %d".format(name, it.id)
                }
            }

            val progressBar = findViewById(R.id.arena_progress) as DonutProgress
            val arenaProgress = data.arenaStat.highestCsrAttained.percentToNextTier
            val anim = ObjectAnimator.ofInt(progressBar, "progress", arenaProgress)
            anim.setDuration(DURATION_PER_PERCENT * arenaProgress.toLong())
            anim.start()
        } else {
            (findViewById(R.id.arena_progress) as DonutProgress).progress = (data.arenaStat.topGameBaseVariants.minBy {
                it.gameBaseVariantRank
            }?.numberOfMatchesCompleted?.toInt()?:0) * 10
        }




        val totalKills = data.arenaStat.totalKills
        val totalDeaths = data.arenaStat.totalDeaths
        (findViewById(R.id.kdr) as TextView).text = "%.3f".format(totalKills.toFloat() / (if (totalDeaths == 0) 1f else totalDeaths.toFloat()))
        (findViewById(R.id.total_kills) as TextView).text = totalKills.toString()
        (findViewById(R.id.total_deaths) as TextView).text = totalDeaths.toString()
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

        val playtime = findViewById(R.id.warzone_playtime) as TextView
        val gamesCompleted = findViewById(R.id.warzone_games_completed) as TextView
        val coreDestructionVictories = findViewById(R.id.warzone_core_destruction_victories) as TextView
        val basesCaptured = findViewById(R.id.warzone_bases_captured) as TextView
        val kills = findViewById(R.id.warzone_kills) as TextView
        val legendaryBossTakedowns = findViewById(R.id.total_legendary_boss_takedowns) as TextView
        val standardBossTakedowns = findViewById(R.id.total_standard_boss_takedowns) as TextView
        val bossTakedowns = findViewById(R.id.boss_takedown_count) as TextView

        playtime.text = PLAYTIME_FORMATTER.print(Period(data.warzoneStat.totalTimePlayed))
        gamesCompleted.text = data.warzoneStat.totalGamesCompleted.toString()
        kills.text = data.warzoneStat.totalKills.toString()

        coreDestructionVictories.text = "%d".format(data.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_CORE_DESTRUCTION_VICTORIES
        }?.count?:0)

        basesCaptured.text = "%d".format(data.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_BASE_CAPTURED
        }?.count?:0)

        val legendKills = data.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_LEGENDARY_TAKEDOWN
        }?.count?:0

        val standardKills = data.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_BOSS_TAKEDOWN
        }?.count?:0

        legendaryBossTakedowns.text = "%d".format(legendKills)
        standardBossTakedowns.text = "%d".format(standardKills)
        bossTakedowns.text = "%d".format(legendKills + standardKills)

        if (legendKills != 0) {
            val legendKillPercentage = ((legendKills.toFloat() / (legendKills.toFloat() + standardKills.toFloat())) * 100).toInt()
            val legendProgress = findViewById(R.id.boss_takedown_progress) as DonutProgress
            val anim = ObjectAnimator.ofInt(legendProgress, "progress", legendKillPercentage)
            anim.setDuration(DURATION_PER_PERCENT * legendKillPercentage)
            anim.start()
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