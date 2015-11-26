package com.exallium.h5statstracker.app.model

import timber.log.Timber
import java.io.File

class DiskCache(val cacheDirectory: File) : BaseCache() {

    override fun syncrhonizedEvictAll() {
        Timber.v("Evicting Disk Cache")
        cacheDirectory.listFiles { it.name.endsWith("cacheRecord") } ?.forEach { it.delete() }
    }

    companion object {
        val CACHE_RECORD_SUFFIX = ".cacheRecord"
        val JSON_SUFFIX = ".json"
    }

    init {
        if (!cacheDirectory.isDirectory) {
            throw IllegalStateException("Passed file is not a directory!")
        }
    }

    override fun synchronizedWrite(data: Any, key: String, ttlMillis: Long) {
        Timber.v("Writing $key to disk")
        val cacheRecordFile = File(cacheDirectory, key + CACHE_RECORD_SUFFIX)
        val jsonFile = File(cacheDirectory, key + JSON_SUFFIX)
        val createdMillis = System.currentTimeMillis()
        cacheRecordFile.writeText("%d:%d".format(ttlMillis, createdMillis))
        OBJECT_MAPPER.writeValue(jsonFile, data)
    }

    override fun <T> synchronizedItemRead(key: String, itemClass: Class<T>): T? {
        Timber.v("Reading $key from disk")
        val jsonFile = File(cacheDirectory, key + JSON_SUFFIX)
        return if (verifyCacheRecord(key)) {
            OBJECT_MAPPER.readValue(jsonFile, itemClass)
        } else {
            null
        }
    }

    override fun <E> synchronizedListRead(key: String, elementClass: Class<E>): List<E> {
        Timber.v("Reading $key from disk")
        val jsonFile = File(cacheDirectory, key + JSON_SUFFIX)
        return if (verifyCacheRecord(key)) {
            OBJECT_MAPPER.readValue(jsonFile,
                    OBJECT_MAPPER
                            .typeFactory
                            .constructCollectionType(List::class.java, elementClass))
        } else {
            listOf()
        }
    }

    private fun verifyCacheRecord(key: String): Boolean {
        Timber.v("Verifying $key")
        val cacheRecordFile = File(cacheDirectory, key + CACHE_RECORD_SUFFIX)
        val currentMillis = System.currentTimeMillis()
        if (cacheRecordFile.exists()) {
            Timber.v("Cache Record for $key exists")
            val cacheRecord = cacheRecordFile.readText().split(":")
            val ttlMillis = cacheRecord[0].toLong()
            val createdMillis = cacheRecord[1].toLong()
            val lifespan = currentMillis - createdMillis
            if (lifespan <= ttlMillis) {
                Timber.v("Cache Record for $key is valid")
                return true
            } else {
                Timber.v("Cache Record for $key is expired")
                cacheRecordFile.delete()
                return false
            }
        }
        Timber.v("Cache Record for $key does not existx")
        return false
    }

}
