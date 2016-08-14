package com.tdevelopers.questo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.Objects.categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class AddQuestion extends AppCompatActivity implements View.OnClickListener {

    static RadioButton ch0;
    static RadioButton ch1;
    static RadioButton ch2;
    static RadioButton ch3;
    TagContainerLayout tags;
    //  static FloatingActionButton b;
    static AppCompatEditText e0, e1, e2, e3, question;
    static int answer = 0;
    static EditText Explanation;
    static Question p;
    static String currentcat;
    TagContainerLayout tagContainerLayout;
    HashSet<String> choosenTags = new HashSet<>();
    MaterialDialog outdialog;
    //  CollapsingToolbarLayout collapsingToolbarLayout;
    SearchView searchView;
    String id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_question, menu);
        return true;

    }

    public void fetchTag() {
        final ArrayList<String> categoriesListNames = new ArrayList<>();
        final ArrayList<categories> categoriesList = new ArrayList<>();
        Query cc = FirebaseDatabase.getInstance().getReference("categories");
        cc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getChildren() != null) { //datasnapshot!=null

                    for (DataSnapshot holder : dataSnapshot.getChildren()) {
                        if (holder != null) {
                            categoriesList.add(holder.getValue(categories.class));
                            categoriesListNames.add(holder.getValue(categories.class).name);

                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddQuestion.this, android.R.layout.select_dialog_item, categoriesListNames);
                    outdialog = new MaterialDialog.Builder(AddQuestion.this)
                            .title("Add Tags")
                            .customView(R.layout.choosetagdialog, true)
                            .positiveText("Ok").negativeText("cancel").neutralText("Add Tag").onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Toast.makeText(AddQuestion.this, "Add a new tag", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(AddQuestion.this, AddNewTag.class);
                                    if (currentcat != null && currentcat.trim().length() != 0)
                                        i.putExtra("id", currentcat);
                                    startActivity(i);
                                }
                            }).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (choosenTags != null) {
                                        tagContainerLayout.setVisibility(View.VISIBLE);
                                        tagContainerLayout.setTags(new ArrayList<>(choosenTags));
                                        tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {

                                            @Override
                                            public void onTagClick(final int position, final String text) {
                                                // ...
                                                //Toast.makeText(AddQuestion.this, "Clicked "+text, Toast.LENGTH_SHORT).show();

                                                AlertDialog dialog = new AlertDialog.Builder(AddQuestion.this)
                                                        .setTitle("Delete " + text)
                                                        .setMessage("Remove " + text + " tag from this question ?")
                                                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                tagContainerLayout.removeTag(position);
                                                                choosenTags.remove(text);
                                                            }
                                                        })
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .create();
                                                dialog.show();
                                            }

                                            @Override
                                            public void onTagLongClick(final int position, String text) {
                                                // ...
                                            }
                                        });
                                        Toast.makeText(AddQuestion.this, "Choosen Tags are" + choosenTags.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).build();
                    tags=(TagContainerLayout)outdialog.findViewById(R.id.tag);
                    if(choosenTags!=null)
                    {
                        tags.setTags(new ArrayList<String>(choosenTags));
                        tags.setOnTagClickListener(new TagView.OnTagClickListener() {

                            @Override
                            public void onTagClick(final int position, final String text) {
                                // ...

                                AlertDialog dialog = new AlertDialog.Builder(AddQuestion.this)
                                        .setTitle("Delete " + text)
                                        .setMessage("Remove " + text + " tag from filter ?")
                                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                tags.removeTag(position);
                                                choosenTags.remove(text);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            }

                            @Override
                            public void onTagLongClick(final int position, String text) {
                                // ...
                            }
                        });


                    }
                    AppCompatSpinner actv = (AppCompatSpinner) outdialog.findViewById(R.id.ack);
                    final RecyclerView rv = (RecyclerView) outdialog.findViewById(R.id.ctag);
                    rv.setNestedScrollingEnabled(false);
                    actv.setAdapter(adapter);

                    if (currentcat != null && currentcat.trim().length() != 0)
                        actv.setSelection(adapter.getPosition(currentcat));


                    if (currentcat != null && !currentcat.equals(""))
                        actv.setSelection(adapter.getPosition(currentcat));
                    actv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                           // choosenTags = new HashSet<String>();
                            if(choosenTags!=null)
                            tagContainerLayout.setTags(new ArrayList<String>(choosenTags));
                            if (categoriesList != null && categoriesList.get(position) != null && position <= categoriesList.size() && parent.getItemAtPosition(position) != null) {
                                Toast.makeText(AddQuestion.this, "" + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                rv.setLayoutManager(new LinearLayoutManager(AddQuestion.this));
                                final TagAdapter tagadapter = new TagAdapter(categoriesList.get(position % categoriesList.size()).tags);
                                rv.setAdapter(tagadapter);
                                currentcat = parent.getItemAtPosition(position).toString();
                                searchView = (SearchView) outdialog.findViewById(R.id.search_filter);
                                searchView.setVisibility(View.VISIBLE);
                                searchView.setIconifiedByDefault(false);
                                searchView.setSubmitButtonEnabled(false);
                                searchView.setQueryHint("Search Tags");
                                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        //        Toast.makeText(AddQuestion.this, "Searched for" + newText, Toast.LENGTH_SHORT).show();
                                        tagadapter.getFilter().filter(newText);
                                        return true;
                                    }
                                });

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    outdialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (MyData.haveNetworkConnection())
            switch (item.getItemId()) {
                case R.id.tags:
                    fetchTag();
                    break;
                case R.id.done:
                case R.id.donetext:

                    if (e0.getText().toString().trim().length() != 0 && e1.getText().toString().trim().length() != 0 && e2.getText().toString().trim().length() != 0 && e3.getText().toString().trim().length() != 0 && question.getText().toString().trim().length() != 0) {
                        submit();
                    } else {
                        Toast.makeText(AddQuestion.this, "Enter Correct Details", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        else
            Toast.makeText(AddQuestion.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void upload(final Question question) {
        final LinkedHashMap<String, String> q = new LinkedHashMap<>();
        DatabaseReference userr = FirebaseDatabase.getInstance().getReference("uploads").child(Profile.getCurrentProfile().getId()).child("question_uploads");
        userr.child(question.id).setValue(true);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Question").child(question.id);
        if (question.explanation != null && question.explanation.trim().length() != 0)
            q.put("explanation", question.explanation);

        q.put("question", question.question);
        q.put("choice0", question.choice0);
        q.put("choice1", question.choice1);
        q.put("choice2", question.choice2);
        q.put("choice3", question.choice3);
        q.put("uploaded_by", question.uploaded_by);
        q.put("username", question.username);
        q.put("id", question.id);
        ref.setValue(q);
        ref.child("answer").setValue(question.answer);
        ref.child("date").setValue(question.date);
        ref.child("likes_count").setValue(0L);
        ref.child("viewcount").setValue(0L);
        ref.child("nlikes_count").setValue(0L);
        ref.child("comment_count").setValue(0L);

        ref.child("correct_count").setValue(0L);
        ref.child("wrong_count").setValue(0L);
        DatabaseReference tagref;
        ArrayList<String> tagsdata = new ArrayList<>();
        if (choosenTags != null && choosenTags.size() != 0) {
            for (String s : choosenTags) {
                if (s != null) {
                    ref.child("tags_here").child(s + "").setValue(true);
                    tagsdata.add(s);
                }
                tagref = FirebaseDatabase.getInstance().getReference("TagUploads").child(s + "").child("Questions").child(question.id);
                tagref.setValue(true);
            }

        }
        for (final String s : tagsdata) {
            FirebaseDatabase.getInstance().getReference("TagFollowers").child(s + "").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        HashMap<String, Boolean> tobesent = new HashMap<String, Boolean>();
                        tobesent = (HashMap<String, Boolean>) dataSnapshot.getValue();
                        for (Map.Entry<String, Boolean> entry : tobesent.entrySet()) {
                            final String key = entry.getKey();
                            // String value = entry.getValue();
                            FirebaseDatabase.getInstance().getReference("myUsers").child(key + "").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {


                                        String link = "Question:" + question.id;
                                        MyData.pushNotification(s, "added question to tag " + s + "\n\n" + question.question, (String) dataSnapshot.getValue(), link, key);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            // do stuff
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                if (dataSnapshot != null && dataSnapshot.getValue() != null)
                    data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                for (final String s : data.keySet())

                    FirebaseDatabase.getInstance().getReference("myUsers").child(s + "").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {


                                String link = "Question:" + question.id;
                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "added question\n\n" + question.question, (String) dataSnapshot.getValue(), link, s);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ref.setPriority(0 - question.date);
        FirebaseDatabase.getInstance().getReference("questions_count").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    p = 1L;
                } else p += 1L;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });

        FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("question_count").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long p = mutableData.getValue(Long.class);
                if (p == null) {
                    p = 1L;
                } else p += 1L;
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });

        for (String s : tagsdata) {

            FirebaseDatabase.getInstance().getReference("Tag").child(s + "").child("question_count").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Long p = mutableData.getValue(Long.class);
                    if (p == null) {
                        p = 1L;
                    } else p += 1L;
                    // Set value and report transaction success
                    mutableData.setValue(p);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                }
            });

        }
        Toast.makeText(AddQuestion.this, "Successfully uploaded question", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void init() {
        tagContainerLayout = (TagContainerLayout) findViewById(R.id.tag_container);
        question = (AppCompatEditText) findViewById(R.id.questionadd);
        ch1 = (RadioButton) findViewById(R.id.ch1add);
        ch2 = (RadioButton) findViewById(R.id.ch2add);
        ch3 = (RadioButton) findViewById(R.id.ch3add);
        ch0 = (RadioButton) findViewById(R.id.ch0add);
        Explanation = (EditText) findViewById(R.id.explanationadd);
        e0 = (AppCompatEditText) findViewById(R.id.e0);
        e1 = (AppCompatEditText) findViewById(R.id.e1);
        e2 = (AppCompatEditText) findViewById(R.id.e2);
        e3 = (AppCompatEditText) findViewById(R.id.e3);
        // b = (FloatingActionButton) findViewById(R.id.submit);
        // collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        if (MyData.getTags() != null && MyData.getTags().size() != 0) {
            tagContainerLayout.setVisibility(View.VISIBLE);
            choosenTags.addAll(MyData.getTags());
            tagContainerLayout.setTags(new ArrayList<String>(choosenTags));
            tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {

                @Override
                public void onTagClick(final int position, final String text) {
                    // ...
                    //Toast.makeText(AddQuestion.this, "Clicked "+text, Toast.LENGTH_SHORT).show();

                    AlertDialog dialog = new AlertDialog.Builder(AddQuestion.this)
                            .setTitle("Delete " + text)
                            .setMessage("Remove " + text + " tag from this question ")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tagContainerLayout.removeTag(position);
                                    choosenTags.remove(text);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }

                @Override
                public void onTagLongClick(final int position, String text) {
                    // ...
                }
            });


            Toast.makeText(AddQuestion.this, "Tags Included " + choosenTags, Toast.LENGTH_SHORT).show();

            MyData.setTags(new HashSet<String>());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Add Question");
        setSupportActionBar((toolbar));
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });
        }

        init();
//        collapsingToolbarLayout.setTitleEnabled(false);
        //b.setOnClickListener(this);
        ch0.setChecked(true);
        ch0.setOnClickListener(this);
        ch1.setOnClickListener(this);
        ch2.setOnClickListener(this);
        ch3.setOnClickListener(this);

    }

    public void submit() {

        {

            if (ch0.isChecked()) {
                answer = 0;

            } else if (ch1.isChecked()) {
                answer = 1;

            } else if (ch2.isChecked()) {
                answer = 2;

            } else if (ch3.isChecked()) {
                answer = 3;
            }
            String c[] = new String[4];
            c[0] = e0.getText().toString();

            c[1] = e1.getText().toString();

            c[2] = e2.getText().toString();

            c[3] = e3.getText().toString();
            {
                //for (int i = 0; i < 100; i++)
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Question");
                    DatabaseReference newref;
                    {
                        newref = ref.push();
                        Date date = new Date();
                        p = new Question(newref.getKey(), date.getTime(), Profile.getCurrentProfile().getName(), question.getText().toString(), Explanation.getText().toString(), c[0], c[1], c[2], c[3], answer, Profile.getCurrentProfile().getId());
                        if (MyData.haveNetworkConnection())
                            upload(p);
                        else
                            Toast.makeText(AddQuestion.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        init();
        if (id == R.id.submit)

        {

            if (e0.getText().toString().trim().length() != 0 && e1.getText().toString().trim().length() != 0 && e2.getText().toString().trim().length() != 0 && e3.getText().toString().trim().length() != 0 && question.getText().toString().trim().length() != 0) {
                submit();
            } else {
                Toast.makeText(AddQuestion.this, "Enter Correct Details", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.ch0add)

        {
            if (((RadioButton) view).isChecked()) {
                ch1.setChecked(false);

                ch2.setChecked(false);

                ch3.setChecked(false);
                ((RadioButton) view).setChecked(true);
            } else {
                ch1.setChecked(false);

                ch2.setChecked(false);

                ch3.setChecked(false);

                ((RadioButton) view).setChecked(true);

            }

        } else if (id == R.id.ch1add)

        {
            if (((RadioButton) view).isChecked()) {
                ch0.setChecked(false);

                ch2.setChecked(false);

                ch3.setChecked(false);

                ((RadioButton) view).setChecked(true);

            } else {
                ch0.setChecked(false);

                ch2.setChecked(false);

                ch3.setChecked(false);

                ((RadioButton) view).setChecked(true);

            }

        } else if (id == R.id.ch2add)

        {
            if (((RadioButton) view).isChecked()) {

                ch1.setChecked(false);

                ch0.setChecked(false);

                ch3.setChecked(false);

                ((RadioButton) view).setChecked(true);
            } else {
                ch1.setChecked(false);

                ch0.setChecked(false);

                ch3.setChecked(false);

                ((RadioButton) view).setChecked(true);

            }

        } else if (id == R.id.ch3add)

        {

            if (((RadioButton) view).isChecked()) {

                ch1.setChecked(false);

                ch2.setChecked(false);

                ch0.setChecked(false);

                ((RadioButton) view).setChecked(true);
            } else {
                ch1.setChecked(false);

                ch2.setChecked(false);

                ch0.setChecked(false);

                ((RadioButton) view).setChecked(true);

            }
        }


    }

    public class TagAdapter extends RecyclerView.Adapter<TagAdapter.tagholder> implements Filterable {
        public ArrayList<String> datalist;
        public ArrayList<String> full; //non volatile
        ValueFilter valueFilter;

        public TagAdapter(HashMap<String, Boolean> r) {
            datalist = new ArrayList<>(r.keySet());
            Collections.sort(datalist);

            full = new ArrayList<>(datalist);

        }

        @Override
        public tagholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filtertagiteem, parent, false);
            return new tagholder(view);
        }

        @Override
        public void onBindViewHolder(final tagholder holder, final int position) {
            if (datalist.get(position) != null)
                holder.cc.setText(datalist.get(position % datalist.size()));
            //     Toast.makeText(getApplicationContext(), choosenTags + "", Toast.LENGTH_SHORT).show();
            if (choosenTags != null && choosenTags.contains(datalist.get(position)))
                holder.cc.setChecked(true);
            else
                holder.cc.setChecked(false);
            holder.cc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.cc.isChecked()) {
                        holder.cc.setChecked(false);

                        choosenTags.remove((datalist.get(position % datalist.size())));
                        tags.setTags(new ArrayList<String>(choosenTags));
                    } else {
                        if (choosenTags != null && choosenTags.size() < 6) {           //code changed here
                            holder.cc.setChecked(true);
                            choosenTags.add((datalist.get(position % datalist.size())));
                            tags.setTags(new ArrayList<>(choosenTags));

                        } else {                                                        //here too
                            Toast.makeText(AddQuestion.this, "Maximum tag count reached", Toast.LENGTH_SHORT).show();
                            holder.cc.setChecked(false);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                //   if (constraint != null && constraint.length() > 0) {
                LinkedList<String> filterList = new LinkedList<>();

                for (int i = 0; i < full.size(); i++) {
                    if ((full.get(i).toUpperCase().contains(constraint.toString().toUpperCase()))) {

                        String temp = full.get(i);

                        filterList.add(temp);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
                // } else {
                //       results.count = dataTrue.size();
                //       results.values = dataTrue;
                //   }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                LinkedList<String> linkedList = (LinkedList<String>) results.values;
                datalist = new ArrayList<>();

                for (int i = 0; i < linkedList.size(); i++) {
                    datalist.add(linkedList.get(i));
                }

                notifyDataSetChanged();

            }

        }

        class tagholder extends RecyclerView.ViewHolder {

            View itemView;
            AppCompatCheckBox cc;

            public tagholder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                cc = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);

            }
        }
    }
}




