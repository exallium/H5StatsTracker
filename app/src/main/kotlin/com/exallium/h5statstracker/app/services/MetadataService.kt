package com.exallium.h5statstracker.app.services

import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.metadata.CSRDesignation
import com.exallium.h5.api.models.metadata.Playlist
import com.exallium.h5.api.models.metadata.SpartanRank
import com.exallium.h5statstracker.app.model.Cache
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import nl.komponents.kovenant.then

class MetadataService(val apiFactory: ApiFactory, val cache: Cache) {

    fun getSpartanRanks() = async {
        val ranks = cache.readListFromCache("spartanRanks", SpartanRank::class.java)
        if (ranks.size != 0) {
            ranks
        } else {
            val response = apiFactory.metadata.spartanRanks.execute()
            if (!response.isSuccess) {
                throw IllegalAccessException("Bad times: %d".format(response.code()))
            }

            cache.writeToCache(response.body(), "spartanRanks", 43800)
            response.body()
        }
    }

    fun getSpartanRank(rank: Int) = getSpartanRanks() then {
        it[rank - 1]
    }

    fun getGameVariant(id: String) = async {
        val response = apiFactory.metadata.getGameVariant(id).execute()
        if (!response.isSuccess) {
            throw IllegalAccessException("Bad times: %d".format(response.code()))
        }

        response.body()
    }

    fun getPlaylists() = async {

        val playlists = cache.readListFromCache("playlists", Playlist::class.java)
        if (playlists.size != 0) {
            playlists
        } else {
            val response = apiFactory.metadata.playlists.execute()
            if (!response.isSuccess) {
                throw IllegalAccessException("Bad times: %d".format(response.code()))
            }
            cache.writeToCache(response.body(), "playlists", 1440)
            response.body()
        }

    }

    fun getPlaylist(playlistId: String) = getPlaylists() then {
        it.find { playlistId == it.id }
    }

    fun getCsrDesignations() = async {

        val csrDesignations = cache.readListFromCache("csrDesignations", CSRDesignation::class.java)
        if (csrDesignations.size != 0) {
            csrDesignations
        } else {
            val response = apiFactory.metadata.csrDesignations.execute()
            if (!response.isSuccess) {
                throw IllegalAccessException("Bad times: %d".format(response.code()))
            }
            cache.writeToCache(response.body(), "csrDesignations", 43800)
            response.body()
        }


    }

    fun getCsrDesignation(id: Long) = getCsrDesignations() then {
        it.find { it.id == id }
    }
}