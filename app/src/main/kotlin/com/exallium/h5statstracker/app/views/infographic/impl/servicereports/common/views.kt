package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common

import android.content.Context
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.getArenaServiceRecordViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.custom.getCustomServiceRecordViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.getMultiplayerViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.warzone.getWarzoneServiceRecordViewByType
import org.joda.time.format.PeriodFormatterBuilder

val getViewByType: (Int, Context, MainController) -> InfographicView<List<BaseServiceRecordResult>> = { viewType: Int, context: Context, mainController: MainController ->
    val viewGroup: Int = viewType / 1000
    when (viewGroup) {
        1 -> getMultiplayerViewByType(viewType, context, mainController)
        2 -> getArenaServiceRecordViewByType(viewType, context, mainController)
        3 -> getWarzoneServiceRecordViewByType(viewType, context, mainController)
        4 -> getCustomServiceRecordViewByType(viewType, context, mainController)
        else -> throw IllegalArgumentException("Unknown View group %d".format(viewGroup))
    }
}

val PLAYTIME_FORMAT = PeriodFormatterBuilder()
        .minimumPrintedDigits(2)
        .appendHours()
        .appendSuffix(":")
        .appendMinutes()
        .appendSuffix(":")
        .appendSeconds()
        .toFormatter()

abstract class CommonTextView<T>(context: Context) : InfographicView<T>(context, R.layout.common_text_view) {
    override fun render(data: T) {
        if (data is List<*> && data.isEmpty()) {
            return
        }

        val dataView = findViewById(R.id.common_text_data) as TextView
        val labelView = findViewById(R.id.common_text_label) as TextView
        bindText(data, labelView, dataView)
    }

    abstract fun bindText(data: T, labelView: TextView, dataView: TextView)
}