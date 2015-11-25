package com.exallium.h5statstracker.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.exallium.h5.api.models.metadata.Medal
import com.exallium.h5.api.models.metadata.SpriteLocation
import com.exallium.h5statstracker.app.views.GamertagContentView
import com.exallium.h5statstracker.app.views.infographic.InfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.impl.medals.MedalAggregateViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.medals.MedalTileDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.medals.getMedalViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena.ArenaDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.campaign.CampaignDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.getViewByType
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.custom.CustomDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.multiplayer.MultiplayerDataFactory
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.warzone.WarzoneDataFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import timber.log.Timber
import java.util.*


public fun getRouterView(request: Router.Request, context: Context, controller: MainController): View {

    if (request.route == Router.Route.GAMERTAG) {
        return GamertagContentView(context, controller, request.bundle)
    }

    val columnCount = context.resources.getInteger(when (request.route) {
        else -> R.integer.service_record_column_count
    })

    val view = RecyclerView(context)
    view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    view.layoutManager = StaggeredGridLayoutManager(
            columnCount,
            StaggeredGridLayoutManager.VERTICAL)
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
        Router.Route.MEDALS -> InfographicAdapter(
                getMedalViewByType,
                MedalTileDataFactory(controller, request.bundle), controller)
        else -> throw IllegalStateException("Unknown Route")
    }
    return view
}

class MedalSpriteTarget(val medalSpriteLocation: SpriteLocation, val medalName: String, val view: ImageView) : Target {

    companion object {
        val MEDAL_CACHE = HashMap<String, Bitmap?>()
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        Timber.v("Preparing to load Medal Sprite")
    }

    override fun onBitmapFailed(errorDrawable: Drawable?) {
        Timber.v("Failed to load Medal Sprite")
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        val transformation = MedalSpriteSheetTransformation(medalSpriteLocation, medalName)
        val cachedMedal = MEDAL_CACHE[transformation.key()]
        if (cachedMedal == null) {
            val medalImage = transformation.transform(bitmap)
            view.setImageBitmap(medalImage)
            MEDAL_CACHE[transformation.key()] = medalImage
        } else {
            view.setImageBitmap(cachedMedal)
        }
    }

}

class MedalSpriteSheetTransformation(val spriteLocation: SpriteLocation, val medalName: String) : Transformation {

    private val x: Int
    private val y: Int
    private val w: Int
    private val h: Int

    init {
        x = spriteLocation.left
        y = spriteLocation.top
        w = spriteLocation.width
        h = spriteLocation.height
    }

    override fun key(): String = "tf:medal:%s:%d:%d:%d:%d".format(medalName, x, y, w, h)

    override fun transform(source: Bitmap?): Bitmap? {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val src = Rect(x, y, x + w, y + h)
        val dst = Rect(0, 0, w, h)
        c.drawBitmap(source, src, dst, null)
        return b
    }

}
