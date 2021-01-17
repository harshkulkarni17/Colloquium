package com.example.colloquium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class subject extends AppCompatActivity {

    ImageView ivupload;
    TextView uploadFile, Filename;
    EditText etFilename;
    Button btnUpload, btnBrowse, btnFetch;
    String subjectCode, subName;
    Uri pdfUri;

    FirebaseStorage storage; //uploading files
    FirebaseDatabase database; //store URLs of uploaded files
    ProgressDialog progressDialog;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        ivupload = findViewById(R.id.ivupload);
        uploadFile = findViewById(R.id.uploadFile);
        Filename = findViewById(R.id.Filename);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnUpload = findViewById(R.id.btnUpload);
        etFilename = findViewById(R.id.etFilename);
        btnFetch = findViewById(R.id.btnFetch);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab_btn);

        subjectCode = getIntent().getExtras().getString("subCode");
        reference = FirebaseDatabase.getInstance().getReference().child("Courses").child(subjectCode);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subName = String.valueOf(dataSnapshot.child("subjectName").getValue());

                Toolbar toolbar = findViewById(R.id.toolbar1);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(subName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(subject.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(subject.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }
                else
                    ActivityCompat.requestPermissions(subject.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfUri!=null)
                    uploadFile(pdfUri);
                else
                    Toast.makeText(subject.this, "Select a File", Toast.LENGTH_SHORT).show();
            }
        });


        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(subject.this, MyRecyclerViewActivity.class);
                intent.putExtra("subCode", subjectCode);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(subject.this, messages.class);
                intent.putExtra("subCode", subjectCode);
                startActivity(intent);
            }
        });



    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String File_Name = etFilename.getText().toString();

        final String fileName = System.currentTimeMillis()+"";
        final String filename1 = File_Name + System.currentTimeMillis();
        final StorageReference storageReference = storage.getReference();

        storageReference.child("Uploads").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        String url = uri.toString();
                        //Store url in realtime database

                        DatabaseReference reference = database.getReference().child("Courses").child(subjectCode).child("Uploads");

                        reference.child(filename1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(subject.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                    Filename.setText("No File Selected");
//
                                }
                                else
                                    Toast.makeText(subject.this, "Something went wrong, File not uploaded", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(subject.this, "Something went wrong, File not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                //track progress of our upload
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectPdf();
        }
        else
            Toast.makeText(subject.this, "please provide permission", Toast.LENGTH_SHORT).show();

    }

    private void selectPdf() {

        // to offer user to select the file using file manager
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); //to fetch files
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check whether user has selected a file or not
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            Filename.setText(data.getData().getLastPathSegment());
        } else
            Toast.makeText(subject.this, "please select a file", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subcode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        getSubjectCode();

        return super.onOptionsItemSelected(item);
    }

    private void getSubjectCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        builder.setTitle("Subject Code : "+subjectCode);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_SUBJECT, "Subject Code");
                shareintent.putExtra(Intent.EXTRA_TEXT, "Enter this code for joining the subject group: "+ subjectCode);
                shareintent.setType("text/plain");
                startActivity(Intent.createChooser(shareintent, "Share Via"));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
