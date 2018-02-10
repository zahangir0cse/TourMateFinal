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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.adapter.FriendsAdapter;
import com.android.zsm.tourmatefinal.model.Events;
import com.android.zsm.tourmatefinal.model.Friends;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FriendList extends AppCompatActivity {

    private TextView show;
    DatabaseReference root;
    private FriendsAdapter friendsAdapter;
    public RecyclerView mRecyclerView;
    FirebaseUser user;
    private FirebaseAuth auth;
    private Events event;
    private FloatingActionButton fab;
    private  AlertDialog.Builder b;
    private boolean validst = false;
    private  String todate;
    private  double restbudget ;
    private double eventbudget;
    public  double eventexpense=0;
    ArrayList<Friends> friends = new ArrayList <>( );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbarFriend);

        mRecyclerView = findViewById(R.id.recyclerViewFriend);
        show = findViewById(R.id.showmessageFriend);
        //  FirebaseDatabase.getInstance ().setPersistenceEnabled ( true );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        event= (Events) intent.getSerializableExtra("obj");
        eventbudget = event.getBudget();
        root = FirebaseDatabase.getInstance ().getReference ("Friends");
        root.keepSynced ( true );
        toolbar.setTitle(event.getEventName()+ " Friends List");
        setSupportActionBar(toolbar);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String eventTitle = intent.getStringExtra(SearchManager.QUERY);
            //call api using city name
            SearchRecentSuggestions searchRecentSuggestions =
                    new SearchRecentSuggestions(this,
                            CityNameSuggestions.AUTHORITY,
                            CityNameSuggestions.MODE);
            searchRecentSuggestions.saveRecentQuery(eventTitle, null);
            // Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            // getSearchEventList(eventTitle);
            //getCurentWeatherinformationByArea(cityName);
        } else {
            getAllFriendList();
        }






    }
    public  void  getAllFriendList() {
        Query budgequery = root.orderByChild( "eventid" ).equalTo(event.getEventID());
        budgequery.addValueEventListener ( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear ();
                for (DataSnapshot d: dataSnapshot.getChildren ()){
                    Friends ev= d.getValue (Friends.class);
                    friends.add ( ev );

                }
                if(friends.size() > 0){
                    friendsAdapter = new FriendsAdapter(FriendList.this ,friends);
                    LinearLayoutManager llm = new LinearLayoutManager(FriendList.this);
                    //GridLayoutManager glm = new GridLayoutManager(context,1);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(llm);
                    mRecyclerView.setAdapter(friendsAdapter);
                    show.setVisibility(View.INVISIBLE);
                } else {
                    show.setText("You have no friend add. Please add friend");
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
        fab = findViewById(R.id.fabFriend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog();
            }
        });


    }

    private void showAddFriendDialog() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname  = alertLayout.findViewById(R.id.fname);
        final EditText frphone  = alertLayout.findViewById(R.id.fphone);
        final EditText fremail  =  alertLayout.findViewById(R.id.femail);
        Button addfriend = alertLayout.findViewById(R.id.addBtn);
        Button cancel = alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = frname.getText().toString();
                String fp = frphone.getText().toString();
                String fe = fremail.getText().toString();

                String frid = root.push().getKey();
                String userid = user.getUid ();
                if (fn.length() == 0) {
                    frname.setError("Enter Friend Name");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fp.length() == 0) {
                    frphone.setError("Enter Friend Phone No");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fe.length() == 0) {
                    fremail.setError("Enter Friend Email");
                    validst = false;
                } else {
                    validst = true;
                }

                if (validst) {

                    Friends frn = new Friends (frid, event.getEventID(),fn,fp, fe);
                    root.child ( frid ).setValue ( frn );
                    Toast.makeText(FriendList.this, "Friend add success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));

                    ad.dismiss();
                    ad.cancel();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                ad.cancel();
            }
        });
        ad.show();
    }

    public void editFriendDialog( Friends friend) {
        final Friends frnd = friend;
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Friend");
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText frname  = alertLayout.findViewById(R.id.fname);
        final EditText frphone  = alertLayout.findViewById(R.id.fphone);
        final EditText fremail  = alertLayout.findViewById(R.id.femail);
        Button addfriend = alertLayout.findViewById(R.id.addBtn);
        Button cancel = alertLayout.findViewById(R.id.cancel);
        frname.setText(frnd.getFriendName());
        frphone.setText(frnd.getFriendPhone());
        fremail.setText(frnd.getFriendEmail());
        addfriend.setText("Update");
        final AlertDialog ad = builder.create();

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = frname.getText().toString();
                String fp = frphone.getText().toString();
                String fe = fremail.getText().toString();


                if (fn.length() == 0) {
                    frname.setError("Enter Friend Name");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fp.length() == 0) {
                    frphone.setError("Enter Friend Phone No");
                    validst = false;
                } else {
                    validst = true;
                }
                if (fe.length() == 0) {
                    fremail.setError("Enter Friend Email");
                    validst = false;
                } else {
                    validst = true;
                }

                if (validst) {

                    Friends frn = new Friends (frnd.getFriendId(), event.getEventID(),fn,fp, fe);
                    root.child ( frnd.getFriendId() ).setValue ( frn );
                    Toast.makeText(FriendList.this, "Friend Update success", Toast.LENGTH_SHORT).show();
                    //  startActivity(new Intent(EventDetail.this, EventDetail.class));

                    ad.dismiss();
                    ad.cancel();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
                ad.cancel();
            }
        });
        ad.show();
    }

    public void gotoweather(View view) {
        startActivity(new Intent (FriendList.this,WeatherInfo.class));
        finish();
    }
    public  void deleteRecord(Friends eve) {
        final String exid = eve.getFriendId ();
        b = new AlertDialog.Builder(FriendList.this);
        b.setTitle("Remove Friend");
        b.setMessage("Are you sure remove this friend?");
        b.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                deleteconfirm(exid);
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        b.show();
    }
    public void deleteconfirm(String eid)
    {
        root.child ( eid ).removeValue ();
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
