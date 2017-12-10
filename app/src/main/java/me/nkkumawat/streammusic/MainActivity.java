package me.nkkumawat.streammusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {

    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private ListView musicList;
    private MusicListAdapter musicListAdapter;
    private ArrayList<MusicList> musicListmodel;
    SeekBar seek_bar;
    Handler seekHandler = new Handler();
    Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
        musicList = (ListView) findViewById(R.id.musicList);
        player = new Player();
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Url = musicListmodel.get(position).MusicName;
//                if (mediaPlayer != null) {
//                    mediaPlayer.reset();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
                playPause = false;
                if (!playPause) {
                    if (initialStage) {
                        player.execute("http://tedxnitkurukshetra.com:3003/music?id="+Url);
                        seek_bar.setMax(240000);
                    } else {
                        assert mediaPlayer != null;
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }
                    playPause = true;
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                    playPause = false;
                }
            }
        });
        try {
            getMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };
    public void seekUpdation() {
        seek_bar.setProgress((mediaPlayer.getCurrentPosition()));
        seekHandler.postDelayed(run, 1000);
    }
//========================================PLAYER CLASS=====================================================
@SuppressLint("StaticFieldLeak")
class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playPause = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (Exception e) {
                prepared = false;
            }
            seekUpdation();
            return prepared;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
                progressDialog.dismiss();
            }
            mediaPlayer.start();
            initialStage = true;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Buffering...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
//========================================GET ALL MUSIC LIST================================================
    void getMusic() throws IOException {
        String url = "http://tedxnitkurukshetra.com:3003/getMusic";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter(myResponse);
                    }
                });
            }
        });
    }
    public void  setAdapter(String result){
        musicListmodel = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(result);
            for(int i = 0; i < jArray.length(); i ++){
                MusicList musicList1 = new MusicList(jArray.getString(i));
                musicListmodel.add(musicList1);
            }
            musicListAdapter = new MusicListAdapter(musicListmodel ,this);
            musicList.setAdapter(musicListAdapter);
        }
        catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Check you Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
}