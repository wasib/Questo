package com.tdevelopers.questo.MainFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.EndlessRecyclerViewScrollListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class New extends Fragment {
    public static AVLoadingIndicatorView avl;

    public static ArrayList<String> FilterTags = new ArrayList<>();
    public static Context context;
    public static RecyclerView recyclerView;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static QuestionRecyclerAdapter adapter;
    public static ArrayList<Question> tobesenter;
    public static int counter = 0;
    public static LinearLayoutManager l;
    public static ArrayList<Question> tobesent = new ArrayList<>();

    public New() {


    }

    public static New newInstance() {
        Bundle args = new Bundle();
        New fragment = new New();
        fragment.setArguments(args);
        return fragment;
    }

    public static void setFilterTags(ArrayList<String> filterTags) {
        FilterTags = filterTags;
        tobesent = new ArrayList<>();
        Toast.makeText(context, "tags setted" + FilterTags.toString(), Toast.LENGTH_SHORT).show();
        if (FilterTags != null && FilterTags.size() == 0)
            LoadData();
        else if (FilterTags != null && FilterTags.size() > 0) {
            l = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(l);
            counter = 0;
            FilterLoadData();
        }
    }

    public static void FilterLoadData() {
      //  avl.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference("Question").orderByPriority().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                     @Override
                                                                                                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                         try {
                                                                                                                             ArrayList<Question> data = new ArrayList<Question>();
                                                                                                                             //avl.setVisibility(View.GONE);
                                                                                                                             swipeRefreshLayout.setRefreshing(false);
                                                                                                                             if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                                                                                                                                 for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                                                                                                     Question current = d.getValue(Question.class);

                                                                                                                                     if (current != null) {

                                                                                                                                         for (String s : FilterTags)
                                                                                                                                             if (current.tags_here != null && current.tags_here.containsKey(s))
                                                                                                                                                 data.add(current);


                                                                                                                                     }

                                                                                                                                 }

                                                                                                                             }
                                                                                                                             recyclerView.setAdapter(new QuestionRecyclerAdapter(data));
                                                                                                                             if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                                                                                                                 swipeRefreshLayout.setRefreshing(false);
                                                                                                                         } catch (Exception e) {
                                                                                                                             FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                                                         }
                                                                                                                     }

                                                                                                                     @Override
                                                                                                                     public void onCancelled(DatabaseError databaseError) {

                                                                                                                     }
                                                                                                                 }

        );

    }

    public static void LoadData() {
        final Query q = FirebaseDatabase.getInstance().getReference("Question").orderByPriority().limitToFirst(10);
        final LinearLayoutManager l = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(l);
     //   avl.setVisibility(View.VISIBLE);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null && dataSnapshot.getChildren() != null) {
                        //without filter
                        avl.setVisibility(View.GONE);
                        tobesenter = new ArrayList<Question>();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d != null) {
                                Question q = d.getValue(Question.class);
                                tobesenter.add(q);
                            }


                        }
                        adapter = new QuestionRecyclerAdapter(tobesenter);
                        recyclerView.setAdapter(adapter);
                        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(l) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount) {
                                final Query extra = FirebaseDatabase.getInstance().getReference("Question").orderByPriority().limitToFirst(totalItemsCount + 5);
                                extra.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            ArrayList<Question> extras = new ArrayList<>();
                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                extras.add(d.getValue(Question.class));
                                            }
                                            tobesenter.removeAll(tobesenter);
                                            tobesenter.addAll(extras);
                                            //   tobesenter.remove(null);
                                            //   tobesenter.add(null);
                                            adapter.notifyItemInserted(tobesenter.size() - 1);
                                            adapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }


                        });

                    }
                } catch (Exception e) {
                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.rvnew);
        avl = (AVLoadingIndicatorView) view.findViewById(R.id.avloading);
        try {
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            LoadData();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if ((FilterTags != null && FilterTags.size() == 0) || FilterTags == null)
                        LoadData();
                    else if (FilterTags != null && FilterTags.size() > 0)
                        FilterLoadData();
                }
            });
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
    }


}
