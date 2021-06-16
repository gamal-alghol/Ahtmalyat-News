package com.karkor.ahtmalyat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.karkor.ahtmalyat.R;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

     //   hideStaustBar();

        ImageView imgFullScreen=findViewById(R.id.img_details_full);

        Glide.with(imgFullScreen).load(getIntent().getStringExtra("urlImage")).into(imgFullScreen);


    }
    public void hideStaustBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    finish();
    }
}