package com.packcheng.update

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * 增量更新TASK
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0* @since 2022/3/12 12:47
 */
class HandleTemplateTask extends DefaultTask {
    /**
     * 文件格式
     */
    private String fileFormat
    /**
     * 文件的路径
     */
    private File fileSourceDir


    @Input
    String getFileFormat() {
        return fileFormat
    }

    void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat
    }

    @InputDirectory
    //@Optional 可添加表示参数可选
    File getFileSourceDir() {
        return fileSourceDir
    }

    void setFileSourceDir(File fileSourceDir) {
        this.fileSourceDir = fileSourceDir
    }

    @TaskAction
    void run() {
        System.out.println(" HandleTemplateTask is running ")
        System.out.println(" Set the file format is \" " + getFileFormat())
        System.out.println(" Set the file source dir is \" " + getFileSourceDir())
    }
}
