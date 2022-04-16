package com.packcheng.method_tracker.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.packcheng.method_tracker.gradle.log.LifecycleClassVisitor
import com.packcheng.method_tracker.gradle.timer.TimerClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MethodTrackerTransform extends Transform {
    public static final TYPE_LOG = 0
    public static final TYPE_TIMER = 1
    public static final TYPE_TIMER_STACK = 2

    @Override
    String getName() {
        return "MethodTrackerTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        def outputProvider = transformInvocation.outputProvider
        // 删除旧输出
        if (null != outputProvider) {
            outputProvider.deleteAll()
        }
        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                handleDirectoryInput(dirInput, outputProvider)
            }

            input.jarInputs.each { jarInput ->
                handleJarInput(jarInput, outputProvider)
            }
        }
    }

    /**
     * 处理以源码方式编译的class文件
     * @param dirInput 源码class文件
     * @param outputProvider
     */
    void handleDirectoryInput(DirectoryInput dirInput, TransformOutputProvider outputProvider) {
        if (dirInput.file.isDirectory()) {
            dirInput.file.eachFileRecurse { file ->
                if (showModifyClass(file.absolutePath)) {
                    if (MethodTrackerPlugin.config.enableBuildLog) {
                        println(" ---------------deal with class's class file: " + file.absolutePath)
                    }
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = createModifyClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] codes = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + file.name)
                    fos.write(codes)
                    fos.flush()
                    fos.close()
                }
            }
        }
        def destOutput = outputProvider.getContentLocation(dirInput.name,
                dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(dirInput.file, destOutput)
    }

    /**
     * 处理以jar包方式编译的jar文件
     * @param dirInput jar文件
     * @param outputProvider
     */
    void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName.substring(0, jarName.size() - 4)
            }

            // 创建jar临时输出路径
            def tempJarOutFile = new File(jarInput.file.parent + File.separator + "classes_temp.jar")
            if (tempJarOutFile.exists()) {
                tempJarOutFile.delete()
            }

            // 临时jar文件输出
            def jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarOutFile))

            def jarFile = new JarFile(jarInput.file)
            def enumerations = jarFile.entries()
            while (enumerations.hasMoreElements()) {
                def element = enumerations.nextElement()
                String entryName = element.name
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(zipEntry)
                jarOutputStream.putNextEntry(zipEntry)
                if (showModifyClass(entryName)) {
                    if (MethodTrackerPlugin.config.enableBuildLog) {
                        println(" ---------------deal with jar's class file: " + entryName)
                    }
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = createModifyClassVisitor(classWriter)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] codes = classWriter.toByteArray()
                    jarOutputStream.write(codes)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }

            jarOutputStream.close()
            jarFile.close()

            def destFile = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tempJarOutFile, destFile)
        }
    }

    /**
     * 当前路径是否包含需要修改的class
     * @param filePath
     * @return
     */
    boolean showModifyClass(String filePath) {
        def TRACKER_TYPE = MethodTrackerPlugin.config.trackType
        if (TYPE_LOG == TRACKER_TYPE) {
            return shouldModifyLogClass(filePath)
        } else if (TYPE_TIMER == TRACKER_TYPE
                || TYPE_TIMER_STACK == TRACKER_TYPE) {
            return shouldModifyTimerClass(filePath)
        } else {
            println(">>>>>>>>>>>>>>>> unSupport tracker type: $TRACKER_TYPE for transform ${getName()}")
            return false
        }
    }

    /**
     * 创建class修改器
     * @param cv
     * @return
     */
    ClassVisitor createModifyClassVisitor(ClassVisitor cv) {
        def TRACKER_TYPE = MethodTrackerPlugin.config.trackType
        if (TYPE_LOG == TRACKER_TYPE) {
            return new LifecycleClassVisitor(cv)
        } else if (TYPE_TIMER == TRACKER_TYPE
                || TYPE_TIMER_STACK == TRACKER_TYPE) {
            return new TimerClassVisitor(cv)
        }
        return null
    }

    /**
     * 当文件的路径符合一下规则时，表示该文件包含需要增加log的class
     * @param filePath
     * @return
     */
    boolean shouldModifyLogClass(String filePath) {
        return (filePath.contains("com/packcheng")
                && filePath.endsWith("Activity.class")
                && !filePath.contains("R.class")
                && !filePath.contains('$')
                && !filePath.contains('R$')
                && !filePath.contains("BuildConfig.class"))
    }

    /**
     * 当文件的路径符合一下规则时，表示该文件包含需要增加方法执行耗时统计的class
     * @param filePath
     * @return
     */
    boolean shouldModifyTimerClass(String filePath) {
        return (filePath.contains("com/packcheng")
                && filePath.endsWith(".class")
                && !filePath.contains("R.class")
                && !filePath.contains('$')
                && !filePath.contains('R$')
                && !filePath.contains("BuildConfig.class"))
    }
}