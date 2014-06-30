package com.nedeljko.bird.app.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.dialogs.TweetComposeDialog;
import com.nedeljko.bird.app.fragments.HomeFragment;
import com.nedeljko.bird.app.fragments.MentionsFragment;
import com.nedeljko.bird.app.fragments.TimelineFragment;
import com.nedeljko.bird.app.helpers.TwitterClient;
import com.nedeljko.bird.app.helpers.FragmentTabListener;
import com.nedeljko.bird.app.models.Tweet;
import com.nedeljko.bird.app.models.User;

public class HomeActivity
        extends OAuthLoginActivity<TwitterClient>
        implements TweetComposeDialog.TweetComposeListener {
    private HomeState mState = HomeState.NEEDS_LOGIN;
    private View mTabContainer;
    private View mEmptyView;
    private boolean mHasTabs = false;

    private enum HomeState {
        DEFAULT,
        NEEDS_LOGIN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTabContainer = findViewById(R.id.tab_container);
        mEmptyView = findViewById(R.id.empty);
    }

    private void setupTabs() {
        if (mHasTabs) {
            return;
        }
        mHasTabs = true;

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tab1 = actionBar.newTab().setText(R.string.home_tab_title)
                .setTag("HomeFragment").setTabListener(
                        new FragmentTabListener<HomeFragment>(R.id.tab_container, this,
                                "HomeFragment", HomeFragment.class));
        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        ActionBar.Tab tab2 = actionBar.newTab().setText(R.string.mentions_tab_title)
                .setTag("MentionsFragment").setTabListener(
                        new FragmentTabListener<MentionsFragment>(R.id.tab_container, this,
                                "MentionsFragment", MentionsFragment.class)
                );
        actionBar.addTab(tab2);
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
            //mHomeFragment.getViewModel().insertTweet(tweet);
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
        } else if (id == R.id.profile_action_item) {
            viewProfile(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setState(HomeState state) {
        mState = state;
        switch (state) {
            case DEFAULT:
                mTabContainer.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                break;
            case NEEDS_LOGIN:
                mTabContainer.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void composeTweet() {
        Intent intent = new Intent(HomeActivity.this, TweetComposeActivity.class);
        startActivityForResult(intent, TweetComposeActivity.COMPOSE_REQUEST_CODE);
    }

    public void viewProfile(User user) {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void composeTweetInDialog() {
        FragmentManager manager = getFragmentManager();
        TweetComposeDialog dialog = new TweetComposeDialog(this);
        dialog.show(manager, "tweet_compose_dialog");
    }

    public void onEmptyButtonClick(View v) {
        getClient().connect();
    }

    //region OAuthAccessHandler

    @Override
    public void onLoginSuccess() {
        setupTabs();
        setState(HomeState.DEFAULT);
    }

    @Override
    public void onLoginFailure(Exception exception) {
        exception.printStackTrace();
    }

    //endregion

    //region TweetComposeListener

    public void onTweetComposeSuccess(Tweet tweet) {
        //mHomeFragment.getViewModel().insertTweet(tweet);
        //mListView.setSelectionAfterHeaderView();
    }

    public void onTweetComposeFailure() {

    }

    //endregion

    //region TimelineFragmentListener

    public void onAvatarViewClick(View view) {
        User user = (User)view.getTag();
        viewProfile(user);
    }

    //endregion
}
