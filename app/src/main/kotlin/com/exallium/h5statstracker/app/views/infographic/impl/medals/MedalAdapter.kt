package com.exallium.h5statstracker.app.views.infographic.impl.medals

import android.content.Context
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import com.exallium.h5statstracker.app.views.infographic.DrawerInfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.DrawerInfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicView

class MedalAdapter(viewFactory: (Int, Context, MainController) -> InfographicView<MedalContainer>,
                   dataFactory: DrawerInfographicDataFactory<MedalContainer>,
                   mainController: MainController) : DrawerInfographicAdapter<MedalContainer>(viewFactory, dataFactory, mainController) {
    override fun getDrawerType() = MedalViewType.DRAWER.getViewType()
    override fun getColumnCount(context: Context) = context.resources.getInteger(R.integer.medal_tile_column_count)
}