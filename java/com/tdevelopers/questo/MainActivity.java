package com.tdevelopers.questo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.cocosw.bottomsheet.BottomSheetHelper;
import com.facebook.Profile;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.squareup.picasso.Picasso;
import com.tdevelopers.questo.Adapters.NotificationAdapter;
import com.tdevelopers.questo.ChatStuff.ChatMain;
import com.tdevelopers.questo.Explore.Explore_Activity;
import com.tdevelopers.questo.Favourites.Favorite_Activity;
import com.tdevelopers.questo.LeaderShip.Leadershipboard_activity;
import com.tdevelopers.questo.LoginStuff.Introduction;
import com.tdevelopers.questo.MainFragments.MostLiked;
import com.tdevelopers.questo.MainFragments.New;
import com.tdevelopers.questo.MainFragments.Trending;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Tag;
import com.tdevelopers.questo.Objects.notifications;
import com.tdevelopers.questo.Objects.user;
import com.tdevelopers.questo.Pushes.FirebaseInstanceIDService;
import com.tdevelopers.questo.Pushes.MyFirebaseMessagingService;
import com.tdevelopers.questo.User.User;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    FloatingActionButton fab1;
    FloatingActionButton fab2, fab3;
    FloatingActionMenu fab;
    CircleImageView circleImageView;
    TabLayout tabLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView mSearchView;
    TagContainerLayout tagContainerLayout;
    LinearLayout back;
    ViewPager mpager;
    Menu menu;
    RadioButton all, following;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userref;
    RecyclerView filterrv;
    HashSet<String> filterTags = new HashSet<>();
    android.support.v7.widget.SearchView filtersearch;
    TagAdapter tagAdapter;
    FullTagAdapter fullTagAdapter;
    AVLoadingIndicatorView avl;
    private SearchHistoryTable mHistoryDatabase;

    private void animateIn(FloatingActionMenu button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_slide_out_to_right);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }

    public void init() {
        mSearchView = (SearchView) findViewById(R.id.searchView);
        setSearchView();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Questo");
        tabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab = (FloatingActionMenu) findViewById(R.id.fabmenu);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab.setAnimated(true);

        mpager = (ViewPager) findViewById(R.id.viewpagermain);
        if (mpager != null && tabLayout != null) {
            mpager.setOffscreenPageLimit(2);

            mpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                @Override
                public Fragment getItem(int position) {
                    switch (position % 3) {
                        case 0:
                            return MostLiked.newInstance();

                        case 1:
                            return Trending.newInstance();
                        case 2:
                            return New.newInstance();

                        default:
                            return null;
                    }
                }

                @Override
                public int getCount() {
                    return 3;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position % 3) {
                        case 0:
                            return "";
                        case 1:
                            return "";
                        case 2:
                            return "";


                    }
                    return "";
                }
            });
            tabLayout.setupWithViewPager(mpager);

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_favorite_border_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_whatshot_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_fiber_new_white_24dp);

            tabLayout.setOnTabSelectedListener(
                    new TabLayout.ViewPagerOnTabSelectedListener(mpager) {
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            super.onTabSelected(tab);
                            RecyclerView recyclerView;
                            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.mainappbar);
                            int numTab = tab.getPosition();
                            if (fab != null)
                                animateIn(fab);

                            switch (numTab) {
                                case 0:
                                    recyclerView = (RecyclerView) findViewById(R.id.rvmostliked);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;
                                case 1:

                                    recyclerView = (RecyclerView) findViewById(R.id.rvtrending);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;
                                case 2:

                                    recyclerView = (RecyclerView) findViewById(R.id.rvnew);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;

                            }
                            if (appBarLayout != null)
                                appBarLayout.setExpanded(true, true);
                        }

                    });


        }


        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avl = (AVLoadingIndicatorView) findViewById(R.id.avloading);

        MyData.context = this;

        startService(new Intent(this, FirebaseInstanceIDService.class));
        startService(new Intent(this, MyFirebaseMessagingService.class));
        //FirebaseAuth.getInstance().signInAnonymously();
        //FirebaseAuth.getInstance().signInAnonymously();


/*        progress = new MaterialDialog.Builder(this)
                .title("Loading").theme(Theme.LIGHT)
                .content("Please Wait")
                .progress(true, 0)
                .show();*/
        init();


        final View Header = navigationView.getHeaderView(0);
        Header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, User.class);
                i.putExtra("id", Profile.getCurrentProfile().getId());
                startActivity(i);
            }
        });
        if (Profile.getCurrentProfile() != null) {

            userref = database.getReference("myUsers").child(Profile.getCurrentProfile().getId());
            userref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                            progress.dismiss();

                    user current = dataSnapshot.getValue(user.class);

                    if (current != null && current.check()) {
                        final TextView textView = (TextView) Header.findViewById(R.id.name);
                        final TextView textView1 = (TextView) Header.findViewById(R.id.sub);

                        final TextView textView2 = (TextView) Header.findViewById(R.id.score);
                        circleImageView = (CircleImageView) Header.findViewById(R.id.CimageView);

                        back = (LinearLayout) Header.findViewById(R.id.backy);
                        if (current.score != null)
                            textView2.setText("Score " + current.score);


                        if (current.following != null && current.followers != null && current.score != null) {

                            textView1.setText(current.followers.size() + " followers | " + current.following.size() + " following");

                        } else if (current.followers == null && current.following == null) {
                            textView1.setText(0 + " followers | " + 0 + " following");

                        } else if (current.followers == null) {
                            textView1.setText(0 + " followers | " + current.following.size() + " following");

                        } else if (current.following == null) {
                            textView1.setText(current.followers.size() + " followers | " + 0 + " following");

                        }


                        String urlImage = "https://graph.facebook.com/" + current.id + "/picture?type=large";
                        textView.setText(current.name);
                        Activity a = MainActivity.this;
                        Picasso.with(a)
                                .load(urlImage)
                                .fit().centerCrop()
                                .into(circleImageView);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                // Toast.makeText(MainActivity.this, "opened drawer", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id;
        id = v.getId();
        if (MyData.haveNetworkConnection()) {
            if (id == R.id.fab1) {
                startActivity(new Intent(MainActivity.this, AddQuestion.class));
                //  fab.close(true);
            } else if (id == R.id.fab2) {
                startActivity(new Intent(MainActivity.this, AddArticleMaterial.class));
                //fab.close(true);
            } else if (id == R.id.fab3) {
                startActivity(new Intent(MainActivity.this, AddNewTag.class));
                //fab.close(true);
            }
        } else Toast.makeText(this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

    }

    public void filter() {
        final MaterialDialog d = new MaterialDialog.Builder(this)
                .title("Filter")
                .customView(R.layout.filterdialog, true)
                .positiveText("Ok").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(MainActivity.this, "Choosen Tags are" + filterTags.toString(), Toast.LENGTH_SHORT).show();
                        if (filterTags != null) {
                            MostLiked.setFilterTags(new ArrayList<String>(filterTags));

                            New.setFilterTags(new ArrayList<String>(filterTags));
                            Trending.setFilterTags(new ArrayList<String>(filterTags));
                            if (menu != null) {
                                if (filterTags != null)
                                    ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.filter), getResources().getDrawable(R.drawable.ic_filter_outline_white_24dp), ActionItemBadge.BadgeStyles.RED, filterTags.size());

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

                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete " + text)
                            .setMessage("Remove " + text + " tag from filter ?")
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

    private void getData(String text, int position) {
        mHistoryDatabase.addItem(new SearchItem(text));
        Intent intent = new Intent(getApplicationContext(), Search_Activity.class);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    protected void setSearchView() {
        mHistoryDatabase = new SearchHistoryTable(this);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        if (mSearchView != null) {
            mSearchView.setVersion(SearchView.VERSION_MENU_ITEM);
            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_BIG);
            mSearchView.setTextSize(16);
            mSearchView.setHint("Search Everywhere");
            mSearchView.setDivider(false);
            mSearchView.setVoice(false);
            mSearchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            mSearchView.setShadowColor(ContextCompat.getColor(this, R.color.search_shadow_layout));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchView.close(false);
                    getData(query, 0);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public void onOpen() {
                    if (fab != null) {
                        //fab.close(true);
                        fab.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onClose() {
                    if (fab != null) {
                        //  fab.open(true);
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            });

            List<SearchItem> suggestionsList = new ArrayList<>();
         /*   suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));*/

            SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
            searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mSearchView.close(false);
                    TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                    String query = textView.getText().toString();
                    getData(query, position);
                }
            });
            mSearchView.setAdapter(searchAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.notifications:

                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (dl != null)
                    dl.openDrawer(Gravity.RIGHT);

                avl.setVisibility(View.VISIBLE);
                //  RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notificationsrv);
                MyData.setLoader(avl);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notificationsrv);
                if (recyclerView != null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    //recyclerView.setNestedScrollingEnabled(false);
                    Query q = FirebaseDatabase.getInstance().getReference("notifications").child(Profile.getCurrentProfile().getId());
                    recyclerView.setAdapter(new NotificationAdapter(q, notifications.class, this));
                }
                ImageView imageView = (ImageView) findViewById(R.id.sweep);
                if (imageView != null) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Delete")
                                    .setMessage("Are you sure to delete all notifications")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            DatabaseReference del = FirebaseDatabase.getInstance().getReference("notifications").child(Profile.getCurrentProfile().getId());
                                            del.removeValue();


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
                    });
                }
                //  startActivity(new Intent(MainActivity.this, MaterialNotifications.class));
                return true;
            case R.id.filter:
                if (MyData.haveNetworkConnection())
                    filter();
                else
                    Toast.makeText(this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.action_search:
                if (mSearchView != null)
                    mSearchView.open(true);
                return true;

            case android.R.id.home:
                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.dl);
                mDrawerLayout.openDrawer(GravityCompat.START); // finish()
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        try {


            ActionItemBadge.update(MainActivity.this, menu.findItem(R.id.filter), getResources().getDrawable(R.drawable.ic_filter_outline_white_24dp), ActionItemBadge.BadgeStyles.RED, filterTags.size());
            fab.close(false);
        } catch (Exception e) {
            return;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.myaccount) {
            Intent intent = new Intent(this, User.class);
            intent.putExtra("id", Profile.getCurrentProfile().getId());
            startActivity(intent);

        } else if (id == R.id.share) {

            String text = "Check out new app Questo : \n " + "https://play.google.com/store/apps/details?id=com.tdevelopers.questo";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            BottomSheet sheet = BottomSheetHelper.shareAction(this, shareIntent).title("Share App").build();
            sheet.show();


        } else if (id == R.id.tagsmain) {
            startActivity(new Intent(this, TagActivity.class));
        } else if (id == R.id.Explore) {
            startActivity(new Intent(this, Explore_Activity.class));
        } else if (id == R.id.Articles) {
            startActivity(new Intent(MainActivity.this, ArticleActivity.class));

        } else if (id == R.id.aboutus) {
            startActivity(new Intent(MainActivity.this, About_me.class));

        } else if (id == R.id.favourites) {
            startActivity(new Intent(MainActivity.this, Favorite_Activity.class));

        } else if (id == R.id.Chat) {
            startActivity(new Intent(MainActivity.this, ChatMain.class));
        } else if (id == R.id.scorechart) {
            startActivity(new Intent(MainActivity.this, ScoreChart.class));
        } else if (id == R.id.myfriends) {
            Intent i = new Intent(MainActivity.this, MyFriends.class);
            startActivity(i);
        } else if (id == R.id.scoreboard) {

            Intent i = new Intent(MainActivity.this, Leadershipboard_activity.class);
            startActivity(i);

        } else if (id == R.id.how) {
            startActivity(new Intent(MainActivity.this, Introduction.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
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
