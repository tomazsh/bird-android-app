package com.nedeljko.bird.app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "Users")
public class User extends Model implements Serializable {
    @Column(name = "Name")
    private String mName;

    @Column(name = "ProfileImageUrl")
    private String mProfileImageUrl;

    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long mRemoteId;

    @Column(name = "ScreenName")
    private String mScreenName;

    @Column(name = "TweetCount")
    private int mTweetCount;

    @Column(name = "FollowingsCount")
    private int mFollowingsCount;

    @Column(name = "FollowersCount")
    private int mFollowersCount;

    public User() {
        super();
    }

    public User(JSONObject jsonObject) {
        super();
        update(jsonObject);
    }

    public void update(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
            mProfileImageUrl = jsonObject.getString("profile_image_url").replace("normal", "bigger");
            mRemoteId = jsonObject.getLong("id");
            mScreenName = jsonObject.getString("screen_name");
            mTweetCount =  Integer.parseInt(jsonObject.getString("statuses_count"));
            mFollowingsCount = Integer.parseInt(jsonObject.getString("friends_count"));
            mFollowersCount = Integer.parseInt(jsonObject.getString("followers_count"));
        } catch (JSONException jsonException) {
            System.out.println("Exception creating User from JSON response: " + jsonException);
        }
        save();
    }

    public String getStyledScreenName() {
        return "@" + mScreenName;
    }

    public List<Tweet> getTweets() {
        return getMany(Tweet.class, "user");
    }

    //region Getters and Setters

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

    public long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(long remoteId) {
        mRemoteId = remoteId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    public int getTweetCount() {
        return mTweetCount;
    }

    public void setTweetCount(int tweetCount) {
        mTweetCount = tweetCount;
    }

    public int getFollowingsCount() {
        return mFollowingsCount;
    }

    public void setFollowingsCount(int followingsCount) {
        mFollowingsCount = followingsCount;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(int followersCount) {
        mFollowersCount = followersCount;
    }

    //endregion
}
