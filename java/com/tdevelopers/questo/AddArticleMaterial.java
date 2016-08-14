package com.tdevelopers.questo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tdevelopers.questo.Objects.Article;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.categories;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class AddArticleMaterial extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    static String currentcat;
    AppCompatEditText input;
    AppCompatEditText title;
    ImageView adp;
    HashSet<String> choosenTags = new HashSet<>();
    MaterialDialog outdialog;
    Bitmap bitmap;
    TagContainerLayout tagContainerLayout,tags;
    CollapsingToolbarLayout collapsingToolbarLayout;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Add Feed");
        tagContainerLayout = (TagContainerLayout) findViewById(R.id.tag_container);
        adp = (ImageView) findViewById(R.id.adp);
        title = (AppCompatEditText) findViewById(R.id.title);

        input = (AppCompatEditText) findViewById(R.id.takearticle);

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                            if (behavior != null) {
                                behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, v.getBottom(), true);
                            }
                        }
                    });
                }
            }
        });
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitleEnabled(false);
        }
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

        }
        try {
            if (MyData.getTags() != null && MyData.getTags().size() != 0) {
                choosenTags.addAll(MyData.getTags());
                tagContainerLayout.setVisibility(View.VISIBLE);
                tagContainerLayout.setTags(new ArrayList<String>(choosenTags));
                tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {

                    @Override
                    public void onTagClick(final int position, final String text) {
                        // ...
                        //Toast.makeText(AddQuestion.this, "Clicked "+text, Toast.LENGTH_SHORT).show();

                        AlertDialog dialog = new AlertDialog.Builder(AddArticleMaterial.this)
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

                Toast.makeText(AddArticleMaterial.this, "Tags Included " + choosenTags, Toast.LENGTH_SHORT).show();
                MyData.setTags(new HashSet<String>());

            }

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (input.getText().toString().trim().length() != 0) {

                                if (title.getText().toString().trim().length() != 0) {
                                    DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Article").push();
                                    Date date = new Date();

                                    if (bitmap != null) {
                                        if (MyData.haveNetworkConnection()) {
                                            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    uploadPic();
                                                }
                                            });
                                        } else
                                            Toast.makeText(AddArticleMaterial.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Article article = new Article(newref.getKey(), date.getTime(), title.getText().toString(), input.getText().toString(), Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getName(), "");
                                        if (MyData.haveNetworkConnection()) {
                                            upload(article);
                                            Toast.makeText(AddArticleMaterial.this, "Sucessfully Uploaded Article", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(AddArticleMaterial.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(AddArticleMaterial.this, "Please Write Title", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Write Content of Article", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                        }
                    }

                });
            }
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
    }

    public void upload(final Article article) {
        try {
            DatabaseReference current = FirebaseDatabase.getInstance().getReference("Article").child(article.id);
            Map<String, String> q = new HashMap<String, String>();
            q.put("description", article.description);
            q.put("username", article.username);
            q.put("uploaded_by", article.uploaded_by);
            q.put("title", article.title);
            q.put("id", article.id);
            q.put("image", article.image);
            current.setValue(q);
            current.child("likes_count").setValue(0L);
            current.child("views_count").setValue(0L);
            current.child("nlikes_count").setValue(0L);
            //current.child("nviews_count").setValue(article.nviews_count);
            current.child("date").setValue(article.date);
            current.setPriority(article.date * -1);
            Toast.makeText(AddArticleMaterial.this, "Article Added Successfully", Toast.LENGTH_SHORT).show();

            DatabaseReference tagref;
            ArrayList<String> tagsdata = new ArrayList<>();
            if (choosenTags != null && choosenTags.size() != 0) {
                for (String s : choosenTags) {
                    if (s != null) {
                        current.child("tags_here").child(s + "").setValue(true);
                        tagsdata.add(s);
                    }
                    tagref = FirebaseDatabase.getInstance().getReference("TagUploads").child(s + "").child("Articles").child(article.id);
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
                                String key = entry.getKey();
                                // String value = entry.getValue();
                                FirebaseDatabase.getInstance().getReference("myUsers").child(key + "").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                                            String link = "Article:" + article.id;
                                            MyData.pushNotification(s, "added article to tag " + s+"\n\n"+article.title, (String) dataSnapshot.getValue(), link, article.uploaded_by);
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
                    for (String s : data.keySet())

                        FirebaseDatabase.getInstance().getReference("myUsers").child(s + "").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {


                                    String link = "Article:" + article.id;
                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "added article\n\n"+article.title, (String) dataSnapshot.getValue(), link, article.uploaded_by);
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
            FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("article_count").runTransaction(new Transaction.Handler() {
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
            FirebaseDatabase.getInstance().getReference("articles_count").runTransaction(new Transaction.Handler() {
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

                FirebaseDatabase.getInstance().getReference("Tag").child(s + "").child("article_count").runTransaction(new Transaction.Handler() {
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
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_article, menu);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {

            switch (requestCode) {

                case SELECT_PHOTO:
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = null;
                        try {
                            imageStream = getContentResolver().openInputStream(selectedImage);
                            Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] byteArray = baos.toByteArray();
                            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
                            adp.setImageBitmap(bitmap);
                            View v = findViewById(R.id.g);
                            if (v != null) {
                                v.setVisibility(View.VISIBLE);
                            }
                        } catch (Error e) {

                            Toast.makeText(AddArticleMaterial.this, "File too big to process", Toast.LENGTH_SHORT).show();

                        }
                    }
            }
        } catch (Exception e) {
            Toast.makeText(AddArticleMaterial.this, "Error occcured :( please try again !", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
    }

    public void fetchTag() {
        try {
            final ArrayList<String> categoriesListNames = new ArrayList<>();
            final ArrayList<categories> categoriesList = new ArrayList<>();
            Query cc = FirebaseDatabase.getInstance().getReference("categories");
            cc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null && dataSnapshot.getChildren() != null) { //datasnapshot!=null

                            for (DataSnapshot holder : dataSnapshot.getChildren()) {
                                if (holder != null) {
                                    categoriesList.add(holder.getValue(categories.class));
                                    categoriesListNames.add(holder.getValue(categories.class).name);

                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddArticleMaterial.this, android.R.layout.select_dialog_item, categoriesListNames);
                            outdialog = new MaterialDialog.Builder(AddArticleMaterial.this)
                                    .title("Choose Tag")
                                    .customView(R.layout.choosetagdialog, true)
                                    .positiveText("Ok").negativeText("cancel").neutralText("Add Tag").onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Toast.makeText(AddArticleMaterial.this, "Add a new tag", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AddArticleMaterial.this, AddNewTag.class);
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

                                                        AlertDialog dialog = new AlertDialog.Builder(AddArticleMaterial.this)
                                                                .setTitle("Delete " + text)
                                                                .setMessage("Remove " + text + " tag from this article ?")
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
                                                Toast.makeText(AddArticleMaterial.this, "Choosen Tags are" + choosenTags.toString(), Toast.LENGTH_SHORT).show();
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

                                        AlertDialog dialog = new AlertDialog.Builder(AddArticleMaterial.this)
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
                                    try {
                                       // choosenTags = new HashSet<String>();
                                        if(choosenTags!=null)
                                        tagContainerLayout.setTags(new ArrayList<String>(choosenTags));
                                        if (categoriesList != null && categoriesList.get(position) != null && position <= categoriesList.size() && parent.getItemAtPosition(position) != null) {
                                            Toast.makeText(AddArticleMaterial.this, "" + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            rv.setLayoutManager(new LinearLayoutManager(AddArticleMaterial.this));
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
                                    } catch (Exception e) {
                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            outdialog.show();
                        }
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {

            switch (item.getItemId()) {
                case R.id.done:
                    try {
                        if (input.getText().toString().trim().length() != 0) {

                            if (title.getText().toString().trim().length() != 0) {
                                DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Article").push();
                                Date date = new Date();

                                if (bitmap != null) {
                                    if (MyData.haveNetworkConnection()) {
                                        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                uploadPic();
                                            }
                                        });
                                    } else
                                        Toast.makeText(AddArticleMaterial.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                } else {
                                    Article article = new Article(newref.getKey(), date.getTime(), title.getText().toString(), input.getText().toString(), Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getName(), "");
                                    if (MyData.haveNetworkConnection()) {
                                        upload(article);
                                        Toast.makeText(AddArticleMaterial.this, "Successfully uploaded Article", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(AddArticleMaterial.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(AddArticleMaterial.this, "Please Write Title", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Write Content of Article", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                    }
                    break;

                case R.id.gallery:

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    break;
                case R.id.tags:
                    if (MyData.haveNetworkConnection())
                        fetchTag();
                    else
                        Toast.makeText(AddArticleMaterial.this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
            break;

            }
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
        return false;

    }

    public void uploadPic() {
        // Create a storage reference from our app

        //   Toast.makeText(AddArticleMaterial.this, "uploaded pic called", Toast.LENGTH_SHORT).show();
        try {


            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Loading").theme(Theme.LIGHT)
                    .content("Please Wait")
                    .progress(true, 0)
                    .show();

            final DatabaseReference newref = FirebaseDatabase.getInstance().getReference("Article").push();
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReferenceFromUrl("gs://questo-1f35e.appspot.com");
            // Create a reference to 'images/mountains.jpg'
            StorageReference mountainImagesRef = storageRef.child("Images").child(newref.getKey() + "");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Date date = new Date();
                    Article article = null;
                    if (downloadUrl != null) {
                        article = new Article(newref.getKey(), date.getTime(), title.getText().toString(), input.getText().toString(), Profile.getCurrentProfile().getId(), Profile.getCurrentProfile().getName(), downloadUrl.toString());
                        upload(article);
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                        Toast.makeText(AddArticleMaterial.this, "Successfully Uploaded Article", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }
            });
        } catch (Error e) {
            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

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
                            Toast.makeText(AddArticleMaterial.this, "Maximum tag count reached", Toast.LENGTH_SHORT).show();
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
