package me.nkkumawat.streammusic;

import java.io.Serializable;

/**
 * Created by sonu on 8/12/17.
 */

public class MusicList implements Serializable {
    public String MusicName;

    public MusicList(String name ) {
        this.MusicName = name;
    }
}