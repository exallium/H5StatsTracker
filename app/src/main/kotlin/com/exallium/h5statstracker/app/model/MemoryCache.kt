package com.exallium.h5statstracker.app.model

import timber.log.Timber
import java.util.*

class MemoryCache : BaseCache()  {

    override fun syncrhonizedEvictAll() {
        Timber.v("Evicting Memory Cache")
        cacheMap.clear()
    }

    override fun synchronizedWrite(data: Any, key: String, ttlMillis: Long) {
        Timber.v("Writing $key to Memory")
        val createdMillis = System.currentTimeMillis()
        val record = CacheRecord(createdMillis, ttlMillis, data)
        cacheMap.put(key, record)
    }

    override fun <T> synchronizedItemRead(key: String, itemClass: Class<T>): T? {
        Timber.v("Reading $key from Memory")
        return if (validateCacheRecord(key)) {
            cacheMap[key]?.data as? T?
        } else {
            null
        }
    }

    override fun <E> synchronizedListRead(key: String, elementClass: Class<E>): List<E> {
        Timber.v("Reading $key from Memory")
        return if (validateCacheRecord(key)) {
            (cacheMap[key]?.data as? List<E>)?:listOf()
        } else {
            listOf()
        }
    }

    private fun validateCacheRecord(key: String): Boolean {
        Timber.v("Validating $key")
        return if (cacheMap.containsKey(key)) {
            Timber.v("Cache Record for $key exists")
            val record = cacheMap[key]
            if (record?.isValid()?:false) {
                Timber.v("Cache Record for $key is valid")
                true
            } else {
                Timber.v("Cache Record for $key is expired")
                cacheMap.remove(key)
                false
            }
        } else {
            Timber.v("Cache Record for $key does not exist")
            false
        }
    }

    private val cacheMap = HashMap<String, CacheRecord<*>>()

    private data class CacheRecord<T>(val createdMillis: Long, val ttlMillis: Long, val data: T) {
        fun isValid() = (System.currentTimeMillis() - createdMillis) <= ttlMillis
    }
}
