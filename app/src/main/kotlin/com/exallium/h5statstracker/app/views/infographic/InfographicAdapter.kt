package com.exallium.h5statstracker.app.views.infographic

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.exallium.h5statstracker.app.services.MetadataService
import java.util.*

public class InfographicAdapter<T>(val viewFactory: (Int, Context, MetadataService) -> InfographicView<T>,
                                   val dataFactory: InfographicDataFactory<T>,
                                   val metadataService: MetadataService)
    : RecyclerView.Adapter<InfographicAdapter.InfographicViewHolder>() {

    init {
        dataFactory.getViewModels {
            viewModels.addAll(it)
            notifyDataSetChanged()
        }
    }

    private val viewModels = ArrayList<InfographicViewModel<T>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfographicViewHolder = InfographicViewHolder(viewFactory(viewType, parent.context, metadataService))

    override fun getItemCount(): Int = viewModels.size

    override fun onBindViewHolder(holder: InfographicViewHolder, position: Int) {
        holder.onBind(viewModels[position])
    }

    override fun getItemViewType(position: Int) = viewModels[position].getViewType();

    public inner class InfographicViewHolder(val infoView: InfographicView<T>) : RecyclerView.ViewHolder(infoView) {

        public fun onBind(viewModel: InfographicViewModel<T>) {
            infoView.render(viewModel.getData())
        }

    }

}
