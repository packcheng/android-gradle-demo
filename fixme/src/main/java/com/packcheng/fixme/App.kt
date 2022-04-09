package com.packcheng.fixme

import android.app.Application
import android.content.Context

/**
 *
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/9 17:45
 */
class App: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        FixUtil.install(base!!)
    }
}