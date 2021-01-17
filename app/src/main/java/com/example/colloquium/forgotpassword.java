package com.example.colloquium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    TextView tvforgotpassword;
    EditText etresetemail;
    Button btnresetpassword;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        tvforgotpassword = findViewById(R.id.tvforgotpassword);
        etresetemail = findViewById(R.id.etresetemail);
        btnresetpassword = findViewById(R.id.btnresetpassword);
        firebaseAuth = FirebaseAuth.getInstance();

        btnresetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etresetemail.getText().toString().trim();

                if (email.equals("")){
                    Toast.makeText(forgotpassword.this, "Enter your registered Email Id", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(forgotpassword.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotpassword.this, MainActivity.class));
                            }
                            else{
                                Toast.makeText(forgotpassword.this, "Error in Sending email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
