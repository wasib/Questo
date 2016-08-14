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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserQuestions extends Fragment {

    RecyclerView recyclerView;
    String id = "";
    AVLoadingIndicatorView avl;

    public UserQuestions() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public UserQuestions(String id) {
        this.id = id;
    }

    public static UserQuestions newInstance(String id) {
        Bundle args = new Bundle();
        UserQuestions fragment = new UserQuestions(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avl = (AVLoadingIndicatorView) view.findViewById(R.id.avloading);
        recyclerView = (RecyclerView) view.findViewById(R.id.userquestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        final TextView textView = (TextView) view.findViewById(R.id.headertt);
        Query q;
        if (id != null && id.trim().length() != 0) {
            q = FirebaseDatabase.getInstance().getReference("Question").orderByChild("uploaded_by").equalTo(id);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Question> data = new ArrayList<Question>();
                    avl.setVisibility(View.GONE);
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if (d != null && d.getValue(Question.class) != null) {
                                data.add(d.getValue(Question.class));

                            }
                        }
                        textView.setText(data.size() + " Questions");
                        recyclerView.setAdapter(new QuestionRecyclerAdapter(data));
                    }
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
        return inflater.inflate(R.layout.fragment_user_questions, container, false);
    }

}
