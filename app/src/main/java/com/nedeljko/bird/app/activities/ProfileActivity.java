package com.nedeljko.bird.app.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.fragments.UserTimelineFragment;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.models.User;

import org.json.JSONObject;

public class ProfileActivity extends FragmentActivity {
    private UserTimelineFragment mUserTimelineFragment;
    private User mUser;
    private View mContainer;
    private ProgressBar mProgressBar;
    private SmartImageView mAvatarView;
    private TextView mNameTextView;
    private TextView mScreenNameTextView;
    private TextView mTweetCountTextView;
    private TextView mFollowerCountTextView;
    private TextView mFollowingCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContainer = findViewById(R.id.main_container);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mAvatarView = (SmartImageView)findViewById(R.id.avatar_view);
        mNameTextView = (TextView)findViewById(R.id.name_text_view);
        mScreenNameTextView = (TextView)findViewById(R.id.screen_name_text_view);
        mTweetCountTextView = (TextView)findViewById(R.id.tweet_count_text_view);
        mFollowerCountTextView = (TextView)findViewById(R.id.follower_count_text_view);
        mFollowingCountTextView = (TextView)findViewById(R.id.following_count_text_view);

        mUser = (User)getIntent().getSerializableExtra("user");
        if (mUser != null) {
            updateViews();
        } else {
            mContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            getCurrentUser();
        }
    }

    private void getCurrentUser() {
        TwitterClient client = BirdApplication.getTwitterClient();
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onSuccess(JSONObject jsonResponse) {
                mUser = new User(jsonResponse);
                updateViews();
                mContainer.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable error, JSONObject errorResponse) {
                System.out.println(errorResponse);
            }

            @Override
            public void onFinish() {
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void updateViews() {
        mAvatarView.setImageUrl(mUser.getProfileImageUrl());
        mNameTextView.setText(mUser.getName());
        mScreenNameTextView.setText(mUser.getScreenName());
        mTweetCountTextView.setText(Integer.toString(mUser.getTweetCount()));
        mFollowerCountTextView.setText(Integer.toString(mUser.getFollowersCount()));
        mFollowingCountTextView.setText(Integer.toString(mUser.getFollowingsCount()));

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        mUserTimelineFragment = new UserTimelineFragment(mUser.getRemoteId());
        transaction.replace(R.id.timeline_container, mUserTimelineFragment);
        transaction.commit();
    }
}
