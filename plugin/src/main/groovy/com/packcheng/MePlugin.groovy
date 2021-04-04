package com.packcheng

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自定义Gradle插件
 *
 * @author packchengConfig* @date 2021/4/4 14:08
 */
class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println 'My Plugin'
        project.extensions.create("myConfig", MyConfig)

        assert null != project.extensions.myConfig

        project.afterEvaluate {
            println('After Evaluate')
            println(project.extensions.myConfig.appName)
            println(project.extensions.myConfig.versionCode)
            println(project.extensions.myConfig.versionName)
        }

    }
}