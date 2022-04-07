package com.packcheng.android

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0* @since 2022/3/12 15:46
 */
class AndroidExtension {
    private static final String TAG = "AndroidExtension"

    private String compileSdkVersion
    private String buildToolsVersion
    private MyDefaultConfig defaultConfig = new MyDefaultConfig()

    // 可以自定义名字的属性扩展
    private NamedDomainObjectContainer<BuildTypes> buildTypes

    // 增加可以自定义名字的属性扩展时添加
    AndroidExtension(Project project) {
        buildTypes = project.container(BuildTypes.class);
    }

    void buildTypes(Action<NamedDomainObjectContainer<BuildTypes>> action) {
        action.execute(buildTypes)
    }

    NamedDomainObjectContainer<BuildTypes> getBuildTypes() {
        return buildTypes
    }

    String getCompileSdkVersion() {
        return compileSdkVersion
    }

    void setCompileSdkVersion(String compileSdkVersion) {
        this.compileSdkVersion = compileSdkVersion
    }

    String getBuildToolsVersion() {
        return buildToolsVersion
    }

    void setBuildToolsVersion(String buildToolsVersion) {
        this.buildToolsVersion = buildToolsVersion
    }

    MyDefaultConfig getDefaultConfig() {
        return defaultConfig
    }

    void setDefaultConfig(Closure config) {
        ConfigureUtil.configure(config, defaultConfig)
    }

    @Override
    String toString() {
        return "AndroidExtension{" +
                "compileSdkVersion='" + compileSdkVersion + '\'' +
                ", buildToolsVersion='" + buildToolsVersion + '\'' +
                ", defaultConfig=" + defaultConfig +
                ", buildTypes=" + buildTypes +
                '}'
    }

//    方式一
//    public void setDefaultConfig(Action<DefaultConfig> action) {
//        action.execute(defaultConfig);
//    }
//    defaultConfig {
//        it.applicationId = "1.0.0"
//        it.minSdkVersion = "3.0.0"
//    }

//    方式二
//    public void setDefaultConfig(Closure config) {
//        ConfigureUtil.configure(config, defaultConfig);
//    }
//
//    defaultConfig {
//        applicationId = "1.0.0"
//        minSdkVersion = "3.0.0"
//    }

//    方式三
//    public void defaultConfig(Action<DefaultConfig> action) {
//        action.execute(defaultConfig);
//    }
//    defaultConfig {
//        applicationId = "1.0.0"
//        minSdkVersion = "3.0.0"
//    }


    class MyDefaultConfig {
        private String applicationId
        private String minSdkVersion

        String getApplicationId() {
            return applicationId
        }

        void setApplicationId(String applicationId) {
            this.applicationId = applicationId
        }

        String getMinSdkVersion() {
            return minSdkVersion
        }

        void setMinSdkVersion(String minSdkVersion) {
            this.minSdkVersion = minSdkVersion
        }

        @Override
        String toString() {
            return "MyDefaultConfig{" +
                    "applicationId='" + applicationId + '\'' +
                    ", minSdkVersion='" + minSdkVersion + '\'' +
                    '}';
        }
    }

    /**
     * 可以自定义名字的属性扩展
     *
     * 1. BuildTypes若为内部类，一定要为静态类
     * 2. BuildTypes一定含有一个参数为String类型的构造函数
     * 3. 一定要有一个name的属性
     */
    static class BuildTypes {
        private boolean signingConfig
        //必须含有name属性，
        // 否则会抛出"'com.wj.plugin.extension.AndroidExtension$BuildTypes@130b3f7d' because it does not have a 'name' property"
        private String name

        BuildTypes(String name) {
            this.name = name
        }

        String getName() {
            return name
        }

        void setSigningConfig(boolean config) {
            this.signingConfig = config
        }

        boolean getSigningConfig() {
            return this.signingConfig
        }


        @Override
        String toString() {
            return "BuildTypes{" +
                    "signingConfig=" + signingConfig +
                    ", name='" + name + '\'' +
                    '}'
        }
    }
}
