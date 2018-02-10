package com.android.zsm.tourmatefinal;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zsm.tourmatefinal.adapter.EventAdapter;
import com.android.zsm.tourmatefinal.model.Events;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventList extends AppCompatActivity {
    private EditText eventNameET, eventBudgetET, eventDateTV;
    private TextView show;
    ListView eventList;
    DatabaseReference root;
    private EventAdapter eventAdapter;
    public RecyclerView mRecyclerView;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Button saveBt;
    private FloatingActionButton fab;
    public String updatedeventid = null;
    private AlertDialog.Builder b;
    ArrayList<Events> events = new ArrayList<Events>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbarEventList);
        toolbar.setTitle("Event List");
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recyclerViewEventList);
        show = findViewById(R.id.showmessageEventList);
        //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        root = FirebaseDatabase.getInstance().getReference("Events");
        root.keepSynced(true);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String eventTitle = intent.getStringExtra(SearchManager.QUERY);
            //call api using city name
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(eventTitle, null);
            //Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            getSearchEventList(eventTitle);
            //getCurentWeatherinformationByArea(cityName);
        } else {
            getAllEventList();
        }


    }

    public void getAllEventList() {
        Query budgequery = root.orderByChild("userID").equalTo(user.getUid());
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Events ev = d.getValue(Events.class);
                    events.add(ev);
                }
                if (events.size() > 0) {
                    eventAdapter = new EventAdapter(EventList.this, events);
                    LinearLayoutManager llm = new LinearLayoutManager(EventList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(eventAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("You have no event yet. Please create event");
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventList.this, AddEvent.class));
            }
        });
    }

    public void getSearchEventList(String ename) {
        Query budgequery = root.orderByChild("userID").equalTo(user.getUid());
        budgequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Events ev = d.getValue(Events.class);
                    events.add(ev);
                }
                if (events.size() > 0) {
                    eventAdapter = new EventAdapter(EventList.this, events);
                    LinearLayoutManager llm = new LinearLayoutManager(EventList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(eventAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("No result found ");
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventList.this, AddEvent.class));

            }
        });


    }

    public void saveEvent(View view) {
        String ename = eventNameET.getText().toString();
        String eventdate = eventDateTV.getText().toString();
        double budget = Double.parseDouble(eventBudgetET.getText().toString());
        String eventId = root.push().getKey();
        String userid = user.getUid();
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        switch (buttonText) {
            case "Save":
                Events ev = new Events(eventId, userid, ename, budget, eventdate);
                root.child(eventId).setValue(ev);
                break;
            case "Update":
                Events evu = new Events(updatedeventid, userid, ename, budget, eventdate);
                root.child(updatedeventid).setValue(evu);
                saveBt.setText("Save");
                break;
        }
    }

    public void gotoweather(View view) {
        startActivity(new Intent(EventList.this, WeatherInfo.class));
        finish();
    }

    public void deleteRecord(Events eve) {
        final String eventId = eve.getEventID();
        b = new AlertDialog.Builder(EventList.this);
        b.setTitle("Delete Event");
        b.setMessage("Are you sure delete this event?");
        b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteConfirm(eventId);
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        b.show();
    }

    public void deleteConfirm(String eid) {
        root.child(eid).removeValue();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("ⓘ Exit ! From " + getString(R.string.app_name));
        alertDialogBuilder
                .setMessage(Html.fromHtml("<p style='text-align:center;'>Please Chose what do you want </p><h3 style='text-align:center;'>Click Yes to Exit !</h4>"))
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new Utility().onCreateOptionsMenuUtil(menu, this, this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        new Utility().onPrepareOptionsMenuUtil(menu, user);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new Utility().onOptionSelectedUtil(item, this, this, this);
        return super.onOptionsItemSelected(item);
    }
}
