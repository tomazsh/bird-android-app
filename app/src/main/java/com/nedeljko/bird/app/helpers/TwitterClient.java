package com.nedeljko.bird.app.helpers;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> API_CLASS = TwitterApi.class;
    public static final String URL = "https://api.twitter.com/1.1";
    public static final String CONSUMER_KEY = "sDACi839FSO4vxKhxne37hrSs";
    public static final String CONSUMER_SECRET = "g5eBtWaM7zIlxyCjr84USuZcsOIoHv6jeohJNO70HuxgvayPx5";
    public static final String CALLBACK_URL = "http://app.bird.nedeljko.com";

    public TwitterClient(Context context) {
        super(context, API_CLASS, URL, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
    }
}
