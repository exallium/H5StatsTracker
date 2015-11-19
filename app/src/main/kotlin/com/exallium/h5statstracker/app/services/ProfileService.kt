package com.exallium.h5statstracker.app.services

import com.exallium.h5.api.Profile
import nl.komponents.kovenant.async

class ProfileService(val profile: Profile) {

    fun getSpartanImageUrl(gamertag: String, size: Int = 256, crop: String = "portrait") = async {
        val response = profile.getSpartan(gamertag, size, crop).execute()
        if (response.code() == 302) {
            response.headers().get("Location")
        } else {
            throw IllegalStateException("No Image")
        }
    }

}
