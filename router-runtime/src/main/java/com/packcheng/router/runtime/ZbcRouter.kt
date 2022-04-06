package com.packcheng.router.runtime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import java.util.*

/**
 * 路由初始化
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @since 2022/4/6 2:18 下午
 */
object ZbcRouter {
    private const val TAG = "ZbcRouterTAG"
    private const val MAPPING_CLASS_NAME = "com.packcheng.router.mapping.generated.RouterMapping"

    private val mMappings = HashMap<String, String>()

    fun init() {
        try {
            val mappingClass = Class.forName(MAPPING_CLASS_NAME)
            val getMethod = mappingClass.getDeclaredMethod("get")
            val allMappings = getMethod.invoke(null) as? Map<String, String>
            allMappings?.let {
                Log.i(TAG, "ZbcRouter init, all mapping's size: ${allMappings.size}")
                mMappings.putAll(allMappings)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error on ZbcRouter init: ")
            e.printStackTrace()
        }
    }

    fun go(ctx: Context, url: String) {
        val targetUri = Uri.parse(url)
        val scheme = targetUri.scheme
        val host = targetUri.host
        val path = targetUri.path

        var activityClassName: String? = null
        mMappings.onEach {
            val keyUri = Uri.parse(it.key)
            val keyScheme = keyUri.scheme
            val keyHost = keyUri.host
            val keyPath = keyUri.path

            if (scheme == keyScheme && host == keyHost && path == keyPath) {
                activityClassName = it.value
            }
        }

        if (activityClassName.isNullOrEmpty()) {
            Log.e(TAG, "No destination find for: $url")
            return
        }

        val activityClass = Class.forName(activityClassName)
        if (activityClassName.isNullOrEmpty()) {
            Log.e(TAG, "No Activity name find for: $activityClassName")
            return
        }

        val args = Bundle()
        val query = targetUri.query
        query?.let {
            if(it.length > 3){
                it.split("&").forEach { pars->
                    val kv = pars.split("=")
                    args.putString(kv[0], kv[1])
                }
            }
        }

        val intent = Intent(ctx, activityClass)
        intent.putExtras(args)
        ctx.startActivity(intent)
    }
}