package com.karkor.ahtmalyat.Fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karkor.ahtmalyat.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ContactUs extends Fragment {


TextView tXtWhatUp,txtTelegram;
ImageButton telgram,facebook,whatsapp;
    String shareBody;
    AdView madview;
    View context;
    public ContactUs() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tXtWhatUp=view.findViewById(R.id.waht_up);
txtTelegram=view.findViewById(R.id.telegram);
facebook=view.findViewById(R.id.imageButton_faceBook);
        whatsapp=view.findViewById(R.id.imageButton_whatsApp);
        telgram=view.findViewById(R.id.imageButton_telegram);

        madview=view.findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        madview.loadAd(adRequest);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


         shareBody="https://play.google.com/store/apps/details?id="+view.getContext().getPackageName();
context=view;

        tXtWhatUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsNumber = "963951591613";
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix

                    startActivity(sendIntent);

            }
        });

        txtTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                    telegramIntent.setData(Uri.parse("http://telegram.me/abo_ryyan11"));
                    final String appName = "org.telegram.messenger";
                    telegramIntent.setPackage(appName);
                    startActivity(telegramIntent);

                } catch (Exception e) {
                    // show error message
                }
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
       if(whatsappIntent!=null){
                whatsappIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(whatsappIntent);

            }else{
           Toast.makeText(view.getContext(), "whatsup is not installed", Toast.LENGTH_SHORT).show();

       }
            }
        });

        telgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                waIntent.setPackage("org.telegram.messenger");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, shareBody);//
                   view.getContext().startActivity(Intent.createChooser(waIntent, "Share with"));
                }
                else
                {
                    Toast.makeText(view.getContext(), "Telegram is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                waIntent.setPackage("com.facebook.orca");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, shareBody);//
                    view.getContext().startActivity(Intent.createChooser(waIntent, "Share with"));
                }
                else
                {
                    Toast.makeText(view.getContext(), "Telegram is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_us, container, false);

    }
}