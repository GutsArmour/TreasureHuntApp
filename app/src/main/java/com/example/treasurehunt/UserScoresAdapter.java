package com.example.treasurehunt;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.treasurehunt.UserScores;

import java.util.List;

public class UserScoresAdapter extends ArrayAdapter<UserScores> {
    private Context mContext;
    private int mResource;
    private List<UserScores> mUserScoresList;

    public UserScoresAdapter(Context context, int resource, List<UserScores> userScoresList) {
        super(context, resource, userScoresList);
        this.mContext = context;
        this.mResource = resource;
        this.mUserScoresList = userScoresList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.pfpImageView = convertView.findViewById(R.id.userImage);
            viewHolder.usernameTextView = convertView.findViewById(R.id.userNameL);
            viewHolder.pointsTextView = convertView.findViewById(R.id.userScores);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserScores userScores = mUserScoresList.get(position);

        Glide.with(mContext).load(userScores.getPfp()).into(viewHolder.pfpImageView);

        viewHolder.pointsTextView.setText(String.valueOf(userScores.getPoints()));
        viewHolder.usernameTextView.setText(String.valueOf(userScores.getUsername()));

        return convertView;
    }

    static class ViewHolder {
        ImageView pfpImageView;
        TextView pointsTextView, usernameTextView;
    }
}
