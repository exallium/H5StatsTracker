package com.exallium.h5statstracker.app.views

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import com.exallium.h5statstracker.app.Constants
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.R
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

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
            val gamertagOk = controller.onSubmitGamertag((findViewById(R.id.input_gamertag) as EditText).text.toString())
            gamertagOk successUi {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
                imm.hideSoftInputFromWindow(this.windowToken, 0);
            } failUi {
                val gamertagInputLayout = findViewById(R.id.input_layout_gamertag) as TextInputLayout
                gamertagInputLayout.error = context.getString(R.string.gamertag_input_error)
                gamertagInputLayout.isErrorEnabled = true
            }
        }
    }
}
