package com.example.firstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseActivity extends AppCompatActivity {
    private ListView list;
    private Button addcontact;
    private ArrayAdapter<String> adapter;
    private List<String> contactList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        list = findViewById(R.id.list);
        addcontact = findViewById(R.id.addcontact);
        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(DatabaseActivity.this, R.layout.items, contactList);
        list.setAdapter(adapter);
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DatabaseActivity.this, ContactActivity.class));
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Vendor1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vendor vendor = snapshot.getValue(Vendor.class);
                    if (vendor != null) {
                        String fullName = vendor.getFirstName() + " " + (vendor.getLastName() != null ? vendor.getLastName() : "");
                        String phoneNumber = vendor.getPhoneNumber();
                        String contactInfo = fullName + " - " + phoneNumber;
                        Log.d("DatabaseActivity", "Contact Info: " + contactInfo);
                        contactList.add(contactInfo);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter after updating list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DatabaseActivity.this, "Failed to load contacts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DatabaseActivity", "onCancelled: DatabaseError=" + databaseError.getMessage());
            }
        });
    }
}