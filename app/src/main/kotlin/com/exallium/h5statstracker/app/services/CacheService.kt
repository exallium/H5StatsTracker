package com.exallium.h5statstracker.app.services

import com.exallium.h5statstracker.app.model.DiskCache
import com.exallium.h5statstracker.app.model.MemoryCache
import nl.komponents.kovenant.async
import timber.log.Timber
import java.io.File

class CacheService(val cacheDirectory: File) {
    private val memoryCache = MemoryCache()
    private val diskCache = DiskCache(cacheDirectory)

    fun <T> readListFromCache(key: String, elementClass: Class<T>, ttlMillis: Long, origin: () -> List<T>) = async {
        val normalizedKey = key.toLowerCase()
        synchronized(normalizedKey) {
            val memResult = memoryCache.readList(normalizedKey, elementClass)
            if (memResult.isNotEmpty()) {
                memResult
            } else {
                val diskResult = diskCache.readList(normalizedKey, elementClass)
                if (diskResult.isNotEmpty()) {
                    memoryCache.write(diskResult, normalizedKey, ttlMillis)
                    diskResult
                } else {
                    Timber.v("Requesting List from Origin: %s".format(normalizedKey))
                    val originResult = origin()
                    diskCache.write(originResult, normalizedKey, ttlMillis)
                    memoryCache.write(originResult, normalizedKey, ttlMillis)
                    originResult
                }
            }
        }
    } fail {
        Timber.e(it, "Failed to get List from Cache")
    }

    fun <T> readItemFromCache(key: String, itemClass: Class<T>, ttlMillis: Long, origin: () -> T?) = async {
        val normalizedKey = key.toLowerCase()
        synchronized(normalizedKey) {
            val memResult = memoryCache.readItem(normalizedKey, itemClass)
            if (memResult != null) {
                memResult
            } else {
                val diskResult = diskCache.readItem(normalizedKey, itemClass)
                if (diskResult != null) {
                    memoryCache.write(diskResult, normalizedKey, ttlMillis)
                    diskResult
                } else {
                    Timber.v("Requesting Item from Origin: %s".format(normalizedKey))
                    val originResult = origin()
                    diskCache.write(originResult as Any, normalizedKey, ttlMillis)
                    memoryCache.write(originResult, normalizedKey, ttlMillis)
                    originResult
                }
            }
        }
    } fail {
        Timber.e(it, "Failed to get Item from Cache")
    }
}