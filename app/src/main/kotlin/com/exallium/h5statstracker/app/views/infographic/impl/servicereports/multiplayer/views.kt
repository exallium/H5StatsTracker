package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.exallium.h5.api.models.stats.common.BaseStats
import com.exallium.h5.api.models.stats.servicerecords.*
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
import java.util.*

internal val MAX_GAME_HISTORY = 20

val getMultiplayerViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when (viewType) {
        MultiplayerServiceRecord.SPARTAN_RANK.getViewType() -> SpartanRankView(context, mainController.metadataService)
        MultiplayerServiceRecord.PLAYTIME.getViewType() -> PlaytimeView(context)
        MultiplayerServiceRecord.GAMES_COMPLETED.getViewType() -> GamesCompletedView(context)
        MultiplayerServiceRecord.WIN_PERCENTAGE.getViewType() -> WinPercentageView(context)
        MultiplayerServiceRecord.KILL_DEATH_RATIO.getViewType() -> KillDeathRatioView(context)
        MultiplayerServiceRecord.MATCH_HISTORY.getViewType() -> MatchResultsView(context, mainController.statsService)
        MultiplayerServiceRecord.ASSASSINATIONS.getViewType() -> AssassinationsView(context)
        MultiplayerServiceRecord.HEADSHOTS.getViewType() -> HeadshotsView(context)
        MultiplayerServiceRecord.ASSISTS.getViewType() -> AssistsView(context)
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
        val periodList = stats
                .map { it.totalTimePlayed }
                .filter { !it.isNullOrEmpty() }
                .map { Period(it) }
        val period = if (periodList.isNotEmpty()) periodList.reduce() { l, r -> l + r } else Period()

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

class AssassinationsView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.assassinations else R.string.multiplayer_assassinations)
        val stats = getMultiplayerStats(data)
        dataView.text = "%d".format(stats.map { it.totalAssassinations } .sum())
    }
}

class HeadshotsView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.headshots else R.string.multiplayer_headshots)
        val stats = getMultiplayerStats(data)
        dataView.text = "%d".format(stats.map { it.totalHeadshots } .sum())
    }
}

class AssistsView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(if (data.size == 1) R.string.assists else R.string.multiplayer_assists)
        val stats = getMultiplayerStats(data)
        dataView.text = "%d".format(stats.map { it.totalAssists } .sum())
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

    companion object {
        val MODE_MAP = mapOf(
                ArenaResult::class.java to "arena",
                WarzoneResult::class.java to "warzone",
                CustomResult::class.java to "custom",
                CampaignResult::class.java to "campaign")
    }

    override fun render(data: List<BaseServiceRecordResult>) {
        if (data.isEmpty()) {
            return
        }

        val player = data.first().player.gamertag
        val bundle = Bundle()
        bundle.putString(Constants.GAMERTAG, player)
        bundle.putInt(Constants.COUNT, MAX_GAME_HISTORY)

        val modes = data.map { MODE_MAP[it.javaClass] }
        bundle.putStringArrayList(Constants.GAME_MODES, ArrayList(modes))

        val stats = getMultiplayerStats(data)
        val totalWins = stats.map { it.totalGamesWon } .sum()
        val totalLosses = stats.map { it.totalGamesLost } .sum()

        (findViewById(R.id.total_wins) as TextView).text = "%d".format(totalWins)
        (findViewById(R.id.total_losses) as TextView).text = "%d".format(totalLosses)

        statsService.onRequestMatchHistory(bundle) successUi {

            val lastGames = findViewById(R.id.last_games) as TextView
            // filter out all games player didn't finish
            val playerResults = it.map {
                val p = it.players.find { it.player.gamertag == player }
                p?.result?:0
            } .filter { it != 0 }

            lastGames.text = context.resources.getString(R.string.last_games, playerResults.size)

            val seg = findViewById(R.id.segments) as SegmentView
            seg.resultSet = playerResults
        }

    }

}

class SegmentView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    var resultSet: List<Int> = listOf()
        get() { return if (isInEditMode) listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3) else field }
        set(value: List<Int>) {
            field = value
            postInvalidate()
        }

    val paint = Paint()

    val winColor = context.resources.getColor(R.color.halo_blue)
    val lossColor = context.resources.getColor(R.color.halo_dark_orange)
    val muteColor = context.resources.getColor(R.color.halo_lt_grey)
    val divColor = context.resources.getColor(R.color.halo_divider_grey)

    companion object {
        val DIVIDER_WIDTH = 2
        val DIVIDER_COMP = DIVIDER_WIDTH / 2
    }

    override fun onDraw(canvas: Canvas) {
        if (resultSet.isNotEmpty()) {
            val segmentWidth = (canvas.width.toFloat() / resultSet.size.toFloat()).toInt()
            val colorWidth = segmentWidth / 2
            val segmentHeight = canvas.height / 2 - DIVIDER_COMP

            val topRect = Rect(0, 0, colorWidth, segmentHeight)
            val botRect = Rect(0, segmentHeight + DIVIDER_WIDTH, segmentWidth, canvas.height)


            paint.color = divColor
            val divRect = Rect(0, segmentHeight, canvas.width, segmentHeight + DIVIDER_WIDTH)
            canvas.drawRect(divRect, paint)

            var i: Int = 0
            resultSet.forEach {
                val topColor = if (it == 3) winColor else muteColor
                val bottomColor = if (it == 1) lossColor else muteColor

                topRect.left = i * segmentWidth
                botRect.left = i * segmentWidth
                topRect.right = topRect.left + colorWidth
                botRect.right = botRect.left + colorWidth

                paint.color = topColor
                canvas.drawRect(topRect, paint)

                paint.color = bottomColor
                canvas.drawRect(botRect, paint)
                i++
            }
        }
    }
}

