package com.tdevelopers.questo.Opens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tdevelopers.questo.AddArticleMaterial;
import com.tdevelopers.questo.AddQuestion;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Tag;
import com.tdevelopers.questo.R;
import com.tdevelopers.questo.TagOpenFragments.TagOpenArticleFragment;
import com.tdevelopers.questo.TagOpenFragments.TagOpenExperts;
import com.tdevelopers.questo.TagOpenFragments.TagOpenFollowers;
import com.tdevelopers.questo.TagOpenFragments.TagOpenQuestionFragment;
import com.tdevelopers.questo.libraries.PaletteTransformation;

import java.util.HashMap;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagOpenActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager mpager;
    String id = "";
    Toolbar toolbar;
    TextView name, followers, stuff;
    Button follow;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView circle;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_open);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = TagOpenActivity.this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS fl
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }
        add = (FloatingActionButton) findViewById(R.id.add);
        id = getIntent().getExtras().getString("id");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mpager = (ViewPager) findViewById(R.id.pager);
        name = (TextView) findViewById(R.id.name);
        follow = (Button) findViewById(R.id.follow);
        name.setText(id + "");
        followers = (TextView) findViewById(R.id.followers);
        stuff = (TextView) findViewById(R.id.stuff);
        circle = (CircleImageView) findViewById(R.id.pic);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        setTitle("Questo");
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
        if (id != null && id.trim().length() != 0) {
            if (mpager != null && tabLayout != null) {
                mpager.setOffscreenPageLimit(3);
                mpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                    @Override
                    public Fragment getItem(int position) {
                        switch (position % 4) {
                            case 0:
                                return TagOpenQuestionFragment.newInstance(id);
                            case 1:
                                return TagOpenArticleFragment.newInstance(id);
                            case 2:
                                return TagOpenFollowers.newInstance(id);
                            case 3:
                                return TagOpenExperts.newInstance(id);
                            default:
                                return null;
                        }
                    }

                    @Override
                    public int getCount() {
                        return 4;
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        switch (position % 4) {
                            case 0:
                                return "Questions";

                            case 1:
                                return "Articles";
                            case 2:
                                return "Followers";
                            case 3:
                                return "Experts";

                        }
                        return "";
                    }
                });

                tabLayout.setupWithViewPager(mpager);

                tabLayout.setOnTabSelectedListener(
                        new TabLayout.ViewPagerOnTabSelectedListener(mpager) {
                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {
                                super.onTabSelected(tab);
                                RecyclerView recyclerView;
                                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                                int numTab = tab.getPosition();
                                if (add != null)
                                    add.show();
                                switch (numTab) {
                                    case 0:
                                        recyclerView = (RecyclerView) findViewById(R.id.tagopenquestionrv);

                                        if (recyclerView != null) {
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                        break;
                                    case 1:

                                        recyclerView = (RecyclerView) findViewById(R.id.tagopenarticlerv);

                                        if (recyclerView != null) {
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                        add.hide();
                                        break;
                                    case 2:

                                        recyclerView = (RecyclerView) findViewById(R.id.tagfollowersrv);

                                        if (recyclerView != null) {
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                        add.hide();
                                        break;

                                }
                                if (appBarLayout != null)
                                    appBarLayout.setExpanded(true, true);
                            }

                        });

            }
            Query query = FirebaseDatabase.getInstance().getReference("Tag").child(id + "");
            query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //     Toast.makeText(TagOpenActivity.this, "" + id, Toast.LENGTH_SHORT).show();
                                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                    final Tag item = dataSnapshot.getValue(Tag.class);
                                                    if (item != null) {


                                                        FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId() + "").child("tagsfollowing").addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                                    HashMap<String, Boolean> data = (HashMap<String, Boolean>) dataSnapshot.getValue();

                                                                    if (data != null && data.containsKey(item.name)) {
                                                                        follow.setText("Following");
                                                                        //   follow.getBackground().setColorFilter(ContextCompat.getColor(TagOpenActivity.this, R.color.green), PorterDuff.Mode.MULTIPLY);
                                                                    } else {
                                                                        follow.setText("Follow");

                                                                      //  follow.getBackground().setColorFilter(ContextCompat.getColor(TagOpenActivity.this, R.color.blue), PorterDuff.Mode.MULTIPLY);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });


                                                        follow.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (MyData.haveNetworkConnection()) {
                                                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("TagFollowers").child(item.name);
                                                                    postRef.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            //    Post p = mutableData.getValue(Post.class);


                                                                            HashMap<String, Boolean> p = (HashMap<String, Boolean>) (mutableData.getValue());
                                                                            if (p == null) {
                                                                                p = new HashMap<String, Boolean>();
                                                                                Log.v("followtrack", "null");
                                                                                p.put(Profile.getCurrentProfile().getId(), true);
                                                                                mutableData.setValue(p);
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                                                // Unstar the post and remove self from stars
                                                                                //p.starCount = p.starCount - 1;
                                                                                p.remove(Profile.getCurrentProfile().getId());
                                                                                Log.v("followtrack", "removed");
                                                                            } else {
                                                                                // Star the post and add self to stars
                                                                                //p.starCount = p.starCount + 1;
                                                                                p.put(Profile.getCurrentProfile().getId(), true);
                                                                                Log.v("followtrack", "added");
                                                                            }

                                                                            // Set value and report transaction success
                                                                            mutableData.setValue(p);
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            Log.d("followtrack", "postTransaction:onComplete:" + databaseError);

                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId() + "").child("tagsfollowing");
                                                                            DatabaseReference tagref = FirebaseDatabase.getInstance().getReference("Tag").child(item.name).child("followers");
                                                                            HashMap<String, Boolean> p = (HashMap<String, Boolean>) (dataSnapshot.getValue());
                                                                            if (p != null) {
                                                                                if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                                                    follow.setText("Following");
                                                                                    // follow.getBackground().setColorFilter(ContextCompat.getColor(TagOpenActivity.this, R.color.green), PorterDuff.Mode.MULTIPLY);
                                                                                    ref.child(item.name).setValue(true);

                                                                                } else {
                                                                                    follow.setText("Follow");
                                                                                    ref.child(item.name).setValue(null);
                                                                                  //  follow.getBackground().setColorFilter(ContextCompat.getColor(TagOpenActivity.this, R.color.blue), PorterDuff.Mode.MULTIPLY);

                                                                                }

                                                                                followers.setText(p.size() + " followers");
                                                                                tagref.setValue(p.size());
                                                                            }
                                                                            if (p == null) {
                                                                                follow.setText("Follow");
                                                                                ref.child(item.name).setValue(null);
                                                                                followers.setText("0 followers");
                                                                                tagref.setValue(0);
                                                                              //  follow.getBackground().setColorFilter(ContextCompat.getColor(TagOpenActivity.this, R.color.blue), PorterDuff.Mode.MULTIPLY);

                                                                            }

                                                                        }
                                                                    });
                                                                } else {
                                                                    Toast.makeText(TagOpenActivity.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });


                                                        if (item.followers != null)
                                                            followers.setText(item.followers + " followers");
                                                        else
                                                            followers.setText("0 followers");
                                                        name.setText(item.name + "");
                                                        stuff.setText(item.question_count + " Questions | " + item.article_count + " Articles");
                                                        if (item.pic != null && item.pic.trim().length() != 0)
                                                            Picasso.with(TagOpenActivity.this)
                                                                    .load(item.pic)
                                                                    .fit().centerCrop()
                                                                    .transform(PaletteTransformation.instance())
                                                                    .into(circle, new Callback.EmptyCallback() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            Palette palette = null;
                                                                            Bitmap bitmap = ((BitmapDrawable) circle.getDrawable()).getBitmap(); // Ew!
                                                                            if (bitmap != null)
                                                                                palette = PaletteTransformation.getPalette(bitmap);
                                                                            if (palette != null && collapsingToolbarLayout != null && tabLayout != null && toolbar != null) {

                                                                                int primary = palette.getVibrantColor(palette.getVibrantColor(getResources().getColor(R.color.color_material_motion)));
                                                                                int primaryDark = palette.getDarkVibrantColor(getResources().getColor(R.color.color_dark_material_motion));

                                                                                if (primary != getResources().getColor(R.color.color_material_motion) && primaryDark != getResources().getColor(R.color.color_dark_material_motion)) {
                                                                                    collapsingToolbarLayout.setBackgroundColor(primaryDark);
                                                                                    toolbar.setBackgroundColor(primaryDark);
                                                                                    tabLayout.setBackgroundColor(primary);
// finally change the color
                                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                                        Window window = TagOpenActivity.this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS fl
                                                                                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                                                                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                                                                                        window.setStatusBarColor(palette.getDarkVibrantColor(TagOpenActivity.this.getResources().getColor(R.color.colorPrimaryDark)));
                                                                                    }
                                                                                }
                                                                            }

                                                                        }
                                                                    });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }

            );
        }

        mpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    add.show();

                } else if (position == 1) {
                    add.show();

                } else if (position == 2) {
                    add.hide();
                }
                else if(position==3)
                {
                    add.hide();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (id != null && id.trim().length() != 0)
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mpager != null) {
                        Intent intent;
                        HashSet<String> data = new HashSet<String>();
                        int position = mpager.getCurrentItem();

                        switch (position) {
                            case 0:
                                intent = new Intent(TagOpenActivity.this, AddQuestion.class);
                                data.add(id);
                                MyData.setTags(data);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(TagOpenActivity.this, AddArticleMaterial.class);
                                data.add(id);
                                MyData.setTags(data);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }

                    }
                }
            });
    }
}
