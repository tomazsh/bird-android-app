package com.nedeljko.bird.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.adapter.TimelineAdapter;
import com.nedeljko.bird.app.helpers.InfiniteScrollListener;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;
import com.nedeljko.bird.app.models.User;
import com.nedeljko.bird.app.viewmodels.TimelineViewModel;

public class TimelineFragment extends android.support.v4.app.Fragment
        implements TimelineViewModelListener {
    private TimelineState mState = TimelineState.DEFAULT;
    protected TimelineViewModel mViewModel;
    private TimelineAdapter mTweetsAdapter;
    private ListView mListView;
    private View mListFooterView;
    private ProgressBar mProgressBar;
    private View mEmptyView;
    private TextView mEmptyTitleTextView;
    private TextView mEmptyMessageTextView;
    private TextView mEmptyActionButton;

    private enum TimelineState {
        DEFAULT,
        LOADING,
        NEEDS_LOGIN,
        NO_TWEETS,
        NETWORK_ERROR
    }

    public TimelineViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        mListView = (ListView)view.findViewById(R.id.list_view);
        mListFooterView = inflater.inflate(R.layout.footer_progress_indicator, null);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        mEmptyView = view.findViewById(R.id.empty);
        mEmptyTitleTextView = (TextView)view.findViewById(R.id.empty_title_text_view);
        mEmptyMessageTextView = (TextView)view.findViewById(R.id.empty_message_text_view);
        mEmptyActionButton = (TextView)view.findViewById(R.id.empty_action_button);

        mTweetsAdapter = new TimelineAdapter(getActivity(), mViewModel.getTweets());
        mListView.setAdapter(mTweetsAdapter);
        mListView.addFooterView(mListFooterView);
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mViewModel.loadNextTimelinePage();
            }
        });

        mViewModel.loadTimeline();

        return view;
    }

    public void setState(TimelineState state) {
        mState = state;
        switch (state) {
            case DEFAULT:
                mListView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case NEEDS_LOGIN:
                mProgressBar.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyTitleTextView.setText(R.string.login_required_title);
                mEmptyMessageTextView.setText(R.string.login_required_message);
                mEmptyActionButton.setText(R.string.login_action_title);
                mEmptyActionButton.setVisibility(View.VISIBLE);
                break;
            case NO_TWEETS:
                mProgressBar.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyTitleTextView.setText(R.string.no_tweets_title);
                mEmptyMessageTextView.setText(R.string.no_tweets_message);
                mEmptyActionButton.setText(R.string.try_again_action_title);
                break;
            case NETWORK_ERROR:
                mProgressBar.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyTitleTextView.setText(R.string.network_error_title);
                mEmptyMessageTextView.setText(R.string.network_error_message);
                mEmptyActionButton.setText(R.string.try_again_action_title);
                break;
        }

        if (mViewModel.hasLocalTweets() || (mViewModel.hasFinishedInitialLoad() && mViewModel.getTweets().size() > 0)) {
            mState = TimelineState.DEFAULT;
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void onEmptyButtonClick(View v) {
        if (mState == TimelineState.NO_TWEETS) {
            mViewModel.loadTimeline();
        }
    }

    //region TimelineViewModelListener

    public void onTimelineLoadStart() {
        setState(TimelineState.LOADING);
    }

    public void onTimelineLoadProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void onTimelineLoadSuccess() {

    }

    public void onTimelineLoadFailure() {

    }

    public void onTimelineLoadFinish() {
        if (!mViewModel.hasFinishedInitialLoad()) {
            mViewModel.fetchLocalTweets();
        }
    }

    public void onTimelineItemsChanged() {
        mTweetsAdapter.notifyDataSetChanged();
        if (mViewModel.hasLocalTweets() || mViewModel.hasFinishedInitialLoad()) {
            if (mViewModel.getTweets().size() > 0) {
                setState(TimelineState.DEFAULT);
            } else {
                setState(TimelineState.NO_TWEETS);
            }
        } else {
            setState(TimelineState.NETWORK_ERROR);
        }
    }

    //endregion
}
