package com.nedeljko.bird.app.helpers;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> API_CLASS = TwitterApi.class;
    public static final String URL = "https://api.twitter.com/1.1";
    public static final String CONSUMER_KEY = "sDACi839FSO4vxKhxne37hrSs";
    public static final String CONSUMER_SECRET = "g5eBtWaM7zIlxyCjr84USuZcsOIoHv6jeohJNO70HuxgvayPx5";
    public static final String CALLBACK_URL = "bird://auth";

    public TwitterClient(Context context) {
        super(context, API_CLASS, URL, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
    }

    public void getHomeTimeline(int count, long sinceId, long maxId,
                                AsyncHttpResponseHandler handler) {
        getTimeline(getApiUrl("statuses/home_timeline.json"), count, sinceId, maxId, handler);
    }

    public void getUserTimeline(long userId, int count, long sinceId, long maxId,
                                    AsyncHttpResponseHandler handler) {
        RequestParams parameters = new RequestParams();
        parameters.put("user_id", Long.toString(userId));
        parameters.put("count", Integer.toString(count));
        if (sinceId > 0) {
            parameters.put("since_id", Long.toString(sinceId));
        }
        if (maxId > 0) {
            parameters.put("max_id", Long.toString(maxId));
        }
        client.get(getApiUrl("statuses/user_timeline.json"), parameters, handler);
    }

    public void getMentionsTimeline(int count, long sinceId, long maxId,
                                    AsyncHttpResponseHandler handler) {
        getTimeline(getApiUrl("statuses/mentions_timeline.json"), count, sinceId, maxId, handler);
    }

    public void getTimeline(String url, int count, long sinceId, long maxId,
                            AsyncHttpResponseHandler handler) {
        RequestParams parameters = new RequestParams();
        parameters.put("count", Integer.toString(count));
        if (sinceId > 0) {
            parameters.put("since_id", Long.toString(sinceId));
        }
        if (maxId > 0) {
            parameters.put("max_id", Long.toString(maxId));
        }
        client.get(url, parameters, handler);
    }

    public void postTweet(String status, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/update.json");
        RequestParams parameters = new RequestParams();
        parameters.put("status", status);
        client.post(url, parameters, handler);
    }

    public void verifyCredentials(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/verify_credentials.json");
        RequestParams parameters = new RequestParams();
        parameters.put("skip_status", "true");
        client.get(url, parameters, handler);
    }
}
