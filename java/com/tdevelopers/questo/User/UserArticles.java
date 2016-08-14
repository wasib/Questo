package com.tdevelopers.questo.User;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.ArticleListAdapter;
import com.tdevelopers.questo.Objects.Article;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserArticles extends Fragment {

    RecyclerView recyclerView;
    String id = "";
AVLoadingIndicatorView avl;
    public UserArticles() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public UserArticles(String id) {
        this.id = id;
    }

    public static UserArticles newInstance(String id) {

        Bundle args = new Bundle();

        UserArticles fragment = new UserArticles(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.userarticles);
        avl=(AVLoadingIndicatorView)view.findViewById(R.id.avloading) ;
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        final TextView textView = (TextView) view.findViewById(R.id.headertt);
        if (id != null && id.trim().length() != 0) {
            Query q = FirebaseDatabase.getInstance().getReference("Article").orderByChild("uploaded_by").equalTo(id);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Article> data = new ArrayList<Article>();
                    avl.setVisibility(View.GONE);
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null)
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Article article = d.getValue(Article.class);
                            if (article != null)
                                data.add(article);
                        }

                    textView.setText(data.size() + " Articles");
                    recyclerView.setAdapter(new ArticleListAdapter(data));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_articles, container, false);
    }

}
