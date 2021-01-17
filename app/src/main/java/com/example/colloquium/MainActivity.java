package com.example.colloquium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText etemail, etpass ;
    Button btnlogin;
    TextView tvfpass, tvregister;
    private FirebaseAuth firebaseAuth;
    ProgressDialog mLoadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.etpass);

        btnlogin = findViewById(R.id.btnlogin);

        tvfpass = findViewById(R.id.tvfpass);
        tvregister = findViewById(R.id.tvregister);

        mLoadingbar = new ProgressDialog(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, pass;

                email = etemail.getText().toString();
                pass = etpass.getText().toString();

                if (email.equals(""))
                {
                    Toast.makeText(MainActivity.this, "Email Required",Toast.LENGTH_SHORT).show();
                }
                else if (pass.equals("")){
                    Toast.makeText(MainActivity.this, "Password Required",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mLoadingbar.setTitle("Login");
                    mLoadingbar.setMessage("Please wait till logging in!");
                    mLoadingbar.setCanceledOnTouchOutside(false);
                    mLoadingbar.show();

                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mLoadingbar.dismiss();
                                Toast.makeText(MainActivity.this, "Loggeg In Successfully!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, userprofile.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            else {
                                mLoadingbar.dismiss();
                                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Signup.class);
                startActivity(i);
            }
        });

        tvfpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,forgotpassword.class);
                startActivity(i);
            }
        });
    }
}
