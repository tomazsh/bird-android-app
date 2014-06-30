package com.nedeljko.bird.app.viewmodels;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeTimelineViewModel extends TimelineViewModel {

    public HomeTimelineViewModel(TwitterClient twitterClient, TimelineViewModelListener listener) {
        super(twitterClient, listener);
    }

    //region TimelineViewModel

    @Override
    public void loadTimeline(boolean loadNewer, final boolean loadOlder) {
        if (loadOlder && !mHasFinishedInitialLoad) {
            return;
        }

        mTwitterClient.getHomeTimeline(200, 0, mMaxId, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mListener.onTimelineLoadStart();
            }

            @Override
            public void onSuccess(JSONArray jsonResponse) {
                processTimelineResponse(jsonResponse, !loadOlder);
                mListener.onTimelineLoadSuccess();
                mHasFinishedInitialLoad = true;
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
        });
    }
    @Override
    public void processTimelineResponse(JSONArray jsonResponse, boolean insertBefore) {
        try {
            ActiveAndroid.beginTransaction();

            // Remove the database cache if we have successfully loaded new tweets from the server.
            if (mMaxId == 0) {
                new Delete().from(Tweet.class).execute();
                mTweets.removeAll(mTweets);
            }

            int start = mMaxId == 0 ? 0 : 1;
            for (int i = start; i < jsonResponse.length(); i++) {
                JSONObject jsonObject = (JSONObject)jsonResponse.get(i);
                Tweet tweet = new Tweet(jsonObject);
                mTweets.add(tweet);
                mMaxId = tweet.getRemoteId();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    //endregion
}
