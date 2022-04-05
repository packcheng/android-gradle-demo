package com.packcheng.router.gradle


import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 路由信息收集类
 *
 * @author packcheng* @date 2022/4/5 18:14
 */
class RouterMappingCollector {
    public static final String PACKAGE_NAME = "com/packcheng/router/routes"
    public static final String CLASS_NAME_PREFIX = "RouterMapping_";
    public static final String CLASS_NAME_SUFFIX = ".class";

    private final Set<String> mappingClassNames = new HashSet<>()

    /**
     * 获取收集到的映射表类名
     * @return
     */
    Set<String> getMappingClassNames() {
        return mappingClassNames
    }

    /**
     * 收集class文件或者class文件目录中的映射表类
     * @param classFile
     */
    void collect(File classFile) {
        if (null == classFile || !classFile.exists()) return

        if (classFile.isFile()) {
            if (classFile.absolutePath.contains(PACKAGE_NAME)
                    && classFile.name.startsWith(CLASS_NAME_PREFIX)
                    && classFile.name.endsWith(CLASS_NAME_SUFFIX)) {
                String className = classFile.name.replace(CLASS_NAME_SUFFIX, "")
                mappingClassNames.add(className)
            }
        } else {
            classFile.listFiles().each {
                collect(it)
            }
        }
    }

    /**
     * 收集Jar包中的目标类
     * @param jarFile
     */
    void collectFromJarFile(File jarFile) {
        Enumeration enumeration = new JarFile(jarFile).entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.name
            if (entryName.contains(PACKAGE_NAME)
                    && entryName.contains(CLASS_NAME_PREFIX)
                    && entryName.contains(CLASS_NAME_SUFFIX)) {
                String className = entryName.replace(PACKAGE_NAME, "")
                        .replace("/", "")
                        .replace(CLASS_NAME_SUFFIX, "")
                mappingClassNames.add(className)
            }
        }
    }
}