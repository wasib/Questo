package com.tdevelopers.questo.SearchFragments;


import android.content.Context;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionSearch extends Fragment {
    static String tobesearch = "";
    static RecyclerView recyclerView;
    static Context context;

    public QuestionSearch() {
        // Required empty public constructor
    }

    public static QuestionSearch newInstance() {

        Bundle args = new Bundle();

        QuestionSearch fragment = new QuestionSearch();
        fragment.setArguments(args);
        return fragment;
    }

    public static void setQuery(String q) {
        tobesearch = q;
        search();
    }

    public static void search() {
        if (context != null)
            if (recyclerView != null && tobesearch != null && tobesearch.trim().length() != 0) {
                Query q = FirebaseDatabase.getInstance().getReference("Question");
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashSet<Question> dataList = new HashSet<Question>();
                        if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {

                                if (d.getValue(Question.class) != null) {
                                    Question a = d.getValue(Question.class);

                                    if (a != null) {

                                        if (a.choice0.toLowerCase().contains(tobesearch.toLowerCase()) || a.choice1.toLowerCase().contains(tobesearch.toLowerCase()) || a.choice2.toLowerCase().contains(tobesearch.toLowerCase()) || a.choice3.toLowerCase().contains(tobesearch.toLowerCase()) || a.question.toLowerCase().contains(tobesearch.toLowerCase()))
                                            dataList.add(a);
                                        else {
                                            if (a.tags_here != null && a.tags_here.size() != 0) {
                                                for (String s : a.tags_here.keySet()) {
                                                    if (s.toLowerCase().contains(tobesearch.toLowerCase()))
                                                        dataList.add(a);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        recyclerView.setAdapter(new QuestionRecyclerAdapter(new ArrayList<Question>(dataList)));
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
        return inflater.inflate(R.layout.fragment_question_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.searchquestionrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if (tobesearch != null && tobesearch.trim().length() != 0)
            setQuery(tobesearch + "");
    }

}
