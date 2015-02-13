package com.example.weiting.intervaltimer;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by weiting on 2015/2/11.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private final String TAG = "SongAdapter";


    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs = theSongs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        Song song = songs.get(position);
        //Log.e(TAG, song.getTitle() + " - " + song.getArtist());

        if (convertView == null){
            //if not inflate previously, create a new viewHolder
            //RelativeLayout songLayout = (RelativeLayout) parent.findViewById(R.id.songItem);
            convertView = songInf.inflate(R.layout.song, null, false);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.song_title_text);
            viewHolder.artistView = (TextView) convertView.findViewById(R.id.artist_text);
            convertView.setTag(viewHolder);
            //Log.e(TAG, "Created convertView");
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
            //Log.e(TAG, "convertView already exists");
        }
        viewHolder.artistView.setText(song.getArtist());
        viewHolder.titleView.setText(song.getTitle());
        viewHolder.pos = position;
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    class ViewHolder{
        TextView titleView;
        TextView artistView;
        int pos;

        @Override
        public String toString() {
            return String.valueOf(pos);
        }
    }
}

