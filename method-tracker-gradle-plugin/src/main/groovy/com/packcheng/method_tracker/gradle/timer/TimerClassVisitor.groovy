package com.packcheng.method_tracker.gradle.timer

import com.packcheng.method_tracker.gradle.MethodTrackerPlugin
import com.packcheng.method_tracker.gradle.MethodTrackerTransform
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 在自己实现的类中增加方法耗时统计
 */
class TimerClassVisitor extends ClassVisitor implements Opcodes {

    TimerClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ((access & ACC_INTERFACE) == 0 && "<init>" != name && "<clinit>" != name) {
            if(MethodTrackerTransform.TYPE_TIMER == MethodTrackerPlugin.config.trackType){
                methodVisitor = new TimerVisitorAdapter(api, methodVisitor, access, name, descriptor)
            }else if(MethodTrackerTransform.TYPE_TIMER_STACK == MethodTrackerPlugin.config.trackType){
                methodVisitor = new StackTimerVisitorAdapter(api, methodVisitor, access, name, descriptor)
            }
        }
        return methodVisitor
    }
}