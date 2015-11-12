package com.exallium.h5statstracker.app.services

import com.exallium.h5.api.ApiFactory
import com.exallium.h5.api.models.metadata.SpartanRank
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.async
import nl.komponents.kovenant.then

class MetadataService(val apiFactory: ApiFactory) {

    fun getSpartanRank(rank: Int): Promise<SpartanRank, Exception> {
        return async {
            val response = apiFactory.metadata.spartanRanks.execute()
            if (!response.isSuccess) {
                throw IllegalAccessException("Bad times: %d".format(response.code()))
            }

            val ranks = response.body()
            ranks[rank - 1]
        }
    }

    fun getGameVariant(id: String) = async {
        val response = apiFactory.metadata.getGameVariant(id).execute()
        if (!response.isSuccess) {
            throw IllegalAccessException("Bad times: %d".format(response.code()))
        }

        response.body()
    }

    fun getPlaylists() = async {
        val response = apiFactory.metadata.playlists.execute()
        if (!response.isSuccess) {
            throw IllegalAccessException("Bad times: %d".format(response.code()))
        }

        response.body()
    }

    fun getPlaylist(playlistId: String) = getPlaylists() then {
        it.find { playlistId == it.id }
    }

    fun getCsrDesignations() = async {
        val response = apiFactory.metadata.csrDesignations.execute()
        if (!response.isSuccess) {
            throw IllegalAccessException("Bad times: %d".format(response.code()))
        }

        response.body()
    }

    fun getCsrDesignation(id: Long) = getCsrDesignations() then {
        it.find { it.id == id }
    }
}