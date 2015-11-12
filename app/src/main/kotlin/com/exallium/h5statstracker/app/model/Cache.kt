package com.exallium.h5statstracker.app.model

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.util.*

public class Cache(val file: File) {

    private data class CacheRecord<T>(val created: Long, val ttlMinutes: Long, val data: T) {
        fun isValid() = ((System.currentTimeMillis() - created) / 1000 / 60) <= ttlMinutes
    }

    // TODO: Consider LRUCache here as app grows
    private val cacheMap = HashMap<String, CacheRecord<*>>()

    init {
        if (!file.isDirectory)
            throw IllegalStateException("Cache must point to a directory")
    }

    val objectMapper = ObjectMapper()

    fun writeToCache(data: Any, key: String, ttlMinutes: Long) {
        synchronized(cacheMap) {
            Log.d("CACHE " + key, "WRITE TO CACHE/DISK")
            val cacheRecord = File(file, key + "cacheRecord")
            val currentTime = System.currentTimeMillis()
            cacheRecord.writeText("%d:%d".format(ttlMinutes, currentTime))
            val f = File(file, key)
            cacheMap.put(key, CacheRecord(currentTime, ttlMinutes, data))
            objectMapper.writeValue(f, data)
        }
    }

    fun <T> readFromCache(key: String, klass: Class<T>): T? {

        synchronized(cacheMap) {
            Log.d("CACHE " + key, "ITEM READ FROM CACHE")
            if (cacheMap.containsKey(key)) {
                val record = cacheMap[key]
                if (record?.isValid() ?: false) {
                    Log.d("CACHE " + key, "ITEM FOUND")
                    val item = (record?.data as? T)
                    if (item == null) {
                        Log.d("CACHE " + key, "ITEM INCONSISTENCY DETECTED")
                    } else {
                        return item
                    }
                } else {
                    Log.d("CACHE " + key, "ITEM FOUND BUT EXPIRED")
                    cacheMap.remove(key)
                }
            }

            return readFromDisk(key, klass)
        }
    }

    private fun <T> readFromDisk(key: String, klass: Class<T>): T? {
        Log.d("CACHE " + key, "ITEM READ FROM DISK")
        val cacheRecord = File(file, key + "cacheRecord")
        if (cacheRecord.exists()) {
            val dat = cacheRecord.readText()
            val cacheData = dat.split(":")
            val lifespan = (System.currentTimeMillis() - cacheData[1].toLong()) / 1000 / 60
            if (lifespan <= cacheData[0].toLong()) {
                Log.d("CACHE " + key, "ITEM FOUND")
                val obj = objectMapper.readValue(File(file, key), klass)
                cacheMap.put(key, CacheRecord(cacheData[1].toLong(), cacheData[2].toLong(), obj))
                return obj
            } else {
                Log.d("CACHE " + key, "ITEM FOUND BUT EXPIRED")
                cacheRecord.delete()
            }
        }

        Log.d("CACHE " + key, "ITEM NOT FOUND")
        return null
    }

    fun <E> readListFromCache(key: String, elementClass: Class<E>): List<E> {
        synchronized(cacheMap) {
            Log.d("CACHE " + key, "LIST READ FROM CACHE")
            if (cacheMap.containsKey(key)) {
                val record = cacheMap[key]
                if (record?.isValid() ?: false) {
                    Log.d("CACHE " + key, "LIST FOUND")
                    val list = (record?.data as? List<E>)
                    if (list == null) {
                        Log.d("CACHE " + key, "LIST INCONSISTENCY DETECTED")
                    } else {
                        return list
                    }
                } else {
                    Log.d("CACHE " + key, "LIST FOUND BUT EXPIRED")
                    cacheMap.remove(key)
                }
            }

            return readListFromDisk(key, elementClass)
        }

    }

    private fun <E> readListFromDisk(key: String, elementClass: Class<E>): List<E> {
        Log.d("CACHE " + key, "LIST READ FROM DISK")
        val cacheRecord = File(file, key + "cacheRecord")
        if (cacheRecord.exists()) {
            val dat = cacheRecord.readText()
            val cacheData = dat.split(":")
            val lifespan = (System.currentTimeMillis() - cacheData[1].toLong()) / 1000 / 60
            if (lifespan <= cacheData[0].toLong()) {
                Log.d("CACHE " + key, "LIST FOUND")
                val obj: List<E> = objectMapper.readValue(File(file, key), objectMapper.typeFactory.constructCollectionType(List::class.java, elementClass))
                cacheMap.put(key, CacheRecord(cacheData[1].toLong(), cacheData[0].toLong(), obj))
                return obj
            } else {
                Log.d("CACHE " + key, "LIST FOUND BUT EXPIRED")
                cacheRecord.delete()
            }
        }

        Log.d("CACHE " + key, "LIST NOT FOUND")
        return listOf()
    }

}