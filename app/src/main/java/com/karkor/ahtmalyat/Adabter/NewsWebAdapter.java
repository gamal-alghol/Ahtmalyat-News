package com.karkor.ahtmalyat.Adabter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karkor.ahtmalyat.Model.Mnews;
import com.karkor.ahtmalyat.R;
import com.karkor.ahtmalyat.View.web;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class NewsWebAdapter extends RecyclerView.Adapter<NewsWebAdapter.ViewHolder>  {
    private Context context;
    private ArrayList<Mnews> mnewsArrayList;
    FragmentManager fragmentManager;
     InterstitialAd mInterstitialAd;
    String nameFileNewsImageOnFireBase ="NowImages/";


    public NewsWebAdapter(ArrayList<Mnews> mnewsArrayList, Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.mnewsArrayList = mnewsArrayList;
this.fragmentManager=fragmentManager;

    }
    @Override
    public NewsWebAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(context);
    /*    mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-5278795364849520/6627141114");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/
            return  new ViewHolder(LayoutInflater.from(context).inflate(R.layout.news_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull NewsWebAdapter.ViewHolder holder, int position) {
        Mnews aMnews = mnewsArrayList.get(position);
      holder.bind(aMnews);
    }

    @Override
    public int getItemCount() {
        return mnewsArrayList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {
TextView title;
CardView cardView;
        SimpleDraweeView imageNews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.news_title);
            imageNews=itemView.findViewById(R.id.news_image_view);
            cardView=itemView.findViewById(R.id.card_view_news);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
            itemView.startAnimation(animation);


        }

        public void bind(Mnews aMnews) {
            title.setText(aMnews.getTitle());
            imageNews.setImageURI(aMnews.getImage());
      cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goTowebSit(aMnews);
                    /*
                    if(mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();

                    }else{
                        goTowebSit(aMnews);
                    }
*/

                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,title.getText()
                            +"\n"+aMnews.getLink()+"\n<*>ارسلت من تطبيق احتميلات نيوز<*>");
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(intent, "ارسال عبر").addFlags(FLAG_ACTIVITY_NEW_TASK));


                    return false;
                }
            });
        }

    }
    private void goTowebSit(Mnews aMnews){
        context.startActivity(new Intent(context, web.class).putExtra("link",aMnews.getLink()));
    /*
        Bundle bundle=new Bundle();
        bundle.putString("link",aMnews.getLink());
        fragmentWebView.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_home,fragmentWebView).addToBackStack("ttt").commit();*/
    }
}
