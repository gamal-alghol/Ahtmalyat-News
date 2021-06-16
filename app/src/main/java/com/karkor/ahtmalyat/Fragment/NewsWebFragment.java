package com.karkor.ahtmalyat.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.karkor.ahtmalyat.Adabter.NewsWebAdapter;
import com.karkor.ahtmalyat.Model.Mnews;
import com.karkor.ahtmalyat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsWebFragment extends Fragment {

    LinearLayoutManager layoutManager;
NewsWebAdapter newsAdabter;
View rootView;
    RecyclerView recyclerView;
    ArrayList<Mnews> newsArrayList;
    ProgressBar bar;
    public NewsWebFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;

        recyclerView=view.findViewById(R.id.recyclerView_news);
        bar=view.findViewById(R.id.progressBar4);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        newsArrayList=new ArrayList<>();
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getAllNews();



    }

    private void getAllNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                FirebaseFirestore.getInstance().collection("NewsWeb").orderBy("time", Query.Direction.DESCENDING).
                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Mnews mnews=  document.toObject(Mnews.class);

                                newsArrayList.add(mnews);
                            }
                            newsAdabter = new NewsWebAdapter(newsArrayList, rootView.getContext(),getFragmentManager());
                            recyclerView.setAdapter(newsAdabter);
                            bar.setVisibility(View.GONE);
                        } else {

                        }
                    }
                });
            }
        }).start();

    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
}}