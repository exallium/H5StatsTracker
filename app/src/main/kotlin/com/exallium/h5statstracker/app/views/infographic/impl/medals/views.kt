package com.exallium.h5statstracker.app.views.infographic.impl.medals

import android.content.Context
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.FrameLayout.INVISIBLE
import android.widget.FrameLayout.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.MedalSpriteTarget
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.squareup.picasso.Picasso

val getMedalViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when (viewType) {
        MedalViewType.TILE.getViewType() -> MedalTileView2(context)
        MedalViewType.DRAWER.getViewType() -> MedalDrawerView(context)
        else -> throw IllegalStateException("Unknown Medal view type %d".format(viewType))
    }
}

class MedalTileView2(context: Context) : InfographicView<MedalContainer>(context, R.layout.medal_tile_2) {
    private val medalImage = findViewById(R.id.medal_tile_image) as ImageView
    private val medalCount = findViewById(R.id.medal_tile_count) as TextView
    private lateinit var medalSpriteTarget: MedalSpriteTarget
    private lateinit var container: MedalContainer

    override fun render(data: MedalContainer) {
        val (aggregate, arrow) = data
        this.container = data
        medalCount.text = "%d".format(aggregate.count)
        medalSpriteTarget = MedalSpriteTarget(aggregate.spriteLocation, aggregate.name, medalImage)
        Picasso.with(context).load(aggregate.spriteLocation.spriteSheetUri).into(medalSpriteTarget)
    }
}

class MedalDrawerView(context: Context) : InfographicView<MedalContainer>(context, R.layout.medal_tile_2_drawer) {
    private val medalTitle = findViewById(R.id.medal_tile_title) as TextView
    private val medalDescription = findViewById(R.id.medal_tile_description) as TextView
    private val columnCount = context.resources.getInteger(R.integer.medal_tile_column_count)

    init {
        val params = StaggeredGridLayoutManager.LayoutParams(layoutParams)
        params.isFullSpan = true
        layoutParams = params
    }

    private val arrowMap = mapOf(
            0 to findViewById(R.id.arrow_1),
            1 to findViewById(R.id.arrow_2),
            2 to findViewById(R.id.arrow_3),
            3 to findViewById(R.id.arrow_4))

    override fun render(data: MedalContainer) {
        medalTitle.text = data.medalAggregate.name
        medalDescription.text = data.medalAggregate.description
        arrowMap.forEach { it.value.visibility = INVISIBLE }
        arrowMap[data.position % columnCount]?.visibility = VISIBLE
    }
}
