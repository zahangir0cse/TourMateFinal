package com.android.zsm.tourmatefinal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.zsm.tourmatefinal.model.Moments;
import com.android.zsm.tourmatefinal.utility.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakeCameraPhoto extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSAVE;
    private ImageView ivImage;
    EditText caption;
    private String userChoosenTask;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Uri uri;
    private StorageReference mStorageRef;
    private String eventid;
    private String userid;
    private String photoname;
    private Uri picUri;
    public Intent intent;
    private TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_camera_photo);
        ivImage = findViewById(R.id.imageView);
        caption = findViewById(R.id.caption);
        btnSAVE = findViewById(R.id.saveBtn);
        photoname = String.valueOf(System.currentTimeMillis());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Moments");
        Toolbar toolbar = findViewById(R.id.toolbarCamra);
        toolbar.setTitle("Take Photo");
        setSupportActionBar(toolbar);
        show = findViewById(R.id.showmessageCamra);
        Intent in = getIntent();
        eventid = in.getStringExtra("eventid");
        userid = in.getStringExtra("userid");
        selectImage();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {

                }
                break;
        }
    }

    private void selectImage() {

        boolean result = Utility.checkPermission(TakeCameraPhoto.this);
        if (result)
            cameraIntent();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= 24) {
                    String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + photoname + ".jpg";
                    File file1 = new File(file);
                    picUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }

        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + photoname + ".jpg";
            File file1 = new File(file);
            picUri = Uri.fromFile(file1);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);

        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
              if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                ivImage.setImageURI(picUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        photoname = String.valueOf(System.currentTimeMillis());
        File destination = new File(Environment.getExternalStorageDirectory(),"DCIM/Camera/" + photoname + ".jpg");
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        Uri uri2 = (Uri) extras.get("data");
        Toast.makeText(this, "inside uri: " + uri2, Toast.LENGTH_SHORT).show();

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
    }

    public void uploadToServer(View view) {
        StorageReference riversRef = mStorageRef.child("gallery/" + photoname + ".jpg");
        Toast.makeText(this, "photo uri is : " + picUri, Toast.LENGTH_SHORT).show();
        riversRef.putFile(picUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String url = downloadUrl.getPath();
                        uploadDatabaseFilename(url);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        show.setText("Yupload fail: " + exception.getMessage().toString());
                        show.setVisibility(View.VISIBLE);
                    }
                });


    }

    public void uploadDatabaseFilename(String url) {
        Toast.makeText(this, "String url: " + url, Toast.LENGTH_SHORT).show();
        String pcaption = caption.getText().toString();
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(new Moments(key, eventid, url, pcaption));

    }

    public void takeAnotherPhoto(View view) {
        selectImage();
    }

}
