package com.exallium.h5statstracker.app.services

import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.metadata.*
import com.exallium.h5statstracker.app.Units
import com.exallium.h5statstracker.app.model.BaseCache
import nl.komponents.kovenant.async
import nl.komponents.kovenant.then
import retrofit.Call
import timber.log.Timber

class MetadataService(val apiFactory: ApiFactory, val cacheService: CacheService) {

    companion object {
        val SPARTAN_RANKS_KEY = "spartanRanks"
        val SPARTAN_RANKS_TTL = Units.MONTH_MILLIS
        val PLAYLISTS_KEY = "playlists"
        val PLAYLISTS_TTL = Units.DAY_MILLIS
        val CSR_DESIGNATIONS_KEY = "csrDesignations"
        val CSR_DESIGNATIONS_TTL = Units.MONTH_MILLIS
        val IMPULSES_KEY = "impulses"
        val IMPULSES_TTL = Units.MONTH_MILLIS
        val GAME_BASE_VARAINTS_KEY = "gameBaseVariants"
        val GAME_BASE_VARIANTS_TTL = Units.DAY_MILLIS * 7
        val FLEXIBLE_STATS = "flexibleStats"
        val FLEXIBLE_STATS_TTL = Units.DAY_MILLIS * 7
    }

    private fun <E> getList(key: String, ttlMillis: Long, elementClass: Class<E>, call: Call<List<E>>) =
        cacheService.readListFromCache(key, elementClass, ttlMillis, {
            val response = call.execute()
            if (!response.isSuccess) {
                listOf()
            } else {
                response.body()
            }
        })

    fun getSpartanRanks() = getList(
            SPARTAN_RANKS_KEY,
            SPARTAN_RANKS_TTL,
            SpartanRank::class.java,
            apiFactory.metadata.spartanRanks)

    fun getSpartanRank(rank: Int) = getSpartanRanks() then {
        it[rank - 1]
    }

    fun getPlaylists() = getList(
            PLAYLISTS_KEY,
            PLAYLISTS_TTL,
            Playlist::class.java,
            apiFactory.metadata.playlists)

    fun getPlaylist(playlistId: String) = getPlaylists() then {
        it.find { playlistId == it.id }
    }

    fun getCsrDesignations() = getList(
            CSR_DESIGNATIONS_KEY,
            CSR_DESIGNATIONS_TTL,
            CSRDesignation::class.java,
            apiFactory.metadata.csrDesignations)

    fun getCsrDesignation(id: Long) = getCsrDesignations() then {
        it.find { it.id == id }
    }

    fun getImpulses() = getList(
            IMPULSES_KEY,
            IMPULSES_TTL,
            Impulse::class.java,
            apiFactory.metadata.impulses)

    fun getImpulse(id: Long) = getImpulses() then {
        it.find { it.id == id }
    }

    fun getGameBaseVariants() = getList(
            GAME_BASE_VARAINTS_KEY,
            GAME_BASE_VARIANTS_TTL,
            GameBaseVariant::class.java,
            apiFactory.metadata.gameBaseVariants)

    fun getGameBaseVariant(id: String) = getGameBaseVariants() then {
        it.find { it.id == id }
    }

    fun getFlexibleStats() = getList(
            FLEXIBLE_STATS,
            FLEXIBLE_STATS_TTL,
            FlexibleStat::class.java,
            apiFactory.metadata.flexibleStats)

    fun getFlexibleStat(id: String) = getFlexibleStats() then {
        it.find { it.id == id }
    }
}