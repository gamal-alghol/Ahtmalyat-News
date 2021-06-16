package com.karkor.ahtmalyat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
public static User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        hideStaustBar();

        /* Create an Intent that will start the Menu-Activity. */

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user==null){
                        finish();

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                //   Log.d("ttt", FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).toString());
                    Intent homeIntent = new Intent(Splash.this, Home.class);
                    Splash.this.startActivity(homeIntent);
                } else {
                    Intent mainIntent = new Intent(Splash.this, MainActivity.class);

                    Splash.this.startActivity(mainIntent);
              }


                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void hideStaustBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        if (getActionBar() != null) {
            getActionBar().hide();
        //2543
        }
    }
}