package com.karkor.ahtmalyat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karkor.ahtmalyat.Fragment.Chat;
import com.karkor.ahtmalyat.Fragment.ContactUs;
import com.karkor.ahtmalyat.Fragment.NewsWebFragment;
import com.karkor.ahtmalyat.Fragment.NowFragment;
import com.karkor.ahtmalyat.Fragment.ReleasesFragment;
import com.karkor.ahtmalyat.Fragment.WhoUs;
import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class Home extends AppCompatActivity {
    public static BottomNavigationView navigationView;
    TextView txtTitle;

    private Toolbar toolbar;
    public static long selectedItem = 0;
    WhoUs whoUsFragment;
    ContactUs contactUsFragment;
    NewsWebFragment newsWebFragment;
    SharedPreferences reader;
    BadgeDrawable badgeDrawableChat;
    BadgeDrawable badgeDrawableUrgent;
public static ContentResolver cc;
NowFragment nowFragment;

ReleasesFragment releasesFragment;
    InterstitialAd mInterstitialAd;
    final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor writer;

    public static Boolean inChat=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5278795364849520~8149666847");
        ViewCompat.setLayoutDirection(findViewById(R.id.constraint_home),ViewCompat.LAYOUT_DIRECTION_LTR);
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-5278795364849520/5122487754");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        navigationView = findViewById(R.id.bottom_navigation);
        txtTitle=findViewById(R.id.text_title);
        toolbar = findViewById(R.id.toolbar_condition);
        setSupportActionBar(toolbar);
        whoUsFragment=new WhoUs();
        contactUsFragment=new ContactUs();

        newsWebFragment =new NewsWebFragment();
        releasesFragment=new ReleasesFragment();
       reader = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        writer=getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        badgeDrawableChat =navigationView.getOrCreateBadge(R.id.chat_menu);
        badgeDrawableUrgent =navigationView.getOrCreateBadge(R.id.urgent);
        nowFragment=new NowFragment();
        cc=getContentResolver();
        checkEnableUser();
createNavigationView();
creatDrawer();
        toolbar.setNavigationIcon(R.drawable.drawer);

        if(reader.getInt("numNotfication",0)>0){
            badgeDrawableChat.setNumber(reader.getInt("numNotfication",0));
        }else{
            badgeDrawableChat.clearNumber();
        }

        if(reader.getInt("numNotficationUrgent",0)>0){
            badgeDrawableUrgent.setNumber(reader.getInt("numNotficationUrgent",0));
        }else{
            badgeDrawableUrgent.clearNumber();
        }
    }


    private void checkEnableUser() {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Splash.user = dataSnapshot.getValue(User.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

    }

    private void creatDrawer() {
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder();
        accountHeaderBuilder.withActivity(this).withHeaderBackground(R.drawable.dddd);
        AccountHeader accountHeader = accountHeaderBuilder.build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("الحساب").withIcon(R.drawable.ic_baseline_account_circle_24);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("الاشعارات").withIcon(R.drawable.ic_baseline_notifications_24);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("من نحن").withIcon(R.drawable.ic_baseline_perm_device_information_24);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("التواصل و المشاركة").withIcon(R.drawable.ic_outline_share_24);


        final Drawer result = new DrawerBuilder().
                withSystemUIHidden(false).withActionBarDrawerToggle(true)
                .withAccountHeader(accountHeader).withCloseOnClick(true)
                .withActivity(this)
                .withToolbar(toolbar)
                .withCloseOnClick(true)
                .addDrawerItems(item1,item2,item3,item4).withSelectedItem(selectedItem).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {

                            case 1:
                                inChat=false;
                                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                                intent.putExtra("activity","account");
                               startActivity(intent);
                                selectedItem =1;
                                break;
                            case 2:

                           showDialogNotifcation();
                                selectedItem =2;
                                break;
                            case 3:
                             if (!whoUsFragment.isAdded()){

                                  getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home,whoUsFragment).commit();
                                        getFragmentManager().popBackStack();
                                txtTitle.setText(R.string.who_us);
                                selectedItem =3;
                                inChat=false;}
                                break;

                            case 4:
                                if (!contactUsFragment.isAdded())
                                    inChat=false;
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home,contactUsFragment).commit();
                                getFragmentManager().popBackStack();
                                txtTitle.setText(R.string.contanct_us);
                                selectedItem =4;
                                break;

                        }
                        return false;
                    }
                }).build();
    }

    private void showDialogNotifcation() {
        new AlertDialog.Builder(Home.this)
                .setTitle("الاشعارات")
                .setMessage("هل تريد كتم صوت الاشعارات؟")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putInt("importance",2);
                        editor.apply();

                    }
                }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt("importance",4);
                editor.apply();
            }
        })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(R.drawable.ic_baseline_notifications_24)
                .show();
    }


    private void createNavigationView() {

        navigationView.performClick();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.news:
                        inChat=false;


                        if (!newsWebFragment.isAdded()){
                            if(mInterstitialAd.isLoaded()){
                                mInterstitialAd.show();

                            }else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, newsWebFragment).commit();
                                txtTitle.setText(R.string.news);
                                inChat=false;

                            }
                            mInterstitialAd.setAdListener(new AdListener(){
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, newsWebFragment).commit();
                                    txtTitle.setText(R.string.news);
                                    inChat=false;

                                }
                            });


                    }
                        return true;

                    case R.id.videos:
                        inChat=false;
                        if (!releasesFragment.isAdded()) {
                            if(mInterstitialAd.isLoaded()){
                                mInterstitialAd.show();

                            }else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, releasesFragment).commit();
                                inChat=false;

                                txtTitle.setText(R.string.videos);
                            }
                            mInterstitialAd.setAdListener(new AdListener(){
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, releasesFragment).commit();
                                    inChat=false;
                                    txtTitle.setText(R.string.videos);

                                }
                            });
                        }
                        return true;
                    case R.id.chat_menu:

if(Splash.user!=null) {

    if (Splash.user.getEnable().equalsIgnoreCase("true")) {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();

        }else{
            inChat=true;
            writer.putInt("numNotfication",0);
            writer.apply();
            badgeDrawableChat.clearNumber();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new Chat()).commit();
            txtTitle.setText(R.string.chat);

        }

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                inChat=true;
                writer.putInt("numNotfication",0);
                writer.apply();
                badgeDrawableChat.clearNumber();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, new Chat()).commit();
                txtTitle.setText(R.string.chat);

            }
        });

        return true;
    } else {
        Toast.makeText(getApplicationContext(), "لقد تم حظرك من قبل الادمن", Toast.LENGTH_LONG).show();

    }
}else{
    Toast.makeText(getApplicationContext(), "انتظار...", Toast.LENGTH_LONG).show();
    txtTitle.setText(R.string.chat);

}


                        return true;
                    case R.id.urgent:
                        inChat=false;
                        if (!nowFragment.isAdded()){
                            mInterstitialAd.setAdListener(new AdListener(){
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    writer.putInt("numNotficationUrgent",0);
                                    writer.apply();
                                    badgeDrawableUrgent.clearNumber();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, nowFragment).commit();
                                    txtTitle.setText(R.string.urgent);

                                }
                            });

                            if(mInterstitialAd.isLoaded()){
                                mInterstitialAd.show();

                            }else{
                                writer.putInt("numNotficationUrgent",0);
                                writer.apply();
                                badgeDrawableUrgent.clearNumber();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, nowFragment).commit();
                                txtTitle.setText(R.string.urgent);
                            }
                        }



                        return true;

                }
                return false;
            }
        });
           navigationView.setSelectedItemId(R.id.urgent);



    }
    @Override
    protected void onResume() {
        super.onResume();
      inChat=false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        inChat=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inChat=false;
    }

}




/*
        FirebaseDatabase.getInstance().getReference("users").child("enable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
User user=dataSnapshot.getValue(User.class);
                FirebaseAuth.getInstance().re;
                FirebaseDatabase.getInstance().getReference("chat").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Msg msg=snapshot.getValue(Msg.class);
                            if(msg.getSender().equalsIgnoreCase(user.getId())){
                                FirebaseDatabase.getInstance().getReference("chat").child(snapshot.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    */

