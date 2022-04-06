package com.packcheng.routerapp

import android.app.Application
import com.packcheng.router.runtime.ZbcRouter

/**
 * 应用Application
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @since 2022/4/6 2:33 下午
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ZbcRouter.init()
    }
}