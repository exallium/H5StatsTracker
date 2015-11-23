package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.custom

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.CustomResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.squareup.picasso.Picasso
import nl.komponents.kovenant.ui.successUi

val getCustomServiceRecordViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when(viewType) {
        CustomServiceRecord.TOP_GAME_BASE_VARIANT.getViewType() -> TopVariantView(context, mainController.metadataService)
        else -> throw IllegalArgumentException("Unknown View Type: %d".format(viewType))
    }
}

class TopVariantView(context: Context, val metadataService: MetadataService) : InfographicView<List<BaseServiceRecordResult>>(context, R.layout.custom_top_variant) {
    override fun render(data: List<BaseServiceRecordResult>) {
        val result = data.filterIsInstance(CustomResult::class.java).first()

        val topVariant = result.customStat.topGameBaseVariants.minBy {
            it.gameBaseVariantRank
        }

        if (topVariant == null) {
            visibility = View.GONE
            return
        }

        val iconView = findViewById(R.id.variant_icon) as ImageView
        val nameView = findViewById(R.id.variant_name) as TextView

        metadataService.getGameBaseVariant(topVariant.gameBaseVariantId) successUi {
            if (it != null) {
                Picasso.with(context).load(it.iconUrl).into(iconView)
                nameView.text = it.name.toUpperCase()
            }
        }
    }

}