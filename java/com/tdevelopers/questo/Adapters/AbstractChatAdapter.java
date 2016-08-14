package com.tdevelopers.questo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tdevelopers.questo.Objects.AbsModel;
import com.tdevelopers.questo.Opens.ChatOpenActivity;
import com.tdevelopers.questo.R;
import com.tdevelopers.questo.libraries.PaletteTransformation;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AbstractChatAdapter extends RecyclerView.Adapter<AbstractChatAdapter.AbsHolder> {

    Context context;
    ArrayList<AbsModel> data;

    public AbstractChatAdapter(ArrayList<AbsModel> data) {
        this.data = data;


    }

    @Override
    public AbsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.abschat_tile, parent, false);
        context = view.getContext();
        return new AbsHolder(view);
    }

    @Override
    public void onBindViewHolder(final AbsHolder holder, int position) {

        final AbsModel current = data.get(position);
        if (current != null) {
            holder.usertext.setText(current.content);
            String urlImage = "https://graph.facebook.com/" + current.userid + "/picture?type=large";
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ChatOpenActivity.class);
                    i.putExtra("id", current.userid);
                    v.getContext().startActivity(i);
                }
            });
            Picasso.with(context)
                    .load(urlImage)
                    .fit()
                    .transform(PaletteTransformation.instance())
                    .into(holder.userdp, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {


                        }
                    });

            FirebaseDatabase.getInstance().getReference("myUsers").child(current.userid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null)
                        holder.username.setText(dataSnapshot.getValue() + "");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class AbsHolder extends RecyclerView.ViewHolder {
        CircleImageView userdp;
        TextView usertext;
        TextView username;

        public AbsHolder(View itemView) {
            super(itemView);
            userdp = (CircleImageView) itemView.findViewById(R.id.userdp);
            usertext = (TextView) itemView.findViewById(R.id.usertext);
            username = (TextView) itemView.findViewById(R.id.username);

        }
    }
}
