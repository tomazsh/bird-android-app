package com.nedeljko.bird.app.fragments;

import android.os.Bundle;

import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.viewmodels.MentionsTimelineViewModel;

public class MentionsFragment extends TimelineFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new MentionsTimelineViewModel(BirdApplication.getTwitterClient(), this);
    }

}
