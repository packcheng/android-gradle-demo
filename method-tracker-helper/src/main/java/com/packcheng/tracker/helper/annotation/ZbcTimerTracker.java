package com.packcheng.tracker.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/15 16:28
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ZbcTimerTracker {
    /**
     * 增加备注信息
     */
    String desc() default "";
}
