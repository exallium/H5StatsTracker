package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.DURATION_PER_PERCENT
import com.github.lzyzsd.circleprogress.DonutProgress
import com.squareup.picasso.Picasso
import nl.komponents.kovenant.ui.successUi

val getArenaServiceRecordViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when(viewType) {
        ArenaServiceRecord.TOP_CSR.getViewType() -> TopCsrView(context, mainController.metadataService)
        ArenaServiceRecord.TOP_CSR_CONDENSED.getViewType() -> TopCsrCondensedView(context, mainController.metadataService)
        else -> throw IllegalArgumentException("Unknown View Type: %d".format(viewType))
    }
}

abstract class BaseArenaResultView(context: Context, layoutId: Int) : InfographicView<List<BaseServiceRecordResult>>(context, layoutId) {
    override fun render(data: List<BaseServiceRecordResult>) {
        if (data.isEmpty() || data.first() !is ArenaResult) {
            return
        }

        val result: ArenaResult = data.first() as ArenaResult
        renderResult(result)
    }

    abstract fun renderResult(result: ArenaResult);
}

class TopCsrCondensedView(context: Context, val metadataService: MetadataService) : BaseArenaResultView(context, R.layout.arena_top_csr_condensed) {

    private val topVariant = findViewById(R.id.top_variant_name) as TextView
    private val csrImage = findViewById(R.id.csr_image) as ImageView
    private val csrTitle = findViewById(R.id.csr_title) as TextView

    override fun renderResult(result: ArenaResult) {
        if (result.arenaStat.highestCsrAttained != null) {
            visibility = View.VISIBLE

            metadataService.getPlaylist(result.arenaStat.highestCsrPlaylistId) successUi {
                topVariant.text = it?.name
            }

            metadataService.getCsrDesignation(result.arenaStat.highestCsrAttained.designationId.toLong()) successUi {
                val tier = it?.tiers?.find { it.id == result.arenaStat.highestCsrAttained.tier.toLong() }
                val name = it?.name?.toUpperCase()?:""
                tier?.let {
                    Picasso.with(context).load(tier.iconImageUrl).into(csrImage)
                    csrTitle.text = "%s %d".format(name, it.id)
                }
            }
        } else {
            visibility = View.GONE
        }
    }
}

class TopCsrView(context: Context, val metadataService: MetadataService) : BaseArenaResultView(context, R.layout.arena_top_csr) {

    private val topVariantName = findViewById(R.id.top_variant_name) as TextView
    private val csrIcon = findViewById(R.id.csr_image) as ImageView
    private val csrTitle = findViewById(R.id.csr_title) as TextView
    private val progressBar = findViewById(R.id.arena_progress) as DonutProgress

    override fun renderResult(result: ArenaResult) {
        if (result.arenaStat.highestCsrAttained != null) {
            metadataService.getPlaylist(result.arenaStat.highestCsrPlaylistId) successUi {
                topVariantName.text = it?.name
            }

            metadataService.getCsrDesignation(result.arenaStat.highestCsrAttained.designationId.toLong()) successUi {
                val tier = it?.tiers?.find { it.id == result.arenaStat.highestCsrAttained.tier.toLong() }
                val name = it?.name?:""
                tier?.let {
                    Picasso.with(context).load(tier.iconImageUrl).into(csrIcon)
                    csrTitle.text = "%s %d".format(name, it.id)
                }
            }

            val arenaProgress = result.arenaStat.highestCsrAttained.percentToNextTier
            val anim = ObjectAnimator.ofInt(progressBar, "progress", arenaProgress)
            anim.setDuration(DURATION_PER_PERCENT * arenaProgress.toLong())
            anim.start()
        } else {
            progressBar.progress = (result.arenaStat.topGameBaseVariants.minBy {
                it.gameBaseVariantRank
            }?.numberOfMatchesCompleted?.toInt()?:0) * 10
        }
    }
}