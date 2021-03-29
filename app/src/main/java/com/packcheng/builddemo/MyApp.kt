package com.packcheng.builddemo

import android.app.Application

/**
 * Application
 *
 * @author packcheng
 * @date 2021/3/28 15:44
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    val isGoogle: Boolean = BuildConfig.isGoogle
    val isDark: Boolean = BuildConfig.isDark

}