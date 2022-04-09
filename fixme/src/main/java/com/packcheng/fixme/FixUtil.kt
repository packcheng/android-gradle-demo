package com.packcheng.fixme

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.*

/**
 * 修复后dex加载工具类
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/9 17:34
 */
object FixUtil {
    private const val TAG = "FixUtil"

    fun install(context: Context) {

        try {
            val fixedDexFile: File? = File(context.getExternalFilesDir("file"),"fix.dex")
            if(null == fixedDexFile || !fixedDexFile.exists()){
                Log.w(TAG, "Not fix.dex file found!")
                return
            }

            val pathListField = ReflectUtil.getField(context.classLoader, "pathList")
            val dexPathList = pathListField.get(context.classLoader)

            val makeDexElementsMethod = ReflectUtil.getMethod(
                dexPathList,
                "makeDexElements",
                List::class.java,
                File::class.java,
                List::class.java,
                ClassLoader::class.java
            )

            val fileToBeInstalled = ArrayList<File>()
            fileToBeInstalled.add(fixedDexFile!!)
            val optimizedDirectory = File(context.filesDir, "fixed_dex")
            val suppressedExceptions = ArrayList<IOException>()

            // 修复后的dexElements
            val extraElements = makeDexElementsMethod.invoke(
                dexPathList,
                fileToBeInstalled, optimizedDirectory,
                suppressedExceptions, context.classLoader
            ) as kotlin.Array<Any>

            // 原dexElements
            val dexElementsField = ReflectUtil.getField(dexPathList, "dexElements")
            val originElements = dexElementsField.get(dexPathList) as kotlin.Array<Any>

            val combinedElements = java.lang.reflect.Array.newInstance(
                originElements.javaClass.componentType,
                originElements.size + extraElements.size
            )

            System.arraycopy(extraElements, 0, combinedElements, 0, extraElements.size)
            System.arraycopy(
                originElements,
                0,
                combinedElements,
                extraElements.size,
                originElements.size
            )

            dexElementsField.set(dexPathList, combinedElements)
        } catch (e: Exception) {
            Log.e(TAG, "Error on fix bug: ")
            e.printStackTrace()
        }
    }
}