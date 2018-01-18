package com.fashare.dl

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import dalvik.system.DexClassLoader
import org.joor.Reflect

/**
 * 动态加载（门面类）
 */
object DL {
    lateinit var dexClassLoader: DexClassLoader
    lateinit var instrumentation: Instrumentation

    /**
     * 加载 sdcard 上的 未安装的 apk
     */
    fun loadApk(context: Context, uri: Uri) {
        dexClassLoader = DexClassLoader(
                uri.path,
                context.cacheDir.path,
                null,
                context.classLoader)
    }

    /**
     * 替换 ActivityThread.mInstrumentation
     */
    fun replaceInstrumentation() {
        Reflect.on("android.app.ActivityThread").call("currentActivityThread").apply {
            val activityThreadRef = this
            val base = activityThreadRef.get<Instrumentation>("mInstrumentation")
            instrumentation = InstrumentationProxy(base)
            activityThreadRef.set("mInstrumentation", instrumentation)
        }
    }

    /**
     * 加载插件 apk 里的资源
     *
     * @param assetPath 插件 apk 路径
     */
    fun addAssetPath(context: Context, assetPath: String) {
        Reflect.on(context.assets).call("addAssetPath", assetPath)
    }
}
