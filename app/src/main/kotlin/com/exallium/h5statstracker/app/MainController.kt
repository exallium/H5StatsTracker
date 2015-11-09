package com.exallium.h5statstracker.app

import java.lang.ref.WeakReference

public class MainController {
    
    lateinit var callbacks: WeakReference<Callbacks>
    
    public interface Callbacks {
        fun getGamertag(): String?
        
        fun showContentGamertag()
        fun showContentServiceRecord()
    }
    
    public fun onResume(callbacks: Callbacks) {
        
        this.callbacks = WeakReference<Callbacks>(callbacks)
        val gamertag = callbacks.getGamertag()
        
        if (gamertag == null) {
            callbacks.showContentGamertag()
        } else {
            callbacks.showContentServiceRecord()
        }
    }
    
}
