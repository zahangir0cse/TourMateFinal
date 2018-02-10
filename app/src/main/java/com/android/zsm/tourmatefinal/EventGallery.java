package com.android.zsm.tourmatefinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.adapter.GalleryAdapter;
import com.android.zsm.tourmatefinal.model.Events;
import com.android.zsm.tourmatefinal.model.Moments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class EventGallery extends AppCompatActivity {
    // Creating DatabaseReference.
    FirebaseDatabase database;
    DatabaseReference myRef;
    String eventid;
    String eventtitle;
    Events event;
    private TextView show;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    GalleryAdapter adapter;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<Moments> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_gallery);
        Intent in = getIntent();
        event = (Events) in.getSerializableExtra("obj");
        eventid = event.getEventID();
        list = new ArrayList<>();
        eventtitle = event.getEventName();
        recyclerView = findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Moments");
        Toolbar toolbar = findViewById(R.id.toolbarEventGallery);
        toolbar.setTitle(eventtitle);
        setSupportActionBar(toolbar);
        show = findViewById(R.id.showmessageEventGallery);
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(EventGallery.this);

// Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Gallery Image.");

// Showing progress dialog.
        progressDialog.show();
        getAllgalleryPhoto();

    }

    public void getAllgalleryPhoto() {
        Toast.makeText(this, "eventis: " + eventid, Toast.LENGTH_SHORT).show();
        Query budgequery = myRef.orderByChild("eventid").equalTo(eventid);
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Moments value = d.getValue(Moments.class);
                    list.add(value);
                }
                Toast.makeText(EventGallery.this, "list size " + list.size(), Toast.LENGTH_SHORT).show();
                if (list.size() > 0) {
                    Toast.makeText(EventGallery.this, "list size inside " + list.size(), Toast.LENGTH_SHORT).show();
                    adapter = new GalleryAdapter(list, EventGallery.this);
                    RecyclerView.LayoutManager recyce = new GridLayoutManager(EventGallery.this, 2);
                    recyclerView.setLayoutManager(recyce);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);

                    show.setVisibility(View.INVISIBLE);
                    // Hiding the progress dialog.
                    progressDialog.dismiss();
                } else {
                    show.setText("You have no Photo yet. Please upload photo to event");
                    show.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
