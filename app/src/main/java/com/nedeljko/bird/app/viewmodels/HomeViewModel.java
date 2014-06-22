package com.nedeljko.bird.app.viewmodels;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeViewModel {
    private TwitterClient mTwitterClient;
    private TimelineViewModelListener mListener;
    private ArrayList<Tweet> mTweets;
    boolean mHasFinishedInitialLoad = false;
    int mStartId = 0;
    int mMaxId = 0;

    public HomeViewModel(TwitterClient twitterClient, TimelineViewModelListener listener) {
        mTwitterClient = twitterClient;
        mListener = listener;
        mTweets = new ArrayList<Tweet>();
    }

    public boolean hasFinishedInitialLoad() {
        return mHasFinishedInitialLoad;
    }

    //region Getters and Setters

    public ArrayList<Tweet> getTweets() {
        return mTweets;
    }

    //endregion

    //region Timeline

    public void loadNewerTimelinePage() {
        loadTimeline(true, false);
    }

    public void loadNextTimelinePage() {
        loadTimeline(false, true);
    }

    private void loadTimeline(boolean loadNewer, final boolean loadOlder) {
        mTwitterClient.getHomeTimeline(200, loadNewer ? mStartId : 0,
                loadOlder ? mMaxId : 0, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mListener.onTimelineLoadStart();
            }

            @Override
            public void onSuccess(JSONArray jsonResponse) {
                processTimelineResponse(jsonResponse, !loadOlder);
                mListener.onTimelineLoadSuccess();
                if (!mHasFinishedInitialLoad) {
                    mHasFinishedInitialLoad = mTweets.size() > 0;
                }
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
        });
    }

    private void processTimelineResponse(JSONArray jsonResponse, boolean insertBefore) {
        try {
            for (int i = 0; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonResponse.get(i);
                Tweet tweet = new Tweet(jsonObject);
                if (insertBefore) {
                    mTweets.add(i, tweet);
                } else {
                    mTweets.add(tweet);
                }
                mMaxId = tweet.getRemoteId();
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    //endregion
}
