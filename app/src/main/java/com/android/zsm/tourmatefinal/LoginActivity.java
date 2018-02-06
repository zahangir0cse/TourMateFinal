package com.android.zsm.tourmatefinal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.preference.UserPreference;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private FirebaseAuth auth;
    UserPreference userpreference;
    private String uid;
    private String email;
    private boolean loginstatus =false;
    private  TextView  verifyTV;
    private Button verifyBt, skipBt;
    private boolean connected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verifyTV = findViewById(R.id.verifyText);
        verifyBt = findViewById(R.id.verifyBtn);
        skipBt = findViewById(R.id.skip);
        userpreference = new UserPreference(this );
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
             connected = true;
        }
        else {
            connected = false;
        }
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            List<AuthUI.IdpConfig> providers
                    = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else{
             uid = user.getUid();
             email = user.getEmail();
             loginstatus = true;
             userpreference.saveUser(uid,email,loginstatus);

            Toast.makeText(this, "logged in as "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,EventList.class));
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            String verificationString = "";
            user = FirebaseAuth.getInstance().getCurrentUser();
            boolean isVerified = user.isEmailVerified();
            if(isVerified){
                verificationString = "Email is verified";
            }else{
                verifyBt.setEnabled(true);
                verifyBt.setText("Verify");
                verifyBt.setVisibility(View.VISIBLE);
                skipBt.setEnabled(true);
                skipBt.setText("Skip");
                skipBt.setVisibility(View.VISIBLE);
                verifyTV.setEnabled(true);
                verifyTV.setText("Your email address is not verified please verify.");
                verifyTV.setVisibility(View.VISIBLE);


            }
            uid = user.getUid();
            email = user.getEmail();
            loginstatus = true;
            userpreference.saveUser(uid,email,loginstatus);
        }
    }

    public void logoutUser(View view) {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    public void goToDatabaseActivity(View view) {
        startActivity(new Intent(this,EventList.class));
    }

    public void veriFyNow( View view) {
        Button b = (Button)view;
        String buttonText = b.getText().toString();
      switch (buttonText){
          case "Verify":
              user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      verifyTV.setEnabled(true);
                      verifyTV.setText("An email hasbeen send to your email address please check and verify.");
                      verifyTV.setVisibility(View.VISIBLE);
                      verifyBt.setText("Check email");

                      Toast.makeText(LoginActivity.this, "email sent", Toast.LENGTH_SHORT).show();
                  }
              });

              break;

          case "Check email":
              Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
              PackageManager pm = getPackageManager();

              List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
              if (resInfo.size() > 0) {
                  ResolveInfo ri = resInfo.get(0);
                  // First create an intent with only the package name of the first registered email app
                  // and build a picked based on it
                  Intent intentChooser = pm.getLaunchIntentForPackage(ri.activityInfo.packageName);
                  Intent openInChooser =
                          Intent.createChooser(intentChooser,
                                  getString(R.string.user_reg_email_client_chooser_title));

                  // Then create a list of LabeledIntent for the rest of the registered email apps
                  List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                  for (int i = 1; i < resInfo.size(); i++) {
                      // Extract the label and repackage it in a LabeledIntent
                      ri = resInfo.get(i);
                      String packageName = ri.activityInfo.packageName;
                      Intent intent = pm.getLaunchIntentForPackage(packageName);
                      intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                  }

                  LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
                  // Add the rest of the email apps to the picker selection
                  openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                  startActivity(openInChooser);
              }
              break;
          case "Skip":
              startActivity(new Intent(LoginActivity.this,EventList.class));
              break;

      }

    }
}
