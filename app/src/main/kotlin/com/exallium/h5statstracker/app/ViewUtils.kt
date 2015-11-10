package com.exallium.h5statstracker.app

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.exallium.h5.api.models.stats.servicerecords.ArenaStat
import com.exallium.h5statstracker.app.views.GamertagContentView
import com.exallium.h5statstracker.app.views.infographic.InfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaServiceRecordDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.getArenaInfographicViewByType


public fun getRouterView(request: Router.Request, context: Context, controller: MainController): View {
    return when(request.route) {
        Router.Route.GAMERTAG -> GamertagContentView(context, controller, request.bundle)
        Router.Route.ARENA_SERVICE_RECORD -> buildServiceRecordView(request, context, controller)
        else -> throw IllegalStateException("Unknown Route")
    }
}

private fun buildServiceRecordView(request: Router.Request, context: Context, controller: MainController): View {
    val view = RecyclerView(context)
    view.layoutManager = LinearLayoutManager(context)
    when (request.route) {
        Router.Route.ARENA_SERVICE_RECORD -> view.adapter = InfographicAdapter(
                getArenaInfographicViewByType,
                ArenaServiceRecordDataFactory(controller, request.bundle))
        else -> throw IllegalStateException("Unknown Service Record Type")
    }
    return view
}

public interface ViewCallback<in T> {
    fun onResult(result: T)
    fun onError()
}
