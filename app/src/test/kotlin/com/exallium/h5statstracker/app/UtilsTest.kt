package com.exallium.h5statstracker.app

import android.os.Bundle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner

@RunWith(RobolectricGradleTestRunner::class)
class UtilsTest {

    @Test
    fun testInterpolator() {
        val bundle = Bundle()
        bundle.putString(Constants.GAMERTAG, "exallium")
        val str = "asdf{%s}asdf{%s}".format(Constants.GAMERTAG, Constants.GAMERTAG).interpolate(bundle)
        Assert.assertEquals("asdfexalliumasdfexallium", str)
    }

}