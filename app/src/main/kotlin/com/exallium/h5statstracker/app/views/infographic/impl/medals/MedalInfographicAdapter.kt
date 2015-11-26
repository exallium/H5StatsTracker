package com.exallium.h5statstracker.app.views.infographic.impl.medals

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.InfographicAdapter
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicView

public class MedalInfographicAdapter(viewFactory: (Int, Context, MainController) -> InfographicView<MedalContainer>,
                                        dataFactory: InfographicDataFactory<MedalContainer>,
                                        mainController: MainController)
: InfographicAdapter<MedalContainer>(viewFactory, dataFactory, mainController) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfographicViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (viewType == MedalViewType.TILE.getViewType()) {
            holder.onClick = onClick
        }
        return holder
    }

    val onClick = object : OnClickListener {
        override fun onClick(viewHolder: InfographicViewHolder) {

            val viewModel = viewModels[viewHolder.position]
            val position = viewHolder.position

            val drawerOffset = viewModels.take(position)
                    .filter { it.getViewType() == MedalViewType.DRAWER.getViewType() }
                    .count()

            val arrow = (viewHolder.position - drawerOffset) % 4
            val index = position + 4 - arrow

            val atPosition = if (viewModels.size > index) viewModels[index] else null
            val container = viewModel.getData()
            val drawerViewModel = MedalContainerViewModel(container, MedalViewType.DRAWER)

            if (atPosition != null && atPosition.getViewType() == MedalViewType.DRAWER.getViewType()) {
                if (atPosition.getData().equals(container)) {
                    viewModels.removeAt(index)
                    notifyItemRemoved(index)
                } else {
                    viewModels.removeAt(index)
                    viewModels.add(index, drawerViewModel)
                    notifyItemChanged(index)
                }
            } else {
                viewModels.add(index, drawerViewModel)
                notifyItemInserted(index)
            }
        }
    }
}