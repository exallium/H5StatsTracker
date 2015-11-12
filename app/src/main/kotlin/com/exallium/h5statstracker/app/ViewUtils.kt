package com.exallium.h5statstracker.app

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.exallium.h5statstracker.app.views.GamertagContentView
import com.exallium.h5statstracker.app.views.infographic.InfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaServiceRecordDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.getArenaInfographicViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary.SummaryDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.summary.getSummaryInfographicViewByType


public fun getRouterView(request: Router.Request, context: Context, controller: MainController): View {
    return when(request.route) {
        Router.Route.GAMERTAG -> GamertagContentView(context, controller, request.bundle)
        Router.Route.SERVICE_RECORD_SUMMARY -> buildServiceRecordView(request, context, controller)
        Router.Route.ARENA_SERVICE_RECORD -> buildServiceRecordView(request, context, controller)
        else -> throw IllegalStateException("Unknown Route")
    }
}

private fun buildServiceRecordView(request: Router.Request, context: Context, controller: MainController): View {
    val view = RecyclerView(context)
    view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    view.layoutManager = LinearLayoutManager(context)
    when (request.route) {
        Router.Route.SERVICE_RECORD_SUMMARY -> view.adapter = InfographicAdapter(
                getSummaryInfographicViewByType,
                SummaryDataFactory(controller, request.bundle), controller.metadataService)
        Router.Route.ARENA_SERVICE_RECORD -> view.adapter = InfographicAdapter(
                getArenaInfographicViewByType,
                ArenaServiceRecordDataFactory(controller, request.bundle), controller.metadataService)
        else -> throw IllegalStateException("Unknown Service Record Type")
    }
    return view
}

public interface ViewCallback<in T> {
    fun onResult(result: T)
    fun onError()
}
