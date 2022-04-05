package com.packcheng.router.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 自定义Transform
 *
 * ref: https://blog.csdn.net/nihaomabmt/article/details/118525113
 *
 * 每个Transform都是一个gradle中task，而Android Gradle中的TaskMananger将每个Transform串联到一起。
 *
 * 有两种方式的Transform：
 * （1）消费型：当前Transform需要将消费型输出给下一个Transform，每个Transform节点都可以对class进行处理之后在传递给下一个Transform。
 *      通过getScopes()设置的就是消费型输入，需要将输出给下一个任务，此时获取的outputProvider不为null；
 *
 * （2）引用型：当前Transform可以读取这些输入，而不需要输出给下一个Transform。
 *      通过getReferencedScopes()设置的为引用型输入，此时获取的outputProvider不为null。
 */
class RouterMappingTransform extends Transform {

    /**
     * 当前Transform的名字
     * Task的名字组成是由“transform+ContentType+With+transform名字+TaskFor+buildType+productFlavor”组成的
     * @return
     */
    @Override
    String getName() {
        return "RouterMappingTransform"
    }

    /**
     * 返回的内容即为指定该Transform要处理的数据类型，即该Transform的输入文件的类型
     *
     * CONTENT_CLASS 要处理的数据类型是java class文件，并且包含jar文件
     * CONTENT_JARS 要处理的数据类型是jar文件
     * CONTENT_RESOURCES 要处理的数据类型是java resource文件
     * CONTENT_DEX 要处理的数据类型是dex文件
     * CONTENT_DEX_WITH_RESOURCES 要处理的数据类型是dex文件、java resource文件
     *
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 返回的内容即指定该Transform的修改input文件的作用域
     *
     * 通过getInputTypes() 和getScopes()就设置好了需要处理的设置为输入的对应的class的字节码，
     * 当复写transform()的时候，如果不进行任何处理，那么将无法生成.dex文件，在最后打包之后的apk文件中无.dex文件
     *
     * PROJECT_ONLY 仅仅当前工程
     * SCOPE_FULL_PROJECT 整个项目工程+外部library库
     *
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     *  返回的内容即指定该Transform的查看input文件的作用域
     *
     *  getReferencedScopes()区别于getScopes()，复写transform()并不会覆盖Android原来的.class文件转换成dex文件的过程。
     *  该方法主要用来该自定义的Transform并不想处理任何input文件的内容，仅仅只是想查看input文件的内容的作用域范围。
     * @return
     */
//    @Override
//    Set<? super QualifiedContent.Scope> getReferencedScopes() {
//        return super.getReferencedScopes()
//    }

    /**
     *  返回的内容即指定该Transform的是否进行增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //如果不带super，就不会生成dex文件
        super.transform(transformInvocation)

        RouterMappingCollector routerMappingCollector = new RouterMappingCollector()

        // 1. 遍历所有的Input
        transformInvocation.inputs.each {
            // 文件夹类型
            it.directoryInputs.each { dirInput ->
                File destDir = transformInvocation.outputProvider
                        .getContentLocation(dirInput.name,
                                dirInput.contentTypes,
                                dirInput.scopes,
                                Format.DIRECTORY)
                // 2. 对Input进行二次处理
                routerMappingCollector.collect(dirInput.file)
                // 3. 将input拷贝到目标目录
                FileUtils.copyDirectory(dirInput.file, destDir)
            }

            // jar类型
            it.jarInputs.each { jarInput ->
                File jarDest = transformInvocation.outputProvider
                        .getContentLocation(jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR)
                // 2. 对Input进行二次处理
                routerMappingCollector.collectFromJarFile(jarInput.file)
                // 3. 将input拷贝到目标目录
                FileUtils.copyFile(jarInput.file, jarDest)
            }
        }

        println(getName() + ">>>> All Mapping class name is: ${routerMappingCollector.getMappingClassNames()}")

        File mappingJarFile = transformInvocation.outputProvider
                .getContentLocation("router-mapping",
                        getOutputTypes(),
                        getScopes(),
                        Format.JAR)
        println("${getName()} >>> mappingJarFile = $mappingJarFile")

        if(!mappingJarFile.parentFile.exists()){
            mappingJarFile.parentFile.mkdirs()
        }
        if(mappingJarFile.exists()){
            mappingJarFile.delete()
        }

        // 将生成的字节码生成本地文件
        FileOutputStream fos = new FileOutputStream(mappingJarFile)
        JarOutputStream jos = new JarOutputStream(fos)
        ZipEntry zipEntry =new ZipEntry(RouterMappingByteCodeBuilder.CLASS_NAME + ".class")
        jos.putNextEntry(zipEntry)
        jos.write(RouterMappingByteCodeBuilder.get(routerMappingCollector.getMappingClassNames()))
        jos.closeEntry()
        jos.close()
        fos.close()
    }
}