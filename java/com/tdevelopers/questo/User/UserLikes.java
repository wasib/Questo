package com.tdevelopers.questo.User;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserLikes extends Fragment {
    RecyclerView recyclerView;
    String id = "";
    SwipeRefreshLayout swipeRefreshLayout;
    AVLoadingIndicatorView avl;

    public UserLikes() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public UserLikes(String id) {
        // Required empty public constructor
        this.id = id;
    }

    public static UserLikes newInstance(String id) {

        Bundle args = new Bundle();

        UserLikes fragment = new UserLikes(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_likes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.userlikesrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        avl = (AVLoadingIndicatorView) view.findViewById(R.id.avloading);
        final TextView textView = (TextView) view.findViewById(R.id.headertt);

        if (id != null && id.trim().length() != 0) {
            FirebaseDatabase.getInstance().getReference("userlikes").child(id).child("questionlikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                    final ArrayList<Question> dataList = new ArrayList<Question>();
                    final QuestionRecyclerAdapter adapter = new QuestionRecyclerAdapter(dataList);
                    recyclerView.setAdapter(adapter);
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();


                    textView.setText(data.size() + " Likes");
                    if(data!=null&&data.size()==0)
                        avl.setVisibility(View.GONE);
                    for (String s : data.keySet()) {
                        FirebaseDatabase.getInstance().getReference("Question").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                avl.setVisibility(View.GONE);
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    Question q = dataSnapshot.getValue(Question.class);

                                    if (q != null) {
                                        dataList.add(q);
                                        adapter.notifyItemInserted(dataList.size());
                                        //f textView.setText(dataList.size() + " Likes");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                    //   recyclerView.setAdapter(new QuestionHashMapAdapter(data));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
