package com.nedeljko.bird.app.viewmodels;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserTimelineViewModel extends TimelineViewModel {
    private long mUserId;

    public UserTimelineViewModel(TwitterClient twitterClient,
                                 TimelineViewModelListener listener, long userId) {
        super(twitterClient, listener);
        mUserId = userId;
    }

    //region TimelineViewModel

    @Override
    public void loadTimeline(boolean loadNewer, final boolean loadOlder) {
        if (loadOlder && !mHasFinishedInitialLoad) {
            return;
        }

        mTwitterClient.getUserTimeline(mUserId, 200, mStartId, mMaxId, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        mListener.onTimelineLoadStart();
                    }

                    @Override
                    public void onSuccess(JSONArray jsonResponse) {
                        processTimelineResponse(jsonResponse, !loadOlder);
                        mListener.onTimelineLoadSuccess();
                        if (!mHasFinishedInitialLoad) {
                            mHasFinishedInitialLoad = getTweets().size() > 0;
                        }
                        mListener.onTimelineItemsChanged();
                    }

                    @Override
                    public void onFailure(Throwable error, JSONObject errorResponse) {
                        System.out.println(errorResponse);
                        mListener.onTimelineLoadFailure();
                    }

                    @Override
                    public void onFinish() {
                        mListener.onTimelineLoadFinish();
                    }
                }
        );
    }

    //endregion
}