package com.karkor.ahtmalyat.Notfication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.karkor.ahtmalyat.View.Home;
import com.karkor.ahtmalyat.View.Splash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFairBaseMessaging extends FirebaseMessagingService {
    int numNotfication=0;
   int numNotficationUrgent=0;
    Uri defuletSound;
    NotificationManager mNotificationManager;
    SharedPreferences reder;
    SharedPreferences.Editor writer;
    NotificationChannel notificationChannel,notificationChannel1;
    final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("ttt","onMessageReceived");
        reder = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        writer=getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        Log.d("ttt",remoteMessage.getData().toString());
reder.getInt("importance",4);
        super.onMessageReceived(remoteMessage);
if(remoteMessage.getData().containsKey("titleNews")){
sendNotficationNews(remoteMessage);
    Log.d("ttt","sendNotficationNews(remoteMessage);\n");
}else if(remoteMessage.getData().containsKey("text")){
    if(Home.inChat==false){
        getInfoUer(remoteMessage);
    }
}


    }

    private void sendNotficationNews(RemoteMessage remoteMessage) {
        Log.d("ttt","sendNotficationNews");
Log.d("ttt",remoteMessage.getData().get("titleNews"));
        defuletSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction("dummy_unique_action_identifyer" + "123123");
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(Splash.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                "default")
                .setSmallIcon(R.drawable.ic_urgent)
                .setContentTitle("عاجل")
                .setContentIntent(pendingIntent)
               .setAutoCancel(true)
                .setContentText(remoteMessage.getData().get("titleNews"));
         mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            crearChannelSound();
            crearChannelMute();
            if (reder.getInt("importance", 4) == 4) {
                mBuilder.setChannelId("10001");

            } else if (reder.getInt("importance", 4) == 2) {
                mBuilder.setChannelId("10002");
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(numNotficationUrgent, mBuilder.build()) ;
            numNotficationUrgent++;
            writer.putInt("numNotficationUrgent" ,reder.getInt("numNotficationUrgent",0)+1);
            writer.apply();

        }
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        Log.d("ttt",s+"   "+e.getMessage());
        super.onSendError(s, e);
    }

    void getInfoUer (RemoteMessage remoteMessage){
        FirebaseDatabase.getInstance().getReference("users").child(remoteMessage.getData().get("sender")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                 defuletSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                                "default" )
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle( user.getName())
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setContentText(remoteMessage.getData().get("text"));
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
                        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
crearChannelSound();
                            crearChannelMute();


                            if(reder.getInt("importance",4)==4){
                                mBuilder.setChannelId( "10001" ) ;

                            }else if(reder.getInt("importance",4)==2){
                                mBuilder.setChannelId( "10002" ) ;
                            }

                            assert mNotificationManager != null;
                            mNotificationManager.createNotificationChannel(notificationChannel) ;
                            mNotificationManager.createNotificationChannel(notificationChannel1) ;

                        }
                        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(remoteMessage.getData().get("sender"))){
                            assert mNotificationManager != null;
                            mNotificationManager.notify(numNotfication, mBuilder.build()) ;
                            numNotfication++;
                            writer.putInt("numNotfication" ,reder.getInt("numNotfication",0)+1);
writer.apply();
                        }

                    }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    private void crearChannelMute() {
        try {
        Log.d("ttt","crearChannelMute");
        notificationChannel1 = new
                NotificationChannel( "10002" , "mute" , NotificationManager.IMPORTANCE_LOW) ;
        notificationChannel1.enableLights( true ) ;
        notificationChannel1.setLightColor(Color.GREEN ) ;
        notificationChannel1.enableVibration( true ) ;
        notificationChannel1.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500}) ;
        mNotificationManager.createNotificationChannel(notificationChannel);
    }catch (NullPointerException d){

    }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearChannelSound() {
        try {

            notificationChannel = new
                    NotificationChannel( "10001" , "sound" , NotificationManager.IMPORTANCE_HIGH) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color.GREEN ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500}) ;

            mNotificationManager.createNotificationChannel(notificationChannel);
        }catch (NullPointerException d){

        }
        Log.d("ttt","crearChannelSound");

    }
}
