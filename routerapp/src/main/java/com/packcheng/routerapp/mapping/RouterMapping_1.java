package com.packcheng.routerapp.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/5 22:17
 */
public class RouterMapping_1 {
    public static Map<String, String> get() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("routter://xxx", "com.xxx.xxx.AAActivity");
        return mapping;
    }
}
