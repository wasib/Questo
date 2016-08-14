package com.tdevelopers.questo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.tdevelopers.questo.Adapters.ArticleListAdapter;
import com.tdevelopers.questo.Objects.Article;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Tag;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class ArticleActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    RadioButton all, following;
    RecyclerView filterrv;
    ArrayList<String> filterTags = new ArrayList<>();
    TagContainerLayout tagContainerLayout;
    SearchView filtersearch;
    Menu menu;
    TagAdapter tagAdapter;
    FullTagAdapter fullTagAdapter;

    int state = 0;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.articlemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.filter:
                if (MyData.haveNetworkConnection())
                    filter();
                else
                    Toast.makeText(this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort:
                sort();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sort() {
        CharSequence c[] = new CharSequence[3];
        c[0] = "Most Liked";
        c[1] = "Trending";
        c[2] = "New";
        new MaterialDialog.Builder(this)
                .title("Sort by")
                .items(c)
                .itemsCallbackSingleChoice(state, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/

                        //  Toast.makeText(ArticleActivity.this, which + "", Toast.LENGTH_SHORT).show();
                        state = which;
                        setData(which);
                        return true;
                    }
                })
                .neutralText("Cancel")
                .positiveText("Okay")
                .show();
    }

    public void filter() {
        final MaterialDialog d = new MaterialDialog.Builder(this)
                .title("Filter")
                .customView(R.layout.filterdialog, true)
                .positiveText("Ok").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Toast.makeText(ArticleActivity.this, "Choosen Tags are" + filterTags.toString(), Toast.LENGTH_SHORT).show();
                        if (filterTags != null) {
                            setData(state);
                            if (menu != null) {
                                if (filterTags != null)
                                    ActionItemBadge.update(ArticleActivity.this, menu.findItem(R.id.filter), getResources().getDrawable(R.drawable.ic_filter_outline_white_24dp), ActionItemBadge.BadgeStyles.RED, filterTags.size());

                            }

                        }
                    }
                }).neutralText("Cancel")
                .build();
        all = (RadioButton) d.findViewById(R.id.all);
        following = (RadioButton) d.findViewById(R.id.following);
        tagContainerLayout = (TagContainerLayout) d.findViewById(R.id.tag_container);
        all.setChecked(true);
        if (filterTags != null) {
            tagContainerLayout.setTags(new ArrayList<>(filterTags));
            tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {

                @Override
                public void onTagClick(final int position, final String text) {
                    // ...

                    AlertDialog dialog = new AlertDialog.Builder(ArticleActivity.this)
                            .setTitle("Delete " + text)
                            .setMessage("Remove " + text + " tag from filter")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tagContainerLayout.removeTag(position);
                                    filterTags.remove(text);
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
        filterrv = (RecyclerView) d.findViewById(R.id.tagsrv);
        filterrv.setLayoutManager(new LinearLayoutManager(this));
        filterrv.setNestedScrollingEnabled(false);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatRadioButton) v).isChecked()) {
                    Query query = FirebaseDatabase.getInstance().getReference("Tag");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                                ArrayList<Tag> data = new ArrayList<Tag>();
                                for (DataSnapshot c : dataSnapshot.getChildren()) {
                                    if (c != null)
                                        data.add(c.getValue(Tag.class));
                                }
                                fullTagAdapter = new FullTagAdapter(data);
                                filterrv.setAdapter(fullTagAdapter);
                                filtersearch = (android.support.v7.widget.SearchView) d.findViewById(R.id.filtersearch);
                                filtersearch.setVisibility(View.VISIBLE);

                                filtersearch.setIconifiedByDefault(false);
                                filtersearch.setSubmitButtonEnabled(false);
                                filtersearch.setQueryHint("Search Tags");
                                filtersearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        //        Toast.makeText(AddQuestion.this, "Searched for" + newText, Toast.LENGTH_SHORT).show();
                                        if (tagAdapter != null)
                                            tagAdapter.getFilter().filter(newText);
                                        if (fullTagAdapter != null)
                                            fullTagAdapter.getFilter().filter(newText);
                                        return true;
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
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatRadioButton) v).isChecked()) {
                    FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("tagsfollowing").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                                if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                    data = (HashMap<String, Boolean>) dataSnapshot.getValue();

                                tagAdapter = new TagAdapter(data);
                                filterrv.setAdapter(tagAdapter);
                                filtersearch = (android.support.v7.widget.SearchView) d.findViewById(R.id.filtersearch);
                                filtersearch.setVisibility(View.VISIBLE);

                                filtersearch.setIconifiedByDefault(false);
                                filtersearch.setSubmitButtonEnabled(false);
                                filtersearch.setQueryHint("Search Tags");
                                filtersearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        //        Toast.makeText(AddQuestion.this, "Searched for" + newText, Toast.LENGTH_SHORT).show();
                                        if (tagAdapter != null)
                                            tagAdapter.getFilter().filter(newText);
                                        if (fullTagAdapter != null)
                                            fullTagAdapter.getFilter().filter(newText);
                                        return true;
                                    }
                                });
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
        if (!all.isChecked()) {
            FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("tagsfollowing").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();

                    tagAdapter = new TagAdapter(data);
                    filterrv.setAdapter(tagAdapter);
                    filtersearch = (android.support.v7.widget.SearchView) d.findViewById(R.id.filtersearch);
                    filtersearch.setVisibility(View.VISIBLE);

                    filtersearch.setIconifiedByDefault(false);
                    filtersearch.setSubmitButtonEnabled(false);
                    filtersearch.setQueryHint("Search Tags");
                    filtersearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            //        Toast.makeText(AddQuestion.this, "Searched for" + newText, Toast.LENGTH_SHORT).show();
                            if (tagAdapter != null)
                                tagAdapter.getFilter().filter(newText);
                            if (fullTagAdapter != null)
                                fullTagAdapter.getFilter().filter(newText);
                            return true;
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (!following.isChecked()) {
            Query query = FirebaseDatabase.getInstance().getReference("Tag");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                        ArrayList<Tag> data = new ArrayList<Tag>();
                        for (DataSnapshot c : dataSnapshot.getChildren()) {
                            if (c != null)
                                data.add(c.getValue(Tag.class));
                        }
                        fullTagAdapter = new FullTagAdapter(data);
                        filterrv.setAdapter(fullTagAdapter);
                        filtersearch = (android.support.v7.widget.SearchView) d.findViewById(R.id.filtersearch);
                        filtersearch.setVisibility(View.VISIBLE);

                        filtersearch.setIconifiedByDefault(false);
                        filtersearch.setSubmitButtonEnabled(false);
                        filtersearch.setQueryHint("Search Tags");
                        filtersearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                //        Toast.makeText(AddQuestion.this, "Searched for" + newText, Toast.LENGTH_SHORT).show();
                                if (tagAdapter != null)
                                    tagAdapter.getFilter().filter(newText);
                                if (fullTagAdapter != null)
                                    fullTagAdapter.getFilter().filter(newText);
                                return true;
                            }
                        });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        d.show();

    }

    @Override
    protected void onResume() {
        super.onResume();


        try {
            ActionItemBadge.update(ArticleActivity.this, menu.findItem(R.id.filter), getResources().getDrawable(R.drawable.ic_filter_outline_white_24dp), ActionItemBadge.BadgeStyles.RED, filterTags.size());

        } catch (Exception e) {
            return;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
      //  avl = (AVLoadingIndicatorView) findViewById(R.id.avloading);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyData.haveNetworkConnection())
                    startActivity(new Intent(ArticleActivity.this, AddArticleMaterial.class));
                else
                    Toast.makeText(ArticleActivity.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

            }
        });
        setTitle("Questo Feed");
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

        }
        setData(state);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData(state);
            }
        });
    }

    public void setData(int i) {


        Query q = FirebaseDatabase.getInstance().getReference("Article");
        if (i == 0) {

            q = FirebaseDatabase.getInstance().getReference("Article").orderByChild("nlikes_count");
        } else if (i == 1) {

            q = FirebaseDatabase.getInstance().getReference("Article").orderByChild("views_count");
        } else if (i == 2) {

            q = FirebaseDatabase.getInstance().getReference("Article").orderByPriority();
        }
//avl.setVisibility(View.VISIBLE);
        if (filterTags != null && filterTags.size() != 0) {

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Article> dataList = new ArrayList<Article>();

                    if (dataSnapshot != null && dataSnapshot.getValue() != null && dataSnapshot.getChildren() != null) {

                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Article a = d.getValue(Article.class);
                            if (a != null) {

                                for (String s : filterTags)
                                    if (a.tags_here != null && a.tags_here.containsKey(s))
                                        dataList.add(a);
                            }
                        }

                    }
                    for (Article a : dataList)
                        Toast.makeText(ArticleActivity.this, a.title, Toast.LENGTH_SHORT).show();

                    recyclerView = (RecyclerView) findViewById(R.id.rvarticles);
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL));
                    recyclerView.setAdapter(new ArticleListAdapter(dataList));
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (filterTags == null || filterTags.size() == 0) {

         //   avl.setVisibility(View.VISIBLE);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Article> dataList = new ArrayList<Article>();

                    if (dataSnapshot != null && dataSnapshot.getValue() != null && dataSnapshot.getChildren() != null) {
                    //    avl.setVisibility(View.GONE);
                        for (DataSnapshot d : dataSnapshot.getChildren()) {


                            Article a = d.getValue(Article.class);
                            if (a != null) {

                                dataList.add(a);
                            }
                        }

                    }

                    recyclerView = (RecyclerView) findViewById(R.id.rvarticles);
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL));
                    recyclerView.setAdapter(new ArticleListAdapter(dataList));
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    public class FullTagAdapter extends RecyclerView.Adapter<FullTagAdapter.tagholder> implements Filterable {

        public ArrayList<Tag> full;
        ArrayList<Tag> datalist;
        ValueFilter valueFilter;

        public FullTagAdapter(ArrayList<Tag> data) {

            datalist = data;
            full = new ArrayList<>(datalist);
        }

        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        @Override
        public tagholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filtertagiteem, parent, false);
            return new tagholder(view);
        }

        @Override
        public void onBindViewHolder(final tagholder holder, final int position) {
            if (datalist != null && datalist.get(position % datalist.size()) != null) {
                Tag t = datalist.get(position % datalist.size());
                if (t != null) {
                    if (t.name != null)
                        holder.cc.setText(t.name);

                    if (filterTags != null) {
                        if (filterTags.contains(t.name)) {
                            holder.cc.setChecked(true);
                        } else
                            holder.cc.setChecked(false);
                    }
                    holder.cc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!holder.cc.isChecked()) {
                                holder.cc.setChecked(false);
                                filterTags.remove((datalist.get(position % datalist.size())).name);
                                tagContainerLayout.setTags(new ArrayList<>(filterTags));
                            } else {
                                holder.cc.setChecked(true);
                                filterTags.add((datalist.get(position % datalist.size())).name);
                                tagContainerLayout.setTags(new ArrayList<>(filterTags));
                            }
                        }
                    });

                }

            }

        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                //   if (constraint != null && constraint.length() > 0) {
                LinkedList<Tag> filterList = new LinkedList<>();

                for (int i = 0; i < full.size(); i++) {
                    if ((full.get(i).name.toUpperCase().contains(constraint.toString().toUpperCase()))) {


                        filterList.add(full.get(i));
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

                LinkedList<Tag> linkedList = (LinkedList<Tag>) results.values;
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
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        @Override
        public tagholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filtertagiteem, parent, false);
            return new tagholder(view);
        }

        @Override
        public void onBindViewHolder(final tagholder holder, final int position) {
            if (datalist.get(position) != null) {
                holder.cc.setText(datalist.get(position % datalist.size()));

                if (filterTags != null) {
                    if (filterTags.contains(datalist.get(position % datalist.size()))) {
                        holder.cc.setChecked(true);
                    } else
                        holder.cc.setChecked(false);
                }

                holder.cc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!holder.cc.isChecked()) {
                            holder.cc.setChecked(false);
                            filterTags.remove((datalist.get(position % datalist.size())));
                            tagContainerLayout.setTags(new ArrayList<>(filterTags));
                        } else {
                            holder.cc.setChecked(true);
                            filterTags.add((datalist.get(position % datalist.size())));
                            tagContainerLayout.setTags(new ArrayList<>(filterTags));
                        }
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return datalist.size();
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
