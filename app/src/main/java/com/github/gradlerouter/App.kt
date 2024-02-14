package com.github.gradlerouter

import android.app.Application
import com.github.gradlerouter.router_runtime.Router

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Router.init()
    }

}