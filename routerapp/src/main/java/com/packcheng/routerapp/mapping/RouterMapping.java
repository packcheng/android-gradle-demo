package com.packcheng.routerapp.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/5 22:18
 */
public class RouterMapping {
    public static Map<String, String> get() {
        Map<String, String> mapping = new HashMap<>();
        mapping.putAll(RouterMapping_1.get());
        mapping.putAll(RouterMapping_2.get());
        return mapping;
    }
}
