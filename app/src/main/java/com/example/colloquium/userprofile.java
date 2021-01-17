package com.example.colloquium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class userprofile extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    CircleImageView profileImageView;
    EditText username, city, profession;
    Button btnsave;
    Uri imageuri;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        profileImageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        city = findViewById(R.id.city);
        profession =  findViewById(R.id.profession);
        btnsave = findViewById(R.id.btnsave);

        mLoadingBar = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        Toolbar toolbar = findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });

    }

    private void SaveData() {
        final String ipusername = username.getText().toString();
        final String ipusercity = city.getText().toString();
        final String ipuserprof = profession.getText().toString();

        if (ipusername.isEmpty() || ipusername.length()<3 || ipusername.length()>10)
        {
           showError(username, "Invalid Username");
        }
        else if (ipusercity.isEmpty())
        {
            showError(city, "Invalid City");
        }
        else if (ipuserprof.isEmpty())
        {
            showError(profession, "Invalid Profession");
        }
        else if (imageuri == null)
        {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mLoadingBar.setMessage("Profile setup in progress");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            storageReference.child(firebaseUser.getUid()).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        storageReference.child(firebaseUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("username", ipusername);
                                hashMap.put("city", ipusercity);
                                hashMap.put("profession", ipuserprof);
                                hashMap.put("profileImage", uri.toString());
                                hashMap.put("status", "offline");


                                databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        startActivity(new Intent(userprofile.this, homescreen.class));
                                        mLoadingBar.dismiss();
                                        Toast.makeText(userprofile.this, "Profile setup complete", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(userprofile.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            imageuri = data.getData();
            profileImageView.setImageURI(imageuri);
        }
    }
}
