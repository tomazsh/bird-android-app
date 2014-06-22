package com.nedeljko.bird.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.viewmodels.TweetComposeViewModel;

public class TweetComposeActivity extends Activity
        implements TweetComposeViewModel.TweetComposeViewModelListener {
    public static final int COMPOSE_REQUEST_CODE = 1337;
    private TweetComposeState mState = TweetComposeState.DEFAULT;
    private TweetComposeViewModel mViewModel;
    private EditText mEditText;
    private ProgressBar mProgressBar;

    private enum TweetComposeState {
        DEFAULT,
        LOADING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_compose);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mEditText = (EditText)findViewById(R.id.edit_text);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        mViewModel = new TweetComposeViewModel(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweet_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.send_action_item) {
            mViewModel.getTweet().setText(mEditText.getText().toString());
            mViewModel.postTweet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setState(TweetComposeState state) {
        mState = state;
        switch (state) {
            case DEFAULT:
                mProgressBar.setVisibility(View.GONE);
                mEditText.setVisibility(View.VISIBLE);
                break;
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mEditText.setVisibility(View.GONE);
                break;
        }
    }

    //region TweetComposeViewModelListener

    public void onTweetPostStart() {
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        setState(TweetComposeState.LOADING);
    }

    public void onTweetPostSuccess() {
        Intent intent = new Intent();
        intent.putExtra("tweet", mViewModel.getTweet());
        setResult(RESULT_OK, intent);
        finish();

        finish();
    }

    public void onTweetPostFailure() {
        Toast.makeText(this, R.string.tweet_post_error_message, Toast.LENGTH_LONG).show();
    }

    public void onTweetPostFinish() {
        setState(TweetComposeState.DEFAULT);
    }

    //endregion

}
