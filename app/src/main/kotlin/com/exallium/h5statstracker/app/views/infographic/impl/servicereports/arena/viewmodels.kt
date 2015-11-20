package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena

import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5.api.models.stats.servicerecords.BaseServiceRecordResult
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel

internal val SERVICE_RECORD_ARENA_PREFIX = 2000
enum class ArenaServiceRecord {
    TOP_CSR, TOP_CSR_CONDENSED;

    fun getViewType(): Int {
        return ordinal + SERVICE_RECORD_ARENA_PREFIX
    }
}

class ArenaViewModel(val list: List<BaseServiceRecordResult>, val record: ArenaServiceRecord) : InfographicViewModel<List<BaseServiceRecordResult>> {
    override fun getViewType() = record.getViewType()

    override fun getData() = list.filterIsInstance(ArenaResult::class.java)

}