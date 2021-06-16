package com.karkor.ahtmalyat.Adabter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.karkor.ahtmalyat.Model.News2;
import com.karkor.ahtmalyat.R;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.keenfin.audioview.AudioView;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<News2> newsList;
    private InterstitialAd mInterstitialAd;
    Context context;
    CollectionReference reference;
    public NewsAdapter(List<News2> newsList, Context context, CollectionReference reference) {
        this.newsList = newsList;
        this.context=context;
        this.reference=reference;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item2, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvDateOfPublish.setText(getDate(newsList.get(position).getDateOfPost()));
        holder.tvViewer.setText(newsList.get(position).getViewsNum()+"");




      reference.document(newsList.get(position).getIdNews()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
              News2 news=documentSnapshot.toObject(News2.class);
                 int views=news.getViewsNum();
                        Log.d("ttt",news.getTitleNews()+"Views iten ==> "+news.getViewsNum());
                    views+=1;
                       reference.document(newsList.get(position).getIdNews())
                               .update("viewsNum",views).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Log.d("ttt","success  updated");


                           }
                       });
           }
       });


        if (newsList.get(position).getTitleNews() == null || newsList.get(position).getTitleNews().equals("")) {
            holder.tvTitleNews.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvTitleNews.setVisibility(View.VISIBLE);
            holder.tvTitleNews.setText(newsList.get(position).getTitleNews());



            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,newsList.get(position).getTitleNews()
                            +"\n<*>ارسلت من تطبيق احتميلات نيوز<*>");
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(intent, "ارسال عبر").addFlags(FLAG_ACTIVITY_NEW_TASK));


                    return false;
                }
            });
        }




           if (newsList.get(position).getListImagesUrl()==null ||newsList.get(position).getListImagesUrl().equals(""))
           {
               holder.progressBar.setVisibility(View.GONE);

               holder.viewPagerImgs.setVisibility(View.GONE);
             //  holder.progressBar.setVisibility(View.GONE);
             holder.springDotsIndicator.setVisibility(View.GONE);
           }else {
               holder.progressBar.setVisibility(View.GONE);

             //  holder.progressBar.setVisibility(View.VISIBLE);
              // holder.imgVideoPlacHolder.setVisibility(View.GONE);
               holder.viewPagerImgs.setVisibility(View.VISIBLE);
               ImagesAdapter imagesAdapter=new ImagesAdapter(context,newsList.get(position).getListImagesUrl());
               holder.viewPagerImgs.setAdapter(imagesAdapter);

           if (newsList.get(position).getListImagesUrl().size()>1){
               holder.progressBar.setVisibility(View.GONE);

               holder.springDotsIndicator.setVisibility(View.VISIBLE);
               holder.springDotsIndicator.setViewPager(holder.viewPagerImgs);
           }else {
               holder.progressBar.setVisibility(View.GONE);

               holder.springDotsIndicator.setVisibility(View.GONE);

           }

       }
           if (newsList.get(position).getAudioUrl()!=null || !TextUtils.isEmpty(newsList.get(position)
                   .getAudioUrl())){
               try {

                   holder.progressBar.setVisibility(View.VISIBLE);
                   holder.audioView.setVisibility(View.VISIBLE);
                   holder.audioView.setDataSource(newsList.get(position).getAudioUrl());
                   holder.audioView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                       @Override
                       public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                           holder.progressBar.setVisibility(View.GONE);

                       }
                   });
               } catch (IOException e) {
                   e.printStackTrace();
               }
               catch (IllegalStateException a){
                   a.printStackTrace();
                   if (a.getMessage()!=null)
                   Log.d("ttt","Eroooorrs"+a.getMessage());
               }

           }else {

               holder.audioView.setVisibility(View.GONE);
           }

















      //Todo Videoooooo
        if (newsList.get(position).getVideoNews()==null ||newsList.get(position).getVideoNews().equals(""))
        {
            holder.progressBar.setVisibility(View.GONE);

            holder.viewPagerVideos.setVisibility(View.GONE);
         //   holder.progressBar.setVisibility(View.GONE);
     //       holder.imgVideoPlacHolder.setVisibility(View.GONE);


        }else {

            holder.progressBar.setVisibility(View.GONE);

            holder.viewPagerVideos.setVisibility(View.VISIBLE);
            VideosAdapter videosAdapter=new VideosAdapter(context,newsList.get(position).getListVideosUrl());
            holder.viewPagerVideos.setAdapter(videosAdapter);
            holder.progressBar.setVisibility(View.GONE);

            if (newsList.get(position).getListVideosUrl().size()>1){
                holder.progressBar.setVisibility(View.GONE);

                holder.springDotsIndicator.setVisibility(View.VISIBLE);
                holder.springDotsIndicator.setViewPager(holder.viewPagerVideos);
            }else {
                holder.progressBar.setVisibility(View.GONE);

                holder.progressBar.setVisibility(View.GONE);
                holder.springDotsIndicator.setVisibility(View.GONE);

            }


        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitleNews;
        TextView tvDateOfPublish;
        TextView tvViewer;
        ImageView imageViewNews;
        ProgressBar progressBar;
        PlayerView playerView ;

         ImageView imgVideoPlacHolder;
        ViewPager viewPagerImgs;
        WormDotsIndicator springDotsIndicator;
        AudioView audioView;



        ViewPager viewPagerVideos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitleNews = itemView.findViewById(R.id.title_news);
           // imageViewNews = itemView.findViewById(R.id.img_news);
            tvDateOfPublish = itemView.findViewById(R.id.time_news);
            tvViewer = itemView.findViewById(R.id.viewer_num);
           progressBar = itemView.findViewById(R.id.progressBar2);
             playerView = itemView.findViewById(R.id.video_news);
             imgVideoPlacHolder = itemView.findViewById(R.id.img_video_placholder);
             viewPagerImgs = itemView.findViewById(R.id.view_pager_imgs);
             springDotsIndicator = itemView.findViewById(R.id.spring_dots_indicator);
            viewPagerVideos = itemView.findViewById(R.id.view_pager_videos);
            audioView = itemView.findViewById(R.id.record_view);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
            itemView.startAnimation(animation);

        }
    }



    String getDate(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.GERMANY);
        return simpleDateFormat.format(date);

    }

    private MediaSource buildMediaSource(Uri uri) {
        com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

}
