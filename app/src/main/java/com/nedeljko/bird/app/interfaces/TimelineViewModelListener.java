package com.nedeljko.bird.app.interfaces;

public interface TimelineViewModelListener {
    void onTimelineLoadStart();
    void onTimelineLoadProgress(int progress);
    void onTimelineLoadSuccess();
    void onTimelineLoadFailure();
    void onTimelineLoadFinish();
}
