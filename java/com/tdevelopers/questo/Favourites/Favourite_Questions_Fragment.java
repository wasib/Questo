package com.tdevelopers.questo.Favourites;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite_Questions_Fragment extends Fragment {
    RecyclerView recyclerView;
AVLoadingIndicatorView avl;
    public Favourite_Questions_Fragment() {
        // Required empty public constructor
    }

    public static Favourite_Questions_Fragment newInstance() {

        Bundle args = new Bundle();

        Favourite_Questions_Fragment fragment = new Favourite_Questions_Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite__questions_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView)view.findViewById(R.id.favq);
        avl=(AVLoadingIndicatorView)view.findViewById(R.id.avloading);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Boolean>data=new HashMap<String, Boolean>();
                if(dataSnapshot!=null&&dataSnapshot.getValue()!=null)
                    data=(HashMap<String,Boolean>)dataSnapshot.getValue();
                final ArrayList<Question>dataList=new ArrayList<Question>();
                final QuestionRecyclerAdapter adapter=new QuestionRecyclerAdapter(dataList);
                recyclerView.setAdapter(adapter);
                for(String s:data.keySet())
                {
                    FirebaseDatabase.getInstance().getReference("Question").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null)
                            {
                                Question q=(Question)dataSnapshot.getValue(Question.class);
                                avl.setVisibility(View.GONE);
                                if(q!=null)
                                {
                                    dataList.add(q);
                                    adapter.notifyItemInserted(dataList.size());
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
