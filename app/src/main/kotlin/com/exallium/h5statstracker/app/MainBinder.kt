package com.exallium.h5statstracker.app

import android.os.Binder

class MainBinder(val mainController: MainController) : Binder()