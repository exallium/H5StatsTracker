package com.exallium.h5statstracker.app.views.infographic.impl.medals

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.MedalSpriteTarget
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.squareup.picasso.Picasso
import timber.log.Timber

val getMedalViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when (viewType) {
        MedalViewType.TILE.getViewType() -> MedalTileView(context)
        else -> throw IllegalStateException("Unknown Medal view type %d".format(viewType))
    }
}

class MedalTileView(context: Context) : InfographicView<MedalAggregate>(context, R.layout.medal_tile) {

    private val medalTitle = findViewById(R.id.medal_tile_title) as TextView
    private val medalImage = findViewById(R.id.medal_tile_image) as ImageView
    private val medalDescription = findViewById(R.id.medal_tile_description) as TextView
    private val medalCount = findViewById(R.id.medal_tile_count) as TextView
    private lateinit var medalSpriteTarget: MedalSpriteTarget

    override fun render(data: MedalAggregate) {
        medalCount.text = "%d".format(data.count)
        medalTitle.text = data.name
        medalDescription.text = data.description
        medalSpriteTarget = MedalSpriteTarget(data.spriteLocation, data.name, medalImage)
        Picasso.with(context).load(data.spriteLocation.spriteSheetUri).into(medalSpriteTarget)
    }

}
