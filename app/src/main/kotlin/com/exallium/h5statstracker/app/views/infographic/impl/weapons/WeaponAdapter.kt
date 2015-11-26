package com.exallium.h5statstracker.app.views.infographic.impl.weapons

import android.content.Context
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.DrawerInfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.DrawerInfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicView

class WeaponAdapter(viewFactory: (Int, Context, MainController) -> InfographicView<WeaponContainer>,
                    dataFactory: DrawerInfographicDataFactory<WeaponContainer>,
                    mainController: MainController) : DrawerInfographicAdapter<WeaponContainer>(viewFactory, dataFactory, mainController) {
    override fun getDrawerType() = WeaponViewType.DRAWER.getViewType()
    override fun getColumnCount(context: Context) = context.resources.getInteger(R.integer.weapon_column_count)
}