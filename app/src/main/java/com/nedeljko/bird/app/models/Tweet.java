package com.nedeljko.bird.app.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    @Column(name = "CreatedAt")
    private Date mCreatedAt;

    @Column(name = "RemoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long mRemoteId;

    @Column(name = "Text")
    private String mText;

    @Column(name = "User", onDelete = Column.ForeignKeyAction.CASCADE)
    private User mUser;

    public Tweet() {
        super();
    }

    public Tweet(JSONObject jsonObject) {
        super();
        update(jsonObject);
    }

    public void update(JSONObject jsonObject) {
        try {
            final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.getDefault());
            try {
                mCreatedAt = dateFormat.parse(jsonObject.getString("created_at"));
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }

            mRemoteId = jsonObject.getLong("id");
            mText = jsonObject.getString("text");

            JSONObject userObject = jsonObject.getJSONObject("user");
            if (mUser == null) {
                mUser = new User(userObject);
            } else {
                mUser.update(userObject);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        save();
    }

    //region Getters and Setters

    public long getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(long remoteId) {
        mRemoteId = remoteId;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    //endregion

    @Override
    public String toString() {
        return mText;
    }
}
