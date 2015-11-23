package com.exallium.h5statstracker.app

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.exallium.h5statstracker.app.views.GamertagContentView
import com.exallium.h5statstracker.app.views.infographic.InfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.campaign.CampaignDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.getViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.custom.CustomDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.warzone.WarzoneDataFactory


public fun getRouterView(request: Router.Request, context: Context, controller: MainController): View {

    if (request.route == Router.Route.GAMERTAG) {
        return GamertagContentView(context, controller, request.bundle)
    }

    val view = RecyclerView(context)
    view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    view.layoutManager = LinearLayoutManager(context)
    view.adapter = when (request.route) {
        Router.Route.SERVICE_RECORD_SUMMARY -> InfographicAdapter(
                getViewByType,
                MultiplayerDataFactory(controller, request.bundle), controller)
        Router.Route.ARENA_SERVICE_RECORD -> InfographicAdapter(
                getViewByType,
                ArenaDataFactory(controller.statsService, request.bundle), controller)
        Router.Route.WARZONE_SERVICE_RECORD -> InfographicAdapter(
                getViewByType,
                WarzoneDataFactory(controller.statsService, request.bundle), controller)
        Router.Route.CUSTOM_SERVICE_RECORD -> InfographicAdapter(
                getViewByType,
                CustomDataFactory(controller.statsService, request.bundle), controller)
        Router.Route.CAMPAIGN_SERVICE_RECORD -> InfographicAdapter(
                getViewByType,
                CampaignDataFactory(controller.statsService, request.bundle), controller)
        else -> throw IllegalStateException("Unknown Route")
    }
    return view
}
