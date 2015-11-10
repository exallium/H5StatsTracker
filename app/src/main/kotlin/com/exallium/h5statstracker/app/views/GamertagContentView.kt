package com.exallium.h5statstracker.app.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.exallium.h5statstracker.app.Constants
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R

public class GamertagContentView(context: Context?,
                                 val controller: MainController,
                                 val bundle: Bundle?) : LinearLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.content_gamertag, this)
        applyState(bundle)
    }

    private fun applyState(bundle: Bundle?) {
        (findViewById(R.id.input_gamertag) as EditText).setText(bundle?.getString(Constants.GAMERTAG, ""))
        (findViewById(R.id.input_submit)).setOnClickListener {
            controller.onSubmitGamertag((findViewById(R.id.input_gamertag) as EditText).text.toString())
        }
    }
}
