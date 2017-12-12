package com.suifeng.app.demo.playerengine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.suifeng.lib.playerengine.api.LoadMusicListener;
import com.suifeng.lib.playerengine.api.NotificationAdapter;
import com.suifeng.lib.playerengine.api.PlaybackMode;
import com.suifeng.lib.playerengine.api.PlayerListener;
import com.suifeng.lib.playerengine.core.PlayListManager;
import com.suifeng.lib.playerengine.core.Player;
import com.suifeng.lib.playerengine.entity.Music;
import com.suifeng.lib.playerengine.util.AudioUtils;
import com.suifeng.lib.playerengine.util.MusicLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PlayerListener, NotificationAdapter {

    private static final String TAG = "MainActivity";

    private TextView titleTv, artistTv, currentTimeTv, durationTv;
    private SeekBar timeSb;
    private ImageView playModeIv, preIv, playOrPauseIv, nextIv;
    private Player player;
    private ProgressDialog progressDialog;
    private List<Music> musicList;
    private PlayListManager playListManager;
    private Music currentMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        initViewListener();
    }

    private void initViews() {
        titleTv = (TextView) findViewById(R.id.tv_title);
        artistTv = (TextView) findViewById(R.id.tv_artist);
        currentTimeTv = (TextView) findViewById(R.id.tv_current_time);
        durationTv = (TextView) findViewById(R.id.tv_duration);
        timeSb = (SeekBar) findViewById(R.id.time_sb);
        playModeIv = (ImageView) findViewById(R.id.iv_play_mode);
        preIv = (ImageView) findViewById(R.id.iv_pre);
        playOrPauseIv = (ImageView) findViewById(R.id.iv_play_or_pause);
        nextIv = (ImageView) findViewById(R.id.iv_next);
    }

    private void initData() {
        loadLocalMusic();

        player = Player.getInstance(this);
        player.setPlayNextWhenError(true);
        player.setPlaybackMode(PlaybackMode.ALL);
        player.setFadeVolumeWhenStartOrPause(false);
        player.setShowNotification(true);
        player.setNotificationAdapter(this);

        player.setListener(this);
    }

    private void loadLocalMusic() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("loading");
        musicList = new ArrayList<>();
        musicList.clear();
        MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
        musicLoader.loadMusic(new LoadMusicListener() {
            @Override
            public void onLoadMusic(List<Music> musics) {
                musicList.addAll(musics);
                progressDialog.dismiss();
                Log.i(TAG, "load complete ---" + musicList.size());
                player.setPlayMusicList(musicList);
                playListManager = player.getPlayListManager();
                currentMusic = musicList.get(playListManager.getSelectedIndex());
                setMusicInfo();
            }

            @Override
            public void onLoadingMusic(final Music music) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage(music.getTitle() + "-" + music.getArtist());
                        progressDialog.show();
                    }
                });

            }
        });
    }

    private void initViewListener() {
        playModeIv.setOnClickListener(this);
        preIv.setOnClickListener(this);
        playOrPauseIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        timeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(timeSb.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_mode:
                player.cyclePlayMode();
                switch (player.getPlaybackMode()) {
                    case ALL:
                        playModeIv.setImageResource(R.mipmap.ic_player_order);
                        break;
                    case SHUFFLE:
                        playModeIv.setImageResource(R.mipmap.ic_player_shuffle);
                        break;
                    case SINGLE_REPEAT:
                        playModeIv.setImageResource(R.mipmap.ic_player_repeat_one);
                        break;
                }
                break;
            case R.id.iv_pre:
                player.prev();
                break;
            case R.id.iv_play_or_pause:
                player.toggle();
                break;
            case R.id.iv_next:
                player.next();
                break;
        }
    }

    @Override
    public void onTrackBuffering(String uri, int percent) {
        Log.i(TAG, "onTrackBuffering---uri：" + uri + "---percent：" + percent);
    }

    @Override
    public void onTrackStart(String uri) {
        Log.i(TAG, "onTrackStart---uri：" + uri);
        playOrPauseIv.setImageResource(R.mipmap.ic_player_pause);
    }

    @Override
    public void onTrackChange(String uri) {
        Log.i(TAG, "onTrackChange---uri：" + uri);
        currentMusic = musicList.get(playListManager.getSelectedIndex());
        Log.i(TAG, "currentMusic--->" + currentMusic.getTitle() + "-" + currentMusic.getArtist());
        setMusicInfo();
    }

    private void setMusicInfo() {
        titleTv.setText(currentMusic.getTitle());
        artistTv.setText(currentMusic.getArtist());
        durationTv.setText(AudioUtils.formatDuration(currentMusic.getDuration()));
    }

    @Override
    public void onTrackProgress(String uri, int percent, int currentDuration, int duration) {
        Log.i(TAG, "onTrackProgress---percent：" + percent + "---currentDuration：" + currentDuration + "---duration：" + duration);
        currentTimeTv.setText(AudioUtils.formatDuration(currentDuration));
        timeSb.setProgress(percent);
    }

    @Override
    public void onTrackPause(String uri) {
        Log.i(TAG, "onTrackPause---uri：" + uri);
        playOrPauseIv.setImageResource(R.mipmap.ic_player_play);
    }

    @Override
    public void onTrackStop(String uri) {
        Log.i(TAG, "onTrackStop---uri：" + uri);
        playOrPauseIv.setImageResource(R.mipmap.ic_player_play);
    }

    @Override
    public void onTrackStreamError(String uri, int what, int extra) {
        Log.i(TAG, "onTrackStreamError---uri：" + uri + "---what：" + what + "---extra：" + extra);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public String getMusicName() {
        return currentMusic.getTitle();
    }

    @Override
    public String getArtistName() {
        return currentMusic.getArtist();
    }

    @Override
    public void loadMusicImage(MusicImageLoadListener musicImageLoadListener) {
        String imgUri = AudioUtils.getLocalAudioAlbumPictureUri(this, currentMusic.getAlbumId());
        Bitmap bitmap = AudioUtils.getLocalAudioAlbumPicture(imgUri);
        musicImageLoadListener.onMusicImageLoaded(imgUri, bitmap);
    }

    @Override
    public void onNotificationClick() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
