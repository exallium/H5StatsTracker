package com.exallium.h5statstracker.app.views.infographic

interface InfographicDataFactory<T> {
    fun getViewModels(fn: (List<InfographicViewModel<T>>) -> Unit)
}