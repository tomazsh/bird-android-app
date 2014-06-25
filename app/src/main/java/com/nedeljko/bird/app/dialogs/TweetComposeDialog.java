package com.nedeljko.bird.app.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.models.Tweet;
import com.nedeljko.bird.app.viewmodels.TweetComposeViewModel;

import java.util.Arrays;
import java.util.List;

public class TweetComposeDialog extends DialogFragment
        implements TweetComposeViewModel.TweetComposeViewModelListener {
    public static final int COMPOSE_REQUEST_CODE = 1337;
    private TweetComposeState mState = TweetComposeState.DEFAULT;
    private TweetComposeViewModel mViewModel;
    private TweetComposeListener mListener;
    private EditText mEditText;
    private ProgressBar mProgressBar;
    private Button mPostButton;

    private enum TweetComposeState {
        DEFAULT,
        LOADING
    }

    public interface TweetComposeListener {
        void onTweetComposeSuccess(Tweet tweet);
        void onTweetComposeFailure();
    }

    public TweetComposeDialog(TweetComposeListener listener) {
        mListener = listener;
        mViewModel = new TweetComposeViewModel(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.fragment_tweet_compose, null);

        mEditText = (EditText)v.findViewById(R.id.edit_text);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progress_bar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v)
                .setPositiveButton(R.string.post_action_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog)getDialog();
        mPostButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.getTweet().setText(mEditText.getText().toString());
                mViewModel.postTweet();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        InputMethodManager manager = (InputMethodManager)getActivity().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        setState(TweetComposeState.LOADING);
    }

    public void onTweetPostSuccess() {
        mListener.onTweetComposeSuccess(mViewModel.getTweet());
        dismiss();
    }

    public void onTweetPostFailure() {
        mListener.onTweetComposeFailure();
    }

    public void onTweetPostFinish() {
        setState(TweetComposeState.DEFAULT);
    }

    //endregion
}
