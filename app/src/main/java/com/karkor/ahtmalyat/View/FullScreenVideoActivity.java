package com.karkor.ahtmalyat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.karkor.ahtmalyat.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class FullScreenVideoActivity extends AppCompatActivity {

    String urlVideo;
    SimpleExoPlayer player;
    ProgressBar loadVideoBar;
    PlayerView playerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        hideStaustBar();
        loadVideoBar=findViewById(R.id.loadVideo);
         playerView =findViewById(R.id.video_fullScreen);
        urlVideo =getIntent().getStringExtra("uri");
        Uri uri1 = Uri.parse(urlVideo);

        playerView.setVisibility(View.VISIBLE);
        int currentWindow = 0;
        long playbackPosition = 0;

        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
         player = ExoPlayerFactory.newSimpleInstance( getApplicationContext(), new DefaultTrackSelector(adaptiveTrackSelection), new DefaultLoadControl());
        playerView.setPlayer(player);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory
                (getApplicationContext(), Util.getUserAgent(getApplicationContext(),
                        "Exo2"), defaultBandwidthMeter);

        MediaSource mediaSource = buildMediaSource(uri1);
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                          loadVideoBar.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        loadVideoBar.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, true, false);



    }
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(getApplicationContext(), "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }
    private void hideStaustBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        if (getActionBar() != null) {
            ;

            getActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.setPlayWhenReady(false);
      finish();
    }
}