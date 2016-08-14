package com.tdevelopers.questo.SearchFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.tagccadapter;
import com.tdevelopers.questo.Objects.Tag;
import com.tdevelopers.questo.R;

import java.util.ArrayList;

public class TagSearch extends Fragment {
    public static RecyclerView recyclerView;

    static String tobesearch = "";

    public TagSearch() {
        // Required empty public constructor
    }

    public static void setQuery(String q) {
        tobesearch = q;

        search();
    }

    public static TagSearch newInstance() {
        Bundle args = new Bundle();
        TagSearch fragment = new TagSearch();
        fragment.setArguments(args);
        return fragment;
    }


    public static void search() {

        if (recyclerView != null&&tobesearch!=null&&tobesearch.trim().length()!=0) {

            FirebaseDatabase.getInstance().getReference("Tag").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Tag> data = new ArrayList<Tag>();
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Tag t = d.getValue(Tag.class);

                            if (t != null && t.name.toLowerCase().contains(tobesearch.toLowerCase())) {
                                data.add(t);
                            }
                        }

                    }
                    recyclerView.setAdapter(new tagccadapter(data));
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
        return inflater.inflate(R.layout.fragment_tag_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.searchtagrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if (tobesearch != null && tobesearch.trim().length() != 0)
            setQuery(tobesearch + "");

    }
}
