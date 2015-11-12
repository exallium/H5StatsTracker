package com.exallium.h5statstracker.app.views.infographic

interface InfographicViewModel<out T> {
    fun getViewType(): Int
    fun getData(): T
}
