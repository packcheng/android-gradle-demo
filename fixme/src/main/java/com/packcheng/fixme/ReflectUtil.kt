package com.packcheng.fixme

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 反射工具类
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/9 14:55
 */
object ReflectUtil {

    /**
     * 反射返回某个类的指定字段
     * @param instance Any
     * @param fieldName String 需要查找的字段名
     * @return Field
     */
    fun getField(instance: Any, fieldName: String): Field {
        var clazz = instance.javaClass
        while (null != clazz) {
            try {
                val field = clazz.getDeclaredField(fieldName)
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                return field
            } catch (e: Exception) {
                clazz = clazz.superclass as Class<Any>
            }
        }
        throw NoSuchFieldException("No such field: $fieldName")
    }

    /**
     * 反射返回某个类的指定方法
     * @param instance Any
     * @param methodName String 需要查找的方法名
     * @param paramType Array<out Class<*>?> 需要查找的方法参数类型列表
     * @return Method
     */
    fun getMethod(instance: Any, methodName: String, vararg paramType: Class<*>?): Method {
        var clazz = instance.javaClass
        while (null != clazz) {
            try {
                val method = clazz.getDeclaredMethod(methodName, *paramType)
                if (!method.isAccessible) {
                    method.isAccessible = true
                }
                return method
            } catch (e: Exception) {
                clazz = clazz.superclass as Class<Any>
            }
        }
        throw NoSuchMethodException("No such method: $methodName")
    }
}