package com.exallium.h5statstracker.app.views.infographic

interface InfographicViewModel<T> {
    fun getViewType(): Int
    fun getData(): T
}
