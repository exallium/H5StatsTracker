package com.exallium.h5statstracker.app

import android.os.Bundle
import flow.Backstack
import flow.Flow
import java.lang.ref.WeakReference

public object Router : Flow.Listener {

    lateinit var listener: WeakReference<Listener>

    public interface Listener {
        fun goToScreen(request: Request)
    }

    override fun go(nextBackstack: Backstack, direction: Flow.Direction, callback: Flow.Callback) {
        val req = nextBackstack.current().screen as Request
        listener.get()?.goToScreen(req)
        callback.onComplete()
    }

    public data class Request(val route: Route, val bundle: Bundle = Bundle())

    public enum class Route(val titleId: Int, val navId: Int) {
        SERVICE_RECORD_SUMMARY(R.string.service_record, R.id.service_record),
        ARENA_SERVICE_RECORD(R.string.arena_stats, R.id.arena_stats),
        WARZONE_SERVICE_RECORD(R.string.warzone_stats, R.id.warzone_stats),
        GAMERTAG(R.string.gamertag_title, R.id.log_out)
    }

    private val bundle = Bundle()
    private val backstack = Backstack.single(Request(Route.GAMERTAG, bundle))
    private val flow = Flow(backstack, this)

    public fun onCreate(listener: Listener) {
        this.listener = WeakReference<Listener>(listener);
        listener.goToScreen(flow.backstack.current().screen as Request)
    }

    public fun onRequest(request: Request) {
        val currentScreen = backstack.current().screen as Request
        if (request != currentScreen) {
            flow.goTo(request)
        }
    }

    public fun goBack(): Boolean {
        return flow.goBack();
    }

    public fun replaceTo(request: Request) {
        flow.replaceTo(request)
    }

}
