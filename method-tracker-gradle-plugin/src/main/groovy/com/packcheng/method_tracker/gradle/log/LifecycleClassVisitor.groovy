package com.packcheng.method_tracker.gradle.log

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LifecycleClassVisitor extends ClassVisitor implements Opcodes {

    private String mClassName

    LifecycleClassVisitor(ClassVisitor cv) {
        super(ASM7, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        log("LifecycleClassVisitor-------------> visit: " + name)
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        log("LifecycleClassVisitor-------------> visitMethod: " + name)
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("onCreate".equals(name)) {
            return new ActivityOnCreateVisitor(mv)
        } else if ("onDestroy".equals(name)) {
            return new ActivityOnDestroyVisitor(mv)
        }
        return mv
    }

    @Override
    void visitEnd() {
        log("LifecycleClassVisitor-------------> visitEnd")
        super.visitEnd()
    }

    void log(String msg) {
        println(msg)
    }
}