package com.exallium.h5statstracker.app.views.infographic.impl.medals

import android.os.Bundle
import com.exallium.h5.api.models.metadata.SpriteLocation
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.getStats
import nl.komponents.kovenant.all
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.successUi
import rx.subjects.PublishSubject

enum class MedalViewType {
    TILE, DRAWER;

    fun getViewType() = ordinal
}

class MedalTileDataFactory(val mainController: MainController, val bundle: Bundle) : InfographicDataFactory<MedalContainer> {
    override fun getViewModels(fn: (List<InfographicViewModel<MedalContainer>>) -> Unit) {
        val arenaRecordPromise = mainController.statsService.onRequestArenaServiceRecord(bundle)
        val warzoneRecordPromise = mainController.statsService.onRequestWarzoneServiceRecord(bundle)
        val customRecordPromise = mainController.statsService.onRequestCustomServiceRecord(bundle)
        val campaignRecordPromise = mainController.statsService.onRequestCampaignServiceRecord(bundle)

        combine(arenaRecordPromise, warzoneRecordPromise, customRecordPromise, campaignRecordPromise) success {
            val arena = arenaRecordPromise.get()
            val warzone = warzoneRecordPromise.get()
            val custom = customRecordPromise.get()
            val campaign = campaignRecordPromise.get()

            val stats = getStats(listOf(arena, warzone, custom, campaign).filterNotNull())
            val medalAwards = stats
                    .map { it.medalAwards }
                    .flatten()
                    .groupBy { it.medalId }

            val medalPromises = medalAwards.map { mainController.metadataService.getMedal(it.key) }
            all(medalPromises) successUi {

                val aggregates = it.map {
                    it?.let {
                        val totalCount = medalAwards[it.id]?.map { it.count } ?.sum()?.toLong() ?: 0L
                        MedalAggregate(it.name, it.description, it.spriteLocation, totalCount)
                    }
                } .filterNotNull().sortedByDescending { it.count }

                val containers = aggregates.mapIndexed { i, medalAggregate ->
                    val container = MedalContainer(medalAggregate, i % 4)
                    MedalContainerViewModel(container, MedalViewType.TILE)
                }

                fn(containers)
            }
        }
    }
}

data class MedalContainer(val medalAggregate: MedalAggregate, val position: Int)
data class MedalAggregate(val name: String, val description: String, val spriteLocation: SpriteLocation, val count: Long)

class MedalContainerViewModel(val medalContainer: MedalContainer, val medalViewType: MedalViewType) : InfographicViewModel<MedalContainer> {
    override fun getViewType() = medalViewType.getViewType()
    override fun getData() = medalContainer
}