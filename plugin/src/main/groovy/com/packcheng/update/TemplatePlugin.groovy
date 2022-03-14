package com.packcheng.update

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * 包含增量检查的插件
 * 参考：https://blog.csdn.net/nihaomabmt/article/details/117984235
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0* @since 2022/3/12 12:53
 */
class TemplatePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("TemplatePlugin-----------------------start")
        createExtensions(project)
        addHandleTemplateTask(project)
        println("TemplatePlugin-----------------------end")
    }

    void createExtensions(Project project) {
        //在这里是无法取到  extension的值，因为此时还没有构建到app中的build.gradle
        project.getExtensions().create(TemplateSettingExtension.TAG, TemplateSettingExtension)
    }
    /**
     * 将HandleTemplateTask加入到任务队列中
     * @param project
     */
    void addHandleTemplateTask(Project project) {
        Task task = project.getTasks().create("handleTemplateTask", HandleTemplateTask)

        //这里是返回的app的这个module，然后在app的project的所有tasks中添加该handleTemplateTask
        project.afterEvaluate {
            project.getTasks().matching {
                //如果将该插件放到'com.android.application',则在"preBuild"之前添加该Task
                (it.name == "preBuild")
            }.each {
                it.dependsOn(task)
                setHandleTemplateTaskInputFromExtension(project, task)
            }
        }
    }
    /**
     * 设置HandleTemplateTask的input
     * @param project
     * @param task
     */
    void setHandleTemplateTaskInputFromExtension(Project project, HandleTemplateTask task) {
        //项目配置完成之后，就可以获得设置的Extension中的内容
        TemplateSettingExtension extension = project.getExtensions().findByName(TemplateSettingExtension.TAG)
        task.setFileFormat(".java")
        String path = project.getProjectDir().getAbsolutePath() + "/src/main/java/mvp"
        task.setFileSourceDir(extension.interfaceSourceDir)
    }
}
