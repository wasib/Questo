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
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.ArticleListAdapter;
import com.tdevelopers.questo.Objects.Article;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class User_Article_Likes extends Fragment {
    String id;
    RecyclerView recyclerView;
    TextView textView;
    AVLoadingIndicatorView avl;

    public User_Article_Likes() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public User_Article_Likes(String id) {
        // Required empty public constructor
        this.id = id;
    }

    public static User_Article_Likes newInstance(String id) {

        Bundle args = new Bundle();

        User_Article_Likes fragment = new User_Article_Likes(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user__article__likes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.userarticlelikes);
        avl = (AVLoadingIndicatorView) view.findViewById(R.id.avloading);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        textView = (TextView) view.findViewById(R.id.headertt);
        recyclerView.setNestedScrollingEnabled(false);
        if (id != null && id.trim().length() != 0)
            FirebaseDatabase.getInstance().getReference("userlikes").child(id).child("articlelikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    final ArrayList<Article> dataList = new ArrayList<Article>();
                    final ArticleListAdapter adapter = new ArticleListAdapter(dataList);
                    recyclerView.setAdapter(adapter);
                    textView.setText(data.size() + " Likes");
                    if(data.size()==0)
                        avl.setVisibility(View.GONE);
                    for (String s : data.keySet()) {
                        FirebaseDatabase.getInstance().getReference("Article").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                avl.setVisibility(View.GONE);
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    Article q = dataSnapshot.getValue(Article.class);

                                    if (q != null) {
                                        dataList.add(q);
                                        adapter.notifyItemInserted(dataList.size());
                                        //textView.setText(dataList.size() + " Likes");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }
}
