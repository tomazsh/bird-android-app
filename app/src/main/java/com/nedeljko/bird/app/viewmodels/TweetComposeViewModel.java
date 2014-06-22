package com.nedeljko.bird.app.viewmodels;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.models.Tweet;

import org.json.JSONObject;

public class TweetComposeViewModel {
    private TwitterClient mTwitterClient;
    private TweetComposeViewModelListener mListener;
    private Tweet mTweet;

    public interface TweetComposeViewModelListener {
        void onTweetPostStart();
        void onTweetPostSuccess();
        void onTweetPostFailure();
        void onTweetPostFinish();
    }

    public TweetComposeViewModel(TweetComposeViewModelListener listener) {
        mTwitterClient = BirdApplication.getTwitterClient();
        mListener = listener;
        mTweet = new Tweet();
    }

    //region Getters and Setters

    public Tweet getTweet() {
        return mTweet;
    }

    //endregion

    //region Posting

    public void postTweet() {
        mTwitterClient.postTweet(mTweet.getText(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mListener.onTweetPostStart();
            }

            @Override
            public void onSuccess(JSONObject jsonResponse) {
                processTweetResponse(jsonResponse);
                mListener.onTweetPostSuccess();
            }

            @Override
            public void onFailure(Throwable error, JSONObject errorResponse) {
                System.out.println(errorResponse);
                mListener.onTweetPostFailure();
            }

            @Override
            public void onFinish() {
                mListener.onTweetPostFinish();
            }
        });
    }

    private void processTweetResponse(JSONObject jsonResponse) {
        mTweet.update(jsonResponse);
    }

    //endregion
}
