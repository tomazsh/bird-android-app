package com.nedeljko.bird.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.helpers.TwitterClient;


public class MainActivity extends OAuthLoginActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.compose_action_item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFailure(Exception exception) {
        System.out.println("Exception signing in: " + exception);
    }

    public void onSignInClick(View view) {
        getClient().connect();
    }

}
