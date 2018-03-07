package com.example.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hunter on 2/6/2018.
 */

public class FeedAdapter extends ArrayAdapter<FeedEntry> {

    FeedAdapter(@NonNull Context context, ArrayList<FeedEntry> entries) {
        super(context, R.layout.list_entry, entries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        FeedEntry feedEntry = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_entry, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tvName);
            viewHolder.artist = convertView.findViewById(R.id.tvArtist);
            viewHolder.releaseDate = convertView.findViewById(R.id.tvReleaseDate);
            viewHolder.genre = convertView.findViewById(R.id.tvGenre);
            viewHolder.summary = convertView.findViewById(R.id.tvSummary);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (feedEntry != null) {
            viewHolder.name.setText(feedEntry.getName());
            viewHolder.artist.setText(feedEntry.getArtist());
            viewHolder.releaseDate.setText(feedEntry.getReleaseDate());
            viewHolder.genre.setText(feedEntry.getGenre());
            if (feedEntry.getSummary() == null || feedEntry.getSummary().length() == 0) {
                viewHolder.summary.setVisibility(View.GONE);
            } else {
                viewHolder.summary.setText(feedEntry.getSummary());
            }
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView artist;
        public TextView releaseDate;
        public TextView genre;
        public TextView summary;
    }
}
