package com.packcheng.method_tracker.gradle

class MethodTrackerConfig {
    private boolean enableBuildLog
    private int trackType

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