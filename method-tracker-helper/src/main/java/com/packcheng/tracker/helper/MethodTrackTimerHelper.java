package com.packcheng.tracker.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 埋点方法耗时统计工具类
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/15 16:24
 */
public class MethodTrackTimerHelper {
    public final static Map<String, Long> sStartTime = new HashMap<>();
    public final static Map<String, Long> sEndTime = new HashMap<>();

    public static void setStartTime(String methodName, long time) {
        sStartTime.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        sEndTime.put(methodName, time);
    }

    public static String getCostTime(String methodName) {
        Long start = sStartTime.remove(methodName);
        Long end = sEndTime.remove(methodName);
        if (null == start || end == null) {
            return "method: " + methodName + " cost UNKNOWN!";
        }
        long dex = end - start;
        return "method: " + methodName + " cost " + dex + " ns";
    }
}
