package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.FriendList;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.model.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>{
    private Context context;
    private ArrayList<Friends> friends;
    private int count = 0;


    public FriendsAdapter(Context context, ArrayList<Friends> friends){
        this.context = context;
        this.friends = friends;

    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_friend_row,parent,false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        final Friends friend = friends.get(position);
        holder.friendname.setText(friends.get(position).getFriendName());
        holder.friendphone.setText("Ph. "+String.valueOf(friends.get(position).getFriendPhone()));
        holder.friendemail.setText( "E.: "+friends.get(position).getFriendEmail());
        holder.pcall.setImageResource(R.drawable.pcalls);
        holder.psms.setImageResource(R.drawable.smss);
        holder.semail.setImageResource(R.drawable.emails);


        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, friend);
            }
        });

        holder.friendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, friend);
            }
        });

        holder.friendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, friend);
            }
        });

        holder.friendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, friend);
            }
        });
    }

    private void onClickListenerUtil(final View v, FriendViewHolder holder, final Friends friend){
        PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
        popupMenu.inflate(R.menu.option_menu2);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit:
                        try {
                            ((FriendList) v.getContext()).editFriendDialog(friend);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.delete:
                        try {
                            ((FriendList) v.getContext()).deleteRecord(friend);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference root;
        private EventAdapter eventAdapter;
        FirebaseUser user;
        private FirebaseAuth auth;
        TextView friendname ;
        TextView friendemail ;
        TextView friendphone;
        TextView optionDigit;
        ImageView pcall;
        ImageView psms;
        ImageView semail;
        public FriendViewHolder(View itemView) {
            super(itemView);
            friendname = itemView.findViewById(R.id.friendname);
            friendemail = itemView.findViewById(R.id.email);
            friendphone = itemView.findViewById(R.id.phone);
            optionDigit = itemView.findViewById(R.id.optionDigit);
            pcall = itemView.findViewById(R.id.phonecall);
            psms = itemView.findViewById(R.id.sendsms);
            semail = itemView.findViewById(R.id.sendemail);
        }
    }
}
