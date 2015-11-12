package com.exallium.h5statstracker.app.views.infographic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout

abstract class InfographicView<in T>(context: Context, val layoutId: Int) : FrameLayout(context) {

    init {
        LayoutInflater.from(context).inflate(layoutId, this)
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    public abstract fun render(data: T)
}
