package com.lazyiones.weather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * @Description:
 * @Author: LazyIonEs
 * @Version: 1.0
 * @Date: 2022/7/7 19:19
 */
class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}