package com.packcheng.android

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyAndroid implements Plugin<Project> {
    private static final String EXT_NAME_MY_ANDROID = "myAndroid"

    @Override
    void apply(Project project) {
        println(">>>>>>   apply MyAndroid")
        project.extensions.create(EXT_NAME_MY_ANDROID, AndroidExtension.class, project)
        project.afterEvaluate {
            AndroidExtension myAndroid = project.extensions.getByName(EXT_NAME_MY_ANDROID)
            println(">>>>>> AndroidExtension config: ${myAndroid.toString()}")
        }
    }
}