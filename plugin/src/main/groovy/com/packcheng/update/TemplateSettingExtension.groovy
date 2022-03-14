package com.packcheng.update
/**
 * 增量更新配置扩展属性
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0* @since 2022/3/12 12:52
 */
class TemplateSettingExtension {
    public static final String TAG = "templateSettingExtension"

    private String compileSdk
    private File interfaceSourceDir

    String getCompileSdk() {
        return compileSdk
    }

    void setCompileSdk(String compileSdk) {
        this.compileSdk = compileSdk
    }

    File getInterfaceSourceDir() {
        return interfaceSourceDir
    }

    void setInterfaceSourceDir(File interfaceSourceDir) {
        this.interfaceSourceDir = interfaceSourceDir
    }
}
