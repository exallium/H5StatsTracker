package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena

import android.content.Context
import android.widget.TextView
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.services.MetadataService
import com.exallium.h5statstracker.app.views.infographic.InfographicView

val getArenaInfographicViewByType = { viewType: Int, context: Context, metadataService: MetadataService ->
    when (viewType) {
        0 -> HeaderView(context)
        else -> throw IllegalStateException("Unknown ViewType %d".format(viewType))
    }
}

public class HeaderView(context: Context) : InfographicView<ArenaResult>(context, R.layout.servicereport_header) {
    override fun render(data: ArenaResult) {
        (findViewById(R.id.header_title) as TextView).text = "Arena Service Record for %s".format(data.player.gamertag)
    }
}
