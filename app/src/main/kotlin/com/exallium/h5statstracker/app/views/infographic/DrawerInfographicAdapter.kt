package com.exallium.h5statstracker.app.views.infographic

import android.content.Context
import android.view.ViewGroup
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.impl.medals.MedalViewType

public abstract class DrawerInfographicAdapter<T>(viewFactory: (Int, Context, MainController) -> InfographicView<T>,
                                                  dataFactory: DrawerInfographicDataFactory<T>,
                                                  mainController: MainController)
: InfographicAdapter<T>(viewFactory, dataFactory, mainController) {

    abstract fun getDrawerType(): Int
    abstract fun getColumnCount(context: Context): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfographicViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (viewType != getDrawerType()) {
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

            val columnCount = getColumnCount(viewHolder.infoView.context)
            val arrow = (viewHolder.position - drawerOffset) % columnCount
            val index = Math.min(position + columnCount - arrow, viewModels.size)

            val (oldModel, oldIndex) = if (viewModels.size > index)
                Pair(viewModels[index], index)
            else
                Pair(viewModels.last(), viewModels.lastIndex)

            val container = viewModel.getData()
            val drawerViewModel = dataFactory.getDrawerViewModel(container)

            if (oldModel.getViewType() == getDrawerType()) {
                if (oldModel.getData()?.equals(container)?:false) {
                    viewModels.removeAt(oldIndex)
                    notifyItemRemoved(oldIndex)
                } else {
                    viewModels.removeAt(oldIndex)
                    viewModels.add(oldIndex, drawerViewModel)
                    notifyItemChanged(oldIndex)
                }
            } else {
                viewModels.add(index, drawerViewModel)
                notifyItemInserted(index)
            }
        }
    }
}