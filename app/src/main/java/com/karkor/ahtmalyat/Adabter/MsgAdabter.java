package com.karkor.ahtmalyat.Adabter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karkor.ahtmalyat.Model.Msg;
import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.karkor.ahtmalyat.View.FullScreenImageActivity;
import com.karkor.ahtmalyat.View.FullScreenVideoActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.keenfin.audioview.AudioView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MsgAdabter extends RecyclerView.Adapter<MsgAdabter.ViewHolder> {

private Context context;
private ArrayList<Msg> msgArrayList;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private SimpleExoPlayer player;
    Msg previousMessage;
    String urlVidoe;
    String nameFileChatImageOnFireBase ="chatImage/";
    String nameFileChatVideoOnFireBase ="chatVideo/";
    String nameFileChatAudioOnFireBase ="chatAudio/";

    public MsgAdabter(ArrayList<Msg> msgArrayList, Context context) {
        this.context = context;
        this.msgArrayList = msgArrayList;



    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(context);

        return  new ViewHolder(LayoutInflater.from(context).inflate(R.layout.massege_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                holder.setIsRecyclable(false);
                Msg message=msgArrayList.get(position);

    /*
                if(position>=0){
                previousMessage=msgArrayList.get(position-1);
                }*/
                holder.bind(message);
            }catch (IndexOutOfBoundsException v){

            }

    }

    @Override
    public int getItemCount() {

            return msgArrayList.size();

    }


     class  ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, sender, time, msgReplay,senderReplay, msgNew;

        SimpleDraweeView imageMsg,imgaeUser;
        ImageView imageVedio;
        CardView cardViewVedio, cardViewReplay;
        PlayerView playerView;
        ProgressBar  lodingVidoe;
        AudioView record;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            try {

                txtMsg = itemView.findViewById(R.id.txt_msg);
                sender = itemView.findViewById(R.id.name);
                time = itemView.findViewById(R.id.txt_time);
                imgaeUser = itemView.findViewById(R.id.news_image_view);
                imageMsg = itemView.findViewById(R.id.image_msg);
                imageVedio=itemView.findViewById(R.id.image_vide);
                msgReplay = itemView.findViewById(R.id.msg_replay);
                senderReplay=itemView.findViewById(R.id.sender_replay);
                msgNew = itemView.findViewById(R.id.msg_new);
                record = itemView.findViewById(R.id.record);
                cardViewReplay = itemView.findViewById(R.id.card_view_raplay_msg);
               // playerView = itemView.findViewById(R.id.video_view);
                cardViewVedio = itemView.findViewById(R.id.card_view_vedio);
                lodingVidoe = itemView.findViewById(R.id.loading);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
                itemView.startAnimation(animation);
            }catch (IndexOutOfBoundsException v){

            }

        }

        void bind(Msg message) {

/*
            if(previousMessage!=null){

            if(previousMessage.getSender().equalsIgnoreCase(message.getSender())){
                sender.setVisibility(View.GONE);
                imgaeUser.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
            }else{
ء
                sender.setVisibility(View.VISIBLE);
                imgaeUser.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
            }


            }*/
            getInfoUer(message);
            imgaeUser.setImageResource(R.drawable.logo);
            txtMsg.setVisibility(View.VISIBLE);
            imageMsg.setVisibility(View.VISIBLE);
            cardViewVedio.setVisibility(View.VISIBLE);
            cardViewReplay.setVisibility(View.VISIBLE);
            record.setVisibility(View.VISIBLE);
            time.setText(getCurrentDate(message.getTime()));

            if (message.getMessage().contains("jpg")|| message.getMessage().contains("png")|| message.getMessage().contains("mp3")|| message.getMessage().contains("mp4")){;
                if (message.getMessage().contains("jpg")|| message.getMessage().contains("png")){

                    txtMsg.setVisibility(View.GONE);
                    cardViewVedio.setVisibility(View.GONE);
                    cardViewReplay.setVisibility(View.GONE);
                    record.setVisibility(View.GONE);
                    lodingVidoe.setVisibility(View.GONE);
                    imageMsg.setImageURI(message.getUri());
                    imageMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent
                                    (context, FullScreenImageActivity.class).
                                    putExtra("urlImage",message.getUri()));
                        }
                    });
/*

                    FirebaseStorage.getInstance().getReference().child(nameFileChatImageOnFireBase + message.getMessage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            lodingVidoe.setVisibility(View.GONE);
                            imageMsg.setImageURI(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
*/
                } else if (message.getMessage().contains("mp4")) {

                            txtMsg.setVisibility(View.GONE);
                            imageMsg.setVisibility(View.GONE);
                            record.setVisibility(View.GONE);
                            cardViewReplay.setVisibility(View.GONE);
                            lodingVidoe.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(message.getUri())
                            .centerCrop()
                            .into(imageVedio);
                    urlVidoe = message.getUri() + "";
                    lodingVidoe.setVisibility(View.GONE);
                    imageVedio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(context, FullScreenVideoActivity.class);
                            intent.putExtra("uri",message.getUri());
                            context.startActivity(intent);
                           /// lodingVidoe.setVisibility(View.VISIBLE);
                          //  downloadVideo(Uri.parse(message.getUri()));
                           // imageVedio.setVisibility(View.GONE);
                         //   playerView.setVisibility(View.VISIBLE);

                        }
                    });


                } else if (message.getUri().contains("mp3")) {


                            txtMsg.setVisibility(View.GONE);
                            imageMsg.setVisibility(View.GONE);
                            record.setVisibility(View.VISIBLE);
                            cardViewReplay.setVisibility(View.GONE);
                            cardViewVedio.setVisibility(View.GONE);
                            lodingVidoe.setVisibility(View.VISIBLE);
                            FirebaseStorage.getInstance().getReference().child(nameFileChatAudioOnFireBase + message.getMessage()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        try {
                                            record.setDataSource(task.getResult());
record.animate();
record.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        lodingVidoe.setVisibility(View.GONE);

    }
});

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (IllegalStateException d) {
                                            d.printStackTrace();
                                        }
                                    }
                                }
                            });


                }
            } else if (!message.getMessageReplay().isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        txtMsg.setVisibility(View.GONE);
                        imageMsg.setVisibility(View.GONE);
                        cardViewVedio.setVisibility(View.GONE);
                        record.setVisibility(View.GONE);
                        cardViewReplay.setVisibility(View.VISIBLE);
                        lodingVidoe.setVisibility(View.GONE);
                        msgNew.setText(message.getMessage());
                        msgReplay.setText(message.getMessageReplay());
                        senderReplay.setText(message.getSenderReplay());
                    }
                }).start();

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        txtMsg.setVisibility(View.VISIBLE);
                        imageMsg.setVisibility(View.GONE);
                        record.setVisibility(View.GONE);
                        cardViewReplay.setVisibility(View.GONE);
                        cardViewVedio.setVisibility(View.GONE);
                        txtMsg.setText(message.getMessage());
                        lodingVidoe.setVisibility(View.GONE);

                        txtMsg.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                copyTextMsg(txtMsg.getText().toString());
                                return true;
                            }
                        });
                    }
                }).start();

            }

            }
/*
        private void downloadVideo(Uri uri) {


                    lodingVidoe.setVisibility(View.GONE);
                    TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
                    player = ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(), new DefaultTrackSelector(adaptiveTrackSelection), new DefaultLoadControl());
                    playerView.setPlayer(player);
                    DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context.getApplicationContext(), Util.getUserAgent(context.getApplicationContext(), "Exo2"), defaultBandwidthMeter);
                    // Uri uri1 = Uri.parse(urlVidoe);
                    MediaSource mediaSource = buildMediaSource(uri);
                    player.setPlayWhenReady(playWhenReady);
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

                }
*/



        private MediaSource buildMediaSource (Uri uri){
                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(context.getApplicationContext(), "exoplayer-codelab");
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            }
        private String getCurrentDate(Date time) {
        DateFormat df = new SimpleDateFormat("h:mm a ,d MMM", Locale.ENGLISH);
        return df.format(time);

        }

            void getInfoUer (Msg msg){
                FirebaseDatabase.getInstance().getReference("users").child(msg.getSender()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);

                        sender.setText(user.getName());
                        FirebaseStorage.getInstance().getReference().child("usersImages/" + user.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                imgaeUser.setImageURI(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("ttt",exception.getMessage());
                        //        Toast.makeText(context.getApplicationContext(), "فشل تحميل بعض الصور الشخصية", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


        }

    private void copyTextMsg(String msg) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(msg);
            Toast.makeText(context.getApplicationContext(),R.string.copy,Toast.LENGTH_SHORT).show();

        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", msg);
          Toast.makeText(context.getApplicationContext(),R.string.copy,Toast.LENGTH_SHORT).show();
            clipboard.setPrimaryClip(clip);
        }

    }


}
      /*
                            FirebaseStorage.getInstance().getReference().child(nameFileChatVideoOnFireBase + message.getMessage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context)
                                            .load(uri)
                                            .centerCrop()
                                            .into(imageVedio);

                                    urlVidoe = uri + "";
                                    lodingVidoe.setVisibility(View.GONE);
                                    imageVedio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lodingVidoe.setVisibility(View.VISIBLE);
                                            downloadVideo(uri);
                                            imageVedio.setVisibility(View.GONE);
                                            playerView.setVisibility(View.VISIBLE);

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(context.getApplicationContext(), "لا يمكن تحميل الفيديو", Toast.LENGTH_SHORT).show();
                                }
                            });*/

