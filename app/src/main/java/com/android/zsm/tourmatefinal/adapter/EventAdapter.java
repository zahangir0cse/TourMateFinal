package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.AddEvent;
import com.android.zsm.tourmatefinal.EventDetail;
import com.android.zsm.tourmatefinal.EventList;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;



public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
    private Context context;
    private ArrayList<Events> events;
    private int count = 0;


    public EventAdapter(Context context, ArrayList<Events> events){
        this.context = context;
        this.events = events;

    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_event_row,parent,false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final Events event = events.get(position);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy" );
        String dstring="";
        Date edate = null;
        try {
            edate = format.parse(events.get(position).getEventDate());
            long timeInMilliseconds = edate.getTime();
            //Toast.makeText(context,"converted "+timeInMilliseconds,Toast.LENGTH_LONG).show();
            long msDiff = timeInMilliseconds - Calendar.getInstance().getTimeInMillis();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff) + 1 ;

            if(daysDiff   < 0) {
                dstring = "Event is past";
            } else if(daysDiff   == 1) {
                dstring = "Today Left";
            } else if(daysDiff   == 1) {
                dstring = daysDiff + " Day Left";
            } else if(daysDiff   > 1) {
                dstring = daysDiff + " Days Left";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.eventName.setText(events.get(position).getEventName());
        holder.eventBudget.setText("Budget: Tk. "+String.valueOf(events.get(position).getBudget())+"0");
        holder.eventDate.setText( "Start on: " +events.get(position).getEventDate());
        holder.createDate.setText( "Created on: "+events.get(position).getCreateDate());
        holder.duration.setText(dstring);

        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                 PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                 popupMenu.inflate(R.menu.option_menu);
                 popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         switch (item.getItemId()){
                             case R.id.detail :
                                 Intent i = new Intent(context, EventDetail.class);
                                 i.putExtra("obj", event);
                                 context.startActivity(i);
                                 break;
                             case R.id.edit:
                                 Intent intent = new Intent(context, AddEvent.class);
                                 intent.putExtra("obj", event);
                                 context.startActivity(intent);
                                 break;
                             case R.id.delete:
                                 try {
                                     ((EventList) v.getContext()).deleteRecord(event);
                                 } catch (Exception e) {
                                     // ignore
                                 }
                                 break;

                         }


                         return false;
                     }
                 });
                 popupMenu.show();
            }
        });

        holder.eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetail.class);
                i.putExtra("obj", event);
                context.startActivity(i);
            }
        });
        holder.eventBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetail.class);
                i.putExtra("obj", event);
                context.startActivity(i);
            }
        });
        holder.eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetail.class);
                i.putExtra("obj", event);
                context.startActivity(i);
            }
        });
        holder.createDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetail.class);
                i.putExtra("obj", event);
                context.startActivity(i);
            }
        });
        holder.duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EventDetail.class);
                i.putExtra("obj", event);
                context.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private DatabaseReference root;
        private EventAdapter eventAdapter;
        private FirebaseUser user;
        private FirebaseAuth auth;
        private TextView eventName ;
        private TextView eventBudget ;
        private TextView eventDate ;
        private TextView optionDigit;
        private TextView createDate;
        private TextView duration;
        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.roweventname);
            eventBudget = itemView.findViewById(R.id.budgetrow);
            eventDate = itemView.findViewById(R.id.eventDaterow);
            createDate = itemView.findViewById(R.id.createtDaterow);
            duration = itemView.findViewById(R.id.leftdaay);
            optionDigit = itemView.findViewById(R.id.optionDigit);

        }

    }
}
