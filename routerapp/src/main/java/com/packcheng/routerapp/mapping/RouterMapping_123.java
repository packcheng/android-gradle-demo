package com.packcheng.routerapp.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/4 18:41
 */
public class RouterMapping_123 {
    public static Map<String, String> get() {
        Map<String, String> mapping = new HashMap<>();


        mapping.put("routter://xxx", "com.xxx.xxx.AAActivity");
        mapping.put("routter://xxxA", "com.xxx.xxx.AActivity");
        mapping.put("routter://xxxB", "com.xxx.xxx.BActivity");
        mapping.put("routter://xxxC", "com.xxx.xxx.CActivity");


        return mapping;
    }

    public static class RouterMapping_1649077999738 {
        public static Map get() {
            Map<String, String> mapping = new HashMap<>();
            mapping.put("router://page-home","com.packcheng.routerapp.MainActivity");
            return mapping;
        }
    }
}

