# 自定义插件

## 1. 创建插件Module

```
创建目录如下：
plugin
|--main
	|--src
		|-- groovy
			|	xxx.groovy
		|-- resources
			|-- META-INFO
				|	gradle-plugins
				|	xxx.properties
```

## 2. 创建build.gradle文件

在创建的Module目录下创建build.gradle文件并进行以下配置:

```groovy
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    implementation gradleApi()
    implementation localGroovy()
}

repositories {
    jcenter()
    mavenCentral()
}

sourceSets{
    main{
        groovy{
            srcDir 'src/main/groovy'
        }
        java {
            srcDir "src/main/java"
        }
        resources{
            srcDir 'src/main/resources'
        }
    }
}
```

## 3. 编写插件代码

在main/src/groovy/xxx.groovy文件中编写插件代码，如下：

```groovy
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
        project.extensions.create("")

    }
}
```

## 4. 关联配件实现与引用

在main/src/resources/META-INF/gradle-plugins目录下创建xxx.properties文件。
**以后apply plugin: "xxx"就是要和此处的文件名保持一致。**

然后在文件中关联插件实现，如下：

```properties
implementation-class=com.packcheng.MyPlugin
```

## 5. 上传本地仓库

在module的build文件中添加上传代码，如下：

```groovy
uploadArchives{
    repositories {
        mavenDeployer{
            pom.groupId = 'com.packchengConfig'
            pom.artifactId = 'my-plugin'
            pom.version = 1.0
            repository(url: uri('../repository'))
        }
    }
}
```

然后在Gradle窗口中找到对应的Module的uploadArchives的Task，运行，上传到本地仓库

## 6. 将本地插件仓库引入到项目中

在需要使用的项目的根目录下做以下配置:

```groovy
buildscript {
    ext.kotlin_version = "1.3.72"
    repositories {
        google()
        jcenter()

        // 引入自己的插件仓库
        maven{
            url uri('./repository')
        }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.1.0"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

        // 引入自定义插件
        classpath "com.packchengConfig:my-plugin:1.0"
    }
}
```

***由于目前插件和引用是在同一个根Project下的，引入后打开插件Module中插件的代码会提示类重复，只需要将插件Module中build目录删除即可。***

## 7. 在需要的模块处引入并使用自定义插件

在Module的build.gradle文件中做以下配置引入插件：

```groovy
apply plugin: 'xxx'
```

**此处的xxx需要和自定义插件Module/main/src/resourse/META-INF/gradle-plugins/xxx.properties中的文件名保持一致。**
