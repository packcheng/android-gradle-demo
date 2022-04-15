package com.packcheng.method_tracker.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.packcheng.method_tracker.gradle.log.LifecycleClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MethodTrackerTransform extends Transform {

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
                if (shouldModifyClass(file.absolutePath)) {
                    println(" ---------------deal with class's class file: " + file.absolutePath)
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    LifecycleClassVisitor cv = new LifecycleClassVisitor(classWriter)
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
                if (shouldModifyClass(entryName)) {
                    println(" ---------------deal with jar's class file: " + entryName)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    LifecycleClassVisitor cv = new LifecycleClassVisitor(classWriter)
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

    boolean shouldModifyClass(String filePath) {
        return (filePath.contains("com/packcheng")
                && filePath.endsWith("Activity.class")
                && !filePath.contains("R.class")
                && !filePath.contains('$')
                && !filePath.contains('R$')
                && !filePath.contains("BuildConfig.class"))
    }
}