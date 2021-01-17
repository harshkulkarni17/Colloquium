package com.example.colloquium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    EditText etname, etemail, etpass, etcpass;
    Button btnsignup;
    private FirebaseAuth firebaseAuth;
    ProgressDialog mLoadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etname = findViewById(R.id.etname);
        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.etpass);
        etcpass = findViewById(R.id.etcpass);

        btnsignup = findViewById(R.id.btnsignup);

        firebaseAuth = FirebaseAuth.getInstance();
        mLoadingbar = new ProgressDialog(this);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, pass, cpass;

                name = etname.getText().toString();
                email = etemail.getText().toString().trim();
                pass = etpass.getText().toString().trim();
                cpass = etcpass.getText().toString().trim();

                if (name.equals(""))
                {
                    Toast.makeText(Signup.this, "Name Required",Toast.LENGTH_SHORT).show();
                }
                else if (email.equals(""))
                {
                    Toast.makeText(Signup.this, "Email Required",Toast.LENGTH_SHORT).show();
                }
                else if (pass.equals("")){
                    Toast.makeText(Signup.this, "Password Required",Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<5)
                {
                    Toast.makeText(Signup.this,"Password is too short",Toast.LENGTH_SHORT).show();
                }
                else if (cpass.equals(""))
                {
                    Toast.makeText(Signup.this, "Confirm the Password",Toast.LENGTH_SHORT).show();
                }
                else if (!cpass.equals(pass))
                {
                    Toast.makeText(Signup.this, "Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mLoadingbar.setTitle("Registration");
                    mLoadingbar.setMessage("Please wait, signing up in process!");
                    mLoadingbar.setCanceledOnTouchOutside(false);
                    mLoadingbar.show();

                   firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()){
                               mLoadingbar.dismiss();
                               Toast.makeText(Signup.this, "Registered successfully",Toast.LENGTH_SHORT).show();
                               Intent i = new Intent(Signup.this,MainActivity.class);
                               startActivity(i);
                               finish();
                           }
                           else {
                               mLoadingbar.dismiss();
                               Toast.makeText(Signup.this, "User Already Exists!",Toast.LENGTH_SHORT).show();
                               Intent i = new Intent(Signup.this,MainActivity.class);
                               startActivity(i);
                               finish();
                           }
                       }
                   }) ;

                }
            }
        });
    }
}
