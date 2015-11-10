package com.exallium.h5statstracker.app.views.infographic.impl.servicereports.arena

import android.os.Bundle
import com.exallium.h5.api.models.stats.servicerecords.ArenaResult
import com.exallium.h5statstracker.app.MainController
import com.exallium.h5statstracker.app.ViewCallback
import com.exallium.h5statstracker.app.views.infographic.InfographicDataFactory
import com.exallium.h5statstracker.app.views.infographic.InfographicViewModel
import com.exallium.h5statstracker.app.views.infographic.SimpleViewModel

public class ArenaServiceRecordDataFactory(val mainController: MainController, val bundle: Bundle?) : InfographicDataFactory<ArenaResult> {
    override fun getViewModels(fn: (List<InfographicViewModel<ArenaResult>>) -> Unit) {

        mainController.onRequestArenaServiceRecord(bundle, object : ViewCallback<ArenaResult> {
            override fun onError() {
                // TODO report error
                fn(listOf())
            }

            override fun onResult(result: ArenaResult) {
                fn(listOf(HeaderViewModel(result)))
            }

        })

    }

}

public class HeaderViewModel(t: ArenaResult) : SimpleViewModel<ArenaResult>(t) {
    override fun getViewType() = 0
}