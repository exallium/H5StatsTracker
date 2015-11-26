package com.exallium.h5statstracker.app.views.infographic.impl.weapons

import android.os.Bundle
import com.exallium.h5.api.models.metadata.Weapon
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.views.infographic.DrawerInfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.impl.servicereports.common.getStats
import nl.komponents.kovenant.all
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.ui.promiseOnUi

enum class WeaponViewType {

    WEAPON, DRAWER;

    fun getViewType() = ordinal
}

class WeaponDataFactory(val mainController: MainController, val bundle: Bundle) : DrawerInfographicDataFactory<WeaponContainer> {
    override fun getDrawerViewModel(data: WeaponContainer) = WeaponViewModel(data, WeaponViewType.DRAWER)

    override fun getViewModels(fn: (List<InfographicViewModel<WeaponContainer>>) -> Unit) {
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
            val weaponsMap = stats.map { it.weapons } .flatten() .groupBy { it.weaponId.stockId }

            all(weaponsMap.keys.map { mainController.metadataService.getWeapon(it) }) success {
                val aggregates = it.filterNotNull().map {
                    val weaponStats = weaponsMap[it.id]
                    if (weaponStats != null) {
                        WeaponAggregate(it,
                                weaponStats.map { it.totalKills } .sum().toLong(),
                                weaponStats.map { it.totalShotsLanded } .sum().toLong(),
                                weaponStats.map { it.totalShotsFired } .sum().toLong(),
                                weaponStats.map { it.totalHeadshots } .sum().toLong())
                    } else {
                        null
                    }
                } .filterNotNull().sortedByDescending { it.kills }

                val viewModels = aggregates.mapIndexed { i, weaponViewModel -> WeaponContainer(weaponViewModel, i) }
                    .map { WeaponViewModel(it, WeaponViewType.WEAPON) }
                promiseOnUi {
                    fn(viewModels)
                }
            }
        }
    }
}

data class WeaponContainer(val weaponAggregate: WeaponAggregate, val position: Int)
data class WeaponAggregate(val weapon: Weapon, val kills: Long, val shotsLanded: Long, val shotsFired: Long, val headshots: Long)

class WeaponViewModel(val weaponContainer: WeaponContainer, val weaponViewType: WeaponViewType) : InfographicViewModel<WeaponContainer> {
    override fun getViewType() = weaponViewType.getViewType()
    override fun getData() = weaponContainer
}