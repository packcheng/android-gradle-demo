package com.packcheng.method_tracker.gradle.timer

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

/**
 * 基于局部变量表统计方法耗时
 */
class StackTimerVisitorAdapter extends BaseTimerVisitorAdapter {

    int slotIndex

    protected StackTimerVisitorAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        if (shouldTrack) {
            log("onMethodEnter------->")
            slotIndex = newLocal(Type.LONG_TYPE)
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
            mv.visitVarInsn(LSTORE, slotIndex)
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        if (shouldTrack) {
            log("onMethodExit------->")

            // 计算时间差
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, slotIndex);
            mv.visitInsn(LSUB);
            mv.visitVarInsn(LSTORE, slotIndex)

            // 比较时间
            mv.visitVarInsn(LLOAD, slotIndex)
            mv.visitLdcInsn(new Long(5L))
            mv.visitInsn(LCMP)

            Label label0 = new Label();
            mv.visitJumpInsn(IFLE, label0);
            mv.visitLdcInsn("MethodTrackTimerHelper");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("#");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(" cost: ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(LLOAD, slotIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("ns");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(POP);
            mv.visitLabel(label0);
        }
    }
}