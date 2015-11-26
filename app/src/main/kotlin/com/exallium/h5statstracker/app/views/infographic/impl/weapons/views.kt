package com.exallium.h5statstracker.app.views.infographic.impl.weapons

import android.content.Context
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.ImageView
import android.widget.TextView
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.InfographicView
import com.squareup.picasso.Picasso

val getWeaponsViewByType = { viewType: Int, context: Context, mainController: MainController ->
    when (viewType) {
        WeaponViewType.WEAPON.getViewType() -> WeaponView(context)
        WeaponViewType.DRAWER.getViewType() -> WeaponDrawerView(context)
        else -> throw IllegalArgumentException("Unknown Weapon ViewType %d".format(viewType))
    }
}

class WeaponView(context: Context) : InfographicView<WeaponContainer>(context, R.layout.weapon) {

    val weaponImage = findViewById(R.id.weaponImage) as ImageView
    val weaponName = findViewById(R.id.weaponName) as TextView

    override fun render(data: WeaponContainer) {
        weaponName.text = data.weaponAggregate.weapon.name.toUpperCase()
        Picasso.with(context).load(data.weaponAggregate.weapon.largeIconImageUrl).into(weaponImage)
    }
}

class WeaponDrawerView(context: Context) : InfographicView<WeaponContainer>(context, R.layout.weapon_drawer) {

    val kills = findViewById(R.id.weapon_kills) as TextView
    val accuracy = findViewById(R.id.weapon_accuracy) as TextView
    val headshots = findViewById(R.id.headshots) as TextView
    val shotsFired = findViewById(R.id.shots_fired) as TextView
    val shotsLanded = findViewById(R.id.shots_landed) as TextView
    val columnCount = context.resources.getInteger(R.integer.weapon_column_count)

    private val arrowMap = mapOf(
            0 to findViewById(R.id.arrow_1),
            1 to findViewById(R.id.arrow_2))

    init {
        val params = StaggeredGridLayoutManager.LayoutParams(layoutParams)
        params.isFullSpan = true
        layoutParams = params
    }

    override fun render(data: WeaponContainer) {
        val aggregate = data.weaponAggregate
        kills.text = "%d".format(aggregate.kills)
        accuracy.text = if (aggregate.shotsFired != 0L)
            "%d%%".format(Math.round((aggregate.shotsLanded.toFloat() / aggregate.shotsFired.toFloat()) * 100))
        else "0"
        headshots.text = "%d".format(aggregate.headshots)
        shotsFired.text = "%d".format(aggregate.shotsFired)
        shotsLanded.text = "%d".format(aggregate.shotsLanded)
        arrowMap.forEach { it.value.visibility = INVISIBLE }
        arrowMap[data.position % columnCount]?.visibility = VISIBLE
    }
}