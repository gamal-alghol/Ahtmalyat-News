package com.karkor.ahtmalyat.Fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karkor.ahtmalyat.Adabter.NewsAdapter;
import com.karkor.ahtmalyat.Model.News2;
import com.karkor.ahtmalyat.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ReleasesFragment extends Fragment {
    private RecyclerView listNews;
    private ProgressBar progressBar_news;
   List<News2>newsList;
   CollectionReference reference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_now, container, false);
        listNews = view.findViewById(R.id.list_news);
        progressBar_news = view.findViewById(R.id.ProgressBar_news);
        return view;
     }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listNews.setLayoutManager(llm);

        progressBar_news.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

       reference= FirebaseFirestore.getInstance().collection("Releases");
                reference.orderBy("dateOfPost", Query.Direction.DESCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    newsList = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        News2 news = doc.toObject(News2.class);
                        news.setIdNews(doc.getId());
                        newsList.add(news);

                    }
                    progressBar_news.setVisibility(View.GONE);
                    NewsAdapter adapter=new NewsAdapter(newsList,getContext(),reference);
                    listNews.setAdapter(adapter);


                } else {
                            Log.d("ttt", "Error getting documents: ", task.getException());
                }
            }
        });


    }


}