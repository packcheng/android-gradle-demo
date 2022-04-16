package com.packcheng.method_tracker.gradle.timer

import org.objectweb.asm.AnnotationVisitor

/**
 * 解析自定义注解的内容
 */
class ZbcTimerAnnotationVisitor extends AnnotationVisitor {

    ZbcTimerAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor)
    }
}