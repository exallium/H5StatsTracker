package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.warzone

import android.content.Context
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5.api.models.stats.servicerecords.WarzoneResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.model.Impulses
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.CommonTextView

val getWarzoneServiceRecordViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when(viewType) {
        WarzoneServiceRecord.BASES_CAPTURED.getViewType() -> BasesCapturedView(context)
        WarzoneServiceRecord.CORE_DESTRUCTION_VICTORIES.getViewType() -> CoreDestructionVictoriesView(context)
        else -> throw IllegalArgumentException("Unknown View Type: %d".format(viewType))
    }
}

class BasesCapturedView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(R.string.bases_captured)
        val warzoneResult = data .filterIsInstance(WarzoneResult::class.java) .first()
        dataView.text = "%d".format(warzoneResult.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_BASE_CAPTURED
        }?.count?:0)
    }
}

class CoreDestructionVictoriesView(context: Context) : CommonTextView<List<BaseServiceRecordResult>>(context) {
    override fun bindText(data: List<BaseServiceRecordResult>, labelView: TextView, dataView: TextView) {
        labelView.setText(R.string.core_destruction_victories)
        val warzoneResult = data .filterIsInstance(WarzoneResult::class.java) .first()
        dataView.text = "%d".format(warzoneResult.warzoneStat.impulses.find {
            it.id == Impulses.WARZONE_CORE_DESTRUCTION_VICTORIES
        }?.count?:0)
    }
}
