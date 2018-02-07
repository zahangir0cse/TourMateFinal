package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.model.Moments;
import com.squareup.picasso.Picasso;
import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyHoder> {

    private List<Moments> list;
    private Context context;

    public GalleryAdapter(List<Moments> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card, parent, false);
        MyHoder viewHolder = new MyHoder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        Moments moment = list.get(position);
        Picasso.with(context).load(moment.getPhotourl()).into(holder.gallerythumb);
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
        } catch (Exception e) {
        }
        return arr;
    }

    class MyHoder extends RecyclerView.ViewHolder {
        ImageView gallerythumb;
        public MyHoder(View itemView) {
            super(itemView);
            gallerythumb = itemView.findViewById(R.id.thumbnail);
        }
    }

}
