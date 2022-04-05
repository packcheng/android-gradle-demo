package com.packcheng.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由路径注解
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/4 17:47
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Destination {

    /**
     * 当前页面URL，不能为空
     *
     * @return 页面url
     */
    String url();

    /**
     * 对于当前页面的描述
     *
     * @return 页面描述。如："个人主页"
     */
    String description();
}
