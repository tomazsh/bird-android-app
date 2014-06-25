package com.nedeljko.bird.app.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.oauth.OAuthLoginActivity;
import com.nedeljko.bird.app.adapter.TimelineAdapter;
import com.nedeljko.bird.app.BirdApplication;
import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.dialogs.TweetComposeDialog;
import com.nedeljko.bird.app.helpers.InfiniteScrollListener;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.interfaces.TimelineViewModelListener;
import com.nedeljko.bird.app.models.Tweet;
import com.nedeljko.bird.app.viewmodels.HomeViewModel;

public class HomeActivity
        extends OAuthLoginActivity<TwitterClient>
        implements TimelineViewModelListener, TweetComposeDialog.TweetComposeListener {
    private HomeState mState = HomeState.DEFAULT;
    private HomeViewModel mHomeViewModel;
    private TimelineAdapter mTweetsAdapter;
    private ListView mListView;
    private View mListFooterView;
    private ProgressBar mProgressBar;
    private View mEmptyView;
    private TextView mEmptyTitleTextView;
    private TextView mEmptyMessageTextView;
    private TextView mEmptyActionButton;

    private enum HomeState {
        DEFAULT,
        LOADING,
        NEEDS_LOGIN,
        NO_TWEETS,
        NETWORK_ERROR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mListView = (ListView)findViewById(R.id.list_view);
        mListFooterView = (View)LayoutInflater.from(this).inflate(R.layout.footer_progress_indicator, null);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mEmptyView = findViewById(R.id.empty);
        mEmptyTitleTextView = (TextView)findViewById(R.id.empty_title_text_view);
        mEmptyMessageTextView = (TextView)findViewById(R.id.empty_message_text_view);
        mEmptyActionButton = (TextView)findViewById(R.id.empty_action_button);

        mHomeViewModel = new HomeViewModel(BirdApplication.getTwitterClient(), this);
        mTweetsAdapter = new TimelineAdapter(this, mHomeViewModel.getTweets());
        mListView.setAdapter(mTweetsAdapter);
        mListView.addFooterView(mListFooterView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getClient().setRequestIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (getClient().checkAccessToken() == null) {
            setState(HomeState.NEEDS_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == TweetComposeActivity.COMPOSE_REQUEST_CODE) {
            Tweet tweet = (Tweet)data.getExtras().getSerializable("tweet");
            mHomeViewModel.insertTweet(tweet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.compose_action_item) {
            composeTweetInDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void composeTweet() {
        Intent intent = new Intent(HomeActivity.this, TweetComposeActivity.class);
        startActivityForResult(intent, TweetComposeActivity.COMPOSE_REQUEST_CODE);
    }

    public void composeTweetInDialog() {
        FragmentManager manager = getFragmentManager();
        TweetComposeDialog dialog = new TweetComposeDialog(this);
        dialog.show(manager, "tweet_compose_dialog");
    }

    public void setState(HomeState state) {
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

        if (mHomeViewModel.hasLocalTweets() || mHomeViewModel.hasFinishedInitialLoad()) {
            mState = HomeState.DEFAULT;
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void onEmptyButtonClick(View v) {
        switch (mState) {
            case DEFAULT:
            case LOADING:
                break;
            case NEEDS_LOGIN:
                getClient().connect();
                break;
            case NO_TWEETS:
            case NETWORK_ERROR:
                mHomeViewModel.loadTimeline();
                break;
        }
    }

    //region OAuthAccessHandler

    @Override
    public void onLoginSuccess() {
        mHomeViewModel.loadTimeline();
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mHomeViewModel.loadNextTimelinePage();
            }
        });
    }

    @Override
    public void onLoginFailure(Exception exception) {
        exception.printStackTrace();
    }

    //endregion

    //region TimelineViewModelListener

    public void onTimelineLoadStart() {
        setState(HomeState.LOADING);
    }

    public void onTimelineLoadProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void onTimelineLoadSuccess() {

    }

    public void onTimelineLoadFailure() {

    }

    public void onTimelineLoadFinish() {
        if (!mHomeViewModel.hasFinishedInitialLoad()) {
            mHomeViewModel.fetchLocalTweets();
        }
    }

    public void onTimelineItemsChanged() {
        mTweetsAdapter.notifyDataSetChanged();
        if (mHomeViewModel.hasLocalTweets() || mHomeViewModel.hasFinishedInitialLoad()) {
            setState(HomeState.DEFAULT);
        } else {
            setState(HomeState.NETWORK_ERROR);
        }
    }

    //endregion

    //region TweetComposeListener

    public void onTweetComposeSuccess(Tweet tweet) {
        mHomeViewModel.insertTweet(tweet);
        mListView.setSelectionAfterHeaderView();
    }

    public void onTweetComposeFailure() {

    }

    //endregion
}
