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

    public void getHomeTimeline(int count, int sinceId, int maxId,
                                AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");
        RequestParams parameters = new RequestParams();
        parameters.put("count", Integer.toString(count));
        parameters.put("sinceId", Integer.toString(sinceId));
        parameters.put("maxId", Integer.toString(maxId));
        client.get(url, parameters, handler);
    }

    public void postTweet(String status, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/update.json");
        RequestParams parameters = new RequestParams();
        parameters.put("status", status);
        client.post(url, parameters, handler);
    }
}
