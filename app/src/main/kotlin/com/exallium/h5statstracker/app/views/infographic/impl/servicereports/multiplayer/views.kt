package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.exallium.h5.api.models.stats.common.BaseStats
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CustomResult
import com.exallium.h5.api.models.stats.servicerecords.WarzoneResult
import com.exallium.h5statstracker.app.Constants
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.services.StatsService
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.CommonTextView
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary.DURATION_PER_PERCENT
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary.PLAYTIME_FORMATTER
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.successUi
import org.joda.time.Period

internal val MAX_GAME_HISTORY = 20

val getMultiplayerViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when (viewType) {
        MultiplayerServiceRecord.SPARTAN_RANK.getViewType() -> SpartanRankView(context, mainController.metadataService)
        MultiplayerServiceRecord.PLAYTIME.getViewType() -> PlaytimeView(context)
        MultiplayerServiceRecord.GAMES_COMPLETED.getViewType() -> GamesCompletedView(context)
        MultiplayerServiceRecord.WIN_PERCENTAGE.getViewType() -> WinPercentageView(context)
        MultiplayerServiceRecord.KILL_DEATH_RATIO.getViewType() -> KillDeathRatioView(context)
        MultiplayerServiceRecord.MATCH_HISTORY.getViewType() -> MatchResultsView(context, mainController.statsService)
        else -> throw IllegalArgumentException("Unknown View %d".format(viewType))
    }
}

internal fun getMultiplayerStats(data: List<BaseServiceRecordResult>): List<BaseStats> {
    val warzoneStat = data.filterIsInstance(WarzoneResult::class.java).map { it.warzoneStat }
    val arenaStat = data.filterIsInstance(ArenaResult::class.java).map { it.arenaStat }
    val customStat = data.filterIsInstance(CustomResult::class.java).map { it.customStat }

    return listOf(warzoneStat, arenaStat, customStat).filter { it.isNotEmpty() }.map { it.first() }
}

class SpartanRankView(context: Context, val metadataService: MetadataService) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.multiplayer_spartan_rank) {
    override fun render(data: List<BaseServiceRecordResult>) {
        if (data.isEmpty()) {
            return
        }

        val result = data.first()

        val spartanRank = result.spartanRank
        val currentRankPromise = metadataService.getSpartanRank(spartanRank)
        val nextRankPromise = metadataService.getSpartanRank(spartanRank + 1)

        (findViewById(R.id.service_rank) as TextView).text = "%s%d".format(context.getString(R.string.sr), spartanRank)

        combine(currentRankPromise, nextRankPromise) successUi {

            val currentRank = currentRankPromise.get()
            val nextRank = nextRankPromise.get()

            val delta = nextRank.startXp - currentRank.startXp
            val progress = result.xp - currentRank.startXp

            val progressBar = (findViewById(R.id.xp_progress_bar) as ProgressBar)
            val progressBarEndValue = Math.round((progress.toFloat() / delta.toFloat()) * 100)
            val anim = ObjectAnimator.ofInt(progressBar, "progress", progressBarEndValue)
            anim.setDuration(DURATION_PER_PERCENT * progressBarEndValue)
            anim.start()

            (findViewById(R.id.xp_progress_text) as TextView).text = "%d / %d".format(progress, delta)
        }
    }

}

class PlaytimeView(context:Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.playtime else R.string.multiplayer_playtime)
        val stats = getMultiplayerStats(data)
        val period = stats
                .map { it.totalTimePlayed }
                .filter { !it.isNullOrEmpty() }
                .map { Period(it) }
                .reduce() { l, r -> l + r }

        dataView.text = PLAYTIME_FORMATTER.print(period.normalizedStandard())
    }
}

class GamesCompletedView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.games_completed else R.string.multiplayer_games_completed)
        val stats = getMultiplayerStats(data)
        val gamesCompleted = stats.map { it.totalGamesCompleted } .sum()
        dataView.text = "%d".format(gamesCompleted)
    }
}

class WinPercentageView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.win_percentage else R.string.multiplayer_win_percentage)
        val stats = getMultiplayerStats(data)
        val wins = stats.map { it.totalGamesWon }.sum()
        val played = stats.map { it.totalGamesCompleted }.sum()
        dataView.text = if (played != 0) {
            "%d%%".format(((wins.toFloat() / played.toFloat()) * 100).toInt())
        } else {
            "0%"
        }
    }
}

class KillDeathRatioView(context: Context) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.multiplayer_kdr) {
    override fun render(data: List<BaseServiceRecordResult>) {

        val kdrView = findViewById(R.id.kdr) as TextView
        val kdrLabelView = findViewById(R.id.kdr_label) as TextView
        val killsView = findViewById(R.id.total_kills) as TextView
        val deathsView = findViewById(R.id.total_deaths) as TextView

        kdrLabelView.setText(if (data.size == 1) R.string.kd else R.string.multiplayer_kd)

        val stats = getMultiplayerStats(data)
        val totalKills = stats.map { it.totalSpartanKills } .sum()
        val totalDeaths = stats.map { it.totalDeaths } .sum()
        val kdr = if (totalDeaths == 0) 1f else (totalKills.toFloat() / totalDeaths.toFloat())
        kdrView.text = "%.3f".format(kdr)
        killsView.text = "%d".format(totalKills)
        deathsView.text = "%d".format(totalDeaths)
    }
}

class MatchResultsView(context: Context, val statsService: StatsService) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.multiplayer_match_results) {

    override fun render(data: List<BaseServiceRecordResult>) {
        if (data.isEmpty()) {
            return
        }

        val player = data.first().player.gamertag
        val bundle = Bundle()
        bundle.putString(Constants.GAMERTAG, player)
        bundle.putInt(Constants.COUNT, MAX_GAME_HISTORY)

        statsService.onRequestMatchHistory(bundle) successUi {

            val lastGames = findViewById(R.id.last_games) as TextView
            // filter out all games player didn't finish
            val playerResults = it.map {
                val p = it.players.find { it.player.gamertag == player }
                p?.result?:0
            } .filter { it != 0 }

            lastGames.text = context.resources.getString(R.string.last_games, playerResults.size)

            val wins = findViewById(R.id.win_container) as ViewGroup
            val losses = findViewById(R.id.loss_container) as ViewGroup

            playerResults.forEach {
                wins.addView(createSegment(it, 3))
                losses.addView(createSegment(it, 1))
            }
        }

    }

    private fun createSegment(result: Int, expected: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.segment, null)
        if (result != expected) {
            return view
        } else if (result == 1) {
            view.setBackgroundResource(R.drawable.segment_loss)
        } else {
            view.setBackgroundResource(R.drawable.segment_win)
        }

        return view
    }

}
