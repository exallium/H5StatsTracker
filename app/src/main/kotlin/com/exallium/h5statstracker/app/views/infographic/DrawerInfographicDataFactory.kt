package com.exallium.h5statstracker.app.views.infographic

interface DrawerInfographicDataFactory<T> : InfographicDataFactory<T> {
    fun getDrawerViewModel(data: T): InfographicViewModel<T>
}