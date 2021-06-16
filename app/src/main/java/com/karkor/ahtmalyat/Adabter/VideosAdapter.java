package com.karkor.ahtmalyat.Adabter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.karkor.ahtmalyat.R;
import com.karkor.ahtmalyat.View.FullScreenVideoActivity;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class VideosAdapter extends PagerAdapter {

    Context context;
    List<String> listUrlVideos;
    ImageView imgFullScreen;
    ImageView img_video_show;
    PlayerView playerView;
    InterstitialAd mInterstitialAd;

    public VideosAdapter(Context context, List<String>listUrlImages){
        this.context=context;
        this.listUrlVideos =listUrlImages;
    }
    @Override
    public int getCount() {
        return listUrlVideos.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, null);
        ImageView imgVideoPlacHolder = view.findViewById(R.id.img_video_placholder);
        imgFullScreen = view.findViewById(R.id.full_screen_img);
        img_video_show = view.findViewById(R.id.img_toshow);
        Glide.with(imgVideoPlacHolder).load(listUrlVideos.get(position)).into(imgVideoPlacHolder);
         playerView =view.findViewById(R.id.video_news);
        container.addView(view);
     //   mInterstitialAd = new InterstitialAd(context);
      //  mInterstitialAd.setAdUnitId("ca-app-pub-5278795364849520/5038727271");
      //  mInterstitialAd.loadAd(new AdRequest.Builder().build());
        img_video_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgVideoPlacHolder.setVisibility(View.GONE);
                img_video_show.setVisibility(View.GONE);
                imgFullScreen.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
                int currentWindow = 0;
                long playbackPosition = 0;

                TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(), new DefaultTrackSelector(adaptiveTrackSelection), new DefaultLoadControl());
                playerView.setPlayer(player);
                DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory
                        (context.getApplicationContext(), Util.getUserAgent(context.getApplicationContext(),
                                "Exo2"), defaultBandwidthMeter);
                Uri uri1 = Uri.parse(listUrlVideos.get(position));
                MediaSource mediaSource = buildMediaSource(uri1);
                player.setPlayWhenReady(true);
                player.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        switch (playbackState) {
                            case ExoPlayer.STATE_READY:
                                //    lodingVidoe.setVisibility(View.GONE);
                                break;
                            case ExoPlayer.STATE_BUFFERING:
                                //  lodingVidoe.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
                player.seekTo(currentWindow, playbackPosition);
                player.prepare(mediaSource, true, false);


                imgFullScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.setPlayWhenReady(false);
                        Intent intent=new Intent(context, FullScreenVideoActivity.class);
                        intent.putExtra("uri",listUrlVideos.get(position));
                        context.startActivity(intent);

                     /*   if(mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();

                        }else{
                            player.setPlayWhenReady(false);
                            Intent intent=new Intent(context, FullScreenVideoActivity.class);
                            intent.putExtra("uri",listUrlVideos.get(position));
                            context.startActivity(intent);
                        }
                        mInterstitialAd.setAdListener(new AdListener(){
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();

                                player.setPlayWhenReady(false);
                                Intent intent=new Intent(context, FullScreenVideoActivity.class);
                                intent.putExtra("uri",listUrlVideos.get(position));
                                context.startActivity(intent);
                            }
                        });*/

                    }
                });






            }
        });



        return view;
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }


    void createFullScreenImg(){

    }
}
