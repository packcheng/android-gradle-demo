package com.packcheng.router.gradle

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RouterMappingByteCodeBuilder implements Opcodes {
    public static final CLASS_NAME = "com/packcheng/router/mapping/generated/RouterMapping"

    static byte[] get(Set<String> appMappingClassNames) {
        // 1. 创建类
        // 2. 创建构造方法
        // 3. 创建get方法
        //      3.1 创建一个Map
        //      3.2 向Map中写入映射表内容
        //      3.3 返回创建的Map

        // 用于生成类 ClassWriter.COMPUTE_MAXS 自动计算类的栈大小
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)

        classWriter.visit(V1_8,
                ACC_PUBLIC | ACC_SUPER,
                CLASS_NAME,
                null,
                "java/lang/Object",
                null)

        // 用于生成或编辑方法
        MethodVisitor methodVisitor

        // 创建构建方法
        methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null)

        // 填充构建方法方法体的内容
        methodVisitor.visitCode()
        // 调用父类的构造方法-此处调用的是Object的构造方法
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false)
        methodVisitor.visitInsn(RETURN)
        methodVisitor.visitMaxs(1, 1)
        methodVisitor.visitEnd()


        // 创建get方法
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC,
                "get",
                "()Ljava/util/Map;",
                "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;",
                null)
        methodVisitor.visitCode()

        // 创建一个HashMap
        methodVisitor.visitTypeInsn(NEW, "java/util/HashMap")
        // 入栈
        methodVisitor.visitInsn(DUP)
        // 调用HashMap的构造方法
        methodVisitor.visitMethodInsn(INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V"
                , false)
        // 将新建的HashMap对象保存起来
        methodVisitor.visitVarInsn(ASTORE, 0)
        // 向Map中，逐个填充映射表中的内容
        appMappingClassNames.each {
            methodVisitor.visitVarInsn(ALOAD, 0)
            methodVisitor.visitMethodInsn(INVOKESTATIC,
                    "com/packcheng/router/routes/$it",
                    "get",
                    "()Ljava/util/Map;",
                    false)
            methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                    "java/util/Map",
                    "putAll",
                    "(Ljava/util/Map;)V",
                    true)
        }

        // 返回Map
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(2, 1)

        methodVisitor.visitEnd()

        return classWriter.toByteArray()
    }
}