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
        synchronized(key) {
            val memResult = memoryCache.readList(key, elementClass)
            if (memResult.isNotEmpty()) {
                memResult
            } else {
                val diskResult = diskCache.readList(key, elementClass)
                if (diskResult.isNotEmpty()) {
                    memoryCache.write(diskResult, key, ttlMillis)
                    diskResult
                } else {
                    Timber.v("Requesting List from Origin: %s".format(key))
                    val originResult = origin()
                    diskCache.write(originResult, key, ttlMillis)
                    memoryCache.write(originResult, key, ttlMillis)
                    originResult
                }
            }
        }
    }

    fun <T> readItemFromCache(key: String, itemClass: Class<T>, ttlMillis: Long, origin: () -> T?) = async {
        synchronized(key) {
            val memResult = memoryCache.readItem(key, itemClass)
            if (memResult != null) {
                memResult
            } else {
                val diskResult = diskCache.readItem(key, itemClass)
                if (diskResult != null) {
                    memoryCache.write(diskResult, key, ttlMillis)
                    diskResult
                } else {
                    Timber.v("Requesting Item from Origin: %s".format(key))
                    val originResult = origin()
                    diskCache.write(originResult as Any, key, ttlMillis)
                    memoryCache.write(originResult, key, ttlMillis)
                    originResult
                }
            }
        }
    }
}