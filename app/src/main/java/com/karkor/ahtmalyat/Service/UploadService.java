package com.karkor.ahtmalyat.Service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.karkor.ahtmalyat.Fragment.Chat;
import com.karkor.ahtmalyat.Model.Msg;
import com.karkor.ahtmalyat.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;


public class UploadService extends Service {
StorageReference storageReference;
    String mediaPath;
    Uri mediaUri;



    private void SendMsgs(String msg,String uri) {

        Msg message = new Msg(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                msg, Chat.txtReplay.getText().toString(), Chat.senderReplay.getText().toString(),uri, getCurrentDate());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("chat").push().setValue(message);
        Chat.msgReplay.setVisibility(View.GONE);
        Chat.txtReplay.setText("");
        Chat.senderReplay.setText("");
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    Chat.notfay = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d("ttt", intent.toString());
        if (intent != null) {

            mediaPath = intent.getStringExtra("mediaPath");
            mediaUri = Uri.parse(intent.getStringExtra("mediaUri"));
            upload(intent.getStringExtra("fileStorage"));
            stopService(intent);
        }
        return START_STICKY;
    }

    private void upload(String nameFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                storageReference = FirebaseStorage.getInstance().getReference().child(nameFile)
                        .child(mediaPath);

                storageReference.putFile(mediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                SendMsgs(mediaPath, String.valueOf(uri));

                                Chat.btnImage.setVisibility(View.VISIBLE);
                                Chat.txtProgressUpload.setVisibility(View.INVISIBLE);
                                Chat.barUpload.setProgress(0);
                                Toast.makeText(getApplicationContext(), "تم الارسال", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }}).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Chat.barUpload.setProgress(0);
                                Chat.btnImage.setVisibility(View.VISIBLE);
                                Chat.txtProgressUpload.setVisibility(View.INVISIBLE);
                                Log.d("ttt", e.getMessage());
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        Chat. txtProgressUpload.setVisibility(View.VISIBLE);
                        Chat. btnImage.setVisibility(View.INVISIBLE);
                        Chat.txtProgressUpload.setText( String.valueOf((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()))+"%");
                        Chat.barUpload.setProgress((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()));
                    }
                });
            }
        }).start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
