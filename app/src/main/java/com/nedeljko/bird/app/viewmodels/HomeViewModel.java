package com.nedeljko.bird.app.viewmodels;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel {
    private TwitterClient mTwitterClient;
    private TimelineViewModelListener mListener;
    private ArrayList<Tweet> mTweets;
    boolean mHasLocalTweets = false;
    boolean mHasFinishedInitialLoad = false;
    int mStartId = 0;
    int mMaxId = 0;

    public HomeViewModel(TwitterClient twitterClient, TimelineViewModelListener listener) {
        mTwitterClient = twitterClient;
        mListener = listener;
        mTweets = new ArrayList<Tweet>();
    }

    public boolean hasLocalTweets() {
        return mHasLocalTweets;
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

    public void insertTweet(Tweet tweet) {
        mTweets.add(0, tweet);
        mListener.onTimelineItemsChanged();
    }

    public void fetchLocalTweets() {
        List<Tweet> tweets = new Select().from(Tweet.class).orderBy("CreatedAt DESC").execute();
        mTweets.removeAll(mTweets);
        mTweets.addAll(tweets);

        if (mTweets.size() > 0) {
            mStartId = mTweets.get(0).getRemoteId();
            mMaxId = mTweets.get(mTweets.size()-1).getRemoteId();
            mHasLocalTweets = true;
        }

        mListener.onTimelineItemsChanged();
    }

    public void loadTimeline() {
        loadTimeline(false, false);
    }

    public void loadNewerTimelinePage() {
        loadTimeline(true, false);
    }

    public void loadNextTimelinePage() {
        loadTimeline(false, true);
    }

    private void loadTimeline(boolean loadNewer, final boolean loadOlder) {
        if (loadOlder && !mHasFinishedInitialLoad) {
            return;
        }

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

    private void processTimelineResponse(JSONArray jsonResponse, boolean insertBefore) {
        try {
            ActiveAndroid.beginTransaction();

            // Remove the database cache if we have successfully loaded new tweets from the server.
            if (mMaxId == 0) {
                new Delete().from(Tweet.class).execute();
                mTweets.removeAll(mTweets);
            }

            for (int i = 0; i < jsonResponse.length(); i++) {
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
