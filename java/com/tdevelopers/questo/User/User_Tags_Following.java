package com.tdevelopers.questo.User;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.tagccadapter;
import com.tdevelopers.questo.Objects.Tag;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class User_Tags_Following extends Fragment {

    public static tagccadapter adapter;
    RecyclerView recyclerView;
    String id;
    TextView textView;
AVLoadingIndicatorView avl;

    public User_Tags_Following() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public User_Tags_Following(String id) {
        // Required empty public constructor
        this.id = id;
    }

    public static User_Tags_Following newInstance(String id) {

        Bundle args = new Bundle();

        User_Tags_Following fragment = new User_Tags_Following(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user__tags__following, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.usertags);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        avl=(AVLoadingIndicatorView)view.findViewById(R.id.avloading);
        textView = (TextView) view.findViewById(R.id.headertt);
        getData();
    }

    public void getData() {
        try {
            if (id != null && id.trim().length() != 0) {
                FirebaseDatabase.getInstance().getReference("myUsers").child(id).child("tagsfollowing").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HashMap<String, Boolean> tags = new HashMap<String, Boolean>();
                        if (dataSnapshot != null && dataSnapshot.getValue() != null)
                            tags = (HashMap<String, Boolean>) dataSnapshot.getValue();


                        final ArrayList<Tag> data = new ArrayList<Tag>();
                        adapter = new tagccadapter(data);
                        recyclerView.setAdapter(adapter);
                        // adapter.getFilter().filter("a");

                        textView.setText(tags.size() + " Tags");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tag");
                        if (tags != null) {
                            if(tags.size()==0)
                            {
                                avl.setVisibility(View.GONE);
                            }
                            for (String s : tags.keySet()) {
                                ref.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        avl.setVisibility(View.GONE);
                                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                            Tag t = dataSnapshot.getValue(Tag.class);
                                            if (t != null) {

                                                data.add(t);
                                                adapter.full.add(t);
                                                adapter.notifyItemInserted(data.size());

                                               // textView.setText(data.size() + " Tags");
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } catch (Exception e) {

        }
    }
}
