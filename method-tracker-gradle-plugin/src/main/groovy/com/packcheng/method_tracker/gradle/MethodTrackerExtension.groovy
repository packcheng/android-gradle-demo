package com.packcheng.method_tracker.gradle

/**
 * 配置是否启用MethodTrackerTransform
 */
class MethodTrackerExtension {
    private boolean enableTrack

    boolean getEnableTrack() {
        return enableTrack
    }

    void setEnableTrack(boolean enableTrack) {
        this.enableTrack = enableTrack
    }
}