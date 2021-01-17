package com.example.colloquium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    TextView tvjoin;
    EditText etSubcode;
    Button btnjoin, btncreate;

    ArrayList<String> code;

    DatabaseReference reference;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tvjoin = root.findViewById(R.id.tvjoin);
        etSubcode = root.findViewById(R.id.etSubcode);
        btnjoin = root.findViewById(R.id.btnjoin);
        btncreate = root.findViewById(R.id.btncreate);

        code = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("Courses");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String key = String.valueOf(dataSnapshot1.child("subjectCode").getValue());
                    code.add(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String subjectCode = etSubcode.getText().toString();

                if (code.contains(subjectCode)){
                    Intent intent = new Intent(getActivity(), subject.class);
                    intent.putExtra("subCode", subjectCode);
                    startActivity(intent);
                }
                else{
                    showError(etSubcode, "Invalid Subject Code");
                }

            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), create_group.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}
