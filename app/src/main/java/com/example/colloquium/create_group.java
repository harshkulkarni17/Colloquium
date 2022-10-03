package com.example.colloquium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.ArrayList;

public class create_group extends AppCompatActivity {

    TextView tvcreate, tvsubcode;
    Button btngenerate, btncreate;
    EditText etcode, etsubname;
    ImageView sharecode;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        tvcreate = findViewById(R.id.tvcreate);
        tvsubcode = findViewById(R.id.tvsubcode);
        sharecode = findViewById(R.id.sharecode);
        etcode = findViewById(R.id.etcode);
        etsubname = findViewById(R.id.etsubname);
        btngenerate = findViewById(R.id.btngenerate);
        btncreate = findViewById(R.id.btncreate);
        ArrayList<String> data = New ArrayList<>();
        data.add(tvcreate.toString());
        data.add(tvsubcode.toString());
        data.add(sharecode.toString());
        data.add(etcode.toString());
        data.add(etsubname.toString());
        data.add(btngenerate.toString());
        data.add(btncreate.toString());
        System.out.println(data); // For debugging purpose
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Courses");

        Toolbar toolbar = findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);

        btngenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etcode.setText(getRandomString(6));
            }
        });

        sharecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etcode == null){
                    Toast.makeText(create_group.this, "Subject Code not available", Toast.LENGTH_SHORT).show();
                }
                else {
                    String text = etcode.getText().toString();
                    Toast.makeText(create_group.this, "Share", Toast.LENGTH_SHORT).show();
                    Intent shareintent = new Intent();
                    shareintent.setAction(Intent.ACTION_SEND);
                    shareintent.putExtra(Intent.EXTRA_SUBJECT, "Subject Code");
                    shareintent.putExtra(Intent.EXTRA_TEXT, "Enter this code for joining the subject group: "+text);
                    shareintent.setType("text/plain");
                    startActivity(Intent.createChooser(shareintent, "Share Via"));
                }
            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String subname = etsubname.getText().toString();
                String subcode = etcode.getText().toString();

                if (subname.isEmpty()){
                    showError(etsubname, "Invalid Subject");
                }

                else if (subcode.isEmpty()){
                    showError(etcode, "Invalid Subject Code");
                }

                else{
                    courseHelperClass helperClass = new courseHelperClass();
                    helperClass.setSubjectName(subname);
                    helperClass.setSubjectCode(subcode);

                    databaseReference.child(subcode).setValue(helperClass);
                    onBackPressed();
                    Toast.makeText(create_group.this, "Group Created Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    public static String getRandomString(int i) {
        final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        while (i > 0){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }
        return result.toString();
    }
}
