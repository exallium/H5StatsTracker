package com.exallium.h5statstracker.app.views.infographic

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout

abstract class InfographicView<in T>(context: Context, val layoutId: Int) : FrameLayout(context) {

    init {
        LayoutInflater.from(context).inflate(layoutId, this)
    }

    public abstract fun render(data: T)
}
