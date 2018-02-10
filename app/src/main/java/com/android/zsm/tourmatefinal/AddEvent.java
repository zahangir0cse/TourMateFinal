package com.android.zsm.tourmatefinal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import com.android.zsm.tourmatefinal.adapter.EventAdapter;
import com.android.zsm.tourmatefinal.model.Events;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEvent extends AppCompatActivity {
    private EditText eventNameET, eventBudgetET, eventDateTV;
    ListView eventList;
    DatabaseReference root;
    private EventAdapter eventAdapter;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Button saveBt;
    private FloatingActionButton fab;
    public String updatedeventid = null;
    public Events event =null;
    private Calendar calendar;
    private  String userid;
    private boolean validst = false;
    private  String todate;
    int year,month,day,hour,min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = findViewById(R.id.toolbarEvent);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        todate = df.format(Calendar.getInstance().getTime());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        eventNameET = findViewById ( R.id.eventName );
        eventBudgetET = findViewById ( R.id.eventBudget );
        eventDateTV = findViewById ( R.id.eventDate );
        saveBt = findViewById ( R.id.save );
       Intent intent = getIntent();
       event= (Events) intent.getSerializableExtra("obj");
        saveBt = findViewById ( R.id.save );
        if(event == null) {
            saveBt.setText ("Save");
            toolbar.setTitle("Create Event");
        } else {
            saveBt.setText ("Update");
            eventNameET.setText(event.getEventName());
            eventBudgetET.setText(String.valueOf(event.getBudget()));
            eventDateTV.setText(event.getEventDate());
            updatedeventid = event.getEventID();
            userid = event.getUserID();
            toolbar.setTitle("Edit Event");
        }
        setSupportActionBar(toolbar);
        root =FirebaseDatabase.getInstance ().getReference ("Events");
        root.keepSynced ( true );
    }

    public void saveEvent(View view) {
        final String eventName = eventNameET.getText().toString();
        final double eventBudget = Double.parseDouble (eventBudgetET.getText().toString());
        final String eventDate = eventDateTV.getText().toString();
        String eventId = root.push().getKey();
        String userid = user.getUid ();
        if (eventName.length() == 0) {
            eventNameET.setError("Enter Event Name");
            validst = false;
        } else {
            validst = true;
        }
        if (eventBudget == 0) {
            eventBudgetET.setError("Enter Event Budget");
            validst = false;
        } else {
            validst = true;
        }
        if (eventDate.length() == 0) {
            eventDateTV.setError("Enter Event Date");
            validst = false;
        } else {
            validst = true;
        }
        if (validst) {

            final String submitbutton  = saveBt.getText().toString();
            switch (submitbutton) {
                case "Save":
                    Events ev = new Events ( eventId,userid,eventName,eventBudget, eventDate,todate);
                    root.child ( eventId ).setValue ( ev );
                    startActivity(new Intent(AddEvent.this, EventList.class));

                    break;
                case "Update":
                    Events evu = new Events ( updatedeventid,userid,eventName,eventBudget, eventDate,event.getCreateDate());
                    root.child ( updatedeventid ).setValue ( evu );
                    startActivity(new Intent(AddEvent.this, EventList.class));
                    break;
            }
        }
    }
    public void selectDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,year,month,day);
        calendar.add(Calendar.DATE, 1);
        Date newDate = calendar.getTime();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year,month,dayOfMonth);
              SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            ((EditText)findViewById(R.id.eventDate)).setText(sdf.format(calendar.getTime()));
        }
    };

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

    public void goToEvent(View view) {
        startActivity(new Intent (AddEvent.this,EventList.class));
        finish();
    }
    
}
