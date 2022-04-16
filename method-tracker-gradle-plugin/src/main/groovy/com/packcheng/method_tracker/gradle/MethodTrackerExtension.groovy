package com.packcheng.method_tracker.gradle

/**
 * 配置是否启用MethodTrackerTransform
 */
class MethodTrackerExtension {
    // 是否启用transform
    private boolean enableTrack
    // 是否在构建过程中输出log
    private boolean enableBuildLog
    // 跟踪类型
    // 0：在onCreate和onDestroy打Log
    // 1：使用静态工具类和注解统计方法时间
    // 2：使用栈数据和注解统计方法时间
    private int trackType

    boolean getEnableTrack() {
        return enableTrack
    }

    void setEnableTrack(boolean enableTrack) {
        this.enableTrack = enableTrack
    }

    boolean getEnableBuildLog() {
        return enableBuildLog
    }

    void setEnableBuildLog(boolean enableBuildLog) {
        this.enableBuildLog = enableBuildLog
    }

    int getTrackType() {
        return trackType
    }

    void setTrackType(int trackType) {
        this.trackType = trackType
    }
}