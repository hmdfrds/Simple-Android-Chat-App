package com.example.simplechatapplication;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout edtEmail, edtPassword, edtConfirmPassword, edtName;
    Button btnRegister;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView profileImg;
    final int take_photo = 100;
    String file_path = Environment.getExternalStorageDirectory() + "/recent.jpg";
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtName = findViewById(R.id.edtName);
        profileImg = findViewById(R.id.profileImg);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            if (!edtPassword.getEditText().getText().toString().equals(edtConfirmPassword.getEditText().getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.createUserWithEmailAndPassword(edtEmail.getEditText().getText().toString(), edtPassword.getEditText().getText().toString()).addOnCompleteListener(task -> {
                    task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", task.getResult().getUser().getUid());
                            user.put("email", task.getResult().getUser().getEmail().toLowerCase());
                            user.put("name", edtName.getEditText().getText().toString());

                            // Get the data from an ImageView as bytes
                            profileImg.setDrawingCacheEnabled(true);
                            profileImg.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) profileImg.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
                            StorageReference mountainsRef = storageRef.child("mountains.jpg");

// Create a reference to 'images/mountains.jpg'
                            StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

                            UploadTask uploadTask = mountainsRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            user.put("imageUrl", uri.toString());
                                            firebaseFirestore.collection("users").document(task.getResult().getUser().getUid()).set(user).addOnCompleteListener(task1 -> {
                                                task1.addOnSuccessListener(unused -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
                                                task1.addOnFailureListener(e -> {

                                                });
                                            });
                                        }
                                    });

                                }
                            });


                        }
                    });
                    task.addOnFailureListener(e -> {

                    });
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            switch (requestCode) {
                case take_photo: {
                    if (data != null) {

                        Bitmap srcBmp = (Bitmap) data.getExtras().get("data");

                        // ... (process image  if necesary)

                        profileImg.setImageBitmap(srcBmp);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        srcBmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                        // you can create a new file name "test.jpg" in sdcard folder.
                        File f = new File(file_path);
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // write the bytes in file
                        FileOutputStream fo = null;
                        try {
                            fo = new FileOutputStream(f);
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        try {
                            fo.write(bytes.toByteArray());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        // remember close de FileOutput
                        try {
                            fo.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.e("take-img", "Image Saved to sd card...");
                        // Toast.makeText(getApplicationContext(),
                        // "Image Saved to sd card...", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }


    }

    public void openCamera(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, take_photo);

    }
}