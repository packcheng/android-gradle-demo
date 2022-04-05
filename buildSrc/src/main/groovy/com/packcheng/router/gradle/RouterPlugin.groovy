package com.packcheng.router.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {
    public static final String NAME_OF_FOLDER_MAPPING_DOC = "router_mapping";

    @Override
    void apply(Project project) {
        println("I am from RouterPlugin, apply from project: ${project.name}")

        // 注册Transform
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            Transform transform = new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }

        project.getExtensions().create("router", RouterExtension)

        // 1. 自动配置传递参数到注解处理器中
        if (project.extensions.findByName("kapt") != null) {
            println("自动配置传递参数到注解处理器中...")
            project.extensions.findByName("kapt").arguments {
                arg("root_project_dir", project.rootProject.projectDir.absolutePath)
            }
        }

        // 2. 实现旧构建产物的自动清理
        project.clean.doFirst {
            println("旧构建产物的自动清理")
            File routerMappingDir = new File(project.rootDir, NAME_OF_FOLDER_MAPPING_DOC)
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }

        project.afterEvaluate {
            RouterExtension extension = project["router"]
            println("用户设置的wiki路径为: ${extension.wikiDir}")

            // 3. 创建markdown说明文档
            project.tasks.findAll {
                it.name.startsWith("compile") && it.name.endsWith("JavaWithJavac")
            }.each {
                // task compileXXXJavaWithJavac
                it.doLast {
                    genMarkDownDoc(project.rootDir, extension.wikiDir)
                }
            }
        }
    }

    /**
     * 创建MarkDown说明文档
     * @return
     */
    def private genMarkDownDoc(File rootDir, final String wikiFilePath) {
        File routerMappingDir = new File(rootDir, NAME_OF_FOLDER_MAPPING_DOC)
        if (!routerMappingDir.exists()) {
            return
        }

        File[] routerMappingFiles = routerMappingDir.listFiles()
        if (null == routerMappingFiles || routerMappingFiles.size() < 1) {
            return
        }

        final StringBuilder markDownBuilder = new StringBuilder()
        markDownBuilder.append("# 页面文档\n\n")

        JsonSlurper jsonSlurper
        routerMappingFiles.each {
            if (it.name.endsWith(".json")) {
                jsonSlurper = new JsonSlurper()
                def contents = jsonSlurper.parse(it)
                contents.each { innerContent ->
                    def url = innerContent["url"]
                    def description = innerContent["description"]
                    def realPath = innerContent["realPath"]

                    markDownBuilder.append("## $description\n")
                    markDownBuilder.append("- url: $url\n")
                    markDownBuilder.append("- realPath: $realPath\n\n")
                }
            }
        }

        File wikiFileDir = new File(wikiFilePath)
        if (!wikiFileDir.exists()) {
            wikiFileDir.mkdirs()
        }

        final String wikiFileName = "页面文档.md"
        File wikiFile = new File(wikiFileDir, wikiFileName)
        if (wikiFile.exists()) {
            wikiFile.delete()
        }
        wikiFile.write(markDownBuilder.toString())
    }
}