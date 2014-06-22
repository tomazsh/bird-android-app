package com.nedeljko.bird.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.nedeljko.bird.app.R;
import com.nedeljko.bird.app.helpers.RelativeDate;
import com.nedeljko.bird.app.models.Tweet;

import java.util.ArrayList;

public class TimelineAdapter extends ArrayAdapter<Tweet> {
    public TimelineAdapter(Context context, ArrayList<Tweet> tweets) {
        super(context, R.layout.list_item_tweet, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_tweet, parent, false);
        }

        SmartImageView avatarView = (SmartImageView)convertView.findViewById(R.id.avatar_view);
        avatarView.setImage(null);
        avatarView.setImageUrl(tweet.getUser().getProfileImageUrl());

        TextView nameTextView = (TextView)convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(tweet.getUser().getName());

        TextView screenNameTextView = (TextView)convertView.findViewById(R.id.screen_name_text_view);
        screenNameTextView.setText(tweet.getUser().getScreenName());

        TextView dateTextView = (TextView)convertView.findViewById(R.id.date_text_view);
        dateTextView.setText(RelativeDate.getString(tweet.getCreatedAt()));

        TextView contentTextView = (TextView)convertView.findViewById(R.id.content_text_view);
        contentTextView.setText(tweet.getText());

        return convertView;
    }
}
