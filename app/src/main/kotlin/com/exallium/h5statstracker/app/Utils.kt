package com.exallium.h5statstracker.app

import android.os.Bundle
import java.util.regex.Pattern


private val keyPattern = Pattern.compile("\\{([A-Za-z0-9.]+)\\}")

public tailrec fun String.interpolate(bundle: Bundle?): String {

    var newString = this
    var matcher = keyPattern.matcher(this)
    if (matcher.find()) {
        val match = matcher.group(1)
        val replace = if (bundle?.containsKey(match)?:false) { bundle?.get(match).toString() } else { match }
        newString = newString.replaceRange(matcher.start(), matcher.end(), replace?:match)
        return newString.interpolate(bundle)
    }
    return newString

}

public object Units {
    val MONTH_MILLIS = 2629746000L
    val DAY_MILLIS = 86400000L
    val MINUTE_MILLIS = 60000L
}