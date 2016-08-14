package com.tdevelopers.questo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.cocosw.bottomsheet.BottomSheetHelper;
import com.facebook.Profile;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.plattysoft.leonids.ParticleSystem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tdevelopers.questo.Objects.MyData;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.Opens.QuestionOpenActivity;
import com.tdevelopers.questo.Opens.TagOpenActivity;
import com.tdevelopers.questo.R;
import com.tdevelopers.questo.Reactions.Reactions_Activity;
import com.tdevelopers.questo.User.User;
import com.tdevelopers.questo.libraries.PaletteTransformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TAG = 0;
    private static final int VIEW_TYPE_NO_TAG = 1;
    private static final int VIEW_TYPE_PROGRESS = -1;
    ArrayList<Question> data;
    Context context;

    public QuestionRecyclerAdapter(ArrayList<Question> recieved) {
        data = recieved;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null)
            return VIEW_TYPE_PROGRESS;
        else if (data.get(position).tags_here != null)
            return VIEW_TYPE_TAG;
        else
            return VIEW_TYPE_NO_TAG;
    }

    @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == VIEW_TYPE_TAG)

        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lquestion, parent, false);
            context = view.getContext();

            return new ViewHolder(view);

        } else if (viewType == VIEW_TYPE_NO_TAG) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notaglquestion, parent, false);
            context = view.getContext();

            return new NoTagViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress, parent, false);
            context = view.getContext();
            return new ProgressViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder out, final int position) {
        try {
            if (out instanceof ViewHolder) {

                final Question item = data.get(position);
                if (item != null) {
                    final ViewHolder holder = (ViewHolder) out;
                    holder.question.setText(item.question);
                    holder.question.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(v.getContext(), QuestionOpenActivity.class);
                            i.putExtra("id", item.id);
                            v.getContext().startActivity(i);
                        }
                    });

                    holder.r[0].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[1].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[2].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[3].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[0].setTypeface(null, Typeface.NORMAL);
                    holder.r[1].setTypeface(null, Typeface.NORMAL);
                    holder.r[2].setTypeface(null, Typeface.NORMAL);
                    holder.r[3].setTypeface(null, Typeface.NORMAL);
                    holder.llExpandArea.setExpanded(false);
                    holder.ll1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(0);
                            v.getContext().startActivity(intent);
                        }
                    });


                    holder.ll2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(1);
                            v.getContext().startActivity(intent);
                        }
                    });

                    holder.ll3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(2);
                            v.getContext().startActivity(intent);
                        }
                    });

                    holder.rll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(v.getContext(), QuestionOpenActivity.class);
                            i.putExtra("id", item.id);
                            v.getContext().startActivity(i);
                        }
                    });
                    ArrayList<String> lister = new ArrayList<>();
                    if (item.tags_here != null) {
                        for (Object key : item.tags_here.keySet()) {
                            lister.add(key.toString());

                            holder.chipView.setTags(lister);
                        }

                        holder.chipView.setOnTagClickListener(new TagView.OnTagClickListener() {

                            @Override
                            public void onTagClick(int position, String text) {
                                // ...
                                Intent i = new Intent(context, TagOpenActivity.class);
                                i.putExtra("id", text);
                                context.startActivity(i);
                            }

                            @Override
                            public void onTagLongClick(final int position, String text) {
                                // ...

                                Intent i = new Intent(context, TagOpenActivity.class);
                                i.putExtra("id", text);
                                context.startActivity(i);
                            }
                        });

                    }

                    if (holder.llExpandArea.isExpanded())
                        holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    else
                        holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);


                    String pattern = "MMM - dd, yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    if (item.date != null)
                        holder.date.setText(simpleDateFormat.format(item.date));

                    holder.username.setText(item.username);
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), User.class);
                            intent.putExtra("id", item.uploaded_by);
                            v.getContext().startActivity(intent);
                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.likecount.setText(dataSnapshot.getValue() + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("comment_count").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.commentcount.setText(dataSnapshot.getValue() + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("viewcount").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.viewcount.setText((long) dataSnapshot.getValue() * -1 + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    String urlImage = "https://graph.facebook.com/" + item.uploaded_by + "/picture?type=large";

                    Picasso.with(context)
                            .load(urlImage)
                            .fit()
                            .transform(PaletteTransformation.instance())
                            .into(holder.userdp, new Callback.EmptyCallback() {
                                @Override
                                public void onSuccess() {


                                }
                            });
                    holder.likecount.setText(item.likes_count + "");
                    FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Boolean> r = new HashMap<String, Boolean>();
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                r = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            if (r.containsKey(item.id))
                                holder.like.setLiked(true);
                            else
                                holder.like.setLiked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Boolean> r = new HashMap<String, Boolean>();
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                r = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            if (r.containsKey(item.id))
                                holder.favourite.setLiked(true);
                            else
                                holder.favourite.setLiked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    holder.likecount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            v.getContext().startActivity(intent);
                        }
                    });
                    holder.userdp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), User.class);
                            intent.putExtra("id", item.uploaded_by);
                            v.getContext().startActivity(intent);
                        }
                    });
                    holder.r[0].setText(item.choice0);
                    holder.r[1].setText(item.choice1);
                    holder.r[2].setText(item.choice2);
                    holder.r[3].setText(item.choice3);
                    holder.expand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.llExpandArea.toggle();

                            if (item.uploaded_by.equals(Profile.getCurrentProfile().getId()))
                                switch (item.answer) {
                                    case 0:
                                        holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[0].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 1:

                                        holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[1].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 2:

                                        holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[2].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 3:

                                        holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[3].setTypeface(null, Typeface.BOLD);
                                        break;
                                    default:
                                        break;
                                }

                            if (!holder.llExpandArea.isExpanded()) {
                                holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);


                            } else {
                                holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                            }

                        }
                    });
                    holder.submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    if (!item.uploaded_by.equals(Profile.getCurrentProfile().getId())) {

                                        if (!holder.r[0].isChecked() && !holder.r[1].isChecked() && !holder.r[2].isChecked() && !holder.r[3].isChecked()) {


                                            Activity at = (Activity) context;
                                            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.toast,
                                                    (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                            ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                            ((TextView) layout.findViewById(R.id.result)).setText("Please select atleast one option");
                                            Toast toast = new Toast(context);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.setView(layout);
                                            toast.show();

                                        } else {
                                            int flag = 0;
                                            for (int count = 0; count < 4; count++) {
                                                if (holder.r[count].isChecked()) {
                                                    if (item.answer == count) {
                                                        flag = 1;
                                                        break;
                                                    }
                                                }
                                            }
                                            final int f = flag;
                                            final DatabaseReference nownerref = FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("nscore");
                                            final DatabaseReference nuserref = FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("nscore");
                                            final DatabaseReference ownerref = FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("score");
                                            final DatabaseReference userref = FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("score");
                                            final DatabaseReference a = FirebaseDatabase.getInstance().getReference("Attempts").child(item.id);
                                            a.child("correct").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    try {
                                                        HashMap<String, Boolean> attempts = new HashMap<String, Boolean>();
                                                        if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                            attempts = (HashMap<String, Boolean>) dataSnapshot.getValue();


                                                        final HashMap<String, Boolean> finalAttempts = attempts;
                                                        a.child("wrong").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                                    finalAttempts.putAll((HashMap<String, Boolean>) dataSnapshot.getValue());
                                                                if (!finalAttempts.containsKey(Profile.getCurrentProfile().getId())) {

                                                                    if (f == 1) { //flag=1 true

                                                                        Activity at = (Activity) context;
                                                                        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                        View layout = inflater.inflate(R.layout.toast,
                                                                                (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                        ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.correct)));
                                                                        ((TextView) layout.findViewById(R.id.result)).setText("Correct");
                                                                        Toast toast = new Toast(context);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.setDuration(Toast.LENGTH_SHORT);
                                                                        toast.setView(layout);
                                                                        toast.show();

                                                                        ParticleSystem ps = new ParticleSystem((Activity) context, 20, R.drawable.ic_star_green_500_18dp, 800);
                                                                        ps.setScaleRange(0.7f, 1.3f);
                                                                        ps.setSpeedRange(0.1f, 0.25f);
                                                                        ps.setAcceleration(0.0001f, 90);
                                                                        ps.setRotationSpeedRange(90, 180);
                                                                        ps.setFadeOut(200, new AccelerateInterpolator());
                                                                        ps.oneShot(holder.itemView, 100);
                                                                        if (item.tags_here != null && item.tags_here.size() != 0) {
                                                                            DatabaseReference useranal = FirebaseDatabase.getInstance().getReference("user_analysis").child(Profile.getCurrentProfile().getId());
                                                                            for (String s : item.tags_here.keySet()) {
                                                                                useranal.child(s).runTransaction(new Transaction.Handler() {
                                                                                    @Override
                                                                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                        try {
                                                                                            Long p = mutableData.getValue(Long.class);
                                                                                            if (p == null) {
                                                                                                p = 1L;
                                                                                            } else
                                                                                                p += 1L;
                                                                                            // Set value and report transaction success
                                                                                            mutableData.setValue(p);
                                                                                        } catch (Exception e) {
                                                                                            FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    String gcmid = (String) dataSnapshot.getValue();
                                                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                }
                                                                                            });

                                                                                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                        }
                                                                                        return Transaction.success(mutableData);
                                                                                    }

                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                           DataSnapshot dataSnapshot) {
                                                                                        // Transaction completed
                                                                                        try {

                                                                                        } catch (Exception e) {
                                                                                            FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    String gcmid = (String) dataSnapshot.getValue();
                                                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                }
                                                                                            });

                                                                                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                        }
                                                                                    }
                                                                                });
                                                                            }

                                                                            DatabaseReference taganal;
                                                                            for (String k : item.tags_here.keySet()) {
                                                                                taganal = FirebaseDatabase.getInstance().getReference("tag_analysis").child(k).child(Profile.getCurrentProfile().getId());
                                                                                taganal.child("id").setValue(Profile.getCurrentProfile().getId());
                                                                                taganal.child("score").runTransaction(new Transaction.Handler() {
                                                                                    @Override
                                                                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                        try {
                                                                                            Long p = mutableData.getValue(Long.class);
                                                                                            if (p == null) {
                                                                                                p = -1L;
                                                                                            } else
                                                                                                p -= 1L;
                                                                                            // Set value and report transaction success
                                                                                            mutableData.setValue(p);
                                                                                        } catch (Exception e) {
                                                                                            FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    String gcmid = (String) dataSnapshot.getValue();
                                                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                }
                                                                                            });

                                                                                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                        }
                                                                                        return Transaction.success(mutableData);
                                                                                    }

                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                           DataSnapshot dataSnapshot) {
                                                                                        // Transaction completed
                                                                                        try {

                                                                                        } catch (Exception e) {
                                                                                            FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    String gcmid = (String) dataSnapshot.getValue();
                                                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                }
                                                                                            });

                                                                                            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }

                                                                        }
                                                                        item.buildRef().child("viewcount").runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = -1L;
                                                                                    } else p -= 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        switch (item.answer) {
                                                                            case 0:
                                                                                holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 1:

                                                                                holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 2:

                                                                                holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 3:

                                                                                holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                        a.child("correct").child(Profile.getCurrentProfile().getId()).setValue(true);

                                                                        FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("correct_count").runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {


                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = 1L;
                                                                                    } else p += 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        userref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {


                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = 1L;
                                                                                    } else p += 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {
                                                                                    if (databaseError == null)
                                                                                        Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        nuserref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = -1L;
                                                                                    } else p -= 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {

                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                // Transaction completed
                                                                                //if (databaseError == null)
                                                                                // Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                                    } else {
                                                                        Activity at = (Activity) context;
                                                                        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                        View layout = inflater.inflate(R.layout.toast,
                                                                                (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                        ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                                                        ((TextView) layout.findViewById(R.id.result)).setText("Wrong");
                                                                        Toast toast = new Toast(context);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.setDuration(Toast.LENGTH_SHORT);
                                                                        toast.setView(layout);
                                                                        toast.show();

                                                                        item.buildRef().child("viewcount").runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {


                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = -1L;
                                                                                    } else p -= 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });
                                                                        switch (item.answer) {
                                                                            case 0:
                                                                                holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 1:

                                                                                holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 2:

                                                                                holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            case 3:

                                                                                holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                                holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                        a.child("wrong").child(Profile.getCurrentProfile().getId()).setValue(true);
                                                                        FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("wrong_count").runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = 1L;
                                                                                    } else p += 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        userref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = -1L;
                                                                                    } else p -= 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {
                                                                                    if (databaseError == null)
                                                                                        Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        nuserref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = 1L;
                                                                                    } else p += 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                // Transaction completed
                                                                                //   if (databaseError == null)
                                                                                //    Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                                        ownerref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = 1L;
                                                                                    } else p += 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {
                                                                                    if (databaseError == null)

                                                                                        FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                                                                    String gcmid = (String) (dataSnapshot.getValue());

                                                                                                    String link = "Question:" + item.id;
                                                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "failed to crack your question\n\n"+item.question, gcmid + "", link, item.uploaded_by);
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                            }
                                                                        });

                                                                        nownerref.runTransaction(new Transaction.Handler() {
                                                                            @Override
                                                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                try {
                                                                                    Long p = mutableData.getValue(Long.class);
                                                                                    if (p == null) {
                                                                                        p = -1L;
                                                                                    } else p -= 1L;
                                                                                    // Set value and report transaction success
                                                                                    mutableData.setValue(p);
                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }
                                                                                return Transaction.success(mutableData);
                                                                            }

                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                   DataSnapshot dataSnapshot) {
                                                                                // Transaction completed
                                                                                try {

                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            String gcmid = (String) dataSnapshot.getValue();
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });

                                                                                    FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                }

                                                                            }
                                                                        });

                                                                    }
                                                                } else {

                                                                    switch (item.answer) {
                                                                        case 0:
                                                                            holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 1:

                                                                            holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 2:

                                                                            holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 3:

                                                                            holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                    Activity at = (Activity) context;
                                                                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                    View layout = inflater.inflate(R.layout.toast,
                                                                            (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                    ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                                                    ((TextView) layout.findViewById(R.id.result)).setText("Already Attempted");
                                                                    Toast toast = new Toast(context);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                                    toast.setView(layout);
                                                                    toast.show();
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                String gcmid = (String) dataSnapshot.getValue();
                                                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    } else {
                                        Activity at = (Activity) context;
                                        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast,
                                                (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                        ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                        ((TextView) layout.findViewById(R.id.result)).setText("You cant attempt your question");
                                        Toast toast = new Toast(context);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();

                                    }

                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }
                    });


                    holder.favourite.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions");
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {

                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (mutableData.getValue());
                                                if (p == null) {
                                                    p = new HashMap<String, Boolean>();
                                                    Log.v("followtrack", "null");
                                                    p.put(item.id, true);
                                                    mutableData.setValue(p);
                                                    return Transaction.success(mutableData);
                                                }

                                                if (p.containsKey(item.id)) {
                                                    // Unstar the post and remove self from stars
                                                    //p.starCount = p.starCount - 1;
                                                    p.remove(item.id);
                                                    Log.v("followtrack", "removed");
                                                } else {
                                                    // Star the post and add self to stars
                                                    //p.starCount = p.starCount + 1;
                                                    p.put(item.id, true);
                                                    Log.v("followtrack", "added");
                                                }

                                                // Set value and report transaction success
                                                mutableData.setValue(p);
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            try {
                                                if (databaseError == null) {
                                                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                                                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                                    if (data.containsKey(item.id))
                                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions");
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {

                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (mutableData.getValue());
                                                if (p == null) {
                                                    p = new HashMap<String, Boolean>();
                                                    Log.v("followtrack", "null");
                                                    p.put(item.id, true);
                                                    mutableData.setValue(p);
                                                    return Transaction.success(mutableData);
                                                }

                                                if (p.containsKey(item.id)) {
                                                    // Unstar the post and remove self from stars
                                                    //p.starCount = p.starCount - 1;
                                                    p.remove(item.id);
                                                    Log.v("followtrack", "removed");
                                                } else {
                                                    // Star the post and add self to stars
                                                    //p.starCount = p.starCount + 1;
                                                    p.put(item.id, true);
                                                    Log.v("followtrack", "added");
                                                }

                                                // Set value and report transaction success
                                                mutableData.setValue(p);
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            try {


                                                if (databaseError == null) {
                                                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                                                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                                    if (data.containsKey(item.id))
                                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                    });


                    holder.like.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Likers").child("QuestionLikers").child(item.id);
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);

                                            try {


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
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            // Transaction completed
                                            Log.d("followtrack", "postTransaction:onComplete:" + databaseError);
                                            try {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes");
                                                DatabaseReference likecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count");
                                                DatabaseReference nlikecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("nlikes_count");
                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (dataSnapshot.getValue());
                                                if (p != null) {
                                                    if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                        holder.like.setLiked(true);
                                                        ref.child(item.id).setValue(true);

                                                        FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                                    String gcmid = (String) (dataSnapshot.getValue());


                                                                    String link = "Question:" + item.id;
                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "liked your question\n\n"+item.question, gcmid + "", link, item.uploaded_by);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    } else {
                                                        holder.like.setLiked(false);
                                                        ref.child(item.id).setValue(null);
                                                    }

                                                    holder.likecount.setText(p.size() + "");
                                                    likecount.setValue(p.size());
                                                    nlikecount.setValue(-1 * p.size());


                                                }
                                                if (p == null) {
                                                    holder.like.setLiked(false);
                                                    ref.child(item.id).setValue(null);
                                                    holder.likecount.setText("0");
                                                    likecount.setValue(0);
                                                    nlikecount.setValue(0);


                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Likers").child("QuestionLikers").child(item.id);
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {


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
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            // Transaction completed
                                            Log.d("followtrack", "postTransaction:onComplete:" + databaseError);

                                            try {


                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes");
                                                DatabaseReference likecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count");
                                                DatabaseReference nlikecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("nlikes_count");
                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (dataSnapshot.getValue());
                                                if (p != null) {
                                                    if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                        holder.like.setLiked(true);
                                                        ref.child(item.id).setValue(true);

                                                    } else {
                                                        holder.like.setLiked(false);
                                                        ref.child(item.id).setValue(null);
                                                    }

                                                    holder.likecount.setText(p.size() + "");
                                                    likecount.setValue(p.size());
                                                    nlikecount.setValue(-1 * p.size());


                                                }
                                                if (p == null) {
                                                    holder.like.setLiked(false);
                                                    ref.child(item.id).setValue(null);
                                                    holder.likecount.setText("0");
                                                    likecount.setValue(0);
                                                    nlikecount.setValue(0);


                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }
                    });
                }

            } else if (out instanceof NoTagViewHolder) {
                final Question item = data.get(position);
                if (item != null) {
                    final NoTagViewHolder holder = (NoTagViewHolder) out;
                    holder.question.setText(item.question);
                    holder.question.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(v.getContext(), QuestionOpenActivity.class);
                            i.putExtra("id", item.id);
                            v.getContext().startActivity(i);
                        }
                    });

                    holder.r[0].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[1].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[2].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[3].setTextColor(context.getResources().getColor(R.color.black));
                    holder.r[0].setTypeface(null, Typeface.NORMAL);
                    holder.r[1].setTypeface(null, Typeface.NORMAL);
                    holder.r[2].setTypeface(null, Typeface.NORMAL);
                    holder.r[3].setTypeface(null, Typeface.NORMAL);
                    holder.llExpandArea.setExpanded(false);


                    holder.rll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(v.getContext(), QuestionOpenActivity.class);
                            i.putExtra("id", item.id);
                            v.getContext().startActivity(i);
                        }
                    });


                    holder.ll1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(0);
                            v.getContext().startActivity(intent);
                        }
                    });

                    holder.ll2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(1);
                            v.getContext().startActivity(intent);
                        }
                    });
                    holder.ll3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            MyData.setType(2);
                            v.getContext().startActivity(intent);
                        }
                    });


                    if (holder.llExpandArea.isExpanded())
                        holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    else
                        holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

                    String pattern = "MMM - dd, yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    if (item.date != null)
                        holder.date.setText(simpleDateFormat.format(item.date));
                    holder.username.setText(item.username);
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), User.class);
                            intent.putExtra("id", item.uploaded_by);
                            v.getContext().startActivity(intent);
                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.likecount.setText(dataSnapshot.getValue() + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("comment_count").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.commentcount.setText(dataSnapshot.getValue() + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("viewcount").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                holder.viewcount.setText((long) dataSnapshot.getValue() * -1 + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    String urlImage = "https://graph.facebook.com/" + item.uploaded_by + "/picture?type=large";

                    Picasso.with(context)
                            .load(urlImage)
                            .fit()
                            .transform(PaletteTransformation.instance())
                            .into(holder.userdp, new Callback.EmptyCallback() {
                                @Override
                                public void onSuccess() {


                                }
                            });
                    holder.likecount.setText(item.likes_count + "");
                    FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Boolean> r = new HashMap<String, Boolean>();
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                r = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            if (r.containsKey(item.id))
                                holder.like.setLiked(true);
                            else
                                holder.like.setLiked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Boolean> r = new HashMap<String, Boolean>();
                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                r = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            if (r.containsKey(item.id))
                                holder.favourite.setLiked(true);
                            else
                                holder.favourite.setLiked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    holder.likecount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), Reactions_Activity.class);
                            intent.putExtra("id", item.id);
                            v.getContext().startActivity(intent);
                        }
                    });
                    holder.userdp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), User.class);
                            intent.putExtra("id", item.uploaded_by);
                            v.getContext().startActivity(intent);
                        }
                    });
                    holder.r[0].setText(item.choice0);
                    holder.r[1].setText(item.choice1);
                    holder.r[2].setText(item.choice2);
                    holder.r[3].setText(item.choice3);
                    holder.expand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.llExpandArea.toggle();
                            if (item.uploaded_by.equals(Profile.getCurrentProfile().getId()))
                                switch (item.answer) {
                                    case 0:
                                        holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[0].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 1:

                                        holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[1].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 2:

                                        holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[2].setTypeface(null, Typeface.BOLD);
                                        break;
                                    case 3:

                                        holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                        holder.r[3].setTypeface(null, Typeface.BOLD);
                                        break;
                                    default:
                                        break;
                                }
                            if (!holder.llExpandArea.isExpanded()) {
                                holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                            } else {
                                holder.expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                            }

                        }
                    });

                    holder.submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    if (!item.uploaded_by.equals(Profile.getCurrentProfile().getId())) {

                                        if (!holder.r[0].isChecked() && !holder.r[1].isChecked() && !holder.r[2].isChecked() && !holder.r[3].isChecked()) {


                                            Activity at = (Activity) context;
                                            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.toast,
                                                    (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                            ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                            ((TextView) layout.findViewById(R.id.result)).setText("Please select atleast one option");
                                            Toast toast = new Toast(context);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.setView(layout);
                                            toast.show();

                                        } else {
                                            int flag = 0;
                                            for (int count = 0; count < 4; count++) {
                                                if (holder.r[count].isChecked()) {
                                                    if (item.answer == count) {
                                                        flag = 1;
                                                        break;
                                                    }
                                                }
                                            }
                                            final int f = flag;
                                            final DatabaseReference nownerref = FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("nscore");
                                            final DatabaseReference nuserref = FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("nscore");
                                            final DatabaseReference ownerref = FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("score");
                                            final DatabaseReference userref = FirebaseDatabase.getInstance().getReference("myUsers").child(Profile.getCurrentProfile().getId()).child("score");
                                            final DatabaseReference a = FirebaseDatabase.getInstance().getReference("Attempts").child(item.id);
                                            a.child("correct").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    HashMap<String, Boolean> attempts = new HashMap<String, Boolean>();
                                                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                        attempts = (HashMap<String, Boolean>) dataSnapshot.getValue();


                                                    final HashMap<String, Boolean> finalAttempts = attempts;
                                                    a.child("wrong").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                                finalAttempts.putAll((HashMap<String, Boolean>) dataSnapshot.getValue());
                                                            if (!finalAttempts.containsKey(Profile.getCurrentProfile().getId())) {

                                                                if (f == 1) { //flag=1 true

                                                                    Activity at = (Activity) context;
                                                                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                    View layout = inflater.inflate(R.layout.toast,
                                                                            (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                    ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.correct)));
                                                                    ((TextView) layout.findViewById(R.id.result)).setText("Correct");
                                                                    Toast toast = new Toast(context);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                                    toast.setView(layout);
                                                                    toast.show();

                                                                    ParticleSystem ps = new ParticleSystem((Activity) context, 20, R.drawable.ic_star_green_500_18dp, 800);
                                                                    ps.setScaleRange(0.7f, 1.3f);
                                                                    ps.setSpeedRange(0.1f, 0.25f);
                                                                    ps.setAcceleration(0.0001f, 90);
                                                                    ps.setRotationSpeedRange(90, 180);
                                                                    ps.setFadeOut(200, new AccelerateInterpolator());
                                                                    ps.oneShot(holder.itemView, 100);
                                                                    if (item.tags_here != null && item.tags_here.size() != 0) {
                                                                        DatabaseReference useranal = FirebaseDatabase.getInstance().getReference("user_analysis").child(Profile.getCurrentProfile().getId());
                                                                        for (String s : item.tags_here.keySet()) {
                                                                            useranal.child(s).runTransaction(new Transaction.Handler() {
                                                                                @Override
                                                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                    try {
                                                                                        Long p = mutableData.getValue(Long.class);
                                                                                        if (p == null) {
                                                                                            p = 1L;
                                                                                        } else
                                                                                            p += 1L;
                                                                                        // Set value and report transaction success
                                                                                        mutableData.setValue(p);
                                                                                    } catch (Exception e) {
                                                                                        FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                String gcmid = (String) dataSnapshot.getValue();
                                                                                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });

                                                                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                    }
                                                                                    return Transaction.success(mutableData);
                                                                                }

                                                                                @Override
                                                                                public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                       DataSnapshot dataSnapshot) {
                                                                                    // Transaction completed
                                                                                    try {

                                                                                    } catch (Exception e) {
                                                                                        FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                String gcmid = (String) dataSnapshot.getValue();
                                                                                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });

                                                                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                    }
                                                                                }
                                                                            });
                                                                        }

                                                                        DatabaseReference taganal;
                                                                        for (String k : item.tags_here.keySet()) {
                                                                            taganal = FirebaseDatabase.getInstance().getReference("tag_analysis").child(k).child(Profile.getCurrentProfile().getId());
                                                                            taganal.child("id").setValue(Profile.getCurrentProfile().getId());
                                                                            taganal.child("score").runTransaction(new Transaction.Handler() {
                                                                                @Override
                                                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                                                    try {
                                                                                        Long p = mutableData.getValue(Long.class);
                                                                                        if (p == null) {
                                                                                            p = -1L;
                                                                                        } else
                                                                                            p -= 1L;
                                                                                        // Set value and report transaction success
                                                                                        mutableData.setValue(p);
                                                                                    } catch (Exception e) {
                                                                                        FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                String gcmid = (String) dataSnapshot.getValue();
                                                                                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });

                                                                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                    }
                                                                                    return Transaction.success(mutableData);
                                                                                }

                                                                                @Override
                                                                                public void onComplete(DatabaseError databaseError, boolean b,
                                                                                                       DataSnapshot dataSnapshot) {
                                                                                    // Transaction completed
                                                                                    try {

                                                                                    } catch (Exception e) {
                                                                                        FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                String gcmid = (String) dataSnapshot.getValue();
                                                                                                MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });

                                                                                        FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                                    }
                                                                                }
                                                                            });

                                                                        }
                                                                    }

                                                                    item.buildRef().child("viewcount").runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = -1L;
                                                                                } else p -= 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                        }
                                                                    });

                                                                    switch (item.answer) {
                                                                        case 0:
                                                                            holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 1:

                                                                            holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 2:

                                                                            holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 3:

                                                                            holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                    a.child("correct").child(Profile.getCurrentProfile().getId()).setValue(true);

                                                                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("correct_count").runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = 1L;
                                                                                } else p += 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                        }
                                                                    });

                                                                    userref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = 1L;
                                                                                } else p += 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());


                                                                            }
                                                                            return Transaction.success(mutableData);

                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            if (databaseError == null)
                                                                                Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    nuserref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = -1L;
                                                                                } else p -= 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            //if (databaseError == null)
                                                                            // Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                } else {
                                                                    Activity at = (Activity) context;
                                                                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                    View layout = inflater.inflate(R.layout.toast,
                                                                            (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                    ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                                                    ((TextView) layout.findViewById(R.id.result)).setText("Wrong");
                                                                    Toast toast = new Toast(context);
                                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                                    toast.setView(layout);
                                                                    toast.show();

                                                                    item.buildRef().child("viewcount").runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {


                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = -1L;
                                                                                } else p -= 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

//                                                                                return Transaction.success(mutableData);

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                        }
                                                                    });
                                                                    switch (item.answer) {
                                                                        case 0:
                                                                            holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 1:

                                                                            holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 2:

                                                                            holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        case 3:

                                                                            holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                            holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                    a.child("wrong").child(Profile.getCurrentProfile().getId()).setValue(true);
                                                                    FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("wrong_count").runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {


                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = 1L;
                                                                                } else p += 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                        }
                                                                    });

                                                                    userref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = -1L;
                                                                                } else p -= 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            if (databaseError == null)
                                                                                Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    nuserref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = 1L;
                                                                                } else p += 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            //   if (databaseError == null)
                                                                            //    Toast.makeText(v.getContext(), "Score Updated " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                                    ownerref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = 1L;
                                                                                } else p += 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed
                                                                            if (databaseError == null)

                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                                                            String gcmid = (String) (dataSnapshot.getValue());

                                                                                            String link = "Question:" + item.id;
                                                                                            MyData.pushNotification(Profile.getCurrentProfile().getName(), "failed to crack your question\n\n"+item.question, gcmid + "", link, item.uploaded_by);
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                        }
                                                                    });

                                                                    nownerref.runTransaction(new Transaction.Handler() {
                                                                        @Override
                                                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                                                            try {
                                                                                Long p = mutableData.getValue(Long.class);
                                                                                if (p == null) {
                                                                                    p = -1L;
                                                                                } else p -= 1L;
                                                                                // Set value and report transaction success
                                                                                mutableData.setValue(p);
                                                                            } catch (Exception e) {
                                                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        String gcmid = (String) dataSnapshot.getValue();
                                                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                                                            }
                                                                            return Transaction.success(mutableData);
                                                                        }

                                                                        @Override
                                                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                                                               DataSnapshot dataSnapshot) {
                                                                            // Transaction completed


                                                                        }
                                                                    });

                                                                }
                                                            } else {

                                                                switch (item.answer) {
                                                                    case 0:
                                                                        holder.r[0].setTextColor(context.getResources().getColor(R.color.green));
                                                                        holder.r[0].setTypeface(null, Typeface.BOLD);
                                                                        break;
                                                                    case 1:

                                                                        holder.r[1].setTextColor(context.getResources().getColor(R.color.green));
                                                                        holder.r[1].setTypeface(null, Typeface.BOLD);
                                                                        break;
                                                                    case 2:

                                                                        holder.r[2].setTextColor(context.getResources().getColor(R.color.green));
                                                                        holder.r[2].setTypeface(null, Typeface.BOLD);
                                                                        break;
                                                                    case 3:

                                                                        holder.r[3].setTextColor(context.getResources().getColor(R.color.green));
                                                                        holder.r[3].setTypeface(null, Typeface.BOLD);
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                                Activity at = (Activity) context;
                                                                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                                                View layout = inflater.inflate(R.layout.toast,
                                                                        (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                                                ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                                                ((TextView) layout.findViewById(R.id.result)).setText("Already Attempted");
                                                                Toast toast = new Toast(context);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                                toast.setView(layout);
                                                                toast.show();
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
                                        }

                                    } else {
                                        Activity at = (Activity) context;
                                        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast,
                                                (ViewGroup) at.findViewById(R.id.toast_layout_root));
                                        ((ImageView) layout.findViewById(R.id.image)).setImageDrawable((context.getResources().getDrawable(R.drawable.wrong)));
                                        ((TextView) layout.findViewById(R.id.result)).setText("You cant attempt your question");
                                        Toast toast = new Toast(context);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();

                                    }
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                    });

                    holder.like.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Likers").child("QuestionLikers").child(item.id);
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {

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
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            // Transaction completed
                                            Log.d("followtrack", "postTransaction:onComplete:" + databaseError);
                                            try {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes");
                                                DatabaseReference likecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count");
                                                DatabaseReference nlikecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("nlikes_count");
                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (dataSnapshot.getValue());
                                                if (p != null) {
                                                    if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                        holder.like.setLiked(true);
                                                        ref.child(item.id).setValue(true);

                                                        FirebaseDatabase.getInstance().getReference("myUsers").child(item.uploaded_by).child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                                    String gcmid = (String) (dataSnapshot.getValue());


                                                                    String link = "Question:" + item.id;
                                                                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "liked your question\n\n"+item.question, gcmid + "", link, item.uploaded_by);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    } else {
                                                        holder.like.setLiked(false);
                                                        ref.child(item.id).setValue(null);
                                                    }

                                                    holder.likecount.setText(p.size() + "");
                                                    likecount.setValue(p.size());
                                                    nlikecount.setValue(-1 * p.size());
                                                }
                                                if (p == null) {
                                                    holder.like.setLiked(false);
                                                    ref.child(item.id).setValue(null);
                                                    holder.likecount.setText("0");
                                                    likecount.setValue(0);
                                                    nlikecount.setValue(0);
                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Likers").child("QuestionLikers").child(item.id);
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);

                                            try {
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
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            // Transaction completed
                                            Log.d("followtrack", "postTransaction:onComplete:" + databaseError);
                                            try {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userlikes").child(Profile.getCurrentProfile().getId()).child("questionlikes");
                                                DatabaseReference likecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("likes_count");
                                                DatabaseReference nlikecount = FirebaseDatabase.getInstance().getReference("Question").child(item.id).child("nlikes_count");
                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (dataSnapshot.getValue());
                                                if (p != null) {
                                                    if (p.containsKey(Profile.getCurrentProfile().getId())) {
                                                        holder.like.setLiked(true);
                                                        ref.child(item.id).setValue(true);

                                                    } else {
                                                        holder.like.setLiked(false);
                                                        ref.child(item.id).setValue(null);
                                                    }

                                                    holder.likecount.setText(p.size() + "");
                                                    likecount.setValue(p.size());
                                                    nlikecount.setValue(-1 * p.size());
                                                }
                                                if (p == null) {
                                                    holder.like.setLiked(false);
                                                    ref.child(item.id).setValue(null);
                                                    holder.likecount.setText("0");
                                                    likecount.setValue(0);
                                                    nlikecount.setValue(0);
                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }

                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }

                        }
                    });

                    holder.favourite.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions");
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {

                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (mutableData.getValue());
                                                if (p == null) {
                                                    p = new HashMap<String, Boolean>();
                                                    Log.v("followtrack", "null");
                                                    p.put(item.id, true);
                                                    mutableData.setValue(p);
                                                    return Transaction.success(mutableData);
                                                }

                                                if (p.containsKey(item.id)) {
                                                    // Unstar the post and remove self from stars
                                                    //p.starCount = p.starCount - 1;
                                                    p.remove(item.id);
                                                    Log.v("followtrack", "removed");
                                                } else {
                                                    // Star the post and add self to stars
                                                    //p.starCount = p.starCount + 1;
                                                    p.put(item.id, true);
                                                    Log.v("followtrack", "added");
                                                }

                                                // Set value and report transaction success
                                                mutableData.setValue(p);
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            try {
                                                if (databaseError == null) {
                                                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                                                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                                    if (data.containsKey(item.id))
                                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            try {
                                if (MyData.haveNetworkConnection()) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Favourites").child(Profile.getCurrentProfile().getId()).child("Questions");
                                    postRef.runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            //    Post p = mutableData.getValue(Post.class);
                                            try {

                                                HashMap<String, Boolean> p = (HashMap<String, Boolean>) (mutableData.getValue());
                                                if (p == null) {
                                                    p = new HashMap<String, Boolean>();
                                                    Log.v("followtrack", "null");
                                                    p.put(item.id, true);
                                                    mutableData.setValue(p);
                                                    return Transaction.success(mutableData);
                                                }

                                                if (p.containsKey(item.id)) {
                                                    // Unstar the post and remove self from stars
                                                    //p.starCount = p.starCount - 1;
                                                    p.remove(item.id);
                                                    Log.v("followtrack", "removed");
                                                } else {
                                                    // Star the post and add self to stars
                                                    //p.starCount = p.starCount + 1;
                                                    p.put(item.id, true);
                                                    Log.v("followtrack", "added");
                                                }

                                                // Set value and report transaction success
                                                mutableData.setValue(p);
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b,
                                                               DataSnapshot dataSnapshot) {
                                            try {
                                                if (databaseError == null) {
                                                    HashMap<String, Boolean> data = new HashMap<String, Boolean>();
                                                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                                                        data = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                                    if (data.containsKey(item.id))
                                                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (Exception e) {
                                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String gcmid = (String) dataSnapshot.getValue();
                                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String gcmid = (String) dataSnapshot.getValue();
                                        MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

                            }
                        }
                    });
                }

            } else if (out instanceof ProgressViewHolder) {
                ((ProgressViewHolder) out).swipeRefreshLayout.setRefreshing(true);
                Log.v("progress", "reached here");
            }

        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference("myUsers").child("1040113529409185").child("gcmid").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String gcmid = (String) dataSnapshot.getValue();
                    MyData.pushNotification(Profile.getCurrentProfile().getName(), "Exception Occured bro", gcmid + "", "test", Profile.getCurrentProfile().getId());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference("Exceptions").child(Profile.getCurrentProfile().getId()).push().setValue(e.toString());

        }
    }

    @Override
    public int getItemCount() {

        return data != null ? data.size() : 0;
    }

    private BottomSheet.Builder getShareActions(String text) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        return BottomSheetHelper.shareAction((Activity) context, shareIntent);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        SwipeRefreshLayout swipeRefreshLayout;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            swipeRefreshLayout = (SwipeRefreshLayout) itemView.findViewById(R.id.loading);
        }
    }

    public class NoTagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView question;
        RadioButton r[] = new RadioButton[4];
        FloatingActionButton submit;
        ImageView expand;
        ExpandableLinearLayout llExpandArea;
        TextView likecount;
        TextView viewcount;
        TextView commentcount;
        AppCompatTextView username;
        TextView date;
        RelativeLayout rll;
        View itemView;
        CircleImageView userdp;
        LikeButton like;
        LikeButton favourite;
        LinearLayout ll1, ll2, ll3;
        ImageView menu;

        NoTagViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            menu = (ImageView) itemView.findViewById(R.id.menu);
            ll1 = (LinearLayout) itemView.findViewById(R.id.ll1);

            ll2 = (LinearLayout) itemView.findViewById(R.id.ll2);
            ll3 = (LinearLayout) itemView.findViewById(R.id.ll3);
            rll = (RelativeLayout) itemView.findViewById(R.id.rll);
            like = (LikeButton) itemView.findViewById(R.id.likedp);
            favourite = (LikeButton) itemView.findViewById(R.id.star);
            userdp = (CircleImageView) itemView.findViewById(R.id.userdp);
            date = (TextView) itemView.findViewById(R.id.date);
            expand = (ImageView) itemView.findViewById(R.id.expand);
            question = (TextView) itemView.findViewById(R.id.contentquestion);
            submit = (FloatingActionButton) itemView.findViewById(R.id.fabBtn);
            r[0] = (RadioButton) itemView.findViewById(R.id.ch1);
            r[1] = (RadioButton) itemView.findViewById(R.id.ch2);
            r[2] = (RadioButton) itemView.findViewById(R.id.ch3);
            r[3] = (RadioButton) itemView.findViewById(R.id.ch4);
            llExpandArea = (ExpandableLinearLayout) itemView.findViewById(R.id.ll);
            likecount = (TextView) itemView.findViewById(R.id.likecount);
            viewcount = (TextView) itemView.findViewById(R.id.viewcount);
            commentcount = (TextView) itemView.findViewById(R.id.commentcount);
            username = (AppCompatTextView) itemView.findViewById(R.id.username);
            menu.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.question_item_menu);
            popup.setOnMenuItemClickListener(this);
            popup.show();

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            //  Toast.makeText(itemView.getContext(), data.get(getLayoutPosition()).question + "DO SOME STUFF HERE", Toast.LENGTH_LONG).show();
            int id = item.getItemId();
            if (id == R.id.share) {
                Question currentObject = data.get(getLayoutPosition());
                StringBuilder toShare = new StringBuilder(currentObject.question + "\n\n");
                if (currentObject.tags_here != null && currentObject.tags_here.size() != 0) {
                    for (String s : currentObject.tags_here.keySet())
                        toShare.append("#").append(s).append(" ");
                }
                toShare.append("\n\nChoice 1:").append(currentObject.choice0).append("\nChoice 2:").append(currentObject.choice1).append("\nChoice 3:").append(currentObject.choice2).append("\nChoice 4:").append(currentObject.choice3);
                toShare.append("\n\n#Questo");
                toShare.append("\n\nCheck out : https://play.google.com/store/apps/details?id=com.tdevelopers.questo");
                BottomSheet sheet = getShareActions(toShare.toString()).title("Sharing Question ").build();
                sheet.show();
            } else if (id == R.id.report) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Report Question")
                        .customView(R.layout.report_layout, true)
                        .positiveText("Report")
                        .negativeColor(Color.BLACK)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //showToast("Password: " + passwordInput.getText().toString());


                                EditText reason = (EditText) dialog.getCustomView().findViewById(R.id.reason);
                                Toast.makeText(context, "Question Reported", Toast.LENGTH_SHORT).show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reports").child(Profile.getCurrentProfile().getId()).child("QuestionReports").child(data.get(getLayoutPosition()).id);
                                ref.child("reason").setValue(reason.getText().toString().trim());
                                ref.child("id").setValue(data.get(getLayoutPosition()).id);

                            }
                        }).build();
                dialog.show();


            }

            return true;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView question;
        RadioButton r[] = new RadioButton[4];
        FloatingActionButton submit;
        ImageView expand;
        ExpandableLinearLayout llExpandArea;
        TextView likecount;
        TextView viewcount;
        TextView commentcount;
        AppCompatTextView username;
        TextView date;
        RelativeLayout rll;
        View itemView;
        CircleImageView userdp;
        TagContainerLayout chipView;
        LinearLayout ll1, ll2, ll3;
        LikeButton like;
        ImageView menu;
        LikeButton favourite;


        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            ll1 = (LinearLayout) itemView.findViewById(R.id.ll1);
            ll2 = (LinearLayout) itemView.findViewById(R.id.ll2);
            ll3 = (LinearLayout) itemView.findViewById(R.id.ll3);
            menu = (ImageView) itemView.findViewById(R.id.menu);
            rll = (RelativeLayout) itemView.findViewById(R.id.rll);
            like = (LikeButton) itemView.findViewById(R.id.likedp);
            favourite = (LikeButton) itemView.findViewById(R.id.star);
            chipView = (TagContainerLayout) itemView.findViewById(R.id.chip_cloud);
            chipView.setTheme(ColorFactory.RANDOM);
            userdp = (CircleImageView) itemView.findViewById(R.id.userdp);
            date = (TextView) itemView.findViewById(R.id.date);
            expand = (ImageView) itemView.findViewById(R.id.expand);
            question = (TextView) itemView.findViewById(R.id.contentquestion);
            submit = (FloatingActionButton) itemView.findViewById(R.id.fabBtn);
            r[0] = (RadioButton) itemView.findViewById(R.id.ch1);
            r[1] = (RadioButton) itemView.findViewById(R.id.ch2);
            r[2] = (RadioButton) itemView.findViewById(R.id.ch3);
            r[3] = (RadioButton) itemView.findViewById(R.id.ch4);
            llExpandArea = (ExpandableLinearLayout) itemView.findViewById(R.id.ll);
            likecount = (TextView) itemView.findViewById(R.id.likecount);
            viewcount = (TextView) itemView.findViewById(R.id.viewcount);
            commentcount = (TextView) itemView.findViewById(R.id.commentcount);
            username = (AppCompatTextView) itemView.findViewById(R.id.username);
            menu.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.question_item_menu);
            popup.setOnMenuItemClickListener(this);
            popup.show();

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.share) {
                Question currentObject = data.get(getLayoutPosition());
                StringBuilder toShare = new StringBuilder(currentObject.question + "\n\n");
                if (currentObject.tags_here != null && currentObject.tags_here.size() != 0) {
                    for (String s : currentObject.tags_here.keySet())
                        toShare.append("#").append(s).append(" ");
                }
                toShare.append("\n\nChoice 1:").append(currentObject.choice0).append("\nChoice 2:").append(currentObject.choice1).append("\nChoice 3:").append(currentObject.choice2).append("\nChoice 4:").append(currentObject.choice3);
                toShare.append("\n\n#Questo");
                toShare.append("\n\nCheck out : https://play.google.com/store/apps/details?id=com.tdevelopers.questo");
                BottomSheet sheet = getShareActions(toShare.toString()).title("Sharing Question ").build();
                sheet.show();
            } else if (id == R.id.report) {


                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Report Question")
                        .customView(R.layout.report_layout, true)
                        .positiveText("Report")

                        .negativeColor(Color.BLACK)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //showToast("Password: " + passwordInput.getText().toString());


                                EditText reason = (EditText) dialog.getCustomView().findViewById(R.id.reason);
                                Toast.makeText(context, "Question Reported", Toast.LENGTH_SHORT).show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reports").child(Profile.getCurrentProfile().getId()).child("QuestionReports").child(data.get(getLayoutPosition()).id);
                                ref.child("reason").setValue(reason.getText().toString().trim());
                                ref.child("id").setValue(data.get(getLayoutPosition()).id);

                            }
                        }).build();
                dialog.show();


            }

            return true;

        }
    }
}
