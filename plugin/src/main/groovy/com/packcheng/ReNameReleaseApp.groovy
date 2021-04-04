package com.packcheng

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 重命名正式包的APK名字
 *
 * @author packcheng* @date 2021/4/4 18:03
 */
class ReNameReleaseApp implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println('Config Plugin: ReNameReleaseApp')

        project.android.applicationVariants.all {
            variant ->
                println("variantName: ${variant.name}")
                println("flavorName: ${variant.flavorName}")
                println("buildType: ${variant.buildType}")

                variant.outputs.each { output ->
                    def defOutputFile = output.outputFileName
                    if (variant.buildType.name == "release") {
                        println("需要修改的默认名字：$defOutputFile")
                        def fileName = "app-${variant.flavorName}-${variant.buildType.name}.apk"
                        println("修改后的名字：$fileName")
                        output.outputFileName = fileName
                    }
                }
        }
    }
}