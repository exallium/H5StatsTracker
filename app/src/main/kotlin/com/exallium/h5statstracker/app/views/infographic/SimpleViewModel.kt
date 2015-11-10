package com.exallium.h5statstracker.app.views.infographic

public abstract class SimpleViewModel<T>(val t: T) : InfographicViewModel<T> {
    override fun getData() = t
}
