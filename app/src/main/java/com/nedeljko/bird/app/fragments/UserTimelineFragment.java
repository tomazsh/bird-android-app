package com.nedeljko.bird.app.fragments;

import android.os.Bundle;

import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.viewmodels.UserTimelineViewModel;

public class UserTimelineFragment extends TimelineFragment {
    long mUserId = 0;

    public UserTimelineFragment(long userId) {
        mUserId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new UserTimelineViewModel(BirdApplication.getTwitterClient(), this, mUserId);
    }
}
