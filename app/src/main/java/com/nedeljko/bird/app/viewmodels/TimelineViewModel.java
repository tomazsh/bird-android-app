package com.nedeljko.bird.app.viewmodels;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineViewModel {
    protected TwitterClient mTwitterClient;
    protected TimelineViewModelListener mListener;
    protected ArrayList<Tweet> mTweets;
    protected boolean mHasLocalTweets = false;
    protected boolean mHasFinishedInitialLoad = false;
    protected long mStartId = 0;
    protected long mMaxId = 0;

    public TimelineViewModel(TwitterClient twitterClient, TimelineViewModelListener listener) {
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

    public void loadTimeline(boolean loadNewer, final boolean loadOlder) {

    }

    public void processTimelineResponse(JSONArray jsonResponse, boolean insertBefore) {
        try {
            ActiveAndroid.beginTransaction();

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
