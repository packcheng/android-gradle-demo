package com.packcheng.method_tracker.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodTrackerPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        if (!target.plugins.hasPlugin(AppPlugin.class)) {
            throw new RuntimeException("MethodTrackerPlugin only support on app module!!!")
        }

        // 增加配置transform是否启用的扩展配置
        target.extensions.create("methodTracker", MethodTrackerExtension.class)

        // 注册Transform
        def appExt = target.extensions.getByType(AppExtension.class)
        appExt.registerTransform(new MethodTrackerTransform())

        // 根据配置决定是否启用transform
        target.afterEvaluate {
            def ext = target.extensions.getByType(MethodTrackerExtension.class)
            if (!ext.enableTrack) {
                target.tasks.findAll {
                    it.name.startsWith("transformClassesWithMethodTrackerTransformFor")
                }.each {
                    {
                        println(">>>>>> 禁用MethodTrack!!!!!")
                        it.enabled = false
                    }
                }
            }
        }
    }
}