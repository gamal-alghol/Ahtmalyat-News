package com.karkor.ahtmalyat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.karkor.ahtmalyat.R;

public class MainActivity extends AppCompatActivity {
Button newAccountBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newAccountBtn=findViewById(R.id.new_account_btn);

        hideStaustBar();
        goSignUp();


    }
private void goSignUp(){
        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SignUp.class);
                intent.putExtra("activity","signup");
                startActivity(intent);
                finish();
            }
        });
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
}