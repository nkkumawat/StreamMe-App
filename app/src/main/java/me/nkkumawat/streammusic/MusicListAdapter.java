package me.nkkumawat.streammusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sonu on 8/12/17.
 */

public class MusicListAdapter extends ArrayAdapter<MusicList> {
    ArrayList<MusicList> musicList;
    public MusicListAdapter(ArrayList<MusicList> musicList, Context mContext ) {
        super(mContext, R.layout.music_list, musicList);
        this.musicList = musicList;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.music_list, parent, false);
        }
        MusicList musicList1 = getItem(position);
        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.musicName.setText(musicList1.MusicName);
        return convertView;
    }
    public class ViewHolder {
        TextView musicName;
        ViewHolder(View view) {
            musicName = (TextView) view.findViewById(R.id.musicName);
        }
    }
}