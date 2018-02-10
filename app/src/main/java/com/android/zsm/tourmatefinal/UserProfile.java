package com.android.zsm.tourmatefinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class UserProfile extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private TextView name;
    private TextView email;
    private ImageView userImage;
    private FirebaseAuth auth;
    private AlertDialog.Builder b;
    private Button changephoto;
    public String userChoosenTask;
    private StorageReference mStorageRef;
    private ImageView ivImage;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_RACCESS_CAMERA = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbarUser);
        toolbar.setTitle("My Profile");
        setSupportActionBar(toolbar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        changephoto = findViewById(R.id.updatephoto);
        name = findViewById(R.id.nameUser);
        email = findViewById(R.id.emailUser);
        userImage = findViewById(R.id.userImage);
        ivImage = findViewById(R.id.ivImage);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UserProfile.this, LoginActivity.class));
        } else {
            Uri photoUri = user.getPhotoUrl();
            if (String.valueOf(photoUri) == null) {
                changephoto.setText("Upload profile photo");
            } else {
                Picasso.with(this).load(photoUri).into(userImage);
                changephoto.setText("Update profile photo");
            }
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }


    }

    public void goToDatabaseActivity(View view) {
        startActivity(new Intent(this, UserProfile.class));
    }

    public void resetPassword(View view) {
        showeditpasswordDialog();

    }


    private void showeditpasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_reset, null);
        builder.setTitle("Reset Password");
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText npass = alertLayout.findViewById(R.id.newpassword);
        final EditText cpass = alertLayout.findViewById(R.id.confirmpassword);
        Button login = alertLayout.findViewById(R.id.ResetBtn);
        Button cancel = alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newpassValue = npass.getText().toString();
                String cpassValue = cpass.getText().toString();
                if (newpassValue.equals(cpassValue)) {
                    updatePass(newpassValue);
                    ad.dismiss();
                    ad.cancel();

                } else {
                    cpass.setError("Confirm Password Didn't Match");
                    Toast.makeText(UserProfile.this, "Confirm Password Didn't Match", Toast.LENGTH_SHORT).show();
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

    private void showeditprofileDialog(final FirebaseUser u) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.update_profile, null);
        builder.setTitle("Update Profile");
        builder.setView(alertLayout);
        builder.setCancelable(true);
        final EditText uname = alertLayout.findViewById(R.id.uname);
        uname.setText(u.getDisplayName());
        final EditText uemail = alertLayout.findViewById(R.id.uemail);
        uemail.setText(u.getEmail());

        Button login = alertLayout.findViewById(R.id.ResetBtn);
        Button cancel =  alertLayout.findViewById(R.id.cancel);

        final AlertDialog ad = builder.create();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newname = uname.getText().toString();
                if (TextUtils.isEmpty(newname)) {
                    uname.setError("Enter your name");
                    return;
                } else {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newname)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserProfile.this, "Profile Update success", Toast.LENGTH_SHORT).show();

                                        Log.d("TAG", "User profile updated.");
                                    }
                                }
                            });
                }
                String newemail = uemail.getText().toString();
                if (TextUtils.isEmpty(newemail)) {
                    uemail.setError("Enter valid email address");
                    return;
                } else {
                    user.updateEmail(newemail);

                }
                ad.dismiss();
                ad.cancel();

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

    public void updateprofile(View view) {
        showeditprofileDialog(user);
    }

    public void updatephoto(View view) {
        selectImage();
    }

    public void updatePass(String newPassword) {

        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfile.this, "Password Successfully Updated", Toast.LENGTH_SHORT).show();

                    Log.d("TAG", "User password updated.");
                }
            }
        });

    }

    // start for upload photo

    private void selectImage() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera", "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                checkPermission();
                                break;
                            case 2:

                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_RACCESS_CAMERA);
            return;
        } else {
            takePhotoFromCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromGallary();
                } else {
                    return;
                }
                break;
            case MY_PERMISSIONS_REQUEST_RACCESS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhotoFromCamera();
                } else return;
                ;
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
        ivImage.setVisibility(View.VISIBLE);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);
        ivImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(UserProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                    ivImage.setImageBitmap(bitmap);
                    ivImage.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            ivImage.setImageBitmap(thumbnail);
            ivImage.setVisibility(View.VISIBLE);
            saveImage(thumbnail);
            Toast.makeText(UserProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            uploadImageTofirebase(f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

public  void uploadImageTofirebase(String path) {
    Uri file = Uri.fromFile(new File(path));
    mStorageRef.putFile( file)
            .

    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess (UploadTask.TaskSnapshot taskSnapshot){
            // Get a URL to the uploaded content
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            uploadFirebasedb(downloadUrl);
        }

        private void uploadFirebasedb(Uri downloadUrl) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, "Profile Photo Update success", Toast.LENGTH_SHORT).show();

                                Log.d("TAG", "User profile photo updated.");
                            }
                        }
                    });
        }
    })
            .

    addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure (@NonNull Exception exception){
        }
    });
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
