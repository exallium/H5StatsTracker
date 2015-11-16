package com.exallium.h5statstracker.app.services

import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.metadata.CSRDesignation
import com.exallium.h5.api.models.metadata.Impulse
import com.exallium.h5.api.models.metadata.Playlist
import com.exallium.h5.api.models.metadata.SpartanRank
import com.exallium.h5statstracker.app.Units
import com.exallium.h5statstracker.app.model.BaseCache
import nl.komponents.kovenant.async
import nl.komponents.kovenant.then
import retrofit.Call
import timber.log.Timber

class MetadataService(val apiFactory: ApiFactory, val memoryCache: BaseCache, val diskCache: BaseCache) {

    companion object {
        val SPARTAN_RANKS_KEY = "spartanRanks"
        val SPARTAN_RANKS_TTL = Units.MONTH_MILLIS
        val PLAYLISTS_KEY = "playlists"
        val PLAYLISTS_TTL = Units.DAY_MILLIS
        val CSR_DESIGNATIONS_KEY = "csrDesignations"
        val CSR_DESIGNATIONS_TTL = Units.MONTH_MILLIS
        val IMPULSES_KEY = "impulses"
        val IMPULSES_TTL = Units.MONTH_MILLIS
    }

    private fun <E> getList(key: String, ttlMillis: Long, elementClass: Class<E>, call: Call<List<E>>) = async {
        val memoryRanks = memoryCache.readList(key, elementClass)
        if (memoryRanks.size != 0) {
            Timber.i("Memory Cache Hit: $key")
            memoryRanks
        } else {
            Timber.i("Memory Cache Miss: $key")
            val diskRanks = diskCache.readList(key, elementClass)
            if (diskRanks.size != 0) {
                Timber.i("Disk Cache Hit: $key")
                memoryCache.write(diskRanks, key, ttlMillis)
                diskRanks
            } else {
                Timber.i("Disk Cache Miss: $key")
                val response = call.execute()
                if (!response.isSuccess) {
                    Timber.i("Network Miss: $key")
                    listOf()
                } else {
                    Timber.i("Network Hit: $key")
                    diskCache.write(response.body(), key, ttlMillis)
                    memoryCache.write(response.body(), key, ttlMillis)
                    response.body()
                }
            }
        }
    }

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
}