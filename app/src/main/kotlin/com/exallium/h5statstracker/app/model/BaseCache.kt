package com.exallium.h5statstracker.app.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import timber.log.Timber

abstract class BaseCache {

    companion object {
        internal val OBJECT_MAPPER = ObjectMapper()

        init {
            OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        }
    }

    val mutex = Any()

    fun write(data: Any, key: String, ttlMillis: Long) {
        synchronized (mutex) {
            synchronizedWrite(data, key, ttlMillis)
        }
    }

    fun <T> readItem(key: String, itemClass: Class<T>): T? {
        synchronized (mutex) {
            return synchronizedItemRead(key, itemClass)
        }
    }

    fun <E> readList(key: String, elementClass: Class<E>): List<E> {
        synchronized (mutex) {
            return synchronizedListRead(key, elementClass)
        }
    }

    protected abstract fun synchronizedWrite(data: Any, key: String, ttlMillis: Long)
    protected abstract fun <T> synchronizedItemRead(key: String, itemClass: Class<T>): T?
    protected abstract fun <E> synchronizedListRead(key: String, elementClass: Class<E>): List<E>
}