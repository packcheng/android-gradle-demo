package com.packcheng.method_tracker.gradle.timer

import com.packcheng.method_tracker.gradle.MethodTrackerPlugin
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 统计方法耗时AdviceAdapter基类
 */
class BaseTimerVisitorAdapter extends AdviceAdapter {
    private static final String MY_ANNOTATION = "Lcom/packcheng/tracker/helper/annotation/ZbcTimerTracker;"

    protected int methodAccess
    protected String methodName
    protected String methodDesc
    protected boolean shouldTrack

    protected BaseTimerVisitorAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
        this.methodAccess = access
        this.methodName = name
        this.methodDesc = descriptor
    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor visitAnnotation = super.visitAnnotation(descriptor, visible)
        if (MY_ANNOTATION == descriptor) {
            log("visitAnnotation-------> $descriptor")
            shouldTrack = true
            return new ZbcTimerAnnotationVisitor(api, visitAnnotation) {
                @Override
                void visit(String name, Object value) {
                    super.visit(name, value)
                    log("visitAnnotation pair-------> name=$name, value: $value")
                }

                @Override
                void visitEnd() {
                    super.visitEnd()
                    log("visitAnnotation end-------> methodName=$methodName, methodDesc: $methodDesc")
                }
            }
        }
        return visitAnnotation
    }

    protected void log(String msg) {
        if (MethodTrackerPlugin.config.enableBuildLog) {
            println(msg)
        }
    }
}