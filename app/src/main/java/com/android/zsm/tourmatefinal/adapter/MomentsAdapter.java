package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.model.Moments;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsHoder> {

    private ArrayList<Moments> list;
    private Context context;
    public MomentsAdapter(ArrayList<Moments> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MomentsHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.moments_items, parent, false);
        MomentsHoder momentsHoder = new MomentsHoder(view);
        return momentsHoder;
    }

    @Override
    public void onBindViewHolder(MomentsHoder holder, int position) {
        Moments mylist = list.get(position);
        holder.imageCaptionTextView.setText(mylist.getCaptions());
        Picasso.with(context).load(mylist.getPhotourl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try {
            if (list.size() == 0) {
                arr = 0;
            } else {
                arr = list.size();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    class MomentsHoder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView imageNameTextView, imageCaptionTextView;
        public MomentsHoder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageCaptionTextView = (TextView) itemView.findViewById(R.id.ImageCaptionTextView);
        }
    }
}


